package org.air_controller.sensor.open_weather_api;

import org.air_controller.http.HttpsGetRequest;
import org.air_controller.sensor.open_weather_api_adapter.OpenWeatherApiAdapter;
import org.air_controller.sensor_data_persistence.ClimateDataPointPersistence;
import org.air_controller.sensor_values.ClimateDataPoint;
import org.air_controller.sensor_values.Humidity;
import org.air_controller.sensor_values.Temperature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpenWeatherApiSensorTest {

    private static final String SAMPLE_HTTP_RESPONSE = """
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

    @Mock
    private ClimateDataPointPersistence persistence;
    @Captor
    private ArgumentCaptor<ClimateDataPoint> outdoorDataPointArgumentCaptor;

    @Test
    void testWhenMeasureValuesThenPersistData() {
        final HttpsGetRequest httpsGetRequest = mock(HttpsGetRequest.class);
        when(httpsGetRequest.sendRequest(any())).thenReturn(SAMPLE_HTTP_RESPONSE);
        final OpenWeatherApiSensor sensor = new OpenWeatherApiSensor(httpsGetRequest);
        final OpenWeatherApiAdapter testee = new OpenWeatherApiAdapter(persistence, sensor);

        testee.run();

        verify(persistence).persist(outdoorDataPointArgumentCaptor.capture());
        final ClimateDataPoint dataPoint = outdoorDataPointArgumentCaptor.getValue();
        final Temperature temperature = dataPoint.temperature();
        assertEquals(10.53, temperature.celsius(), 0.1);
        final Humidity humidity = dataPoint.humidity();
        assertEquals(87.0, humidity.getRelativeHumidity(temperature), 0.1);
    }

    @Test
    void testWhenMeasureValuesEmptyThenDontPersistData() {
        final HttpsGetRequest httpsGetRequest = mock(HttpsGetRequest.class);
        when(httpsGetRequest.sendRequest(any())).thenReturn("");
        final OpenWeatherApiSensor sensor = new OpenWeatherApiSensor(httpsGetRequest);
        final OpenWeatherApiAdapter testee = new OpenWeatherApiAdapter(persistence, sensor);

        testee.run();

        verifyNoInteractions(persistence);
    }
}