package org.air_controller;

import org.air_controller.gpio.GpioPin;
import org.air_controller.gpio.GpioPins;
import org.air_controller.gpio.MockGpioPin;
import org.air_controller.system_action.SystemActionDbAccessor;
import org.air_controller.system_action.SystemActionDbAccessors;
import org.air_controller.system_action.SystemPart;
import org.h2.jdbcx.JdbcDataSource;

import java.sql.Connection;
import java.sql.SQLException;

class MainMock {

    public static void main(String[] args) throws InterruptedException, SQLException {
        final GpioPin airFlow = new MockGpioPin("AIR_FLOW", true);
        final GpioPin humidityExchanger = new MockGpioPin("HUMIDITY_EXCHANGER", true);
        final GpioPins gpioPins = new GpioPins(airFlow, humidityExchanger);
        final ApplicationBuilder builder = new ApplicationBuilder();
        builder.setGpioPins(gpioPins);
        builder.setSystemActionDbAccessors(createSystemActionDbAccessors());
        final Application application = builder.build();
        application.run();
        Thread.currentThread().join();
    }

    private static SystemActionDbAccessors createSystemActionDbAccessors() throws SQLException {
        return new SystemActionDbAccessors(
                createSystemActionDbAccessorWithLocalDb(SystemPart.AIR_FLOW),
                createSystemActionDbAccessorWithLocalDb(SystemPart.HUMIDITY));
    }

    private static SystemActionDbAccessor createSystemActionDbAccessorWithLocalDb(SystemPart systemPart) throws SQLException {
        return new SystemActionDbAccessor(createLocalDbConnection(), systemPart);
    }

    private static Connection createLocalDbConnection() throws SQLException {
        final JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        dataSource.setPassword("");
        return dataSource.getConnection();
    }
}