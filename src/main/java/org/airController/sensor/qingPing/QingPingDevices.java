package org.airController.sensor.qingPing;

import java.util.List;

public class QingPingDevices {
    static final String MAC_AIR_PRESSURE_DEVICE = "582D3480A7F4";
    static final String MAC_CO2_DEVICE = "582D34831850";

    public static List<String> getDeviceList() {
        return List.of(MAC_AIR_PRESSURE_DEVICE, MAC_CO2_DEVICE);
    }
}
