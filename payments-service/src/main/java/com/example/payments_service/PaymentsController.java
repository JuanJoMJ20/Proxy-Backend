package com.example.payments;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentsController {

    @GetMapping
    public Map<String, Object> getPayments() {
        return Map.of("paymentId", "txn_98765", "status", "Success", "amount", 150.50);
    }
}
