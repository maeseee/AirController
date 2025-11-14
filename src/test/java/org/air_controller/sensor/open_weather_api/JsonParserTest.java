package org.air_controller.sensor.open_weather_api;

import org.air_controller.sensor_values.Humidity;
import org.air_controller.sensor_values.ClimateDataPoint;
import org.air_controller.sensor_values.Temperature;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class JsonParserTest {

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

        final Optional<ClimateDataPoint> result = JsonParser.parse(sampleHttpResponse);

        assertThat(result).isPresent();
        final Temperature temperature = result.get().temperature();
        assertThat(temperature.celsius()).isCloseTo(10.53, within(0.1));
        final Humidity humidity = result.get().humidity();
        assertThat(humidity.getRelativeHumidity(temperature)).isCloseTo(87.0, within(0.1));
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

        final Optional<ClimateDataPoint> result = JsonParser.parse(sampleHttpResponse);

        assertThat(result).isEmpty();
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

        final Optional<ClimateDataPoint> result = JsonParser.parse(sampleHttpResponse);

        assertThat(result).isEmpty();
    }
}