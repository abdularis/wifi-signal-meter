package com.aar.app.wifinetanalyzer.scanner

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.util.Log
import com.aar.app.wifinetanalyzer.common.getHexMac
import com.aar.app.wifinetanalyzer.common.intToStringIP
import com.aar.app.wifinetanalyzer.data.VendorFinder
import com.aar.app.wifinetanalyzer.scanner.error.EmptyInterfaceAddressException
import com.aar.app.wifinetanalyzer.scanner.error.WifiInterfaceNotFoundException
import com.aar.app.wifinetanalyzer.scanner.error.WifiNotConnectedException
import com.stealthcopter.networktools.ARPInfo
import com.stealthcopter.networktools.Ping
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import java.math.BigInteger
import java.net.Inet4Address
import java.net.InetAddress
import java.net.InterfaceAddress
import java.net.NetworkInterface
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class NetworkScanner(ctx: Context, private val vendorFinder: VendorFinder) {

    private val connManager = ctx.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val wifiManager = ctx.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    inner class PingObservableOnSubscribe(private val scannerInfo: ScannerInfo): ObservableOnSubscribe<Pair<Int, ScanResponse>> {
        override fun subscribe(emitter: ObservableEmitter<Pair<Int, ScanResponse>>) {
            val ip4Iterator = scannerInfo.ip4Iterator
            val executor = Executors.newFixedThreadPool(16)

            val progress = AtomicInteger(0)
            val count = ip4Iterator.numberOfIp
            while (ip4Iterator.hasNext()) {
                ip4Iterator.next()
                val ip = intToStringIP(Integer.reverseBytes(ip4Iterator.currentIp))

                executor.submit(Callable {
                    val result = Ping.onAddress(ip).setTimes(1).doPing()
                    val scanResp = if (result.isReachable) {
                        val mac =
                                if (ip == scannerInfo.ip) scannerInfo.mac
                                else ARPInfo.getMACFromIPAddress(ip) ?: ""
                        val vendor = vendorFinder.findVendor(mac) ?: ""
                        val type = when (ip) {
                            scannerInfo.ip -> ScanResponse.DeviceType.Me
                            scannerInfo.gatewayIp -> ScanResponse.DeviceType.Router
                            else -> ScanResponse.DeviceType.Other
                        }
                        ScanResponse(ip, mac, vendor, type,true)
                    } else {
                        ScanResponse(ip = ip)
                    }

                    val percentage = (progress.incrementAndGet().toFloat() / count.toFloat() * 100f).toInt()
                    emitter.onNext(Pair(percentage, scanResp))
                })
            }
            executor.shutdown()
            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS)
            } catch (e: Exception) {
                Log.d("NetworkScanner", "Scanning network was cancelled or intercepted")
            }
            emitter.onComplete()
        }
    }

    fun scanConnectedWifiNetwork(): Observable<Pair<Int, ScanResponse>> {
        return getScannerInfoObservable()
                .flatMap { Observable.create(PingObservableOnSubscribe(it)) }
    }

    fun getScannerInfoObservable(): Observable<ScannerInfo> {
        return getWifiInetAddressObservable()
                .flatMap(this::getWifiNetworkInterfaceObservable)
                .flatMap(this::getScannerInfoObservable)
    }

    private fun getScannerInfoObservable(wifiNi: NetworkInterface): Observable<ScannerInfo> {
        return Observable.create {
            var interfaceAddr: InterfaceAddress? = null
            for (ia in wifiNi.interfaceAddresses) {
                if (ia.address is Inet4Address) {
                    interfaceAddr = ia
                    break
                }
            }

            if (interfaceAddr != null) {
                val ip = ByteBuffer.wrap(interfaceAddr.address.address).int
                val netmask = (0xFFFFFFFF shl (32 - interfaceAddr.networkPrefixLength)).toInt()
                val mac = getHexMac(wifiNi.hardwareAddress)
                val scannerInfo = ScannerInfo(
                        interfaceName = wifiNi.displayName,
                        mac = mac,
                        ip = intToStringIP(Integer.reverseBytes(ip)),
                        netmask = intToStringIP(Integer.reverseBytes(netmask)),
                        networkPrefixLength = interfaceAddr.networkPrefixLength,
                        gatewayIp = intToStringIP(wifiManager.dhcpInfo.gateway),
                        ip4Iterator = Ip4Iterator(ip, netmask))
                it.onNext(scannerInfo)
                it.onComplete()
            } else {
                it.onError(EmptyInterfaceAddressException())
            }
        }
    }

    private fun getWifiNetworkInterfaceObservable(ia: InetAddress): Observable<NetworkInterface> {
        return Observable.create {
            val ni = NetworkInterface.getByInetAddress(ia)
            if (ni != null) {
                it.onNext(ni)
                it.onComplete()
            } else {
                it.onError(WifiInterfaceNotFoundException())
            }
        }
    }

    private fun getWifiInetAddressObservable(): Observable<InetAddress> {
        return Observable.create {
            val connInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            if (!connInfo.isConnected) {
                it.onError(WifiNotConnectedException())
            } else {
                val wifiInfo = wifiManager.connectionInfo
                val addr = BigInteger.valueOf(wifiInfo.ipAddress.toLong()).toByteArray()
                val inetAddr = Inet4Address
                        .getByAddress(if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) addr.reversedArray() else addr)
                it.onNext(inetAddr)
                it.onComplete()
            }
        }
    }
}