package org.airController;

import org.airController.controllers.AirController;
import org.airController.gpio.GpioPinImpl;
import org.airController.gpioAdapter.GpioFunction;
import org.airController.gpioAdapter.GpioPin;
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
import java.net.http.HttpTimeoutException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Application {
    private static final Logger logger = Logger.getLogger(Application.class.getName());
    private static final String LAT = "47.127459";
    private static final String LON = "8.245553";
    private static final String ENCRYPTED_API_KEY = "JWHqsiARWGfnwhAp/qvt7aWlmhsyXvOtnsYN32HH5J2m2/QGb/OnuhnGzooxh1onTK+ynB9f038EMbUnOZMjNw==";

    private final ControlledVentilationSystem ventilationSystem;

    private OutdoorAirMeasurement outdoorAirMeasurement;

    public Application() {
        this(new GpioPinImpl(GpioFunction.MAIN_SYSTEM), new GpioPinImpl(GpioFunction.HUMIDITY_EXCHANGER));
    }

    Application(GpioPin airFlow, GpioPin humidityExchanger) {
        this.ventilationSystem = new ControlledVentilationSystemImpl(airFlow, humidityExchanger);
    }

    public void init() throws HttpTimeoutException {
        final AirController airController = new AirController(ventilationSystem);
        initHttpsRequest();
        if (outdoorAirMeasurement == null) {
            throw new HttpTimeoutException("Http Request could not be setup!");
        }
        outdoorAirMeasurement.addObserver(airController);
    }

    public void run() {
        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(outdoorAirMeasurement, 0, 10, TimeUnit.MINUTES);

        logger.info("All setup and running...");
    }

    private void initHttpsRequest() {
        final String decryptedApiKey = getApiKeyForHttpRequest();
        if (decryptedApiKey == null) {
            return;
        }
        final HttpsRequest httpsRequest = createHttpRequest(decryptedApiKey);
        if (httpsRequest == null) {
            return;
        }
        outdoorAirMeasurement = new OutdoorAirMeasurement(httpsRequest);
    }

    private String getApiKeyForHttpRequest() {
        System.out.println("Enter the master password:");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        final String masterPassword;
        try {
            masterPassword = reader.readLine();
        } catch (IOException e) {
            return null;
        }
        final SecretsEncryption secretsEncryption = new SecretsEncryption(masterPassword);
        final String decryptedApiKey = secretsEncryption.decrypt(ENCRYPTED_API_KEY);
        if (decryptedApiKey == null) {
            System.err.println("Wrong master password entered!");
            return null;
        }
        System.out.println("API_KEY is " + decryptedApiKey);
        return decryptedApiKey;
    }

    private HttpsRequest createHttpRequest(String decryptedApiKey) {
        final String urlString = "https://api.openweathermap.org/data/2.5/weather?lat=" + LAT + "&lon=" + LON + "&appid=" + decryptedApiKey;
        final URI uri;
        try {
            uri = new URI(urlString);
        } catch (URISyntaxException e) {
            return null;
        }
        return new HttpsRequest(uri);
    }
}