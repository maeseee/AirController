package org.airController;

import org.airController.controllers.CurrentSensorValues;
import org.airController.controllers.FreshAirController;
import org.airController.controllers.Rule;
import org.airController.gpio.GpioPinImpl;
import org.airController.gpioAdapter.GpioFunction;
import org.airController.gpioAdapter.GpioPin;
import org.airController.persistence.SensorDataPersistenceObserver;
import org.airController.rules.*;
import org.airController.sensor.openWeatherApi.OutdoorSensorImpl;
import org.airController.sensor.qingPing.QingPingSensor;
import org.airController.sensorAdapter.IndoorSensor;
import org.airController.sensorAdapter.OutdoorSensor;
import org.airController.system.ControlledVentilationSystemImpl;
import org.airController.system.ControlledVentilationSystemTimeKeeper;
import org.airController.systemAdapter.ControlledVentilationSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
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
    private final FreshAirController freshAirController;
    private final ScheduledExecutorService executor;

    public Application() throws IOException, URISyntaxException {
        this(new GpioPinImpl(GpioFunction.AIR_FLOW, true), new GpioPinImpl(GpioFunction.HUMIDITY_EXCHANGER, false), new OutdoorSensorImpl(),
                new QingPingSensor(), new SensorDataPersistenceObserver(), Executors.newScheduledThreadPool(1));
    }

    Application(GpioPin airFlow, GpioPin humidityExchanger, OutdoorSensor outdoorSensor, IndoorSensor indoorSensor,
                SensorDataPersistenceObserver persistenceObserver, ScheduledExecutorService executor) {
        final ControlledVentilationSystem ventilationSystem = new ControlledVentilationSystemImpl(airFlow, humidityExchanger);
        this.outdoorSensor = outdoorSensor;
        this.indoorSensor = indoorSensor;
        final CurrentSensorValues sensorValues = new CurrentSensorValues();
        outdoorSensor.addObserver(sensorValues);
        outdoorSensor.addObserver(persistenceObserver);
        indoorSensor.addObserver(sensorValues);
        indoorSensor.addObserver(persistenceObserver);
        this.freshAirController = createFreshAirController(ventilationSystem, sensorValues);
        this.executor = executor;
    }

    public void run() {
        executor.scheduleAtFixedRate(outdoorSensor, 0, OUTDOOR_SENSOR_READ_PERIOD_MINUTES, TimeUnit.MINUTES);
        executor.scheduleAtFixedRate(indoorSensor, 0, INDOOR_SENSOR_READ_PERIOD_MINUTES, TimeUnit.MINUTES);
        executor.scheduleAtFixedRate(freshAirController, 0, VENTILATION_SYSTEM_PERIOD_MINUTES, TimeUnit.MINUTES);

        logger.info("All setup and running...");
    }

    private FreshAirController createFreshAirController(ControlledVentilationSystem ventilationSystem, CurrentSensorValues sensorValues) {
        List<ControlledVentilationSystem> ventilationSystems = List.of(ventilationSystem, new ControlledVentilationSystemTimeKeeper());
        ControlledVentilationSystemTimeKeeper timeKeeper = new ControlledVentilationSystemTimeKeeper();
        CO2ControlAirFlow co2ControlAirFlow = new CO2ControlAirFlow(sensorValues);
        DailyAirFlow dailyAirFlow = new DailyAirFlow();
        HumidityControlAirFlow humidityControlAirFlow = new HumidityControlAirFlow(sensorValues);
        PeriodicallyAirFlow periodicallyAirFlow = new PeriodicallyAirFlow(timeKeeper);
        HumidityControlExchanger humidityControlExchanger = new HumidityControlExchanger(humidityControlAirFlow);

        List<Rule> freshAirRules = List.of(co2ControlAirFlow, dailyAirFlow, humidityControlAirFlow, periodicallyAirFlow);
        List<Rule> exchangeHumidityRules = List.of(humidityControlExchanger);
        return new FreshAirController(ventilationSystems, freshAirRules, exchangeHumidityRules);
    }
}