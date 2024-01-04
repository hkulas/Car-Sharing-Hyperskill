package org.example;

import org.example.dao.CarDAO;
import org.example.dao.CarDAOImpl;
import org.example.dao.CompanyDAO;
import org.example.dao.CompanyDAOImpl;
import org.example.dao.CustomerDAO;
import org.example.dao.CustomerDAOImpl;
import org.example.model.Car;
import org.example.model.Company;
import org.example.model.Customer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static CompanyDAO companyDAO;
    private static CarDAO carDAO;
    private static CustomerDAO customerDAO;

    public static void main(String[] args) {
        String databaseFileName = "carsharing";

        for (int i = 0; i < args.length; i++) {
            if ("-databaseFileName".equals(args[i]) && i + 1 < args.length) {
                databaseFileName = args[i + 1];
                break;
            }
        }
        String url = "jdbc:h2:file:../task/src/carsharing/db/" + databaseFileName + ";DB_CLOSE_DELAY=-1";

        try (Connection connection = DriverManager.getConnection(url)) {
            Class.forName("org.h2.Driver");
            connection.setAutoCommit(true);

            createTablesIfNeeded(connection);
            companyDAO = new CompanyDAOImpl(connection);
            carDAO = new CarDAOImpl(connection);
            customerDAO = new CustomerDAOImpl(connection);

            while (true) {
                System.out.println("1. Log in as a manager\n2. Log in as a customer\n3. Create a customer\n0. Exit");

                try {
                    int choice = Integer.parseInt(scanner.nextLine());

                    switch (choice) {
                        case 1:
                            manageCompanies();
                            break;
                        case 2:
                            manageCustomers();
                            break;
                        case 3:
                            createCustomer();
                            break;
                        case 0:
                            return;
                        default:
                            System.out.println("Invalid input. Please enter a valid option number.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void manageCompanies() {
        while (true) {
            System.out.println("1. Company list");
            System.out.println("2. Create a company");
            System.out.println("0. Back");

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

    private static void createTablesIfNeeded(Connection connection) {
        try (Statement statement = connection.createStatement()) {


            String createTable = "CREATE TABLE IF NOT EXISTS COMPANY(ID INT AUTO_INCREMENT, NAME VARCHAR(20) NOT NULL UNIQUE, PRIMARY KEY (ID));";

            String sqlCreateCompany = "CREATE TABLE IF NOT EXISTS COMPANY (" +
                    "ID INT AUTO_INCREMENT PRIMARY KEY," +
                    "NAME VARCHAR(100) UNIQUE NOT NULL);";
            statement.execute(createTable);

            String createCar = "CREATE TABLE IF NOT EXISTS CAR(ID INT AUTO_INCREMENT, NAME VARCHAR(20) NOT NULL UNIQUE, COMPANY_ID INT NOT NULL, FLAG INT DEFAULT 0, PRIMARY KEY (ID), FOREIGN KEY (COMPANY_ID) REFERENCES COMPANY(ID));";

            String sqlCreateCar = "CREATE TABLE IF NOT EXISTS CAR (" +
                    "ID INT AUTO_INCREMENT PRIMARY KEY," +
                    "NAME VARCHAR(100) UNIQUE NOT NULL," +
                    "COMPANY_ID INT NOT NULL," +
                    "FOREIGN KEY (COMPANY_ID) REFERENCES COMPANY(ID));";
            statement.execute(createCar);
            String createCustomer = "CREATE TABLE IF NOT EXISTS CUSTOMER(ID INT AUTO_INCREMENT, NAME VARCHAR(20) NOT NULL UNIQUE, RENTED_CAR_ID INT, PRIMARY KEY (ID), FOREIGN KEY (RENTED_CAR_ID) REFERENCES CAR(ID));";


            String sqlCreateCustomer = "CREATE TABLE IF NOT EXISTS CUSTOMER (" +
                    "ID INT AUTO_INCREMENT PRIMARY KEY," +
                    "NAME VARCHAR(100) UNIQUE NOT NULL," +
                    "RENTED_CAR_ID INT," +
                    "FOREIGN KEY (RENTED_CAR_ID) REFERENCES CAR(ID));";
            statement.execute(createCustomer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void manageCustomers() {
        List<Customer> customers = customerDAO.findAll();
        if (customers.isEmpty()) {
            System.out.println("The customer list is empty!");
            return;
        }

        for (int i = 0; i < customers.size(); i++) {
            System.out.println((i + 1) + ". " + customers.get(i).getName());
        }
        System.out.println("0. Back");

        int choice = Integer.parseInt(scanner.nextLine());
        if (choice != 0) {
            manageCustomer(customers.get(choice - 1).getId());
        }
    }

    private static void createCustomer() {
        System.out.println("Enter the customer name:");
        String name = scanner.nextLine();
        Customer customer = new Customer();
        customer.setName(name);
        customerDAO.create(customer);
        System.out.println("The customer was added!");
    }

    private static void manageCustomer(int customerId) {
        Customer customer = customerDAO.findById(customerId);

        while (true) {
            System.out.println("1. Rent a car\n2. Return a rented car\n3. My rented car\n0. Back");
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1:
                    rentCar(customer);
                    break;
                case 2:
                    returnRentedCar(customer);
                    break;
                case 3:
                    showRentedCar(customer);
                    break;
                case 0:
                    return;
            }
        }
    }

    private static void rentCar(Customer customer) {
        if (customer.getRentedCarId() != null) {
            System.out.println("You've already rented a car!");
            return;
        }

        List<Company> companies = companyDAO.findAll();
        if (companies.isEmpty()) {
            System.out.println("The company list is empty!");
            return;
        }

        for (int i = 0; i < companies.size(); i++) {
            System.out.println((i + 1) + ". " + companies.get(i).getName());
        }
        System.out.println("0. Back");

        int companyChoice = Integer.parseInt(scanner.nextLine());
        if (companyChoice == 0) return;

        Company selectedCompany = companies.get(companyChoice - 1);
        List<Car> availableCars = carDAO.findAvailableCars(selectedCompany.getId());

        if (availableCars.isEmpty()) {
            System.out.println("No available cars in the '" + selectedCompany.getName() + "' company.");
            return;
        }

        for (int i = 0; i < availableCars.size(); i++) {
            System.out.println((i + 1) + ". " + availableCars.get(i).getName());
        }
        System.out.println("0. Back");

        int carChoice = Integer.parseInt(scanner.nextLine());
        if (carChoice == 0) return;

        Car selectedCar = availableCars.get(carChoice - 1);
        customer.setRentedCarId(selectedCar.getId());
        customerDAO.update(customer);

        System.out.println("You rented '" + selectedCar.getName() + "'");
    }

    private static void returnRentedCar(Customer customer) {
        if (customer.getRentedCarId() == null) {
            System.out.println("You didn't rent a car!");
            return;
        }

        customer.setRentedCarId(null);
        customerDAO.update(customer);
        System.out.println("You've returned a rented car!");
    }

    private static void showRentedCar(Customer customer) {
        if (customer.getRentedCarId() == null) {
            System.out.println("You didn't rent a car!");
            return;
        }

        Car rentedCar = carDAO.findById(customer.getRentedCarId());
        Company company = companyDAO.findById(rentedCar.getCompanyId());

        System.out.println("Your rented car:\n" + rentedCar.getName());
        System.out.println("Company:\n" + company.getName());
    }
}