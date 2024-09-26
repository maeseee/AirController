package org.airController.sensor.qingPing;

import java.util.List;

public class QingPingDevices {
    private static final String MAC_AIR_PRESSURE_DEVICE = "582D3480A7F4";
    private static final String MAC_CO2_DEVICE = "582D34831850";

    public static List<String> getDeviceList() {
        return List.of(MAC_AIR_PRESSURE_DEVICE, MAC_CO2_DEVICE);
    }

    public static List<String> getAirPressureDevices() {
        return List.of(MAC_AIR_PRESSURE_DEVICE);
    }

    public static List<String> getCo2Devices() {
        return List.of(MAC_CO2_DEVICE);
    }
}
