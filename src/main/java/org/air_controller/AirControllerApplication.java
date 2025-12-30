package org.air_controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AirControllerApplication {

    static void main(String[] args) {
        final Application application = createApplication();
        application.run();
        SpringApplication.run(AirControllerApplication.class, args);
    }

    private static Application createApplication() {
        final ApplicationBuilder builder = ApplicationBuilder.createBuilder();
        return builder.build();
    }
}
