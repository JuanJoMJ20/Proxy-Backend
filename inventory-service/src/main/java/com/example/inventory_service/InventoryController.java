package com.example.inventory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @GetMapping
    public List<String> getInventory() {
        return List.of("Laptop", "Mouse", "Keyboard", "Monitor");
    }
}
