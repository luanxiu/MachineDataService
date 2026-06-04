package com.industry.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChannelEntity {

    private String messageId;
    private String time;
    private String schema;
    private String machineIp;
    private String device;
    private String source;
    private Map<String, Double> channels;
    private Map<String, Object> fields;
    private Map<String, String> fieldTypes;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getMachineIp() {
        return machineIp;
    }

    public void setMachineIp(String machineIp) {
        this.machineIp = machineIp;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Map<String, Double> getChannels() {
        return channels;
    }

    public void setChannels(Map<String, Double> channels) {
        this.channels = channels;
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public void setFields(Map<String, Object> fields) {
        this.fields = fields;
    }

    public Map<String, String> getFieldTypes() {
        return fieldTypes;
    }

    public void setFieldTypes(Map<String, String> fieldTypes) {
        this.fieldTypes = fieldTypes;
    }

    public String fallbackMessageId() {
        StringBuilder canonical = new StringBuilder();
        canonical.append(time == null ? "" : time).append('|')
                .append(machineIp == null ? "" : machineIp).append('|')
                .append(device == null ? "" : device).append('|')
                .append(source == null ? "" : source).append('|');
        if (channels != null) {
            Map<String, Double> sorted = new TreeMap<>(channels);
            sorted.forEach((k, v) -> canonical.append(k).append('=').append(v).append(';'));
        }
        if (fields != null) {
            Map<String, Object> sorted = new TreeMap<>(fields);
            sorted.forEach((k, v) -> canonical.append(k).append('=').append(v).append(';'));
        }
        return sha256(canonical.toString());
    }

    private String sha256(String source) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(source.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 not available", ex);
        }
    }
}
