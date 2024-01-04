package org.example.dao;

import org.example.model.Car;

import java.util.List;

public interface CarDAO {
    void create(Car car);

    List<Car> findByCompanyId(int companyId);

    List<Car> findAvailableCars(int companyId);

    Car findById(int carId);
}
