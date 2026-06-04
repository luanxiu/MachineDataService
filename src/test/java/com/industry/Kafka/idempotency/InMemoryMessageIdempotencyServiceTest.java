package com.industry.Kafka.idempotency;

import com.industry.Config.KafkaConsumerProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemoryMessageIdempotencyServiceTest {

    private InMemoryMessageIdempotencyService service;

    @BeforeEach
    void setUp() {
        KafkaConsumerProperties properties = new KafkaConsumerProperties();
        properties.getIdempotency().setEnabled(true);
        properties.getIdempotency().setProcessingTtlSeconds(600L);
        properties.getIdempotency().setDoneTtlHours(72L);
        service = new InMemoryMessageIdempotencyService(properties);
    }

    @Test
    void shouldAcquireNewMessageThenReportInProgress() {
        assertEquals(MessageIdempotencyService.AcquireResult.NEW, service.tryAcquire("msg-1"));
        assertEquals(MessageIdempotencyService.AcquireResult.IN_PROGRESS, service.tryAcquire("msg-1"));
    }

    @Test
    void shouldReportDuplicateAfterDone() {
        assertEquals(MessageIdempotencyService.AcquireResult.NEW, service.tryAcquire("msg-2"));

        service.markDone("msg-2");

        assertEquals(MessageIdempotencyService.AcquireResult.DUPLICATE_DONE, service.tryAcquire("msg-2"));
    }

    @Test
    void shouldAllowRetryAfterRelease() {
        assertEquals(MessageIdempotencyService.AcquireResult.NEW, service.tryAcquire("msg-3"));

        service.releaseProcessing("msg-3");

        assertEquals(MessageIdempotencyService.AcquireResult.NEW, service.tryAcquire("msg-3"));
    }

    @Test
    void shouldThrowWhenMessageIdBlank() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.tryAcquire(" "));

        assertEquals("messageId must not be blank", ex.getMessage());
    }
}
