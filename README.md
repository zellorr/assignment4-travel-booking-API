# Travel Booking System - SOLID & Advanced OOP

Java-based travel booking system demonstrating SOLID principles, generics, lambdas, and layered architecture.

## SOLID Principles

### Single Responsibility (SRP)
- **Controller**: User input only
- **Service**: Business logic & validation
- **Repository**: Database operations only

### Open-Closed (OCP)
- New booking types extend `Booking` without modifying existing code
- `CrudRepository<T>` interface extensible for any entity

### Liskov Substitution (LSP)
- `FlightBooking` and `HotelBooking` fully substitute `Booking`
```java
List<Booking> bookings = getAll();
bookings.forEach(b -> b.confirm()); // Works for all types
```

### Interface Segregation (ISP)
- `Validatable`: validation only
- `Billable`: pricing only
- `CrudRepository<T>`: CRUD only

### Dependency Inversion (DIP)
```java
public class BookingService {
    private final CrudRepository<Booking> repo; // Interface, not concrete
}
```

## Advanced Features

### Generics
```java
public interface CrudRepository<T> {
    void create(T entity);
    List<T> getAll();
}
```

### Lambdas
```java
bookings.stream()
    .sorted((b1, b2) -> Double.compare(b1.getPrice(), b2.getPrice()))
    .collect(Collectors.toList());
```

### Reflection
```java
ReflectionUtils.inspect(flight);
// Output: Class, Fields, Methods
```

### Interface Features
```java
// Default method
default boolean isValid() {
    try { validate(); return true; }
    catch (Exception e) { return false; }
}

// Static method
static void printValidationMessage(Validatable obj) { ... }
```

## Database Schema

```sql
customers (id, name, email, phone, passport_number)
bookings (id, customer_id, booking_date, total_price, status, type)
flight_bookings (booking_id, flight_number, origin, destination, seat_class)
hotel_bookings (booking_id, hotel_name, room_type, nights)
travel_packages (id, name, customer_id, discount_percentage)
```

**Foreign Keys**:
- `bookings.customer_id → customers.id`
- `flight_bookings.booking_id → bookings.id` (CASCADE)

## Architecture

```
Controller → Service → Repository → Database
```

**Flow**:
1. Controller receives input
2. Service validates & applies business rules
3. Repository executes SQL
4. Database persists data

## Execution

### Setup
```bash
# Create database
createdb travel_booking

# Run schema
psql -d travel_booking -f resources/schema.sql
```

### Update credentials
```java
// DatabaseConnection.java
private static final String URL = "jdbc:postgresql://localhost:5432/travel_booking";
private static final String USER = "your_username";
private static final String PASSWORD = "your_password";
```

### Run
```bash
javac -d bin -cp .:lib/postgresql-*.jar src/**/*.java
java -cp bin:lib/postgresql-*.jar Main
```

## Key Classes

- **Abstract**: `Booking` (getBookingType, calculatePrice)
- **Subclasses**: `FlightBooking`, `HotelBooking`
- **Composition**: `TravelPackage` has List<Booking>
- **Interfaces**: `Validatable`, `Billable`, `CrudRepository<T>`

## Reflection

**Learned**:
- SOLID makes code testable and extensible
- Generics eliminate duplicate CRUD code
- Lambdas reduce boilerplate significantly

**Challenges**:
- Mapping inheritance to database tables
- Transaction management across multiple tables
- Exception handling at service layer

**Value**: Code is now maintainable, testable, and follows industry standards.

---
