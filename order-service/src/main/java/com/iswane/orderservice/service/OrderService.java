package com.iswane.orderservice.service;

import com.iswane.orderservice.dto.OrderLineItemsDTO;
import com.iswane.orderservice.dto.OrderRequest;
import com.iswane.orderservice.model.Order;
import com.iswane.orderservice.model.OrderLineItems;
import com.iswane.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public void placeOrder(OrderRequest orderRequest) {
        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItems()
                .stream()
                .map(this::mapToOrderLineItems)
                .toList();

        Order order = Order.builder()
                .orderNumber(UUID.randomUUID().toString())
                .orderLineItems(orderLineItems)
                .build();
        orderRepository.save(order);
    }

    private OrderLineItems mapToOrderLineItems(OrderLineItemsDTO orderLineItemsDTO) {
        return OrderLineItems.builder()
                .price(orderLineItemsDTO.getPrice())
                .skuCode(orderLineItemsDTO.getSkuCode())
                .quantity(orderLineItemsDTO.getQuantity())
                .build();
    }
}
