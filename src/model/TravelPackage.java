package model;

import java.util.ArrayList;
import java.util.List;

public class TravelPackage implements Validatable, Billable {
    private int id;
    private String packageName;
    private int customerId;
    private List<Booking> bookings;
    private double discountPercentage;

    public TravelPackage(int id, String packageName, int customerId, double discountPercentage) {
        this.id = id;
        this.packageName = packageName;
        this.customerId = customerId;
        this.bookings = new ArrayList<>();
        this.discountPercentage = discountPercentage;
    }

    public void addBooking(Booking booking) {
        if (booking == null) {
            throw new IllegalArgumentException("Booking cannot be null");
        }
        if (booking.getCustomerId() != this.customerId) {
            throw new IllegalArgumentException("Booking must belong to the same customer");
        }
        this.bookings.add(booking);
    }

    public void removeBooking(Booking booking) {
        this.bookings.remove(booking);
    }

    @Override
    public double calculatePrice() {
        double totalPrice = 0;
        for (Booking booking : bookings) {
            totalPrice += booking.calculatePrice();
        }
        return totalPrice * (1 - discountPercentage / 100.0);
    }

    @Override
    public void validate() {
        if (packageName == null || packageName.trim().isEmpty()) {
            throw new IllegalArgumentException("Package name cannot be empty");
        }
        if (customerId <= 0) {
            throw new IllegalArgumentException("Customer ID must be valid");
        }
        if (discountPercentage < 0 || discountPercentage > 100) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100");
        }
        if (bookings.isEmpty()) {
            throw new IllegalArgumentException("Package must contain at least one booking");
        }
    }

    public void confirmAllBookings() {
        for (Booking booking : bookings) {
            booking.confirm();
        }
        System.out.println("All bookings in package '" + packageName + "' confirmed");
    }

    public void cancelAllBookings() {
        for (Booking booking : bookings) {
            booking.cancel();
        }
        System.out.println("All bookings in package '" + packageName + "' cancelled");
    }

    public String getPackageSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append(String.format("Package: %s (Customer ID: %d)\n", packageName, customerId));
        summary.append(String.format("Total Bookings: %d\n", bookings.size()));
        summary.append(String.format("Discount: %.2f%%\n", discountPercentage));
        summary.append(String.format("Total Price: $%.2f\n", calculatePrice()));
        summary.append("Bookings:\n");
        for (Booking booking : bookings) {
            summary.append(" - ").append(booking.getBookingDetails()).append("\n");
        }
        return summary.toString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be greater than 0");
        }
        this.id = id;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        if (packageName == null || packageName.trim().isEmpty()) {
            throw new IllegalArgumentException("Package name cannot be empty");
        }
        this.packageName = packageName;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        if (customerId <= 0) {
            throw new IllegalArgumentException("Customer ID must be greater than 0");
        }
        this.customerId = customerId;
    }

    public List<Booking> getBookings() {
        return new ArrayList<>(bookings);
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        if (discountPercentage < 0 || discountPercentage > 100) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100");
        }
        this.discountPercentage = discountPercentage;
    }

    @Override
    public String toString() {
        return String.format("TravelPackage[id=%d, name=%s, bookings=%d, discount=%.2f%%, totalPrice=%.2f]",
                id, packageName, bookings.size(), discountPercentage, calculatePrice());
    }
}
