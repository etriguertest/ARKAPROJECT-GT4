package com.grupo4.ms_authentication.service;

import com.grupo4.ms_authentication.dto.CustomerRequest;
import com.grupo4.ms_authentication.dto.CustomerResponse;
import com.grupo4.ms_authentication.entity.Customer;
import com.grupo4.ms_authentication.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerResponse createCustomer(CustomerRequest customerRequest){
        if(customerRepository.findByEmail(customerRequest.getEmail()).isPresent()){
            throw new RuntimeException("Client already exist");
        }else{
            Customer customer = Customer.builder()
                    .name(customerRequest.getName())
                    .email(customerRequest.getEmail())
                    .build();
            customerRepository.save(customer);

            CustomerResponse customerResponse = new CustomerResponse();
            customerResponse.setId(customer.getId());
            customerResponse.setName(customer.getName());
            customerResponse.setEmail(customer.getEmail());

            return customerResponse;
        }
    }


    public List<Customer> getAllCustomers(){
        return customerRepository.findAll();
    }


    public Optional<Customer> getCustomerById(Long id){
        return customerRepository.findById(id);
    }
}
