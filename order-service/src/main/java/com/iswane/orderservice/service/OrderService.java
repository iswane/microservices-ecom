package com.iswane.orderservice.service;

import com.iswane.orderservice.dto.InventoryResponse;
import com.iswane.orderservice.dto.OrderLineItemsDTO;
import com.iswane.orderservice.dto.OrderRequest;
import com.iswane.orderservice.model.Order;
import com.iswane.orderservice.model.OrderLineItems;
import com.iswane.orderservice.repository.OrderRepository;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private static final String URL_INVENTORY_SERVICE = "http://inventory-service/api/v1/inventory";
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final Tracer tracer;


    public String placeOrder(OrderRequest orderRequest) {
        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItems()
                .stream()
                .map(this::mapToOrderLineItems)
                .toList();

        Order order = Order.builder()
                .orderNumber(UUID.randomUUID().toString())
                .orderLineItems(orderLineItems)
                .build();

        List<String> skuCodes = orderRequest.getOrderLineItems()
                .stream()
                .map(OrderLineItemsDTO::getSkuCode)
                .toList();

        log.info("Calling inventory service");
        Span inventoryServiceLookup = tracer.nextSpan().name("InventoryServiceLookup");

        try (Tracer.SpanInScope spanInScope = tracer.withSpan(inventoryServiceLookup.start())) {

            InventoryResponse[] inventoryResponses = webClientBuilder.build().get()
                    .uri(URL_INVENTORY_SERVICE, uriBuilder -> uriBuilder.queryParam("skuCodes", skuCodes).build())
                    .retrieve()
                    .bodyToMono(InventoryResponse[].class)
                    .block();
            assert inventoryResponses != null;
            boolean allProductsInStock = Arrays.stream(inventoryResponses)
                    .allMatch(InventoryResponse::isInStock);

            if (allProductsInStock) {
                orderRepository.save(order);
                return "Order Placed Successfully";
            } else {
                throw new IllegalArgumentException("Product is not in stock, please try again later.");
            }
        } finally {
            inventoryServiceLookup.end();
        }
    }

    private OrderLineItems mapToOrderLineItems(OrderLineItemsDTO orderLineItemsDTO) {
        return OrderLineItems.builder()
                .price(orderLineItemsDTO.getPrice())
                .skuCode(orderLineItemsDTO.getSkuCode())
                .quantity(orderLineItemsDTO.getQuantity())
                .build();
    }
}
