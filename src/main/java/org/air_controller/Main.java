package org.air_controller;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws InterruptedException, SQLException {
        final Application application = createApplication();
        application.run();
        Thread.currentThread().join();
    }

    private static Application createApplication() {
        final ApplicationBuilder builder = new ApplicationBuilder();
        return builder.build();
    }
}