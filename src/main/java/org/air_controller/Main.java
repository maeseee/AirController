package org.air_controller;

public class Main {

    static void main() throws InterruptedException {
        final Application application = createApplication();
        application.run();
        Thread.currentThread().join();
    }

    private static Application createApplication() {
        final ApplicationBuilder builder = ApplicationBuilder.createBuilder();
        return builder.build();
    }
}