package com.milktea.service;

import com.milktea.entity.Customer;
import java.util.List;

public interface CustomerService {

    List<Customer> searchCustomers(String keyword);

    List<Customer> getAllCustomers();

    Customer getCustomerById(Integer id);

    Customer saveCustomer(Customer customer);

    void deleteCustomer(Integer id);
}