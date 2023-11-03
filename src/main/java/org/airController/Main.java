package org.airController;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws InterruptedException, IOException {
        final Application application = new Application();
        application.init();
        application.run();
        Thread.currentThread().join();
    }
}