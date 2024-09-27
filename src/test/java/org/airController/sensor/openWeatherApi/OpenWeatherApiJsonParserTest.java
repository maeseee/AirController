package org.airController.sensor.openWeatherApi;

import org.airController.sensorValues.Temperature;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OpenWeatherApiJsonParserTest {

    @Test
    void testParsing() {
        final String sampleHttpResponse = """
                {
                "coord":{"lon":8.246,"lat":47.1281},
                "weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10n"}],
                "base":"stations",
                "main":{"temp":283.68,"feels_like":283.06,"temp_min":282.88,"temp_max":284.93,"pressure":1005,"humidity":87},
                "visibility":10000,
                "wind":{"speed":5.66,"deg":160},
                "rain":{"1h":0.21},
                "clouds":{"all":100},
                "dt":1697912720,
                "sys":{"type":1,"id":6933,"country":"CH","sunrise":1697867574,"sunset":1697905812},
                "timezone":7200,
                "id":7286081,
                "name":"Hildisrieden",
                "cod":200
                }
                """;

        final Optional<OpenWeatherApiSensorData> result = OpenWeatherApiJsonParser.parse(sampleHttpResponse);

        assertTrue(result.isPresent());
        assertTrue(result.get().getTemperature().isPresent());
        assertTrue(result.get().getHumidity().isPresent());
        final Temperature temperature = result.get().getTemperature().get();
        assertEquals(10.53, temperature.getCelsius(), 0.1);
        assertEquals(87.0, result.get().getHumidity().get().getRelativeHumidity(temperature), 0.1);
    }

    @Test
    void testParsingWhenNodeMainIsMissingThenEmpty() {
        final String sampleHttpResponse = """
                {
                "coord":{"lon":8.246,"lat":47.1281},
                "weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10n"}],
                "base":"stations",
                "visibility":10000,
                "wind":{"speed":5.66,"deg":160},
                "rain":{"1h":0.21},
                "clouds":{"all":100},
                "dt":1697912720,
                "sys":{"type":1,"id":6933,"country":"CH","sunrise":1697867574,"sunset":1697905812},
                "timezone":7200,
                "id":7286081,
                "name":"Hildisrieden",
                "cod":200
                }
                """;

        final Optional<OpenWeatherApiSensorData> result = OpenWeatherApiJsonParser.parse(sampleHttpResponse);

        assertTrue(result.isEmpty());
    }

    @Test
    void testParsingWhenNodeTempIsMissingThenEmpty() {
        final String sampleHttpResponse = """
                {
                "coord":{"lon":8.246,"lat":47.1281},
                "weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10n"}],
                "base":"stations",
                "main":{"feels_like":283.06,"temp_min":282.88,"temp_max":284.93,"pressure":1005,"humidity":87},
                "visibility":10000,
                "wind":{"speed":5.66,"deg":160},
                "rain":{"1h":0.21},
                "clouds":{"all":100},
                "dt":1697912720,
                "sys":{"type":1,"id":6933,"country":"CH","sunrise":1697867574,"sunset":1697905812},
                "timezone":7200,
                "id":7286081,
                "name":"Hildisrieden",
                "cod":200
                }
                """;

        final Optional<OpenWeatherApiSensorData> result = OpenWeatherApiJsonParser.parse(sampleHttpResponse);

        assertTrue(result.isEmpty());
    }
}