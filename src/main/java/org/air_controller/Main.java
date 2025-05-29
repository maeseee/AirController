package org.air_controller;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws InterruptedException, SQLException {
        final Application application = new Application();
        application.run();
        Thread.currentThread().join();
    }
}