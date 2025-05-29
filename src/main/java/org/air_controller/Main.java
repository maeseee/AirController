package org.air_controller;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        final Application application = new Application();
        application.run();
        Thread.currentThread().join();
    }
}