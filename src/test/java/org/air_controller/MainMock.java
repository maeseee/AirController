package org.air_controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.annotation.Import;

@Import({ApplicationConfig.class, MockApplicationConfig.class})
class MainMock {

    static void main() {
        final SpringApplication app = new SpringApplication(AirControllerApplication.class);
        app.setWebApplicationType(WebApplicationType.SERVLET);
        app.run();
    }
}