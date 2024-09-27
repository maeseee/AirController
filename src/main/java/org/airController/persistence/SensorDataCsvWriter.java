package org.airController.persistence;

import org.airController.sensorValues.SensorData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

class SensorDataCsvWriter implements SensorValuePersistence {
    private static final Logger logger = LogManager.getLogger(SensorDataCsvWriter.class);

    private final String filePath;

    public SensorDataCsvWriter(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void persist(SensorData sensorData) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        final String formattedTime = sensorData.getTimeStamp().format(formatter);
        final String formattedTemperature = sensorData.getTemperature()
                .map(temperature -> String.valueOf(temperature.getCelsius()))
                .orElse("");
        final String formattedHumidity = sensorData.getHumidity()
                .map(humidity -> String.valueOf(humidity.getRelativeHumidity(sensorData.getTemperature().get())))
                .orElse("");
        final String formattedCo2 = sensorData.getCo2()
                .map(co2 -> String.valueOf(co2.getPpm()))
                .orElse("");
        final String csvLine = String.format("%s,%s,%s,%s", formattedTime, formattedTemperature, formattedHumidity, formattedCo2);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(csvLine);
            writer.newLine();
        } catch (IOException e) {
            logger.error("CSV could not be written! {}", e.getMessage());
        }
    }
}
