package com.example.customerdataservice.web;

import com.example.customerdataservice.entities.Customer;
import com.example.customerdataservice.mapper.CustomerMapper;
import com.example.customerdataservice.repository.CustomerRepository;
import com.example.customerdataservice.stub.CustomerServiceGrpc;
import com.example.customerdataservice.stub.CustomerServiceOuterClass;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@GrpcService
public class CustomerGrpcService extends CustomerServiceGrpc.CustomerServiceImplBase {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CustomerMapper customerMapper;
    @Override
    public void getAllCustomers(CustomerServiceOuterClass.GetAllCustomersRequest request, StreamObserver<CustomerServiceOuterClass.GetCustomersResponse> responseObserver) {
        List<Customer> customers = customerRepository.findAll();
        CustomerServiceOuterClass.GetCustomersResponse getCustomersResponse = CustomerServiceOuterClass.GetCustomersResponse
                .newBuilder()
                        .addAllCustomers(customers.stream().map(customerMapper::fromCustomer).collect(Collectors.toList()))
                                .build();
        responseObserver.onNext(getCustomersResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void getCustomerById(CustomerServiceOuterClass.GetCustomerByIdRequest request, StreamObserver<CustomerServiceOuterClass.GetCustomerByIdResponse> responseObserver) {
        Customer customerEntity = customerRepository.findById(request.getCustomer()).get();
        CustomerServiceOuterClass.GetCustomerByIdResponse getCustomerByIdResponse =
                 CustomerServiceOuterClass.GetCustomerByIdResponse.newBuilder()
                         .setCustomer(customerMapper.fromCustomer(customerEntity))
                         .build();
        responseObserver.onNext(getCustomerByIdResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void saveCustomer(CustomerServiceOuterClass.SaveCustomerRequest request, StreamObserver<CustomerServiceOuterClass.SaveCustomerResponse> responseObserver) {
            Customer customer = customerMapper.fromGrpcCustomer(request.getCustomer());
        Customer saveCustomer = customerRepository.save(customer);
        CustomerServiceOuterClass.SaveCustomerResponse saveCustomerResponse =  CustomerServiceOuterClass.SaveCustomerResponse.newBuilder()
                .setCustomer(customerMapper.fromCustomer(saveCustomer))
                .build();
        responseObserver.onNext(saveCustomerResponse);
        responseObserver.onCompleted();
    }
}
