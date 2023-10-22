package org.sensors;

import org.entities.AirValues;
import org.util.HttpsRequest;
import org.util.JsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class OutdoorAirMeasurement {

    private final HttpsRequest httpsRequest;

    public OutdoorAirMeasurement(HttpsRequest httpsRequest) {
        this.httpsRequest = httpsRequest;
    }

    public OutdoorAirValues measureValues() {
        final Optional<String> request = httpsRequest.sendRequest();
        if (request.isEmpty()) {
            return new OutdoorAirValues(null);
        }

        final AirValues airValues = JsonParser.parse(request.get());
        return new OutdoorAirValues(airValues);
    }

    private final List<IndoorAirMeasurementObserver> observers = new ArrayList<>();

    public void addObserver(IndoorAirMeasurementObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(IndoorAirValues indoorAirValues) {
        observers.forEach(observer -> observer.updateAirMeasurement(indoorAirValues));
    }
}
