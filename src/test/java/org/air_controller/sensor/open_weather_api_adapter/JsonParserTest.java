package org.air_controller.sensor.open_weather_api_adapter;

import org.air_controller.sensor.solar_events.SolarEvent;
import org.air_controller.sensor_values.ClimateDataPoint;
import org.air_controller.sensor_values.Humidity;
import org.air_controller.sensor_values.Temperature;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
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

        final Optional<ClimateDataPoint> result = JsonParser.parseDataPoint(sampleHttpResponse);

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

        final Optional<ClimateDataPoint> result = JsonParser.parseDataPoint(sampleHttpResponse);

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

        final Optional<ClimateDataPoint> result = JsonParser.parseDataPoint(sampleHttpResponse);

        assertThat(result).isEmpty();
    }

    @Test
    void testParsingSolarEvents() {
        final String sampleHttpResponse = """
                {
                "coord":{"lon":8.2456,"lat":47.1275},
                "weather":[{"id":802,"main":"Clouds","description":"scattered clouds","icon":"03d"}],
                "base":"stations",
                "main":{"temp":292.29,"feels_like":291.57,"temp_min":292.29,"temp_max":292.29,"pressure":1016,"humidity":50,"sea_level":1016,"grnd_level":941},
                "visibility":10000,
                "wind":{"speed":1.15,"deg":251,"gust":1.39},
                "clouds":{"all":38},
                "dt":1781548841,
                "sys":{"type":2,"id":2010942,"country":"CH","sunrise":1781494253,"sunset":1781551448},
                "timezone":7200,
                "id":7286081,
                "name":"Hildisrieden",
                "cod":200
                }
                """;

        final Optional<SolarEvent> result = JsonParser.parsesSolarEvent(sampleHttpResponse);

        assertThat(result).isPresent();
        assertThat(result.get().sunrise())
                .isCloseTo(ZonedDateTime.of(2026, 6, 15, 5, 30, 53, 0, ZoneId.of("Europe/Berlin")),
                        within(1, ChronoUnit.MINUTES));
        assertThat(result.get().sunset())
                .isCloseTo(ZonedDateTime.of(2026, 6, 15, 21, 24, 28, 0, ZoneId.of("Europe/Berlin")),
                        within(1, ChronoUnit.MINUTES));
    }
}