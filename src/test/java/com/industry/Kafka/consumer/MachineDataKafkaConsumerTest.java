package com.industry.Kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.industry.Config.IoTDBProperties;
import com.industry.Kafka.idempotency.MessageIdempotencyService;
import com.industry.iotdb.model.dto.IoTDBField;
import com.industry.iotdb.model.dto.IoTDBRecord;
import com.industry.iotdb.repository.IoTDBRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MachineDataKafkaConsumerTest {

    private MessageIdempotencyService idempotencyService;
    private IoTDBRepository ioTDBRepository;
    private Acknowledgment acknowledgment;
    private MachineDataKafkaConsumer consumer;

    @BeforeEach
    void setUp() {
        idempotencyService = mock(MessageIdempotencyService.class);
        ioTDBRepository = mock(IoTDBRepository.class);
        acknowledgment = mock(Acknowledgment.class);
        IoTDBProperties ioTDBProperties = new IoTDBProperties();
        ioTDBProperties.setDatabase("root.test");
        consumer = new MachineDataKafkaConsumer(
                idempotencyService,
                new ObjectMapper(),
                ioTDBRepository,
                ioTDBProperties);
    }

    @Test
    void shouldWriteJd50FieldsMessageToSingleIoTDBDevice() {
        when(idempotencyService.tryAcquire(anyString()))
                .thenReturn(MessageIdempotencyService.AcquireResult.NEW);
        String payload = """
                {
                  "messageId": "jd50-1",
                  "schema": "jd50.fields.v1",
                  "machineIp": "169.254.0.26",
                  "device": "jd50_169_254_0_26",
                  "source": "spin_data.csv",
                  "time": "2026-05-18 09:49:28.559000",
                  "fields": {
                    "FeedRate": 100,
                    "SpindleCurrent": 483.0,
                    "ProgramState": "RUN",
                    "Success": true
                  },
                  "fieldTypes": {
                    "FeedRate": "INT64",
                    "SpindleCurrent": "DOUBLE",
                    "ProgramState": "TEXT",
                    "Success": "BOOLEAN"
                  }
                }
                """;
        ConsumerRecord<String, String> record = new ConsumerRecord<>("test-topic", 0, 1L, "jd50-1", payload);

        consumer.onMessage(record, acknowledgment);

        verify(ioTDBRepository).insertRecords(org.mockito.ArgumentMatchers.argThat(records -> {
            if (records.size() != 1) {
                return false;
            }
            IoTDBRecord written = records.get(0);
            assertEquals("root.test.jd50_169_254_0_26.spin_data", written.getDevice());
            assertEquals(1779068968559L, written.getTimestamp());
            assertField(written.getFields(), "FeedRate", "INT64", 100);
            assertField(written.getFields(), "SpindleCurrent", "DOUBLE", 483.0);
            assertField(written.getFields(), "ProgramState", "TEXT", "RUN");
            assertField(written.getFields(), "Success", "BOOLEAN", true);
            return true;
        }));
        verify(idempotencyService).markDone("jd50-1");
        verify(acknowledgment).acknowledge();
    }

    private void assertField(List<IoTDBField> fields, String measurement, String dataType, Object value) {
        IoTDBField field = fields.stream()
                .filter(candidate -> measurement.equals(candidate.getMeasurement()))
                .findFirst()
                .orElseThrow();
        assertEquals(dataType, field.getDataType());
        assertEquals(value, field.getValue());
    }
}
