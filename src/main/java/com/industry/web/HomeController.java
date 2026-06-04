package com.industry.web;

import com.industry.Config.KafkaConsumerProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@RestController
public class HomeController {

    private final String applicationName;
    private final KafkaConsumerProperties kafkaConsumerProperties;

    public HomeController(
            @Value("${spring.application.name:MachineData-Service}") String applicationName,
            KafkaConsumerProperties kafkaConsumerProperties) {
        this.applicationName = applicationName;
        this.kafkaConsumerProperties = kafkaConsumerProperties;
    }

    @GetMapping("/")
    public Map<String, Object> home() {
        return Map.of(
                "service", applicationName,
                "status", "UP",
                "time", OffsetDateTime.now().toString(),
                "kafkaConsumerEnabled", kafkaConsumerProperties.getEnabled(),
                "kafkaTopics", kafkaConsumerProperties.getTopics(),
                "endpoints", List.of(
                        "GET /",
                        "GET /actuator/health",
                        "GET /iotdb/examples",
                        "POST /iotdb/write/single",
                        "POST /iotdb/write/batch",
                        "POST /iotdb/query/single",
                        "POST /iotdb/query/batch",
                        "POST /iotdb/update/single",
                        "POST /iotdb/update/batch",
                        "DELETE /iotdb/delete"
                )
        );
    }
}
