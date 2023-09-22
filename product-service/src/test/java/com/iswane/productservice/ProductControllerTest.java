package com.iswane.productservice;

import com.iswane.productservice.controller.ProductController;
import com.iswane.productservice.dto.ProductRequest;
import com.iswane.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MockMvc mockMvc;

    private final String urlTemplate = "/api/v1/product";

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Test
    void shouldCreateProduct() throws Exception {
        mockMvc.perform(post(urlTemplate)
                        .contentType(APPLICATION_JSON)
                        .content(getProductRequest()))
                .andExpect(status().isCreated());
        assertEquals(1, productRepository.findAll().size());
    }

    @Test
    void shouldGetAllProducts() throws Exception {
        mockMvc.perform(get(urlTemplate)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private String getProductRequest() {
        return """
                {
                   "name": "IPhone 15",
                   "description": "New Iphone 15",
                   "price": 1500
                 }
                """;
    }
}
