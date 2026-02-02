package controller;

import model.Customer;
import service.interfaces.CustomerServiceInterface;

import java.util.List;

public class CustomerController {
    private final CustomerServiceInterface customerService;

    public CustomerController(CustomerServiceInterface customerService) {
        this.customerService = customerService;
    }

    public void createCustomer(Customer customer) {
        customerService.createCustomer(customer);
    }

    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    public Customer getCustomerById(int id) {
        return customerService.getCustomerById(id);
    }

    public void updateCustomer(int id, Customer customer) {
        customerService.updateCustomer(id, customer);
    }

    public void deleteCustomer(int id) {
        customerService.deleteCustomer(id);
    }
}