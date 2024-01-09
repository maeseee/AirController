package org.airController.persistence;

import org.airController.entities.AirValue;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class SensorValueCsvWriter implements SensorValuePersistence {

    private final String filePath;

    public SensorValueCsvWriter(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void persist(LocalDateTime time, AirValue value) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        final String formattedTime = time.format(formatter);
        final String formattedTemperature = String.format("%.2f", +value.getTemperature().getCelsius());
        final String formattedHumidity = String.format("%.2f", +value.getHumidity().getRelativeHumidity());
        final String csvLine = formattedTime + "," + formattedTemperature + "," + formattedHumidity;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(csvLine);
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
