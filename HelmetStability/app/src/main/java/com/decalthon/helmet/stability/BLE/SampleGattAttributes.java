/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.decalthon.helmet.stability.ble;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    private static final HashMap<String, String> attributes = new HashMap();

    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
//    /**
//     * UART characteristics for oblu
//     * 00002a19-0000-1000-8000-00805f9b34fb
//     */
//    public static final String NORDIC_SERVER_UART = "0003cdd0-0000-1000-8000-00805f9b0131";//sevice
//
//    public static final String NORDIC_UART_tx = "0003cdd1-0000-1000-8000-00805f9b0131";//characterristic
//    public static final String NORDIC_UART_rx = "0003cdd2-0000-1000-8000-00805f9b0131";//characterristic static

    /**
     * Nordic UART characteristics
     * 00002a19-0000-1000-8000-00805f9b34fb
     */
    public static final String NORDIC_SERVER_UART = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";//sevice

    public static final String NORDIC_UART_tx = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";//characterristic
    public static final String NORDIC_UART_rx = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";//characterristic static

    // Heart Rate
    public static final String SERVER_HEART_RATE = "0000180d-0000-1000-8000-00805f9b34fb"; //Heart Rate Service
    public static final String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb"; // Heart Rate Characteristics (Notify & Read)

    public static final String SERVER_BATTERY = "0000180f-0000-1000-8000-00805f9b34fb"; //Battery service
    public static final String BATTERY_LEVEL_MEASUREMENT = "00002a19-0000-1000-8000-00805f9b34fb"; //Battery level characteristic


    static {
        // Sample Services.
        attributes.put(NORDIC_SERVER_UART, "UART Service");
        attributes.put(NORDIC_UART_rx, "UART rx Service");
        attributes.put(NORDIC_UART_tx, "UART tx Service");
        attributes.put(SERVER_HEART_RATE, "Heart Rate Service");
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Data");
        attributes.put(SERVER_BATTERY,"Battery Service");
        attributes.put(BATTERY_LEVEL_MEASUREMENT,"Battery Level Data");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
