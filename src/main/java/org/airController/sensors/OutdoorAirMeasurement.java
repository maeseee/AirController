package org.airController.sensors;

import org.airController.entities.AirVO;
import org.airController.sensorAdapter.OutdoorAirMeasurementObserver;
import org.airController.sensorAdapter.OutdoorAirValues;
import org.airController.util.HttpsRequest;
import org.airController.util.JsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class OutdoorAirMeasurement implements Runnable{

    private final HttpsRequest httpsRequest;

    public OutdoorAirMeasurement(HttpsRequest httpsRequest) {
        this.httpsRequest = httpsRequest;
    }

    @Override
    public void run() {
        measureValue();
    }

    public void measureValue() {
        final Optional<String> request = httpsRequest.sendRequest();
        if (request.isEmpty()) {
            return;
        }

        final AirVO airValues = JsonParser.parse(request.get());
        notifyObservers(new OutdoorAirValues(airValues));
    }

    private final List<OutdoorAirMeasurementObserver> observers = new ArrayList<>();

    public void addObserver(OutdoorAirMeasurementObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(OutdoorAirValues outdoorAirValues) {
        observers.forEach(observer -> observer.updateAirMeasurement(outdoorAirValues));
    }
}
