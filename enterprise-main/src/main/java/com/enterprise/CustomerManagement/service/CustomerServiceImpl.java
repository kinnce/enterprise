package com.enterprise.CustomerManagement.service;

import com.enterprise.CustomerManagement.model.Customer;
import com.enterprise.CustomerManagement.model.dto.CustomerRequest;
import com.enterprise.CustomerManagement.model.dto.CustomerResponse;
import com.enterprise.CustomerManagement.repository.CustomerRepository;
import com.enterprise.CustomerManagement.mapper.CustomerMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public CustomerResponse createCustomer(CustomerRequest request) {
        // Бизнес-логика: проверка уникальности email
        if (customerRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Клиент с email " + request.email() + " уже существует");
        }

        Customer customer = customerMapper.toEntity(request);
        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toResponse(savedCustomer);
    }

    @Override
    public CustomerResponse getCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Клиент с id " + id + " не найден"));
        return customerMapper.toResponse(customer);
    }

    @Override
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(customerMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Клиент с id " + id + " не найден"));

        // Бизнес-логика: если email меняется, проверяем его уникальность
        if (!customer.getEmail().equals(request.email()) &&
                customerRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Клиент с email " + request.email() + " уже существует");
        }

        customerMapper.updateEntity(customer, request);
        Customer updatedCustomer = customerRepository.save(customer);
        return customerMapper.toResponse(updatedCustomer);
    }

    @Override
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new NoSuchElementException("Клиент с id " + id + " не найден");
        }
        customerRepository.deleteById(id);
    }
}