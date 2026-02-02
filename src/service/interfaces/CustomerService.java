package service;

import exception.DuplicateResourceException;
import exception.ResourceNotFoundException;
import model.Customer;
import repository.interfaces.CrudRepository;
import service.interfaces.CustomerServiceInterface;

import java.util.List;

public class CustomerService implements CustomerServiceInterface {
    private final CrudRepository<Customer> customerRepository;

    public CustomerService(CrudRepository<Customer> customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public void createCustomer(Customer customer) {
        customer.validate();
        if (customerRepository.getAll().stream().anyMatch(c -> c.getEmail().equals(customer.getEmail()))) {
            throw new DuplicateResourceException("Customer", "email: " + customer.getEmail());
        }
        customerRepository.create(customer);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.getAll();
    }

    @Override
    public Customer getCustomerById(int id) {
        Customer customer = customerRepository.getById(id);
        if (customer == null) {
            throw new ResourceNotFoundException("Customer", id);
        }
        return customer;
    }

    @Override
    public void updateCustomer(int id, Customer customer) {
        getCustomerById(id);
        customer.validate();
        customerRepository.update(id, customer);
    }

    @Override
    public void deleteCustomer(int id) {
        getCustomerById(id);
        customerRepository.delete(id);
    }
}