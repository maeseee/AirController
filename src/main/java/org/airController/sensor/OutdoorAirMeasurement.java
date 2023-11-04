package org.airController.sensor;

import org.airController.entities.AirVO;
import org.airController.sensorAdapter.OutdoorAirMeasurementObserver;
import org.airController.sensorAdapter.OutdoorAirValues;
import org.airController.util.JsonParser;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class OutdoorAirMeasurement implements Runnable {

    private static final String LAT = "47.127459";
    private static final String LON = "8.245553";

    private final List<OutdoorAirMeasurementObserver> observers = new ArrayList<>();
    private final HttpsRequest httpsRequest;

    public OutdoorAirMeasurement(String apiKey) throws URISyntaxException {
        this(createHttpRequest(apiKey));
    }

    OutdoorAirMeasurement(HttpsRequest httpsRequest) {
        this.httpsRequest = httpsRequest;
    }

    @Override
    public void run() {
        final Optional<String> request = httpsRequest.sendRequest();
        if (request.isEmpty()) {
            return;
        }

        final AirVO airValues = JsonParser.parse(request.get());
        notifyObservers(new OutdoorAirValues(airValues));
    }

    public void addObserver(OutdoorAirMeasurementObserver observer) {
        observers.add(observer);
    }

    private static HttpsRequest createHttpRequest(String decryptedApiKey) throws URISyntaxException {
        final String urlString = "https://api.openweathermap.org/data/2.5/weather?lat=" + LAT + "&lon=" + LON + "&appid=" + decryptedApiKey;
        final URI uri = new URI(urlString);
        return new HttpsRequest(uri);
    }

    private void notifyObservers(OutdoorAirValues outdoorAirValues) {
        observers.forEach(observer -> observer.updateAirMeasurement(outdoorAirValues));
    }
}
