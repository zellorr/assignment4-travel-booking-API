package service.interfaces;

import model.Customer;

import java.util.List;

public interface CustomerServiceInterface {
    void createCustomer(Customer customer);
    List<Customer> getAllCustomers();
    Customer getCustomerById(int id);
    void updateCustomer(int id, Customer customer);
    void deleteCustomer(int id);
}