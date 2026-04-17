package com.example.orders;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrdersController {

    @GetMapping
    public Map<String, Object> getOrders() {
        return Map.of("orderId", 12345, "status", "Shipped", "total", 150.50);
    }
}
