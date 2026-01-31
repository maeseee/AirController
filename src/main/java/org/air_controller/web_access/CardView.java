package org.air_controller.web_access;

import org.jspecify.annotations.NonNull;

public record CardView(String name, String value, String unit) {

    @Override
    public @NonNull String toString() {
        return name + "=" + value + unit;
    }
}
