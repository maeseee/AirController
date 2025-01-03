package org.air_controller;

import org.air_controller.gpio.GpioFunction;
import org.air_controller.gpio.GpioPin;
import org.air_controller.gpio.RaspberryGpioPin;
import org.air_controller.rules.*;
import org.air_controller.sensor.Sensor;
import org.air_controller.sensor.dht22.OneWireSensor;
import org.air_controller.sensor.open_weather_api.OpenWeatherApiSensor;
import org.air_controller.sensor.qing_ping.QingPingSensor;
import org.air_controller.sensorValues.CurrentSensorData;
import org.air_controller.sensor_data_persistence.SensorDataCollection;
import org.air_controller.sensor_data_persistence.SensorDataCsv;
import org.air_controller.sensor_data_persistence.SensorDataDb;
import org.air_controller.sensor_data_persistence.SensorDataPersistence;
import org.air_controller.system.ControlledVentilationSystem;
import org.air_controller.system.VentilationSystem;
import org.air_controller.system.VentilationSystemTimeKeeper;
import org.air_controller.systemPersitence.SystemActions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
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

    private final Sensor outdoorSensor;
    private final Sensor indoorSensor;
    private final RuleApplier ruleApplier;
    private final TimeKeeper timeKeeper;
    private final ScheduledExecutorService executor;

    public Application() throws IOException, URISyntaxException {
        this(new RaspberryGpioPin(GpioFunction.AIR_FLOW, true), new RaspberryGpioPin(GpioFunction.HUMIDITY_EXCHANGER, false));
    }

    // Used for MainMock
    Application(GpioPin airFlow, GpioPin humidityExchanger) throws URISyntaxException {
        this(new ControlledVentilationSystem(airFlow, humidityExchanger), createOutdoorSensor(), createIndoorSensor(),
                new VentilationSystemTimeKeeper(new SystemActions()));
    }

    private Application(VentilationSystem ventilationSystem, Sensor outdoorSensor, Sensor indoorSensor,
            VentilationSystemTimeKeeper timeKeeper) {
        this(outdoorSensor, indoorSensor, createFreshAirController(ventilationSystem, outdoorSensor, indoorSensor, timeKeeper), timeKeeper,
                Executors.newScheduledThreadPool(1));
    }

    // Used for tests
    Application(Sensor outdoorSensor, Sensor indoorSensor, RuleApplier ruleApplier, TimeKeeper timeKeeper, ScheduledExecutorService executor) {
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

        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        final ZonedDateTime midnight = ZonedDateTime.of(now.toLocalDate().atStartOfDay().plusDays(1), ZoneOffset.UTC);
        final long initialDelay = Duration.between(now, midnight).plusSeconds(1).toSeconds();
        executor.scheduleAtFixedRate(timeKeeper, initialDelay, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);

        logger.info("All setup and running...");
    }

    private static Sensor createOutdoorSensor() throws URISyntaxException {
        final SensorDataPersistence persistence = new SensorDataCollection(List.of(
                new SensorDataDb(SensorDataPersistence.OUTDOOR_TABLE_NAME),
                new SensorDataCsv(SensorDataPersistence.OUTDOOR_SENSOR_CSV_PATH)));
        return new OpenWeatherApiSensor(persistence);
    }

    private static Sensor createIndoorSensor() {
        final SensorDataPersistence persistence = new SensorDataCollection(List.of(
                new SensorDataDb(SensorDataPersistence.INDOOR_TABLE_NAME),
                new SensorDataCsv(SensorDataPersistence.INDOOR_SENSOR_CSV_PATH)));
        Sensor oneWireSensor = null;
        try {
            oneWireSensor = new OneWireSensor(persistence);
        } catch (IOException | IllegalArgumentException | UnsatisfiedLinkError e) {
            logger.error("Could not load backup sensor!{}", e.getMessage());
        }
        try {
            return new QingPingSensor(persistence, oneWireSensor);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static RuleApplier createFreshAirController(VentilationSystem ventilationSystem, Sensor outdoorSensor, Sensor indoorSensor,
            VentilationSystemTimeKeeper timeKeeper) {
        final CurrentSensorData currentOutdoorSensorData = new CurrentSensorData(outdoorSensor.getPersistence());
        final CurrentSensorData currentIndoorSensorData = new CurrentSensorData(indoorSensor.getPersistence());
        final List<VentilationSystem> ventilationSystems = List.of(ventilationSystem, timeKeeper);
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