package com.aar.app.wifinetanalyzer;

import com.aar.app.wifinetanalyzer.model.ConnectionInfo;
import com.aar.app.wifinetanalyzer.model.WifiAccessPoint;
import com.aar.app.wifinetanalyzer.model.WifiAccessPointBuilder;
import com.aar.app.wifinetanalyzer.model.WifiChannel;
import com.aar.app.wifinetanalyzer.model.WifiSignal;
import com.aar.app.wifinetanalyzer.wifilist.WifiAccessPointList;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class WifiApListTest {

    @Test
    public void testWithConnected() {
        List<WifiAccessPoint> wifiApList = new ArrayList<>();
        wifiApList.add(createWifiAp("test1", "01:00:00:00:00:00", 2412, true));
        wifiApList.add(createWifiAp("test2", "02:00:00:00:00:00", 2412, false));
        wifiApList.add(createWifiAp("test3", "03:00:00:00:00:00", 2412, false));
        wifiApList.add(createWifiAp("test4", "04:00:00:00:00:00", 2417, false));
        wifiApList.add(createWifiAp("test4", "05:00:00:00:00:00", 2422, false));
        WifiAccessPointList aplist = new WifiAccessPointList(wifiApList);


        assertEquals(wifiApList.size(), aplist.getWifiList().size());
        assertNotNull(aplist.getConnectedWifi());
        assertEquals(2, aplist.getInterferingWifiList().size());
        assertEquals(2, aplist.getOtherWifiList().size());
    }

    @Test
    public void testNoConnected() {
        List<WifiAccessPoint> wifiApList = new ArrayList<>();
        wifiApList.add(createWifiAp("test1", "01:00:00:00:00:00", 2412, false));
        wifiApList.add(createWifiAp("test2", "02:00:00:00:00:00", 2412, false));
        wifiApList.add(createWifiAp("test3", "03:00:00:00:00:00", 2417, false));
        WifiAccessPointList aplist = new WifiAccessPointList(wifiApList);


        assertEquals(wifiApList.size(), aplist.getWifiList().size());
        assertNull(aplist.getConnectedWifi());
        assertEquals(0, aplist.getInterferingWifiList().size());
        assertEquals(wifiApList.size(), aplist.getOtherWifiList().size());
    }

    private WifiAccessPoint createWifiAp(String ssid, String bssid, int freq, boolean connected) {
        ConnectionInfo ci = connected ? new ConnectionInfo("", 0, "", "", "", "", "") : null;
        return new WifiAccessPoint(
                new WifiSignal(bssid, ssid, "[]", new WifiChannel(freq), -50, "non"),
                ci
        );
    }
}
