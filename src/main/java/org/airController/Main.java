package org.airController;

import org.airController.controllers.AirController;
import org.airController.gpio.GpioFunction;
import org.airController.gpio.GpioPin;
import org.airController.sensors.OutdoorAirMeasurement;
import org.airController.system.ControlledVentilationSystemImpl;
import org.airController.systemAdapter.ControlledVentilationSystem;
import org.airController.util.HttpsRequest;
import org.airController.util.SecretsEncryption;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;

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
    private static final String ENCRYPTED_API_KEY = "6DiM9NdAMx8V9M6/CSBC7wzUDcs2nJqmZsG5DzM6jya+FJIvxPrOWrpfuJe2Mph3";

    public static void main(String[] args) throws URISyntaxException, InterruptedException, IOException {
        System.out.println("Enter the master password:");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        final String masterPassword = reader.readLine();
        String decryptedApiKey;
        try {
            final SecretsEncryption secretsEncryption = new SecretsEncryption(masterPassword);
            decryptedApiKey = secretsEncryption.decrypt(ENCRYPTED_API_KEY);
        } catch (EncryptionOperationNotPossibleException exception) {
            System.out.println("Wrong master password entered!");
            return;
        }

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