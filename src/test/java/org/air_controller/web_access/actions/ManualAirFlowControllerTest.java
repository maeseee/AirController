package org.air_controller.web_access.actions;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ManualAirFlowController.class)
class ManualAirFlowControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private ManualAirFlowService service;

    @Test
    void testEndpoint() throws Exception {
        mockMvc.perform(post("/action/airflow/on/180"))
                .andExpect(status().isOk());

        verify(service).notifyManualAirFlowOverride(Duration.ofHours(3), true);
    }
}