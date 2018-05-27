package com.aar.app.wifinetanalyzer.scanner.error

class WifiNotConnectedException: RuntimeException("Wifi is not connected to any network/access point")
class WifiInterfaceNotFoundException: RuntimeException("There is no wifi network interface found in this device")
class EmptyInterfaceAddressException: RuntimeException("Network interface is not assign to any address/empty")