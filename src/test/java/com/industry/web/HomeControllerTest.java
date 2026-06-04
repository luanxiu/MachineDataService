package com.industry.web;

import com.industry.Config.KafkaConsumerProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = HomeController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KafkaConsumerProperties kafkaConsumerProperties;

    @Test
    void shouldReturnServiceStatus() throws Exception {
        when(kafkaConsumerProperties.getEnabled()).thenReturn(true);
        when(kafkaConsumerProperties.getTopics()).thenReturn(List.of("machine-data"));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.service").value("MachineData-Service"))
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.kafkaConsumerEnabled").value(true))
                .andExpect(jsonPath("$.kafkaTopics[0]").value("machine-data"));
    }
}
