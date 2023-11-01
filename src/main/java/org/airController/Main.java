package org.airController;

import org.airController.controllers.AirController;
import org.airController.gpio.GpioFunction;
import org.airController.gpio.GpioPin;
import org.airController.sensors.OutdoorAirMeasurement;
import org.airController.system.ControlledVentilationSystemImpl;
import org.airController.systemAdapter.ControlledVentilationSystem;
import org.airController.util.HttpsRequest;
import org.airController.util.SecretsEncryption;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final String LAT = "47.127459";
    private static final String LON = "8.245553";
    private static final String ENCRYPTED_API_KEY = "JWHqsiARWGfnwhAp/qvt7aWlmhsyXvOtnsYN32HH5J2m2/QGb/OnuhnGzooxh1onTK+ynB9f038EMbUnOZMjNw==";

    public static void main(String[] args) throws URISyntaxException, InterruptedException, IOException {
        System.out.println("Enter the master password:");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        final String masterPassword = reader.readLine();
        final SecretsEncryption secretsEncryption = new SecretsEncryption(masterPassword);
        final String decryptedApiKey = secretsEncryption.decrypt(ENCRYPTED_API_KEY);
        if (decryptedApiKey == null) {
            System.err.println("Wrong master password entered!");
            return;
        }
        System.out.println("API_KEY is " + decryptedApiKey);

        final GpioPin airFlow = new GpioPin(GpioFunction.MAIN_SYSTEM);
        final GpioPin humidityExchanger = new GpioPin(GpioFunction.HUMIDITY_EXCHANGER);
        final ControlledVentilationSystem ventilationSystem = new ControlledVentilationSystemImpl(airFlow, humidityExchanger);

        final AirController airController = new AirController(ventilationSystem);

        final HttpsRequest httpsRequest = createHttpRequest(decryptedApiKey);
        final OutdoorAirMeasurement outdoorAirMeasurement = new OutdoorAirMeasurement(httpsRequest);
        outdoorAirMeasurement.addObserver(airController);

        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(outdoorAirMeasurement, 0, 10, TimeUnit.MINUTES);

        Thread.currentThread().join();
    }

    private static HttpsRequest createHttpRequest(String decryptedApiKey) throws URISyntaxException {
        final String urlString = "https://api.openweathermap.org/data/2.5/weather?lat=" + LAT + "&lon=" + LON + "&appid=" + decryptedApiKey;
        final URI uri = new URI(urlString);
        return new HttpsRequest(uri);
    }
}