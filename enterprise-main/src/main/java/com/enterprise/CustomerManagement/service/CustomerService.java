package com.enterprise.CustomerManagement.service;

import com.enterprise.CustomerManagement.model.dto.CustomerRequest;
import com.enterprise.CustomerManagement.model.dto.CustomerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerService {
    CustomerResponse createCustomer(CustomerRequest request);
    CustomerResponse getCustomer(Long id);
    Page<CustomerResponse> getAllCustomers(String firstName, String lastName, String email, Pageable pageable);
    CustomerResponse updateCustomer(Long id, CustomerRequest request);
    void deleteCustomer(Long id);
}