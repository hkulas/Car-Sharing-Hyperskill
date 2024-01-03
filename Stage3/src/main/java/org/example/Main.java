package org.example;

import org.example.dao.CarDAO;
import org.example.dao.CarDAOImpl;
import org.example.dao.CompanyDAO;
import org.example.dao.CompanyDAOImpl;
import org.example.model.Car;
import org.example.model.Company;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static CompanyDAO companyDAO;
    private static CarDAO carDAO;

    public static void main(String[] args) {
        String url = "jdbc:h2:./src/carsharing/db/carsharing";

        try (Connection connection = DriverManager.getConnection(url)) {
            connection.setAutoCommit(true);
            createTablesIfNeeded(connection);

            companyDAO = new CompanyDAOImpl(connection);
            carDAO = new CarDAOImpl(connection);

            while (true) {
                System.out.println("1. Log in as a manager\n0. Exit");
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        manageCompanies();
                        break;
                    case 0:
                        return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createTablesIfNeeded(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            String sqlCreateCompany = "CREATE TABLE IF NOT EXISTS COMPANY (" +
                    "ID INT AUTO_INCREMENT PRIMARY KEY," +
                    "NAME VARCHAR(100) UNIQUE NOT NULL);";
            statement.execute(sqlCreateCompany);

            String sqlCreateCar = "CREATE TABLE IF NOT EXISTS CAR (" +
                    "ID INT AUTO_INCREMENT PRIMARY KEY," +
                    "NAME VARCHAR(100) UNIQUE NOT NULL," +
                    "COMPANY_ID INT NOT NULL," +
                    "FOREIGN KEY (COMPANY_ID) REFERENCES COMPANY(ID));";
            statement.execute(sqlCreateCar);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void manageCompanies() {
        while (true) {
            System.out.println("1. Company list\n2. Create a company\n0. Back");
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    listCompanies();
                    break;
                case 2:
                    createCompany();
                    break;
                case 0:
                    return;
            }
        }
    }

    private static void listCompanies() {
        List<Company> companies = companyDAO.findAll();
        if (companies.isEmpty()) {
            System.out.println("The company list is empty!");
        } else {
            for (int i = 0; i < companies.size(); i++) {
                System.out.println((i + 1) + ". " + companies.get(i).getName());
            }
            System.out.println("0. Back");
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice != 0) {
                manageCompany(companies.get(choice - 1).getId());
            }
        }
    }

    private static void createCompany() {
        System.out.println("Enter the company name:");
        String name = scanner.nextLine();
        Company company = new Company();
        company.setName(name);
        companyDAO.create(company);
        System.out.println("The company was created!");
    }

    private static void manageCompany(int companyId) {
        Company company = companyDAO.findById(companyId);
        System.out.println("'" + company.getName() + "' company:");

        while (true) {
            System.out.println("1. Car list\n2. Create a car\n0. Back");
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    listCars(companyId);
                    break;
                case 2:
                    createCar(companyId);
                    break;
                case 0:
                    return;
            }
        }
    }

    private static void listCars(int companyId) {
        List<Car> cars = carDAO.findByCompanyId(companyId);
        if (cars.isEmpty()) {
            System.out.println("The car list is empty!");
        } else {
            for (int i = 0; i < cars.size(); i++) {
                System.out.println((i + 1) + ". " + cars.get(i).getName());
            }
        }
    }

    private static void createCar(int companyId) {
        System.out.println("Enter the car name:");
        String name = scanner.nextLine();
        Car car = new Car();
        car.setName(name);
        car.setCompanyId(companyId);
        carDAO.create(car);
        System.out.println("The car was added!");
    }
}
