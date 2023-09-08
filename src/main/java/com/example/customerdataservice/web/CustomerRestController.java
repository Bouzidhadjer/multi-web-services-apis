package com.example.customerdataservice.web;

import com.example.customerdataservice.entities.Customer;
import com.example.customerdataservice.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class CustomerRestController {

    private CustomerRepository customerRepository;

    @GetMapping("/customers")
    public List<Customer> customerList() {
        return customerRepository.findAll();
    }
    @GetMapping("/customers/{id}")
    public  Customer customerById(@PathVariable  Long id) {
        return customerRepository.findById(id).get();
    }

    @PostMapping("/customers")
    public  Customer saveCustomer(@RequestBody Customer customer) {
        return customerRepository.save(customer);
    }
}
