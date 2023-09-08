package com.example.customerdataservice.web;

import com.example.customerdataservice.dto.CustomerRequest;
import com.example.customerdataservice.entities.Customer;
import com.example.customerdataservice.mapper.CustomerMapper;
import com.example.customerdataservice.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@AllArgsConstructor
public class CustomerGraphQLController {
    private CustomerRepository customerRepository;
    private CustomerMapper customerMapper;

    @QueryMapping
    public List<Customer> allCustomers() {
        return customerRepository.findAll();
    }
    @QueryMapping
    public Customer customerById(@Argument Long id) {
        Customer customer = customerRepository.findById(id).orElse(null);
        if(customer != null) {
              return customer;
        }  else  {
             throw  new RuntimeException(String.format("Customer not found",  id));
        }
    }

    @MutationMapping
    public  Customer saveCustomer(@Argument CustomerRequest customer) {
         return customerRepository.save(customerMapper.from(customer));
    }
}
