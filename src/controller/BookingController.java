package controller;

import model.Booking;
import service.interfaces.BookingServiceInterface;

import java.util.List;

public class BookingController {
    private final BookingServiceInterface bookingService;

    public BookingController(BookingServiceInterface bookingService) {
        this.bookingService = bookingService;
    }

    public void createBooking(Booking booking) {
        bookingService.createBooking(booking);
    }

    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    public Booking getBookingById(int id) {
        return bookingService.getBookingById(id);
    }

    public void updateBooking(int id, Booking booking) {
        bookingService.updateBooking(id, booking);
    }

    public void deleteBooking(int id) {
        bookingService.deleteBooking(id);
    }

    public void confirmBooking(int id) {
        bookingService.confirmBooking(id);
    }

    public void cancelBooking(int id) {
        bookingService.cancelBooking(id);
    }

    public List<Booking> getSortedBookingsByPrice() {
        return bookingService.getSortedBookingsByPrice();
    }
}