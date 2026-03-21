package org.air_controller.web_access.graph;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IndoorSensorGraphController.class)
@DisabledIfEnvironmentVariable(named = "GITHUB_ACTIONS", matches = "true")
class IndoorSensorGraphControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    @Qualifier("indoorGraphService")
    private SensorGraphService graphService;

    @Test
    void testEndpoint() throws Exception {
        final GraphView graphView = new GraphView("Test", List.of());
        when(graphService.getGraphView(any(), any(), anyString())).thenReturn(graphView);

        final MvcResult result = mockMvc.perform(get("/graph/indoor/TEMPERATURE/24"))
                .andExpect(status().isOk())
                .andReturn();
        final String jsonResponse = result.getResponse().getContentAsString();

        assertThat(jsonResponse).isEqualTo("{\"nameWithUnit\":\"Test\",\"items\":[]}");
    }
}