package org.air_controller;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws InterruptedException, SQLException {
        final Application application = createApplication();
        application.run();
        Thread.currentThread().join();
    }

    private static Application createApplication() {
        final ApplicationBuilderSharedObjects sharedObjects = new ApplicationBuilderSharedObjects();
        final ApplicationBuilder builder = new ApplicationBuilder(sharedObjects);
        builder.setRuleApplier(builder.createRuleApplier());
        builder.setSystemStateLogger(builder.createSystemStateLogger());
        return builder.build();
    }
}