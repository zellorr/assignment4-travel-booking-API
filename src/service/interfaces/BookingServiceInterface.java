package service.interfaces;

import model.Booking;

import java.util.List;

public interface BookingServiceInterface {
    void createBooking(Booking booking);
    List<Booking> getAllBookings();
    Booking getBookingById(int id);
    void updateBooking(int id, Booking booking);
    void deleteBooking(int id);
    void confirmBooking(int id);
    void cancelBooking(int id);
    List<Booking> getSortedBookingsByPrice();
}