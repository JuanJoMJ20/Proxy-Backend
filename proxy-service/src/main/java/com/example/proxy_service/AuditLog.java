package com.example.proxy_service;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String serviceName;
    private String method;
    private String path;
    private int statusCode;
    private long executionTimeMs;
    private LocalDateTime timestamp;

    public AuditLog() {}

    public AuditLog(String serviceName, String method, String path, int statusCode, long executionTimeMs, LocalDateTime timestamp) {
        this.serviceName = serviceName;
        this.method = method;
        this.path = path;
        this.statusCode = statusCode;
        this.executionTimeMs = executionTimeMs;
        this.timestamp = timestamp;
    }

    // Getters
    public Long getId() { return id; }
    public String getServiceName() { return serviceName; }
    public String getMethod() { return method; }
    public String getPath() { return path; }
    public int getStatusCode() { return statusCode; }
    public long getExecutionTimeMs() { return executionTimeMs; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
