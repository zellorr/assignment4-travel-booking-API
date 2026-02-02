import controller.BookingController;
import controller.CustomerController;
import exception.InvalidInputException;
import model.*;
import service.BookingService;
import service.CustomerService;
import repository.BookingRepository;
import repository.CustomerRepository;
import utils.DatabaseConnection;
import utils.ReflectionUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        if (!DatabaseConnection.testConnection()) {
            System.err.println("Database connection failed.");
            return;
        }

        // DIP: Inject dependencies
        CustomerService customerService = new CustomerService(new CustomerRepository());
        BookingService bookingService = new BookingService(new BookingRepository());

        CustomerController customerController = new CustomerController(customerService);
        BookingController bookingController = new BookingController(bookingService);

        // Demo Customer CRUD with duplicate check
        Customer candidate = new Customer(0, "Test User", "test@email.com", "123456", "PASS123");
        ensureCustomerExists(customerController, candidate);

        // Demo Booking CRUD + Polymorphism
        FlightBooking flight = new FlightBooking(0, LocalDate.now().plusDays(10), 500, BookingStatus.PENDING, candidate.getId(), "FL123", "NYC", "LAX", SeatClass.ECONOMY);
        bookingController.createBooking(flight);

        List<Booking> bookings = bookingController.getAllBookings();
        bookings.forEach(b -> System.out.println(b.getBookingType() + ": " + b.getBookingDetails()));  // Polymorphism

        // Lambda sorting
        List<Booking> sorted = bookingController.getSortedBookingsByPrice();
        System.out.println("Sorted Bookings:");
        for (Booking b : sorted) {
            System.out.println(" - " + b);
        }

        // Interface default/static
        Validatable.printValidationMessage(flight);  // Static
        System.out.println("Is valid: " + flight.isValid());  // Default

        // Reflection
        ReflectionUtils.inspect(flight);

        // Composition
        TravelPackage pkg = new TravelPackage(1, "Test Package", candidate.getId(), 5.0);
        pkg.addBooking(flight);
        System.out.println(pkg.getPackageSummary());

        // Exceptions (trigger in service)
        try {
            bookingController.deleteBooking(flight.getId());  // If confirmed, throws InvalidInputException
        } catch (InvalidInputException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    private static void ensureCustomerExists(CustomerController controller, Customer candidate) {
        List<Customer> existing = controller.getAllCustomers();
        Optional<Customer> found = existing.stream()
                .filter(c -> c.getEmail().equalsIgnoreCase(candidate.getEmail()))
                .findFirst();
        if (found.isPresent()) {
            Customer e = found.get();
            candidate.setId(e.getId());
            System.out.println("Customer already exists: " + e);
        } else {
            controller.createCustomer(candidate);
            System.out.println("Customer created successfully: " + candidate);
        }
    }
}