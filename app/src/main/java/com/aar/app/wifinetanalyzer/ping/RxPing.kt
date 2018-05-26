package com.aar.app.wifinetanalyzer.ping

import com.stealthcopter.networktools.Ping
import com.stealthcopter.networktools.ping.PingResult
import com.stealthcopter.networktools.ping.PingStats
import io.reactivex.Observable

class RxPing {

    companion object {

        fun ping(ip: String, count: Int): Observable<PingResult> {
            return Observable.create { emmiter ->
                val ping = Ping.onAddress(ip)
                        .setTimes(count)
                        .doPing(object: Ping.PingListener {
                            override fun onResult(p: PingResult?) {
                                p?.let { emmiter.onNext(it) }
                            }

                            override fun onFinished(p0: PingStats?) {
                                emmiter.onComplete()
                            }
                        })
                emmiter.setCancellable {
                    ping.cancel()
                }
            }
        }

    }

}