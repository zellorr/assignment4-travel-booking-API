package service;

import exception.DuplicateResourceException;
import exception.InvalidInputException;
import exception.ResourceNotFoundException;
import model.Booking;
import model.BookingStatus;
import repository.interfaces.CrudRepository;
import service.interfaces.BookingServiceInterface;
import utils.SortingUtils;

import java.util.List;

public class BookingService implements BookingServiceInterface {
    private final CrudRepository<Booking> bookingRepository;

    public BookingService(CrudRepository<Booking> bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public void createBooking(Booking booking) {
        booking.validate();
        if (existsSimilarBooking(booking)) {
            throw new DuplicateResourceException("Booking", "similar details");
        }
        bookingRepository.create(booking);
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.getAll();
    }

    @Override
    public Booking getBookingById(int id) {
        Booking booking = bookingRepository.getById(id);
        if (booking == null) {
            throw new ResourceNotFoundException("Booking", id);
        }
        return booking;
    }

    @Override
    public void updateBooking(int id, Booking booking) {
        getBookingById(id);
        booking.validate();
        bookingRepository.update(id, booking);
    }

    @Override
    public void deleteBooking(int id) {
        Booking booking = getBookingById(id);
        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            throw new InvalidInputException("Cannot delete confirmed booking");
        }
        bookingRepository.delete(id);
    }

    @Override
    public void confirmBooking(int id) {
        Booking booking = getBookingById(id);
        booking.confirm();
        bookingRepository.update(id, booking);
    }

    @Override
    public void cancelBooking(int id) {
        Booking booking = getBookingById(id);
        booking.cancel();
        bookingRepository.update(id, booking);
    }

    @Override
    public List<Booking> getSortedBookingsByPrice() {
        List<Booking> bookings = getAllBookings();
        return SortingUtils.sortByPrice(bookings);
    }

    private boolean existsSimilarBooking(Booking booking) {
        // Логика проверки дубликатов (можно расширить, querying DB)
        return false;
    }
}