package org.airController.sensor;

import org.airController.entities.AirVO;

import java.util.Optional;

public class Dht22Mock implements Dht22 {

    private AirVO data;

    @Override
    public Optional<AirVO> refreshData() {
        return Optional.ofNullable(data);
    }

    public void setData(AirVO data) {
        this.data = data;
    }
}
