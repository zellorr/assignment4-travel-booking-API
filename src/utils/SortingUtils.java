package utils;

import model.Booking;

import java.util.List;
import java.util.stream.Collectors;

public class SortingUtils {
    public static List<Booking> sortByPrice(List<Booking> bookings) {
        return bookings.stream()
                .sorted((b1, b2) -> Double.compare(b1.getTotalPrice(), b2.getTotalPrice()))
                .collect(Collectors.toList());
    }
}