package org.example.dao;

import org.example.model.Customer;

import java.util.List;

public interface CustomerDAO {
    void create(Customer customer);

    List<Customer> findAll();

    Customer findById(int id);

    void update(Customer customer);
}

