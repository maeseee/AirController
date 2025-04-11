package org.air_controller.sensor.open_weather_api;

import lombok.Getter;
import org.air_controller.http.HttpsGetRequest;
import org.air_controller.secrets.Secret;
import org.air_controller.sensor.Sensor;
import org.air_controller.sensor_data_persistence.SensorDataPersistence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;


public class OpenWeatherApiSensor implements Sensor {
    private static final Logger logger = LogManager.getLogger(OpenWeatherApiSensor.class);
    private static final String LAT = "47.127459";
    private static final String LON = "8.245553";
    private static final String ENVIRONMENT_VARIABLE_API_KEY = "weather_api_key";
    private static final String ENCRYPTED_API_KEY = "JWHqsiARWGfnwhAp/qvt7aWlmhsyXvOtnsYN32HH5J2m2/QGb/OnuhnGzooxh1onTK+ynB9f038EMbUnOZMjNw==";

    @Getter
    private final SensorDataPersistence persistence;
    private final HttpsGetRequest httpsGetRequest;

    public OpenWeatherApiSensor(SensorDataPersistence persistence) {
        this(persistence, new HttpsGetRequest());
    }

    OpenWeatherApiSensor(SensorDataPersistence persistence, HttpsGetRequest httpsGetRequest) {
        this.persistence = persistence;
        this.httpsGetRequest = httpsGetRequest;
    }

    @Override
    public void run() {
        try {
            doRun();
        } catch (Exception exception) {
            logger.error("Exception in OutdoorSensor loop:", exception);
        }
    }

    private void doRun() {
        final Optional<String> request = httpsGetRequest.sendRequest(createHttpsGetUrl());
        if (request.isEmpty()) {
            logger.error("Outdoor sensor request failed");
            return;
        }

        final Optional<OpenWeatherApiSensorData> sensorData = OpenWeatherApiJsonParser.parse(request.get());
        sensorData.ifPresentOrElse(
                this::persistData,
                () -> logger.error("Outdoor sensor out of order"));
    }

    private String createHttpsGetUrl() {
        final String apiKeyForHttpRequest = getDecryptedApiKeyForHttpRequest();
        return "https://api.openweathermap.org/data/2.5/weather?lat=" + LAT + "&lon=" + LON + "&appid=" + apiKeyForHttpRequest;
    }

    private static String getDecryptedApiKeyForHttpRequest() {
        return Secret.getSecret(ENVIRONMENT_VARIABLE_API_KEY, ENCRYPTED_API_KEY);
    }

    private void persistData(OpenWeatherApiSensorData outdoorSensorData) {
        logger.info("New outdoor sensor data: {}", outdoorSensorData);
        persistence.persist(outdoorSensorData);
    }
}
