package repository;

import exception.DatabaseOperationException;
import exception.DuplicateResourceException;
import exception.ResourceNotFoundException;
import model.Customer;
import repository.interfaces.CrudRepository;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerRepository implements CrudRepository<Customer> {

    @Override
    public void create(Customer customer) {
        String sql = "INSERT INTO customers (name, email, phone, passport_number) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, customer.getName());
            ps.setString(2, customer.getEmail());
            ps.setString(3, customer.getPhone());
            ps.setString(4, customer.getPassportNumber());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    customer.setId(keys.getInt(1));
                } else {
                    throw new DatabaseOperationException("Creating customer failed, no ID obtained");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("create", "Customer", e);
        }
    }

    @Override
    public List<Customer> getAll() {
        String sql = "SELECT * FROM customers ORDER BY id";
        List<Customer> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapCustomer(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("getAll", "Customer", e);
        }
        return list;
    }

    @Override
    public Customer getById(int id) {
        String sql = "SELECT * FROM customers WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return mapCustomer(rs);
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("getById", "Customer", e);
        }
    }

    @Override
    public void update(int id, Customer customer) {
        String sql = "UPDATE customers SET name = ?, email = ?, phone = ?, passport_number = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customer.getName());
            ps.setString(2, customer.getEmail());
            ps.setString(3, customer.getPhone());
            ps.setString(4, customer.getPassportNumber());
            ps.setInt(5, id);
            ps.executeUpdate();
            customer.setId(id);
        } catch (SQLException e) {
            throw new DatabaseOperationException("update", "Customer", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM customers WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseOperationException("delete", "Customer", e);
        }
    }

    private Customer mapCustomer(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("passport_number")
        );
    }
}