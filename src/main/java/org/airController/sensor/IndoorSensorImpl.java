package org.airController.sensor;

import org.airController.entities.AirValue;
import org.airController.sensorAdapter.IndoorSensor;
import org.airController.sensorAdapter.IndoorSensorObserver;
import org.airController.util.Logging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IndoorSensorImpl implements IndoorSensor {
    private final List<IndoorSensorObserver> observers = new ArrayList<>();
    private final Dht22 dht22;

    public IndoorSensorImpl() throws IOException {
        this.dht22 = new Dht22Impl();
    }

    public IndoorSensorImpl(Dht22 dht22) {
        this.dht22 = dht22;
    }

    @Override
    public void run() {
        final Optional<AirValue> indoorAirValue = dht22.refreshData();
        indoorAirValue.ifPresentOrElse(
                this::notifyObservers,
                () -> Logging.getLogger().severe("Indoor sensor out of order"));
    }

    @Override
    public void addObserver(IndoorSensorObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(AirValue indoorAirValue) {
        Logging.getLogger().info("New indoor sensor value: " + indoorAirValue);
        observers.forEach(observer -> observer.updateIndoorAirValue(indoorAirValue));
        observers.forEach(IndoorSensorObserver::runOneLoop);
    }
}
