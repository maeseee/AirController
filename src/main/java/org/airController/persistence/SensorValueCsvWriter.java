package org.airController.persistence;

import org.airController.controllers.SensorData;
import org.airController.controllers.SensorValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class SensorValueCsvWriter implements SensorValuePersistence {
    private static final Logger logger = LogManager.getLogger(SensorValueCsvWriter.class);

    private final String filePath;

    public SensorValueCsvWriter(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void persist(SensorValue value) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        final String formattedTime = value.getTimeStamp().format(formatter);
        final String formattedTemperature = String.format("%.2f", value.getTemperature().getCelsius());
        final String formattedHumidity = String.format("%.2f", value.getHumidity().getRelativeHumidity());
        final String formattedCo2 = value.getCo2().isPresent() ? String.format("%.2f", value.getCo2().get().getPpm()) : "";
        final String csvLine = String.format("%s,%s,%s,%s", formattedTime, formattedTemperature, formattedHumidity, formattedCo2);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(csvLine);
            writer.newLine();
        } catch (IOException e) {
            logger.error("CSV could not be written! {}", e.getMessage());
        }
    }

    @Override
    public void persist(SensorData sensorData) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        final String formattedTime = LocalDateTime.now().format(formatter);
        final String formattedTemperature = sensorData.getTemperature().isPresent() ? String.format("%.2f", sensorData.getTemperature().get().getCelsius()) : "";
        final String formattedHumidity = sensorData.getHumidity().isPresent() ? String.format("%.2f", sensorData.getHumidity().get().getRelativeHumidity()) : "";
        final String formattedCo2 = sensorData.getCo2().isPresent() ? String.format("%.2f", sensorData.getCo2().get().getPpm()) : "";
        final String csvLine = String.format("%s,%s,%s,%s", formattedTime, formattedTemperature, formattedHumidity, formattedCo2);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(csvLine);
            writer.newLine();
        } catch (IOException e) {
            logger.error("CSV could not be written! {}", e.getMessage());
        }
    }
}
