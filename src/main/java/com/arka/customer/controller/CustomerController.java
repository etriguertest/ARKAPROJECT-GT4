package com.arka.customer.controller;

import com.arka.customer.dto.CustomerRequest;
import com.arka.customer.dto.CustomerResponse;
import com.arka.customer.entity.Customer;
import com.arka.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping()
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody CustomerRequest customerRequest){
        return new ResponseEntity<>(customerService.createCustomer(customerRequest), HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<List<Customer>> getAllCustomers(){
        return new ResponseEntity<List<Customer>>(customerService.getAllCustomers(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Customer>> getCustomerById(@PathVariable("id") Long id){
        return new ResponseEntity<Optional<Customer>>(customerService.getCustomerById(id), HttpStatus.OK);
    }
}
