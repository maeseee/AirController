package org.air_controller.sensor.qing_ping;

import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class Devices {
    public static final String MAC_AIR_PRESSURE_DEVICE = "582D3480A7F4"; // Entrance
    public static final String MAC_CO2_DEVICE_1 = "582D34831850"; // Living
    public static final String MAC_CO2_DEVICE_2 = "582D34844FE9"; // Love
    public static final String MAC_CO2_DEVICE_3 = "582D34845035"; // Office

    public static List<String> getDeviceList() {
        return List.of(MAC_AIR_PRESSURE_DEVICE, MAC_CO2_DEVICE_1, MAC_CO2_DEVICE_2, MAC_CO2_DEVICE_3);
    }
}
