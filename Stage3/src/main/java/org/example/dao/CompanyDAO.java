package org.example.dao;

import org.example.model.Company;

import java.util.List;

public interface CompanyDAO {
    void create(Company company);
    List<Company> findAll();
    Company findById(int id);
}
