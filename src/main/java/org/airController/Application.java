package org.airController;

import com.google.inject.internal.Nullable;
import org.airController.gpio.GpioFunction;
import org.airController.gpio.GpioPin;
import org.airController.gpio.RaspberryGpioPin;
import org.airController.persistence.SensorDataCsv;
import org.airController.persistence.SensorDataDb;
import org.airController.persistence.SensorDataPersistence;
import org.airController.persistence.SensorDataPersistenceObserver;
import org.airController.rules.*;
import org.airController.sensor.Sensor;
import org.airController.sensor.dht22.OneWireSensor;
import org.airController.sensor.openWeatherApi.OpenWeatherApiSensor;
import org.airController.sensor.qingPing.QingPingSensor;
import org.airController.sensorValues.CurrentSensorData;
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

import static org.airController.persistence.SensorDataPersistenceObserver.*;

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
        this(new RaspberryGpioPin(GpioFunction.AIR_FLOW, true), new RaspberryGpioPin(GpioFunction.HUMIDITY_EXCHANGER, false),
                new OpenWeatherApiSensor(),
                new QingPingSensor(), new OneWireSensor());
    }

    // Used for MainMock
    Application(GpioPin airFlow, GpioPin humidityExchanger, Sensor outdoorSensor, QingPingSensor indoorSensor,
            @Nullable Sensor backupSensor) {
        this(new ControlledVentilationSystem(airFlow, humidityExchanger), outdoorSensor, indoorSensor,
                createCurrentIndoorSensorValue(indoorSensor, backupSensor),
                createCurrentOutdoorSensorValue(outdoorSensor), new VentilationSystemTimeKeeper());
    }

    private Application(VentilationSystem ventilationSystem, Sensor outdoorSensor, QingPingSensor indoorSensor,
            CurrentSensorData indoorSensorData, CurrentSensorData outdoorSensorData, VentilationSystemTimeKeeper timeKeeper) {
        this(outdoorSensor, indoorSensor, createFreshAirController(ventilationSystem, indoorSensorData, outdoorSensorData, timeKeeper), timeKeeper,
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

        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime midnight = now.toLocalDate().atStartOfDay().plusDays(1);
        final long initialDelay = Duration.between(now, midnight).plusSeconds(1).toSeconds();
        executor.scheduleAtFixedRate(timeKeeper, initialDelay, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);

        logger.info("All setup and running...");
    }

    private static CurrentSensorData createCurrentOutdoorSensorValue(Sensor outdoorSensor) {
        final List<SensorDataPersistence> persistences = List.of(
                new SensorDataCsv(OUTDOOR_SENSOR_CSV_PATH),
                new SensorDataDb(OUTDOOR_TABLE_NAME));
        final SensorDataPersistenceObserver observer = new SensorDataPersistenceObserver(persistences);
        final CurrentSensorData sensorValues = new CurrentSensorData();
        outdoorSensor.addObserver(sensorValues);
        outdoorSensor.addObserver(observer);
        return sensorValues;
    }

    private static CurrentSensorData createCurrentIndoorSensorValue(QingPingSensor indoorSensor, Sensor backupSensor) {
        final List<SensorDataPersistence> persistences = List.of(
                new SensorDataCsv(INDOOR_SENSOR_CSV_PATH),
                new SensorDataDb(INDOOR_TABLE_NAME));
        final SensorDataPersistenceObserver observer = new SensorDataPersistenceObserver(persistences);
        final CurrentSensorData sensorValue = new CurrentSensorData();
        indoorSensor.addObserver(sensorValue);
        indoorSensor.addObserver(observer);
        if (backupSensor != null) {
            indoorSensor.addBackupSensor(backupSensor);
            backupSensor.addObserver(sensorValue);
            backupSensor.addObserver(observer);
        }
        return sensorValue;
    }

    private static RuleApplier createFreshAirController(VentilationSystem ventilationSystem, CurrentSensorData indoorSensorData,
            CurrentSensorData outdoorSensorData, VentilationSystemTimeKeeper timeKeeper) {
        final List<VentilationSystem> ventilationSystems = List.of(ventilationSystem, timeKeeper);
        final CO2ControlAirFlow co2ControlAirFlow = new CO2ControlAirFlow(indoorSensorData);
        final DailyAirFlow dailyAirFlow = new DailyAirFlow();
        final HumidityControlAirFlow humidityControlAirFlow = new HumidityControlAirFlow(indoorSensorData, outdoorSensorData);
        final PeriodicallyAirFlow periodicallyAirFlow = new PeriodicallyAirFlow(timeKeeper);
        final HumidityControlExchanger humidityControlExchanger = new HumidityControlExchanger(humidityControlAirFlow);

        final List<Rule> freshAirRules = List.of(co2ControlAirFlow, dailyAirFlow, humidityControlAirFlow, periodicallyAirFlow);
        final List<Rule> exchangeHumidityRules = List.of(humidityControlExchanger);
        return new RuleApplier(ventilationSystems, freshAirRules, exchangeHumidityRules);
    }

}