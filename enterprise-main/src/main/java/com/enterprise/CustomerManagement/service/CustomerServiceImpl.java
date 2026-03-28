package com.enterprise.CustomerManagement.service;

import com.enterprise.CustomerManagement.jms.NotificationProducer;
import com.enterprise.CustomerManagement.model.Customer;
import com.enterprise.CustomerManagement.model.dto.CustomerRequest;
import com.enterprise.CustomerManagement.model.dto.CustomerResponse;
import com.enterprise.CustomerManagement.repository.CustomerRepository;
import com.enterprise.CustomerManagement.repository.CustomerSpecification;
import com.enterprise.CustomerManagement.mapper.CustomerMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final NotificationProducer notificationProducer;

    @Override
    @CacheEvict(value = {"customers", "allCustomers"}, allEntries = true)
    public CustomerResponse createCustomer(CustomerRequest request) {
        if (customerRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Клиент с email " + request.email() + " уже существует");
        }
        Customer customer = customerMapper.toEntity(request);
        Customer savedCustomer = customerRepository.save(customer);
        notificationProducer.sendWelcomeEmail(savedCustomer.getId(), savedCustomer.getEmail(), savedCustomer.getFirstName());
        return customerMapper.toResponse(savedCustomer);
    }

    @Override
    @Cacheable(value = "customers", key = "#id")
    public CustomerResponse getCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Клиент с id " + id + " не найден"));
        return customerMapper.toResponse(customer);
    }

    @Override
    @Cacheable(value = "allCustomers")
    public Page<CustomerResponse> getAllCustomers(String firstName, String lastName, String email, Pageable pageable) {
        Specification<Customer> spec = Specification
                .where(CustomerSpecification.hasFirstName(firstName))
                .and(CustomerSpecification.hasLastName(lastName))
                .and(CustomerSpecification.hasEmail(email));
        return customerRepository.findAll(spec, pageable)
                .map(customerMapper::toResponse);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"customers", "allCustomers"}, allEntries = true)
    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Клиент с id " + id + " не найден"));
        if (!customer.getEmail().equals(request.email()) &&
                customerRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Клиент с email " + request.email() + " уже существует");
        }
        customerMapper.updateEntity(customer, request);
        Customer updatedCustomer = customerRepository.save(customer);
        return customerMapper.toResponse(updatedCustomer);
    }

    @Override
    @CacheEvict(value = {"customers", "allCustomers"}, allEntries = true)
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new NoSuchElementException("Клиент с id " + id + " не найден");
        }
        customerRepository.deleteById(id);
    }
}