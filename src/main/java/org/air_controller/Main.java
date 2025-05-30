package org.air_controller;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws InterruptedException, SQLException {
        final ApplicationBuilder builder = new ApplicationBuilder();
        final Application application = builder.build();
        application.run();
        Thread.currentThread().join();
    }
}