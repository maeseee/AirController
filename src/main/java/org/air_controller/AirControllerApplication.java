package org.air_controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AirControllerApplication {

    static void main(String[] args) {
        final Application application = createApplication();
        application.run();
        final SpringApplication app = new SpringApplication(AirControllerApplication.class);
        app.setWebApplicationType(WebApplicationType.SERVLET);
        app.run(args);
    }

    private static Application createApplication() {
        final ApplicationBuilder builder = new ApplicationBuilder();
        return builder.build();
    }
}
