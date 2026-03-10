package org.air_controller.web_access.graph;

import java.awt.*;
import java.time.LocalDateTime;

public record GraphItem(LocalDateTime timeStamp, double value, String dataPointColor) {

    public static String toColorString(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }
}
