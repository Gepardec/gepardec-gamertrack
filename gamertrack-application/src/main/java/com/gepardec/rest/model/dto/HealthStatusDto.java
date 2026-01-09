package com.gepardec.rest.model.dto;

import java.time.OffsetDateTime;

public class HealthStatusDto {
    private String status; // UP or DOWN
    private String message;
    private OffsetDateTime timestamp;

    public HealthStatusDto() {}

    public HealthStatusDto(String status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = OffsetDateTime.now();
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public OffsetDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(OffsetDateTime timestamp) { this.timestamp = timestamp; }
}
