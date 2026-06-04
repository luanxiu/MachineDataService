package com.industry.support;

import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@EnabledIfSystemProperty(
        named = "real.integration.tests",
        matches = "true",
        disabledReason = "Requires reachable real Kafka/Redis/IoTDB services")
public @interface RealEnvironmentTest {
}
