package org.airController;

import org.airController.controllers.AirController;
import org.airController.gpio.GpioPinImpl;
import org.airController.gpioAdapter.GpioFunction;
import org.airController.gpioAdapter.GpioPin;
import org.airController.sensor.IndoorSensorImpl;
import org.airController.sensor.OutdoorSensorImpl;
import org.airController.sensorAdapter.IndoorSensor;
import org.airController.sensorAdapter.OutdoorSensor;
import org.airController.system.ControlledVentilationSystemImpl;
import org.airController.systemAdapter.ControlledVentilationSystem;
import org.airController.util.Logging;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Application {
    private static final int OUTDOOR_SENSOR_READ_PERIOD_MINUTES = 10;
    private static final int INDOOR_SENSOR_READ_PERIOD_MINUTES = 3;

    private final OutdoorSensor outdoorSensor;
    private final IndoorSensor indoorSensor;

    public Application() throws IOException, URISyntaxException {
        this(new GpioPinImpl(GpioFunction.MAIN_SYSTEM, true), new GpioPinImpl(GpioFunction.HUMIDITY_EXCHANGER, false), new IndoorSensorImpl());
    }

    Application(GpioPin airFlow, GpioPin humidityExchanger, IndoorSensor indoorSensor) throws URISyntaxException {
        final ControlledVentilationSystem ventilationSystem = new ControlledVentilationSystemImpl(airFlow, humidityExchanger);
        this.outdoorSensor = new OutdoorSensorImpl();
        this.indoorSensor = indoorSensor;

        final AirController airController = new AirController(ventilationSystem);
        outdoorSensor.addObserver(airController);
        indoorSensor.addObserver(airController);
    }

    public void run() {
        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(outdoorSensor, 0, OUTDOOR_SENSOR_READ_PERIOD_MINUTES, TimeUnit.MINUTES);
        executor.scheduleAtFixedRate(indoorSensor, 0, INDOOR_SENSOR_READ_PERIOD_MINUTES, TimeUnit.MINUTES);

        Logging.getLogger().info("All setup and running...");
    }
}