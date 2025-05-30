package org.air_controller.sensor_data_persistence;

import org.air_controller.sensor_values.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SensorDataCsv implements SensorDataPersistence {
    private static final Logger logger = LogManager.getLogger(SensorDataCsv.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String filePath;

    public SensorDataCsv(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void persist(SensorData sensorData) {
        final String formattedTime = sensorData.getTimeStamp().format(FORMATTER);
        final String formattedTemperature = sensorData.getTemperature()
                .map(temperature -> String.format("%.2f", temperature.getCelsius()))
                .orElse("");
        final String formattedHumidity = sensorData.getHumidity()
                .map(humidity -> String.format("%.2f", humidity.getRelativeHumidity(sensorData.getTemperature().get())))
                .orElse("");
        final String formattedCo2 = sensorData.getCo2()
                .map(co2 -> String.format("%.0f", co2.getPpm()))
                .orElse("");
        final String csvLine = String.format("%s,%s,%s,%s", formattedTime, formattedTemperature, formattedHumidity, formattedCo2);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(csvLine);
            writer.newLine();
        } catch (IOException e) {
            logger.error("CSV could not be written! {}", e.getMessage());
        }
    }

    @Override
    public List<SensorData> read() {
        final List<SensorData> entries = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                addDataIfAvailable(entries, currentLine);
            }
        } catch (IOException e) {
            throw new ParseException("CSV could not be read! " + e.getMessage(), e.getCause());
        }
        return entries;
    }

    @Override
    public Optional<SensorData> getMostCurrentSensorData(ZonedDateTime lastValidTimestamp) {
        final List<SensorData> data = read();
        return data.isEmpty() ? Optional.empty() : Optional.of(data.getLast());
    }

    private SensorData createSensorData(String csvLine) throws InvalidArgumentException {
        final String[] csv = csvLine.split(",");
        assert (csv.length > 2);
        final ZonedDateTime timestamp = ZonedDateTime.of(LocalDateTime.parse(csv[0], FORMATTER), ZoneOffset.UTC);
        final double tempCelsius = Double.parseDouble(csv[1]);
        final Temperature temperature = Temperature.createFromCelsius(tempCelsius);
        final double humRelative = Double.parseDouble(csv[2]);
        final Humidity humidity = Humidity.createFromRelative(humRelative, temperature);
        final CarbonDioxide co2 = csv.length > 3 ? CarbonDioxide.createFromPpm(Double.parseDouble(csv[3])) : null;
        return new SensorDataImpl(temperature, humidity, co2, timestamp);
    }

    private void addDataIfAvailable(List<SensorData> entries, String currentLine) {
        try {
            final SensorData sensorData = getSensorData(currentLine);
            entries.add(sensorData);
        } catch (InvalidArgumentException e) {
            logger.error(e.getMessage());
        }
    }

    private SensorData getSensorData(String currentLine) throws InvalidArgumentException {
        return createSensorData(currentLine);
    }
}
