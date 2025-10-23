package com.arka.customer.service;

import com.arka.customer.dto.CustomerRequest;
import com.arka.customer.dto.CustomerResponse;
import com.arka.customer.entity.Customer;
import com.arka.customer.repository.CustomerRepository;
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
