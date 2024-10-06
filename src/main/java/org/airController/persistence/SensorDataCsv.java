package org.airController.persistence;

import org.airController.sensorValues.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

class SensorDataCsv implements SensorDataPersistence {
    private static final Logger logger = LogManager.getLogger(SensorDataCsv.class);

    private final String filePath;

    public SensorDataCsv(String filePath) {
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

    @Override public List<SensorData> read() {
        final List<SensorData> entries = new ArrayList<>();
        try {
            final BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                try {
                    entries.add(createSensorData(currentLine));
                } catch (InvalidArgumentException e) {
                    logger.error(e.getMessage());
                }
            }
        } catch (IOException e) {
            logger.error("CSV could not be read! {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return entries;
    }

    private static SensorData createSensorData(String csvLine) throws InvalidArgumentException {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        final String[] csv = csvLine.split(",");
        final LocalDateTime timestamp = LocalDateTime.parse(csv[0], formatter);
        final double tempCelsius = Double.parseDouble(csv[1]);
        final Temperature temperature = Temperature.createFromCelsius(tempCelsius);
        final double humRelative = Double.parseDouble(csv[2]);
        final Humidity humidity = Humidity.createFromRelative(humRelative, temperature);
        final double co2Ppm = Double.parseDouble(csv[3]);
        final CarbonDioxide co2 = CarbonDioxide.createFromPpm(co2Ppm);
        return new SensorDataImpl(temperature, humidity, co2, timestamp);
    }
}
