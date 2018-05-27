package com.aar.app.wifinetanalyzer;

import com.aar.app.wifinetanalyzer.common.NetUtilsKt;
import com.aar.app.wifinetanalyzer.scanner.Ip4Iterator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Ip4IteratorTest {

    private int ip = 0xC0A80096;

    @Test
    public void test1() {
        Ip4Iterator ip4Iterator = new Ip4Iterator(ip, 0xffff0000);
        assertEquals(0xffff, ip4Iterator.getNumberOfIp());
        assertEquals("192.168.0.0", NetUtilsKt.intToStringIP(Integer.reverseBytes(ip4Iterator.getNetworkId())));

        String ipStr = "192.168.%d.%d";
        for (int i = 0; i <= 255; i++) {
            for (int j = 0; j <= 255; j++) {
                if (i == 0 && j == 0) continue;

                ip4Iterator.next();
                assertEquals(String.format(ipStr, i, j), NetUtilsKt.intToStringIP(Integer.reverseBytes(ip4Iterator.getCurrentIp())));
            }
        }
    }

    @Test
    public void test2() {
        Ip4Iterator ip4Iterator = new Ip4Iterator(ip, 0xffffff00);
        assertEquals(0xff, ip4Iterator.getNumberOfIp());
        assertEquals("192.168.0.0", NetUtilsKt.intToStringIP(Integer.reverseBytes(ip4Iterator.getNetworkId())));

        int idx = 0;
        while (ip4Iterator.hasNext()) {
            ip4Iterator.next();
            idx++;

            System.out.println(NetUtilsKt.intToStringIP(Integer.reverseBytes(ip4Iterator.getCurrentIp())));
        }

        assertEquals(0xff, idx);

//        String ipStr = "192.168.0.%d";
//        for (int j = 0; j <= 255; j++) {
//            if (j == 0) continue;
//            ip4Iterator.next();
//            assertEquals(String.format(ipStr, j), NetUtilsKt.intToStringIP(Integer.reverseBytes(ip4Iterator.getCurrentIp())));
//        }
    }
}
