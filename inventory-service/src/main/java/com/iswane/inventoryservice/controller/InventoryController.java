package com.iswane.inventoryservice.controller;

import com.iswane.inventoryservice.dto.InventoryResponse;
import com.iswane.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<List<InventoryResponse>> isInStock(@RequestParam List<String> skuCodes) {
        List<InventoryResponse> inStock = inventoryService.isInStock(skuCodes);
        return new ResponseEntity<>(inStock, HttpStatus.OK);
    }
}
