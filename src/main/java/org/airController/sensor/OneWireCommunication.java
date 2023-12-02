package org.airController.sensor;

import org.airController.gpioAdapter.GpioFunction;
import org.airController.util.Logging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;

class OneWireCommunication {

    public OneWireCommunication(GpioFunction gpioFunction) {
    }

    public Optional<String> readSensorData() {
        final ProcessBuilder processBuilder = new ProcessBuilder("python3", "src/main/java/org/airController/sensor/DhtDevice.py");
        processBuilder.redirectErrorStream(true);

        try {
            final Process process = processBuilder.start();
            final String output = readProcessOutput(process.getInputStream());
            System.out.println(output);

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return Optional.of(output);
            }
        } catch (IOException | InterruptedException e) {
            Logging.getLogger().severe("Invalid indoor sensor data!");
        }

        return Optional.empty();
    }

    private String readProcessOutput(InputStream inputStream) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
            output.append(line).append("\n");
        }
        return output.toString();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final OneWireCommunication oneWireCommunication = new OneWireCommunication(GpioFunction.DHT22_SENSOR);
        final Optional<String> sensorData = oneWireCommunication.readSensorData();
        System.out.println(sensorData);
    }
}
