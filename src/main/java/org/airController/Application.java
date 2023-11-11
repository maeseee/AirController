package org.airController;

import org.airController.controllers.AirController;
import org.airController.gpio.GpioPinImpl;
import org.airController.gpioAdapter.GpioFunction;
import org.airController.gpioAdapter.GpioPin;
import org.airController.sensor.IndoorAirMeasurement;
import org.airController.sensor.OutdoorAirMeasurement;
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

    private final OutdoorAirMeasurement outdoorAirMeasurement;
    private final IndoorAirMeasurement indoorAirMeasurement;

    public Application() throws IOException, URISyntaxException {
        this(new GpioPinImpl(GpioFunction.MAIN_SYSTEM), new GpioPinImpl(GpioFunction.HUMIDITY_EXCHANGER), new IndoorAirMeasurement());
    }

    Application(GpioPin airFlow, GpioPin humidityExchanger, IndoorAirMeasurement indoorAirMeasurement) throws URISyntaxException {
        final ControlledVentilationSystem ventilationSystem = new ControlledVentilationSystemImpl(airFlow, humidityExchanger);
        this.outdoorAirMeasurement = new OutdoorAirMeasurement();
        this.indoorAirMeasurement = indoorAirMeasurement;

        final AirController airController = new AirController(ventilationSystem);
        outdoorAirMeasurement.addObserver(airController);
        indoorAirMeasurement.addObserver(airController);
    }

    public void run() {
        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(outdoorAirMeasurement, 0, OUTDOOR_SENSOR_READ_PERIOD_MINUTES, TimeUnit.MINUTES);
        executor.scheduleAtFixedRate(indoorAirMeasurement, 0, INDOOR_SENSOR_READ_PERIOD_MINUTES, TimeUnit.MINUTES);

        Logging.getLogger().info("All setup and running...");
    }
}