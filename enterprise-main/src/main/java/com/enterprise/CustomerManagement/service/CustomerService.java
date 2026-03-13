package com.enterprise.CustomerManagement.service;

import com.enterprise.CustomerManagement.model.dto.CustomerRequest;
import com.enterprise.CustomerManagement.model.dto.CustomerResponse;
import java.util.List;

public interface CustomerService {
    CustomerResponse createCustomer(CustomerRequest request);
    CustomerResponse getCustomer(Long id);
    List<CustomerResponse> getAllCustomers();
    CustomerResponse updateCustomer(Long id, CustomerRequest request);
    void deleteCustomer(Long id);
}