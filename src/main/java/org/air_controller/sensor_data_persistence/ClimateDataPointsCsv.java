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

public class ClimateDataPointsCsv implements ClimateDataPointPersistence {
    private static final Logger logger = LogManager.getLogger(ClimateDataPointsCsv.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String filePath;

    public ClimateDataPointsCsv(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void persist(ClimateDataPoint dataPoint) {
        final String formattedTime = dataPoint.timestamp().format(FORMATTER);
        final String formattedTemperature = String.format("%.2f", dataPoint.temperature().celsius());
        final String formattedHumidity = String.format("%.2f", dataPoint.humidity().getRelativeHumidity(dataPoint.temperature()));
        final String formattedCo2 = dataPoint.co2()
                .map(co2 -> String.format("%.0f", co2.ppm()))
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
    public List<ClimateDataPoint> read() {
        try {
            final File file = new File(filePath);
            final boolean newlyCreated = file.createNewFile();
            if (newlyCreated) {
                logger.info("File {} has been created", filePath);
            }
            return readCsvFile(file);
        } catch (IOException e) {
            throw new ParseException("Could not create the CSV file! " + e.getMessage(), e.getCause());
        }
    }

    @Override
    public Optional<ClimateDataPoint> getMostCurrentClimateDataPoint(ZonedDateTime lastValidTimestamp) {
        final List<ClimateDataPoint> data = read();
        return data.isEmpty() ? Optional.empty() : Optional.of(data.getLast());
    }

    private ClimateDataPoint createClimateDataPoint(String csvLine) throws InvalidArgumentException {
        final String[] csv = csvLine.split(",");
        assert (csv.length > 2);
        return new SensorDataBuilder()
                .setTime(ZonedDateTime.of(LocalDateTime.parse(csv[0], FORMATTER), ZoneOffset.UTC))
                .setTemperatureCelsius(Double.parseDouble(csv[1]))
                .setHumidityRelative(Double.parseDouble(csv[2]))
                .setCo2(csv.length > 3 ? CarbonDioxide.createFromPpm(Double.parseDouble(csv[3])) : null)
                .build();
    }

    private void addDataIfAvailable(List<ClimateDataPoint> entries, String currentLine) {
        try {
            final ClimateDataPoint climateDataPoint = createClimateDataPoint(currentLine);
            entries.add(climateDataPoint);
        } catch (InvalidArgumentException e) {
            logger.error(e.getMessage());
        }
    }

    private List<ClimateDataPoint> readCsvFile(File file) throws IOException {
        final List<ClimateDataPoint> entries = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                addDataIfAvailable(entries, currentLine);
            }
        } catch (IOException e) {
            throw new ParseException("CSV could not be read! " + e.getMessage(), e.getCause());
        }
        return entries;
    }
}
