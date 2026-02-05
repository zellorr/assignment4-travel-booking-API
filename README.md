Project Overview
This application manages travel bookings (Flight and Hotel) with full CRUD operations, customer management, and travel packages. Built using SOLID principles and advanced Java features including generics, lambdas, and reflection.
Main Features

✅ Create and manage two booking types (Flight, Hotel)
✅ Travel package system with multiple bookings
✅ Booking status and price calculation
✅ Customer validation and management
✅ Advanced sorting and filtering
✅ Polymorphic behavior for bookings

SOLID Principles

Single Responsibility Principle (SRP)
Each class has ONE clear responsibility:Main (Controller Layer)
Responsibility: Handle user input/output only
Does NOT: Contain validation or database logic
Javapublic class Main {
    private static final BookingService bookingService = new BookingService();
    
    private static void demonstrateBookingCreation() {
        // Only handles user input/demo
        FlightBooking flight = new FlightBooking(...);
        
        // Delegates to service
        bookingService.createBooking(flight);
    }
}BookingService
Responsibility: Business logic and validation only
Does NOT: Handle database operations or user interaction
Javapublic class BookingService {
    private BookingRepository bookingRepository;
    
    public void createBooking(Booking booking) {
        // Validation (SRP: business rules only)
        booking.validate();
        
        // Delegates to repository
        bookingRepository.create(booking);
    }
}BookingRepository
Responsibility: Database operations only
Does NOT: Contain validation or business logic
Javapublic class BookingRepository {
    public void create(Booking booking) {
        // Only database operations
        String sql = "INSERT INTO bookings ...";
        // JDBC execution
    }
}
Open-Closed Principle (OCP)
System is open for extension, closed for modification.
Example: Adding new booking typesJava// Abstract base - closed for modification
abstract class Booking implements Validatable, Billable {
    abstract String getBookingType();
}

// Open for extension - add new types without changing base
class FlightBooking extends Booking {
    String getBookingType() { return "FLIGHT"; }
}

class HotelBooking extends Booking {
    String getBookingType() { return "HOTEL"; }
}

// NEW: Can add TrainBooking without modifying existing code
class TrainBooking extends Booking {
    String getBookingType() { return "TRAIN"; }
}No existing code needs to change when adding TrainBooking!
Liskov Substitution Principle (LSP)
Subclasses can replace parent class without breaking functionality.Java// Parent reference, child object
Booking flight = new FlightBooking(...);
Booking hotel = new HotelBooking(...);

// Works identically - LSP satisfied
flight.getBookingDetails();  // Flight details
hotel.getBookingDetails();   // Hotel details

// Can store in same collection
List<Booking> bookings = Arrays.asList(flight, hotel);
for (Booking b : bookings) {
    b.confirm();  // Polymorphic behavior
}All subclasses behave correctly when used through parent type.
Interface Segregation Principle (ISP)
Clients should not depend on interfaces they don't use.
Good: Small, focused interfacesJava// Small interface - only validation
interface Validatable {
    void validate();
}

// Small interface - only billing
interface Billable {
    double calculatePrice();
}

// Classes implement only what they need
class Booking implements Validatable, Billable {
    // Implements both
}

class Customer implements Validatable {
    // Only validation, ISP: Not forced to implement billing
}Bad example (fat interface):Java// BAD: Forces all entities to implement unused methods
interface Entity {
    void validate();
    double calculatePrice();  // Customers don't need pricing!
}
Dependency Inversion Principle (DIP)
High-level modules depend on abstractions, not concrete classes.
Service depends on Repository abstractionJavapublic class BookingService {
    // DIP: Depends on abstraction (could be interface)
    private BookingRepository repository;
    
    public BookingService(BookingRepository repository) {
        this.repository = repository;  // Injected
    }
    
    public Booking getById(int id) {
        return repository.getById(id);  // Uses abstraction
    }
}Benefits:
Easy to swap implementations (e.g., MockRepository for testing)
Service doesn't know about concrete DB details
Loose coupling between layers


Advanced OOP Features

Generics
Generic Repository handling (adapted) and collectionsJava// Generic list in TravelPackage
public class TravelPackage {
    private List<Booking> bookings = new ArrayList<>();  // <Booking> for type safety
    
    public void addBooking(Booking booking) {
        bookings.add(booking);
    }
}Benefits:
Type safety at compile time
Code reuse for different entities
No casting needed

Lambda Expressions
Sorting and filtering with lambdasJavapublic class SortingUtils {
    // Sort by date
    public static void sortByDate(List<Booking> bookings) {
        bookings.sort((b1, b2) -> b1.getBookingDate().compareTo(b2.getBookingDate()));
    }
    
    // Filter by status
    public static List<Booking> filterByStatus(List<Booking> bookings, BookingStatus status) {
        return bookings.stream()
            .filter(b -> b.getStatus() == status)
            .toList();
    }
}Usage:JavaList<Booking> bookings = bookingService.getAllBookings();

// Lambda sorting
SortingUtils.sortByDate(bookings);
bookings.forEach(System.out::println);

// Lambda filtering
List<Booking> pending = SortingUtils.filterByStatus(bookings, BookingStatus.PENDING);
Reflection (RTTI)
ReflectionUtils class for runtime inspectionJavapublic class ReflectionUtils {
    public static void inspectObject(Object obj) {
        Class<?> clazz = obj.getClass();
        
        System.out.println("Class Name: " + clazz.getSimpleName());
        System.out.println("Package: " + clazz.getPackageName());
        
        // Get superclass
        if (clazz.getSuperclass() != null) {
            System.out.println("Superclass: " + clazz.getSuperclass().getSimpleName());
        }
        
        // Get interfaces
        for (Class<?> iface : clazz.getInterfaces()) {
            System.out.println("Interface: " + iface.getSimpleName());
        }
        
        // Get fields
        for (Field field : clazz.getDeclaredFields()) {
            System.out.println("Field: " + field.getType().getSimpleName() + " " + field.getName());
        }
        
        // Get methods
        for (Method method : clazz.getDeclaredMethods()) {
            System.out.println("Method: " + method.getName());
        }
    }
}Example output:text========== REFLECTION INSPECTION ==========
Class Name: FlightBooking
Package: model
Superclass: Booking
Interfaces:
  - Validatable
  - Billable
Fields:
  - String flightNumber
  - String origin
  - String destination
  - SeatClass seatClass
Methods:
  - getBookingType
  - getBookingDetails
  - calculatePrice
  - validate
==========================================
Interface Default and Static Methods
Billable Interface with default/static methodsJavapublic interface Billable {
    double calculatePrice();
    
    // DEFAULT method - provides default implementation
    default double calculateWithDiscount(double discount) {
        return calculatePrice() * (1 - discount / 100);
    }
    
    // STATIC method - utility
    static double getTaxRate() {
        return 10.0;
    }
    
    static boolean isValidPrice(double price) {
        return price > 0;
    }
}Usage:Java// Static methods
double tax = Billable.getTaxRate();  // 10.0
boolean valid = Billable.isValidPrice(750);  // true

// Default method
FlightBooking flight = new FlightBooking(...);
double discounted = flight.calculateWithDiscount(10);  // Applies 10% discount

Architecture
Three-Layer Architecture
![Uploading image.png…]()

Request Flow Example
Creating a Booking:

User Input (Controller)
└→ Main.demonstrateBookingCreation()
├→ Collects: date, price, details
└→ Creates: new FlightBooking(...)
Validation (Service)
└→ bookingService.createBooking(flight)
├→ Validates: date, price
├→ Checks: business rules
└→ Calls: repository.create()
Database (Repository)
└→ bookingRepository.create(flight)
├→ INSERT INTO bookings
├→ INSERT INTO flight_bookings
└→ Returns: generated ID
Response
└→ Success message + booking details

Database Design
Schema
customers table
SQLCREATE TABLE customers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(50) NOT NULL,
    passport_number VARCHAR(50) UNIQUE NOT NULL
);
bookings table
SQLCREATE TABLE bookings (
    id SERIAL PRIMARY KEY,
    booking_date DATE NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    customer_id INTEGER REFERENCES customers(id) ON DELETE CASCADE,
    type VARCHAR(20) NOT NULL
);
character_attributes table (Polymorphic storage)
flight_bookings
SQLCREATE TABLE flight_bookings (
    booking_id INTEGER PRIMARY KEY REFERENCES bookings(id) ON DELETE CASCADE,
    flight_number VARCHAR(20) NOT NULL,
    origin VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL,
    seat_class VARCHAR(20) NOT NULL
);
hotel_bookings
SQLCREATE TABLE hotel_bookings (
    booking_id INTEGER PRIMARY KEY REFERENCES bookings(id) ON DELETE CASCADE,
    hotel_name VARCHAR(255) NOT NULL,
    room_type VARCHAR(20) NOT NULL,
    nights INTEGER NOT NULL CHECK (nights > 0)
);
Entity Relationships
textCustomer (1) ────< (N) Booking
         "has many"

TravelPackage (1) ────< (N) Booking
         "has many"
Usage Examples
Create a Booking
text========== CREATE BOOKING ==========
Select type:
1. Flight
2. Hotel
Choice: 1

Enter date: 2024-12-01
Enter price: 750.00

--- Flight Stats ---
Flight Number: AA123
Origin: New York
Destination: Tokyo
Seat Class: BUSINESS

✓ SUCCESS: Booking created with ID: 1
Polymorphism Demo
text========== POLYMORPHISM DEMONSTRATION ==========

Booking: AA123 (FLIGHT)
Details: Flight AA123 from New York to Tokyo (BUSINESS class)
Price: 1875.00

Booking: Grand Hyatt (HOTEL)
Details: Hotel: Grand Hyatt, DELUXE room for 4 night(s)
Price: 800.00
Reflection Output
text========== REFLECTION INSPECTION ==========
Class Name: FlightBooking
Superclass: Booking
Interfaces:
  - Validatable
  - Billable
Fields:
  - String flightNumber
  - String origin
  - String destination
  - SeatClass seatClass
Methods:
  - getBookingType
  - getBookingDetails
  - calculatePrice
  - validate
==========================================
Lambda Sorting
textSorted by Date:
  Booking #1 - 2024-11-01
  Booking #2 - 2024-12-01

Sorted by Price (descending):
  Booking #2 - Price: 800.00
  Booking #1 - Price: 750.00
Key Features
CRUD Operations

✅ Create entities with validation
✅ Read all or by ID
✅ Update entities
✅ Delete with foreign key checks

Booking Types

✈️ Flight - Route and seat class based
🏨 Hotel - Room type and nights based

Travel Package System

Create packages
Add/remove bookings
Calculate total with discount

Advanced Features

Generic repository pattern (adapted)
Lambda sorting and filtering
Reflection inspection
Interface default/static methods

Project Structure
<img width="356" height="659" alt="image" src="https://github.com/user-attachments/assets/2e1df3df-896e-4b55-8901-7722c7aa457d" />

Exception Handling
Exception Hierarchy
textRuntimeException
    ├── InvalidInputException
    │       └── DuplicateResourceException
    ├── ResourceNotFoundException
    └── DatabaseOperationException
Usage Examples
Java// Validation error
try {
    FlightBooking invalid = new FlightBooking(..., LocalDate.now().minusDays(1), ...);
    invalid.validate();
} catch (IllegalStateException e) {
    System.out.println("✗ " + e.getMessage());
    // Output: Booking date cannot be in the past
}

// Duplicate
try {
    customerService.createCustomer(duplicateCustomer);
} catch (DuplicateResourceException e) {
    System.out.println("✗ " + e.getMessage());
    // Output: Duplicate Customer found with email: alice.w@email.com
}

// Not found
try {
    bookingService.getById(9999);
} catch (ResourceNotFoundException e) {
    System.out.println("✗ " + e.getMessage());
    // Output: Booking with ID 9999 not found
}
What I Learned
SOLID Principles

How to apply SRP to create focused, maintainable classes
Using OCP to make code extensible without modification
LSP ensures subclasses work correctly as parent types
ISP creates small, focused interfaces
DIP reduces coupling between layers

Advanced Java Features

Generics provide type safety and code reuse
Lambdas make code more concise and functional
Reflection enables runtime type inspection
Interface evolution with default/static methods

Architecture Benefits

Layered architecture separates concerns clearly
Each layer has a single, well-defined purpose
Easy to test, modify, and extend
Changes in one layer don't affect others

Challenges Faced

Ensuring polymorphic database storage for subclasses
Balancing validation between models and services
Refactoring Assignment 3 without breaking functionality
Implementing DIP with constructor injection

Execution Instructions

Requirements: Java 17+, PostgreSQL, JDBC driver
How to compile and run:
Clone repo
Setup DB: Run resources/schema.sql
Compile: javac -d bin src/**/*.java
Run: java -cp bin Main

DB connection: Update DatabaseConnection.java with URL/user/password


Commit Storyline Example
init: migrate project from milestone1 to milestone2 structure
refactor(oop): apply abstract entity + subclasses (OCP/LSP)
feat(interface): add Validatable interface with default/static methods
feat(generics): implement generic lists in TravelPackage
feat(utils): add SortingUtils using lambdas
feat(utils): add ReflectionUtils (RTTI)
refactor(service): apply SRP, DIP, validation improvements
refactor(controller): simplify API, remove business logic
docs: update README with SOLID and advanced OOP documentation
release: submission milestone2
