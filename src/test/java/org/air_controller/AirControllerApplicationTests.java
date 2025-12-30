package org.air_controller;

import org.air_controller.system.OutputState;
import org.air_controller.system_action.SystemAction;
import org.air_controller.web_access.AirControllerService;
import org.air_controller.web_access.SystemController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SystemController.class)
class SystemControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private AirControllerService airControllerService;

    @Test
    void testEndpoint() throws Exception {
        final SystemAction systemAction = new SystemAction(ZonedDateTime.now(ZoneOffset.UTC), OutputState.ON);
        when(airControllerService.getCurrentStateForFreshAir()).thenReturn(Optional.of(systemAction));

        final MvcResult result = mockMvc.perform(get("/currentState/freshAir"))
                .andExpect(status().isOk())
                .andReturn();
        final String jsonResponse = result.getResponse().getContentAsString();
        final OutputState actualState = objectMapper.readValue(jsonResponse, OutputState.class);

        assertThat(actualState).isEqualTo(OutputState.ON);
    }
}