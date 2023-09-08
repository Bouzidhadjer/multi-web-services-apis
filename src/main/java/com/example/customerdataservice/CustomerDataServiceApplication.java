package com.example.customerdataservice;

import com.example.customerdataservice.entities.Customer;
import com.example.customerdataservice.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CustomerDataServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerDataServiceApplication.class, args);
	}


	@Bean
	CommandLineRunner  start(CustomerRepository customerRepository){
		return  args -> {
			customerRepository.save(Customer.builder().name("Hassan").email("hasssan@gmail.com").build());
			customerRepository.save(Customer.builder().name("Imane").email("Imane@gmail.com").build());
			customerRepository.save(Customer.builder().name("Mohamed").email("mohamd@gmail.com").build());

		};
	}
}
