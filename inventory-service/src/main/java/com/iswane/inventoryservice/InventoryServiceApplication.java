package com.iswane.inventoryservice;

import com.iswane.inventoryservice.model.Inventory;
import com.iswane.inventoryservice.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner loadData(InventoryRepository inventoryRepository) {
		return  args -> {
			Inventory inventory = new Inventory();
			inventory.setSkuCode("IPhone_13");
			inventory.setQuantity(100);

			Inventory inventory1 = new Inventory();
			inventory1.setSkuCode("IPhone_13_red");
			inventory1.setQuantity(10);

			inventoryRepository.save(inventory);
			inventoryRepository.save(inventory1);
		};
	}

}
