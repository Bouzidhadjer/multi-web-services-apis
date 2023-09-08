package com.example.accountdataservice.mapper;


import com.example.accountdataservice.model.Customer;
import com.example.customerdataservice.stub.CustomerServiceOuterClass;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {
    private ModelMapper modelMapper=new ModelMapper();
    public Customer fromSoapCustomer(com.example.customerdataservice.web.Customer soapCustomer){
        return modelMapper.map(soapCustomer,Customer.class);
    }

    public Customer fromGrpcCustomer(CustomerServiceOuterClass.Customer grpcCustomer){
        return modelMapper.map(grpcCustomer, Customer.class);
    }
}