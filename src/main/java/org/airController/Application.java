package org.airController;

import org.airController.controllers.AirController;
import org.airController.controllers.SensorValues;
import org.airController.gpio.GpioPinImpl;
import org.airController.gpioAdapter.GpioFunction;
import org.airController.gpioAdapter.GpioPin;
import org.airController.persistence.SensorValuePersistenceObserver;
import org.airController.sensor.OutdoorSensorImpl;
import org.airController.sensor.QingPingSensor;
import org.airController.sensorAdapter.IndoorSensor;
import org.airController.sensorAdapter.OutdoorSensor;
import org.airController.system.ControlledVentilationSystemImpl;
import org.airController.systemAdapter.ControlledVentilationSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Application {
    private static final Logger logger = LogManager.getLogger(Application.class);
    private static final int OUTDOOR_SENSOR_READ_PERIOD_MINUTES = 10;
    private static final int INDOOR_SENSOR_READ_PERIOD_MINUTES = 10;
    private static final int VENTILATION_SYSTEM_PERIOD_MINUTES = 1;

    private final OutdoorSensor outdoorSensor;
    private final IndoorSensor indoorSensor;
    private final AirController airController;
    private final ScheduledExecutorService executor;

    public Application() throws IOException, URISyntaxException {
        this(new GpioPinImpl(GpioFunction.AIR_FLOW, true), new GpioPinImpl(GpioFunction.HUMIDITY_EXCHANGER, false), new OutdoorSensorImpl(),
                new QingPingSensor(), new SensorValuePersistenceObserver(), Executors.newScheduledThreadPool(1));
    }

    Application(GpioPin airFlow, GpioPin humidityExchanger, OutdoorSensor outdoorSensor, IndoorSensor indoorSensor,
                SensorValuePersistenceObserver persistenceObserver, ScheduledExecutorService executor) {
        final ControlledVentilationSystem ventilationSystem = new ControlledVentilationSystemImpl(airFlow, humidityExchanger);
        this.outdoorSensor = outdoorSensor;
        this.indoorSensor = indoorSensor;
        final SensorValues sensorValues = new SensorValues();
        outdoorSensor.addObserver(sensorValues);
        outdoorSensor.addObserver(persistenceObserver);
        indoorSensor.addObserver(sensorValues);
        indoorSensor.addObserver(persistenceObserver);
        this.airController = new AirController(ventilationSystem, sensorValues);
        this.executor = executor;
    }

    public void run() {
        executor.scheduleAtFixedRate(outdoorSensor, 0, OUTDOOR_SENSOR_READ_PERIOD_MINUTES, TimeUnit.MINUTES);
        executor.scheduleAtFixedRate(indoorSensor, 0, INDOOR_SENSOR_READ_PERIOD_MINUTES, TimeUnit.MINUTES);
        executor.scheduleAtFixedRate(airController, 0, VENTILATION_SYSTEM_PERIOD_MINUTES, TimeUnit.MINUTES);

        logger.info("All setup and running...");
    }
}