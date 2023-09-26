package com.iswane.inventoryservice.service;

import com.iswane.inventoryservice.dto.InventoryResponse;
import com.iswane.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @SneakyThrows
   @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCodes) {
       log.info("Wait Started");
       Thread.sleep(10000);
       log.info("Wait Ended");
        return inventoryRepository.findBySkuCodeIn(skuCodes)
                .stream()
                .map(inventory -> InventoryResponse.builder()
                        .skuCode(inventory.getSkuCode())
                        .inStock(inventory.getQuantity() > 0)
                        .build())
                .toList();
    }
}
