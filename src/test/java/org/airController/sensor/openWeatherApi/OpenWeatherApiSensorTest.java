package org.airController.sensor.openWeatherApi;

import org.airController.sensor.OutdoorSensorObserver;
import org.airController.sensorValues.Humidity;
import org.airController.sensorValues.Temperature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpenWeatherApiSensorTest {

    private final static String SAMPLE_HTTP_RESPONSE = """
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

    @Captor
    ArgumentCaptor<OpenWeatherApiSensorData> outdoorSensorDataArgumentCaptor;

    @Test
    void testWhenMeasureValuesThenCallObservers() {
        final HttpsGetRequest httpsGetRequest = mock(HttpsGetRequest.class);
        when(httpsGetRequest.sendRequest()).thenReturn(Optional.of(SAMPLE_HTTP_RESPONSE));
        final OutdoorSensorObserver observer = mock(OutdoorSensorObserver.class);
        final OpenWeatherApiSensor testee = new OpenWeatherApiSensor(httpsGetRequest);
        testee.addObserver(observer);

        testee.run();

        verify(observer).updateOutdoorSensorData(outdoorSensorDataArgumentCaptor.capture());
        final OpenWeatherApiSensorData sensorData = outdoorSensorDataArgumentCaptor.getValue();
        assertTrue(sensorData.getTemperature().isPresent());
        assertTrue(sensorData.getHumidity().isPresent());
        final Temperature temperature = sensorData.getTemperature().get();
        assertEquals(10.53, temperature.getCelsius(), 0.1);
        final Humidity humidity = sensorData.getHumidity().get();
        assertEquals(87.0, humidity.getRelativeHumidity(temperature), 0.1);
    }

    @Test
    void testWhenMeasureValuesEmptyThenDontCallObservers() {
        final HttpsGetRequest httpsGetRequest = mock(HttpsGetRequest.class);
        when(httpsGetRequest.sendRequest()).thenReturn(Optional.empty());
        final OutdoorSensorObserver observer = mock(OutdoorSensorObserver.class);
        final OpenWeatherApiSensor testee = new OpenWeatherApiSensor(httpsGetRequest);
        testee.addObserver(observer);

        testee.run();

        verifyNoInteractions(observer);
    }
}