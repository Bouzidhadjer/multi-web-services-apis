package com.example.accountdataservice.web;

import com.example.accountdataservice.mapper.CustomerMapper;
import com.example.accountdataservice.feign.CustomerRestClient;
import com.example.accountdataservice.model.Customer;
import com.example.customerdataservice.stub.CustomerServiceGrpc;
import com.example.customerdataservice.stub.CustomerServiceOuterClass;
import com.example.customerdataservice.web.CustomerSoapService;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/account-service")
public class AccountRestController {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private CustomerRestClient customerRestClient;

    @Autowired
    private CustomerSoapService customerSoapService;

    @Autowired
    private CustomerMapper customerMapper;
    @GrpcClient(value = "customerService")
    private CustomerServiceGrpc.CustomerServiceBlockingStub customerServiceBlockingStub;
    @GetMapping("/customers")
    public List<Customer>  customerList() {
        Customer[] customers = restTemplate.getForObject("http://localhost:8082/customers", Customer[].class);
        return List.of(customers);
    }
    @GetMapping("/customers/{id}")
    public Customer  customerById(@PathVariable Long id) {
        Customer customer = restTemplate.getForObject("http://localhost:8082/customers/" + id, Customer.class);
        return customer;
    }
    @GetMapping("/customers/v2")
    public Flux<Customer> customerListV2() {
        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:8082").build();
        Flux<Customer> customerFlux = webClient.get()
                .uri("/customers").retrieve().bodyToFlux(Customer.class);
        return customerFlux;
    }
    @GetMapping("/customers/v2/{id}")
    public  Mono<Customer>  customerByIdV2(@PathVariable Long id) {
       WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:8082").build();
        Mono<Customer> customerMono = webClient.get()
                .uri("/customers/{id}",id).retrieve().bodyToMono(Customer.class);
        return customerMono;
    }
    @GetMapping("/customers/v3")
    public List<Customer>  customerListV3() {
        return customerRestClient.getCustomers();
    }
    @GetMapping("/customers/v3/{id}")
    public Customer  customerByIdV3(@PathVariable Long id) {
        return customerRestClient.getCustomerById(id);
    }

    @GetMapping("/gql/customers")
    public Mono<List<Customer>> customerListGql(){
        HttpGraphQlClient graphQlClient=HttpGraphQlClient.builder()
                .url("http://localhost:8082/graphql")
                .build();
        var httpRequestDocument= """
                 query {
                     allCustomers{
                       name,email, id
                     }
                   }
                """;
        Mono<List<Customer>> customers = graphQlClient.document(httpRequestDocument)
                .retrieve("allCustomers")
                .toEntityList(Customer.class);
        return customers;
    }
    @GetMapping("/gql/customers/{id}")
    public Mono<Customer> customerByIdGql(@PathVariable Long id){
        HttpGraphQlClient graphQlClient=HttpGraphQlClient.builder()
                .url("http://localhost:8082/graphql")
                .build();
        var httpRequestDocument= """
                 query($id:Int) {
                    customerById(id:$id){
                      id, name, email
                    }
                  }
                """;
        Mono<Customer> customerById = graphQlClient.document(httpRequestDocument)
                .variable("id",id)
                .retrieve("customerById")
                .toEntity(Customer.class);
        return customerById;
    }

    @GetMapping("/soap/customers")
    public List<Customer> soapCustomers() {
        List<com.example.customerdataservice.web.Customer> soapCustomers = customerSoapService.customers();
        return soapCustomers.stream().map(customerMapper::fromSoapCustomer).collect(Collectors.toList());    }
    @GetMapping("/soap/customerById/{id}")
    public Customer customerByIdSoap(@PathVariable Long id){
        com.example.customerdataservice.web.Customer soapCustomer = customerSoapService.customerById(id);
        return customerMapper.fromSoapCustomer(soapCustomer);
    }


    @GetMapping("/grpc/customers")
    public List<Customer> grpcCustomers() {
        CustomerServiceOuterClass.GetAllCustomersRequest request = CustomerServiceOuterClass.GetAllCustomersRequest.newBuilder().build();
        CustomerServiceOuterClass.GetCustomersResponse allCustomersResponse = customerServiceBlockingStub.getAllCustomers(request);
        return allCustomersResponse.getCustomersList().stream().map(customerMapper::fromGrpcCustomer).collect(Collectors.toList());
    }

    @GetMapping("/grpc/customers/{id}")
    public Customer grpcCustomerById(@PathVariable Long id){
        CustomerServiceOuterClass.GetCustomerByIdRequest request =
                CustomerServiceOuterClass.GetCustomerByIdRequest.newBuilder()
                        .setCustomer(id)
                        .build();
        CustomerServiceOuterClass.GetCustomerByIdResponse response =
                customerServiceBlockingStub.getCustomerById(request);
        return customerMapper.fromGrpcCustomer(response.getCustomer());
    }

}
