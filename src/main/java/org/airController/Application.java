package org.airController;

import org.airController.controllers.AirController;
import org.airController.gpio.GpioPinImpl;
import org.airController.gpioAdapter.GpioFunction;
import org.airController.gpioAdapter.GpioPin;
import org.airController.sensor.IndoorAirMeasurement;
import org.airController.sensor.OutdoorAirMeasurement;
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
    private static final int OUTDOOR_SENSOR_READ_PERIOD_MINUTES = 10;
    private static final int INDOOR_SENSOR_READ_PERIOD_MINUTES = 3;

    private final ControlledVentilationSystem ventilationSystem;
    private final OutdoorAirMeasurement outdoorAirMeasurement;
    private final IndoorAirMeasurement indoorAirMeasurement;

    public Application() throws IOException, URISyntaxException {
        this(new GpioPinImpl(GpioFunction.MAIN_SYSTEM), new GpioPinImpl(GpioFunction.HUMIDITY_EXCHANGER), new IndoorAirMeasurement());
    }

    Application(GpioPin airFlow, GpioPin humidityExchanger, IndoorAirMeasurement indoorAirMeasurement) throws IOException, URISyntaxException {
        this.ventilationSystem = new ControlledVentilationSystemImpl(airFlow, humidityExchanger);
        this.outdoorAirMeasurement = createOutdoorMeasurement();
        this.indoorAirMeasurement = indoorAirMeasurement;

        final AirController airController = new AirController(ventilationSystem);
        outdoorAirMeasurement.addObserver(airController);
        indoorAirMeasurement.addObserver(airController);
    }

    public void run() {
        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(outdoorAirMeasurement, 0, OUTDOOR_SENSOR_READ_PERIOD_MINUTES, TimeUnit.MINUTES);
        executor.scheduleAtFixedRate(indoorAirMeasurement, 0, INDOOR_SENSOR_READ_PERIOD_MINUTES, TimeUnit.MINUTES);

        logger.info("All setup and running...");
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

    private OutdoorAirMeasurement createOutdoorMeasurement() throws IOException, URISyntaxException {
        final String decryptedApiKey = getApiKeyForHttpRequest();
        return new OutdoorAirMeasurement(decryptedApiKey);
    }
}