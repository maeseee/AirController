package org.air_controller;

import org.air_controller.gpio.GpioPins;
import org.air_controller.rules.*;
import org.air_controller.sensor.Sensor;
import org.air_controller.sensor.SensorException;
import org.air_controller.sensor.Sensors;
import org.air_controller.sensor.open_weather_api.OpenWeatherApiSensor;
import org.air_controller.sensor.qing_ping.QingPingSensor;
import org.air_controller.sensor_data_persistence.SensorDataCollection;
import org.air_controller.sensor_data_persistence.SensorDataCsv;
import org.air_controller.sensor_data_persistence.SensorDataDb;
import org.air_controller.sensor_data_persistence.SensorDataPersistence;
import org.air_controller.sensor_values.CurrentSensorData;
import org.air_controller.system.ControlledVentilationSystem;
import org.air_controller.system.VentilationSystem;
import org.air_controller.system.VentilationSystemTimeKeeper;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.air_controller.system_action.SystemActionDbAccessors;
import org.air_controller.system_action.SystemActionPersistence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URISyntaxException;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Application {
    private static final Logger logger = LogManager.getLogger(Application.class);
    private static final int OUTDOOR_SENSOR_READ_PERIOD_MINUTES = 10;
    private static final int INDOOR_SENSOR_READ_PERIOD_MINUTES = 10;
    private static final int RULE_APPLIER_PERIOD_MINUTES = 1;

    private final Sensors sensors;
    private final RuleApplier ruleApplier;
    private final TimeKeeper timeKeeper;
    private final ScheduledExecutorService executor;

    // Used for MainMock
    Application(GpioPins gpioPins, SystemActionDbAccessors systemActionDbAccessors) {
        this(new ControlledVentilationSystem(gpioPins),
                createSensors(),
                createVentilationSystemTimeKeeper(systemActionDbAccessors.airFlow()),
                new SystemActionPersistence(systemActionDbAccessors));
    }

    private static Sensors createSensors() {
        return new Sensors(createOutdoorSensor(), createIndoorSensor());
    }

    private static Sensor createOutdoorSensor() {
        final SensorDataPersistence persistence = new SensorDataCollection(List.of(
                new SensorDataDb(SensorDataPersistence.OUTDOOR_TABLE_NAME),
                new SensorDataCsv(SensorDataPersistence.OUTDOOR_SENSOR_CSV_PATH)));
        return new OpenWeatherApiSensor(persistence);
    }

    private static Sensor createIndoorSensor() {
        final SensorDataPersistence persistence = new SensorDataCollection(List.of(
                new SensorDataDb(SensorDataPersistence.INDOOR_TABLE_NAME),
                new SensorDataCsv(SensorDataPersistence.INDOOR_SENSOR_CSV_PATH)));
        try {
            return new QingPingSensor(persistence);
        } catch (URISyntaxException e) {
            throw new SensorException("Indoor sensor could not be created", e.getCause());
        }
    }

    private Application(VentilationSystem ventilationSystem, Sensors sensors,
            VentilationSystemTimeKeeper timeKeeper, SystemActionPersistence systemActionPersistence) {
        this(sensors,
                createRuleApplier(ventilationSystem, sensors, timeKeeper, systemActionPersistence),
                timeKeeper,
                Executors.newScheduledThreadPool(1));
    }

    // Used for tests
    Application(Sensors sensors, RuleApplier ruleApplier, TimeKeeper timeKeeper, ScheduledExecutorService executor) {
        this.sensors = sensors;
        this.ruleApplier = ruleApplier;
        this.timeKeeper = timeKeeper;
        this.executor = executor;
    }

    public void run() {
        executor.scheduleAtFixedRate(sensors.outdoor(), 0, OUTDOOR_SENSOR_READ_PERIOD_MINUTES, TimeUnit.MINUTES);
        executor.scheduleAtFixedRate(sensors.indoor(), 0, INDOOR_SENSOR_READ_PERIOD_MINUTES, TimeUnit.MINUTES);
        executor.scheduleAtFixedRate(ruleApplier, 0, RULE_APPLIER_PERIOD_MINUTES, TimeUnit.MINUTES);

        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        final ZonedDateTime midnight = ZonedDateTime.of(now.toLocalDate().atStartOfDay().plusDays(1), ZoneOffset.UTC);
        final long initialDelay = Duration.between(now, midnight).plusSeconds(1).toSeconds();
        executor.scheduleAtFixedRate(timeKeeper, initialDelay, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);

        logger.info("All setup and running...");
    }

    private static VentilationSystemTimeKeeper createVentilationSystemTimeKeeper(SystemActionDbAccessor airFlowDbAccessor) {
        return new VentilationSystemTimeKeeper(airFlowDbAccessor);
    }

    private static RuleApplier createRuleApplier(VentilationSystem ventilationSystem, Sensors sensors, VentilationSystemTimeKeeper timeKeeper,
            SystemActionPersistence systemActionPersistence) {
        final CurrentSensorData currentOutdoorSensorData = new CurrentSensorData(sensors.outdoor().getPersistence());
        final CurrentSensorData currentIndoorSensorData = new CurrentSensorData(sensors.indoor().getPersistence());
        final List<VentilationSystem> ventilationSystems = List.of(ventilationSystem, timeKeeper, systemActionPersistence);
        final CO2ControlAirFlow co2ControlAirFlow = new CO2ControlAirFlow(currentIndoorSensorData);
        final DailyAirFlow dailyAirFlow = new DailyAirFlow();
        final HumidityControlAirFlow humidityControlAirFlow = new HumidityControlAirFlow(currentIndoorSensorData, currentOutdoorSensorData);
        final PeriodicallyAirFlow periodicallyAirFlow = new PeriodicallyAirFlow(timeKeeper);
        final HumidityControlExchanger humidityControlExchanger = new HumidityControlExchanger(humidityControlAirFlow);

        final List<Rule> freshAirRules = List.of(co2ControlAirFlow, dailyAirFlow, humidityControlAirFlow, periodicallyAirFlow);
        final List<Rule> exchangeHumidityRules = List.of(humidityControlExchanger);
        return new RuleApplier(ventilationSystems, freshAirRules, exchangeHumidityRules);
    }

}