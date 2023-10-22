package org.application;

import org.controllers.AirController;
import org.outputsystem.ControlledVentilationSystem;
import org.sensors.OutdoorAirMeasurement;
import org.util.HttpsRequest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final String LAT = "47.127459";
    private static final String LON = "8.245553";
    private static final String API_KEY = ""; // TODO encrypt key
    private static final String urlString = "https://api.openweathermap.org/data/2.5/weather?lat=" + LAT + "&lon=" + LON + "&appid=" + API_KEY;

    public static void main(String[] args) throws URISyntaxException, InterruptedException {
        final ControlledVentilationSystem ventilationSystem = new ControlledVentilationSystem();

        final AirController airController = new AirController(ventilationSystem);

        final HttpsRequest httpsRequest = createHttpRequest();
        final OutdoorAirMeasurement outdoorAirMeasurement = new OutdoorAirMeasurement(httpsRequest);
        outdoorAirMeasurement.addObserver(airController);

        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(outdoorAirMeasurement, 0, 10, TimeUnit.MINUTES);

        Thread.currentThread().join();
    }

    private static HttpsRequest createHttpRequest() throws URISyntaxException {
        final URI uri = new URI(urlString);
        return new HttpsRequest(uri);
    }
}