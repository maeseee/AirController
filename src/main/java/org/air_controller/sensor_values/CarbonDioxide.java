package org.air_controller.sensor_values;

import org.air_controller.web_access.card.CardView;
import org.jetbrains.annotations.NotNull;

public record CarbonDioxide(double ppm) {

    public CarbonDioxide {
        validate(ppm, IllegalArgumentException.class);
    }

    public CardView toCardView() {
        return new CardView("CO2", ppmString(), "ppm");
    }

    private @NotNull String ppmString() {
        return String.format("%.0f", ppm);
    }

    public static CarbonDioxide createFromPpm(double ppm) throws InvalidArgumentException {
        validate(ppm, InvalidArgumentException.class);
        return new CarbonDioxide(ppm);
    }

    private static <T extends Exception> void validate(double ppm, Class<T> exceptionClass) throws T {
        if (ppm < 0.0 || ppm > 1000000.0) {
            final String message = "Given humidity of " + ppm + "% is out of range!";
            try {
                throw exceptionClass.getConstructor(String.class).newInstance(message);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("Failed to create exception of type " + exceptionClass.getName(), e);
            }
        }
    }
}
