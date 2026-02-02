package org.air_controller.web_access.card_view;

import org.jspecify.annotations.NonNull;

public record CardItem(String name, String value, String unit) {

    @Override
    public @NonNull String toString() {
        return name + "=" + value + unit;
    }
}
