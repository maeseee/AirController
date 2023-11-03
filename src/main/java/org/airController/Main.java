package org.airController;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {

    public static void main(String[] args) throws InterruptedException, IOException, URISyntaxException {
        final Application application = new Application();
        application.init();
        application.run();
        Thread.currentThread().join();
    }
}