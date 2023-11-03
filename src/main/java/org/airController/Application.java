package org.airController;

import org.airController.controllers.AirController;
import org.airController.gpio.GpioPinImpl;
import org.airController.gpioAdapter.GpioFunction;
import org.airController.gpioAdapter.GpioPin;
import org.airController.sensors.OutdoorAirMeasurement;
import org.airController.system.ControlledVentilationSystemImpl;
import org.airController.systemAdapter.ControlledVentilationSystem;
import org.airController.util.SecretsEncryption;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Application {
    private static final Logger logger = Logger.getLogger(Application.class.getName());
    private static final String ENCRYPTED_API_KEY = "JWHqsiARWGfnwhAp/qvt7aWlmhsyXvOtnsYN32HH5J2m2/QGb/OnuhnGzooxh1onTK+ynB9f038EMbUnOZMjNw==";

    private final ControlledVentilationSystem ventilationSystem;

    private OutdoorAirMeasurement outdoorAirMeasurement;

    public Application() {
        this(new GpioPinImpl(GpioFunction.MAIN_SYSTEM), new GpioPinImpl(GpioFunction.HUMIDITY_EXCHANGER));
    }

    Application(GpioPin airFlow, GpioPin humidityExchanger) {
        this.ventilationSystem = new ControlledVentilationSystemImpl(airFlow, humidityExchanger);
    }

    public void init() throws IOException, URISyntaxException {
        final AirController airController = new AirController(ventilationSystem);
        initHttpsRequest();
        outdoorAirMeasurement.addObserver(airController);
    }

    public void run() {
        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(outdoorAirMeasurement, 0, 10, TimeUnit.MINUTES);

        logger.info("All setup and running...");
    }

    private void initHttpsRequest() throws IOException, URISyntaxException {
        final String decryptedApiKey = getApiKeyForHttpRequest();
        outdoorAirMeasurement = new OutdoorAirMeasurement(decryptedApiKey);
    }

    private String getApiKeyForHttpRequest() throws IOException {
        System.out.println("Enter the master password:");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        final String masterPassword = reader.readLine();
        final SecretsEncryption secretsEncryption = new SecretsEncryption(masterPassword);
        final String decryptedApiKey = secretsEncryption.decrypt(ENCRYPTED_API_KEY);
        if (decryptedApiKey == null) {
            System.err.println("Wrong master password entered!");
            return null;
        }
        System.out.println("API_KEY is " + decryptedApiKey);
        return decryptedApiKey;
    }
}