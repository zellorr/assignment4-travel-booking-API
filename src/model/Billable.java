package model;

public interface Billable {
    double calculatePrice();

    default double applyTax(double price) {  // Default
        return price * 1.1;
    }

    static double getDiscountedPrice(double price, double discount) {
        return price * (1 - discount / 100);
    }
}