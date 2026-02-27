package org.air_controller;

import org.air_controller.gpio.GpioPins;
import org.air_controller.gpio.dingtian_relay.DingtianPin;
import org.air_controller.gpio.dingtian_relay.DingtianRelay;
import org.air_controller.persistence.MariaDatabase;
import org.air_controller.rules.FreshAirRuleFactory;
import org.air_controller.rules.HumidityExchangerRuleFactory;
import org.air_controller.rules.Rule;
import org.air_controller.rules.RuleApplier;
import org.air_controller.sensor.IndoorSensorFactory;
import org.air_controller.sensor.OutdoorSensorFactory;
import org.air_controller.sensor.SensorFactory;
import org.air_controller.sensor_data_persistence.ClimateDataPointPersistence;
import org.air_controller.sensor_data_persistence.ClimateDataPointsDbAccessor;
import org.air_controller.sensor_data_persistence.ClimateSensorAccessors;
import org.air_controller.sensor_values.ClimateSensors;
import org.air_controller.statistics.DailyOnTimeLogger;
import org.air_controller.system.ControlledVentilationSystem;
import org.air_controller.system.VentilationSystem;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.air_controller.system_action.SystemPart;
import org.air_controller.system_action.VentilationSystemDbAccessors;
import org.air_controller.system_action.VentilationSystemPersistence;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class ApplicationConfig {

    @Bean
    public GpioPins createDingtianPins() {
        return new GpioPins(new DingtianPin(DingtianRelay.AIR_FLOW, true), new DingtianPin(DingtianRelay.HUMIDITY_EXCHANGER, false));
    }

    @Bean
    public VentilationSystemDbAccessors createSystemActionDbAccessors() {
        return new VentilationSystemDbAccessors(
                createSystemActionDbAccessor(SystemPart.AIR_FLOW),
                createSystemActionDbAccessor(SystemPart.HUMIDITY));
    }

    @Bean
    public ClimateSensorAccessors createClimateSensorAccessors() {
        return new ClimateSensorAccessors(
                createClimateSensorAccessor(ClimateDataPointPersistence.INDOOR_TABLE_NAME),
                createClimateSensorAccessor(ClimateDataPointPersistence.OUTDOOR_TABLE_NAME));
    }

    @Bean
    public ClimateSensors createSensors(ClimateSensorAccessors accessors) {
        final SensorFactory indoorSensorFactory = new IndoorSensorFactory();
        final SensorFactory outdoorSensorFactory = new OutdoorSensorFactory();
        return new ClimateSensors(
                indoorSensorFactory.build(accessors.indoor()),
                outdoorSensorFactory.build(accessors.outdoor()));
    }

    @Bean
    public VentilationSystem createVentilationSystem(GpioPins gpios) {
        return new ControlledVentilationSystem(gpios);
    }

    @Bean
    public VentilationSystemPersistence createVentilationSystemPersistence(VentilationSystemDbAccessors accessors) {
        return new VentilationSystemPersistence(accessors);
    }

    @Bean
    public List<Rule> createFreshAirRules(ClimateSensors sensors, VentilationSystemDbAccessors accessors) {
        return new FreshAirRuleFactory().build(sensors, accessors.airFlow());
    }

    @Bean
    public DailyOnTimeLogger createStatistics(VentilationSystemDbAccessors accessors) {
        return new DailyOnTimeLogger(accessors.airFlow());
    }

    @Bean
    public RuleApplier createRuleApplier(VentilationSystem ventilationSystem, VentilationSystemPersistence ventilationSystemPersistence,
            List<Rule> freshAirRules, ClimateSensors sensors) {
        final List<Rule> humidityExchangeRules = new HumidityExchangerRuleFactory().build(sensors);
        return new RuleApplier(ventilationSystem, ventilationSystemPersistence, freshAirRules, humidityExchangeRules);
    }

    @Bean
    public Application createApplication(ClimateSensors sensors, RuleApplier ruleApplier, DailyOnTimeLogger statistics) {
        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        return new Application(sensors, ruleApplier, statistics, executor);
    }

    @Bean
    public CommandLineRunner runApplication(Application application) {
        return _ -> application.run();
    }

    private SystemActionDbAccessor createSystemActionDbAccessor(SystemPart systemPart) {
        return new SystemActionDbAccessor(new MariaDatabase(), systemPart);
    }

    private ClimateDataPointPersistence createClimateSensorAccessor(String tableName) {
        return new ClimateDataPointsDbAccessor(new MariaDatabase(), tableName);
    }
}
