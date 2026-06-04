package com.industry.Kafka.idempotency;

import com.industry.Config.KafkaConsumerProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@ConditionalOnProperty(prefix = "machine.redis", name = "enabled", havingValue = "false", matchIfMissing = true)
public class InMemoryMessageIdempotencyService implements MessageIdempotencyService {

    private final KafkaConsumerProperties properties;
    private final Map<String, Instant> processing = new ConcurrentHashMap<>();
    private final Map<String, Instant> done = new ConcurrentHashMap<>();

    public InMemoryMessageIdempotencyService(KafkaConsumerProperties properties) {
        this.properties = properties;
    }

    @Override
    public AcquireResult tryAcquire(String messageId) {
        if (!Boolean.TRUE.equals(properties.getIdempotency().getEnabled())) {
            return AcquireResult.NEW;
        }
        if (!StringUtils.hasText(messageId)) {
            throw new IllegalArgumentException("messageId must not be blank");
        }

        cleanupExpired();

        if (done.containsKey(messageId)) {
            return AcquireResult.DUPLICATE_DONE;
        }

        Instant processingExpiresAt = Instant.now().plus(Duration.ofSeconds(
                properties.getIdempotency().getProcessingTtlSeconds()));
        Instant existing = processing.putIfAbsent(messageId, processingExpiresAt);
        if (existing == null) {
            return AcquireResult.NEW;
        }
        if (existing.isBefore(Instant.now())) {
            processing.put(messageId, processingExpiresAt);
            return AcquireResult.NEW;
        }

        if (done.containsKey(messageId)) {
            return AcquireResult.DUPLICATE_DONE;
        }
        return AcquireResult.IN_PROGRESS;
    }

    @Override
    public void markDone(String messageId) {
        if (!Boolean.TRUE.equals(properties.getIdempotency().getEnabled())) {
            return;
        }
        done.put(messageId, Instant.now().plus(Duration.ofHours(
                properties.getIdempotency().getDoneTtlHours())));
        processing.remove(messageId);
    }

    @Override
    public void releaseProcessing(String messageId) {
        if (!Boolean.TRUE.equals(properties.getIdempotency().getEnabled())) {
            return;
        }
        processing.remove(messageId);
    }

    private void cleanupExpired() {
        Instant now = Instant.now();
        removeExpired(processing, now);
        removeExpired(done, now);
    }

    private void removeExpired(Map<String, Instant> values, Instant now) {
        values.entrySet().removeIf(entry -> entry.getValue().isBefore(now));
    }
}
