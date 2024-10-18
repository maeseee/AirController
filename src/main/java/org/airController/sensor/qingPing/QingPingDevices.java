package org.airController.sensor.qingPing;

import java.util.List;

class QingPingDevices {
    static final String MAC_AIR_PRESSURE_DEVICE = "582D3480A7F4"; // Entrance
    static final String MAC_CO2_DEVICE_1 = "582D34831850"; // Living
    static final String MAC_CO2_DEVICE_2 = "582D34844FE9"; // Love
    static final String MAC_CO2_DEVICE_3 = "582D34845035"; // Office

    public static List<String> getDeviceList() {
        return List.of(MAC_AIR_PRESSURE_DEVICE, MAC_CO2_DEVICE_1, MAC_CO2_DEVICE_2, MAC_CO2_DEVICE_3);
    }
}
