package com.example.proxy_service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*") // Allow frontend to call
public class ProxyController {

    private final RestTemplate restTemplate = new RestTemplate();
    
    @GetMapping("/")
    public String index() {
        return "<h1>Proxy Service is Running</h1><p>The observability engine is active. Visit /observability/logs to see the raw data.</p>";
    }

    @Autowired
    private AuditLogRepository auditLogRepository;

    private final Map<String, String> serviceUrls = Map.of(
            "inventory", "http://inventory-service:8081",
            "orders", "http://orders-service:8082",
            "payments", "http://payments-service:8083"
    );

    @RequestMapping(value = "/api/{service}/**")
    public ResponseEntity<String> proxyRequest(
            @PathVariable String service,
            HttpServletRequest request,
            @RequestBody(required = false) String body) {
        
        long startTime = System.currentTimeMillis();
        int statusCode = 500;
        
        String targetBaseUrl = serviceUrls.get(service);
        if (targetBaseUrl == null) {
            // Local fallback for local execution without docker-compose
            targetBaseUrl = getLocalFallbackUrl(service);
        }

        if (targetBaseUrl == null) {
            long executionTime = System.currentTimeMillis() - startTime;
            saveLog(service, request.getMethod(), request.getRequestURI(), 404, executionTime);
            return ResponseEntity.notFound().build();
        }

        String path = request.getRequestURI();
        String queryString = request.getQueryString();
        String targetUrl = targetBaseUrl + path + (queryString != null ? "?" + queryString : "");

        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if (!headerName.equalsIgnoreCase("host")) {
                headers.add(headerName, request.getHeader(headerName));
            }
        }

        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = null;

        try {
            response = restTemplate.exchange(targetUrl, HttpMethod.valueOf(request.getMethod()), entity, String.class);
            statusCode = response.getStatusCode().value();
        } catch (HttpStatusCodeException e) {
            statusCode = e.getStatusCode().value();
            response = ResponseEntity.status(statusCode).headers(e.getResponseHeaders()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            statusCode = 500;
            response = ResponseEntity.status(500).body("Internal Proxy Error: " + e.getMessage());
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            saveLog(service, request.getMethod(), path, statusCode, executionTime);
        }

        return response;
    }

    private String getLocalFallbackUrl(String service) {
        return switch (service) {
            case "inventory" -> "http://localhost:8081";
            case "orders" -> "http://localhost:8082";
            case "payments" -> "http://localhost:8083";
            default -> null;
        };
    }

    private void saveLog(String service, String method, String path, int statusCode, long executionTime) {
        AuditLog log = new AuditLog(service, method, path, statusCode, executionTime, LocalDateTime.now());
        auditLogRepository.save(log);
    }
}
