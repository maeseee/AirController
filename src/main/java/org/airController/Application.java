package org.airController;

import com.google.inject.internal.Nullable;
import org.airController.gpio.GpioFunction;
import org.airController.gpio.GpioPin;
import org.airController.gpio.RaspberryGpioPin;
import org.airController.persistence.SensorDataPersistenceObserver;
import org.airController.rules.*;
import org.airController.sensor.IndoorSensor;
import org.airController.sensor.OutdoorSensor;
import org.airController.sensor.dht22.OneWireSensor;
import org.airController.sensor.openWeatherApi.OpenWeatherApiSensor;
import org.airController.sensor.qingPing.QingPingSensor;
import org.airController.sensorValues.CurrentSensorValues;
import org.airController.system.ControlledVentilationSystem;
import org.airController.system.VentilationSystem;
import org.airController.system.VentilationSystemTimeKeeper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Application {
    private static final Logger logger = LogManager.getLogger(Application.class);
    private static final int OUTDOOR_SENSOR_READ_PERIOD_MINUTES = 10;
    private static final int INDOOR_SENSOR_READ_PERIOD_MINUTES = 10;
    private static final int RULE_APPLIER_PERIOD_MINUTES = 1;

    private final OutdoorSensor outdoorSensor;
    private final IndoorSensor indoorSensor;
    private final RuleApplier ruleApplier;
    private final TimeKeeper timeKeeper;
    private final ScheduledExecutorService executor;

    public Application() throws IOException, URISyntaxException {
        this(new RaspberryGpioPin(GpioFunction.AIR_FLOW, true), new RaspberryGpioPin(GpioFunction.HUMIDITY_EXCHANGER, false),
                new OpenWeatherApiSensor(),
                new QingPingSensor(), new OneWireSensor(), new SensorDataPersistenceObserver(), Executors.newScheduledThreadPool(1));
    }

    // Used for MainMock
    Application(GpioPin airFlow, GpioPin humidityExchanger, OutdoorSensor outdoorSensor, QingPingSensor indoorSensor,
            @Nullable IndoorSensor backupSensor, SensorDataPersistenceObserver persistenceObserver, ScheduledExecutorService executor) {
        this(new ControlledVentilationSystem(airFlow, humidityExchanger), outdoorSensor, indoorSensor,
                createSensorValues(outdoorSensor, indoorSensor, backupSensor, persistenceObserver), new VentilationSystemTimeKeeper(), executor);
    }

    private Application(VentilationSystem ventilationSystem, OutdoorSensor outdoorSensor, QingPingSensor indoorSensor,
            CurrentSensorValues sensorValues, VentilationSystemTimeKeeper timeKeeper, ScheduledExecutorService executor) {
        this(outdoorSensor, indoorSensor, createFreshAirController(ventilationSystem, sensorValues, timeKeeper), timeKeeper, executor);
    }

    // Used for tests
    Application(OutdoorSensor outdoorSensor, IndoorSensor indoorSensor, RuleApplier ruleApplier, TimeKeeper timeKeeper,
            ScheduledExecutorService executor) {
        this.outdoorSensor = outdoorSensor;
        this.indoorSensor = indoorSensor;
        this.ruleApplier = ruleApplier;
        this.timeKeeper = timeKeeper;
        this.executor = executor;
    }

    public void run() {
        executor.scheduleAtFixedRate(outdoorSensor, 0, OUTDOOR_SENSOR_READ_PERIOD_MINUTES, TimeUnit.MINUTES);
        executor.scheduleAtFixedRate(indoorSensor, 0, INDOOR_SENSOR_READ_PERIOD_MINUTES, TimeUnit.MINUTES);
        executor.scheduleAtFixedRate(ruleApplier, 0, RULE_APPLIER_PERIOD_MINUTES, TimeUnit.MINUTES);

        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime midnight = now.toLocalDate().atStartOfDay().plusDays(1);
        final long initialDelay = Duration.between(now, midnight).plusSeconds(1).toSeconds();
        executor.scheduleAtFixedRate(timeKeeper, initialDelay, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);

        logger.info("All setup and running...");
    }

    private static CurrentSensorValues createSensorValues(OutdoorSensor outdoorSensor, QingPingSensor indoorSensor, IndoorSensor backupSensor,
            SensorDataPersistenceObserver persistenceObserver) {
        final CurrentSensorValues sensorValues = new CurrentSensorValues();
        outdoorSensor.addObserver(sensorValues);
        outdoorSensor.addObserver(persistenceObserver);
        indoorSensor.addObserver(sensorValues);
        indoorSensor.addObserver(persistenceObserver);
        if (backupSensor != null) {
            indoorSensor.addBackupSensor(backupSensor);
            backupSensor.addObserver(sensorValues);
            backupSensor.addObserver(persistenceObserver);
        }
        return sensorValues;
    }

    private static RuleApplier createFreshAirController(VentilationSystem ventilationSystem, CurrentSensorValues sensorValues,
            VentilationSystemTimeKeeper timeKeeper) {
        List<VentilationSystem> ventilationSystems = List.of(ventilationSystem, timeKeeper);
        CO2ControlAirFlow co2ControlAirFlow = new CO2ControlAirFlow(sensorValues);
        DailyAirFlow dailyAirFlow = new DailyAirFlow();
        HumidityControlAirFlow humidityControlAirFlow = new HumidityControlAirFlow(sensorValues);
        PeriodicallyAirFlow periodicallyAirFlow = new PeriodicallyAirFlow(timeKeeper);
        HumidityControlExchanger humidityControlExchanger = new HumidityControlExchanger(humidityControlAirFlow);

        List<Rule> freshAirRules = List.of(co2ControlAirFlow, dailyAirFlow, humidityControlAirFlow, periodicallyAirFlow);
        List<Rule> exchangeHumidityRules = List.of(humidityControlExchanger);
        return new RuleApplier(ventilationSystems, freshAirRules, exchangeHumidityRules);
    }

}