package com.aar.app.wifinetanalyzer;

import com.aar.app.wifinetanalyzer.common.NetUtilsKt;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NetUtilsTest {

    @Test
    public void intToIpString_test() {
        String str = "00000";
        String sub = str.substring(0, 8);
    }

    @Test
    public void getHexMac_test() {
        byte mac[] = {0x00, (byte) 0xAB, 0x00, (byte) 0xC9, 0x45, 0x20};
        assertEquals("00:AB:00:C9:45:20", NetUtilsKt.getHexMac(mac));
    }
}
