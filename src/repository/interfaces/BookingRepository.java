package repository;

import exception.DatabaseOperationException;
import exception.ResourceNotFoundException;
import model.*;
import repository.interfaces.CrudRepository;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingRepository implements CrudRepository<Booking> {

    @Override
    public void create(Booking booking) {
        if (booking instanceof FlightBooking) {
            createFlightBooking((FlightBooking) booking);
        } else if (booking instanceof HotelBooking) {
            createHotelBooking((HotelBooking) booking);
        } else {
            throw new IllegalArgumentException("Unknown booking type");
        }
    }

    private void createFlightBooking(FlightBooking booking) {
        String bookingSql = "INSERT INTO bookings (booking_date, total_price, status, customer_id, type) VALUES (?, ?, ?, ?, ?)";
        String flightSql = "INSERT INTO flight_bookings (booking_id, flight_number, origin, destination, seat_class) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement bookingStmt = conn.prepareStatement(bookingSql, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement flightStmt = conn.prepareStatement(flightSql)) {

                bookingStmt.setDate(1, Date.valueOf(booking.getBookingDate()));
                bookingStmt.setDouble(2, booking.getTotalPrice());
                bookingStmt.setString(3, booking.getStatus().name());
                bookingStmt.setInt(4, booking.getCustomerId());
                bookingStmt.setString(5, "FLIGHT");
                bookingStmt.executeUpdate();

                try (ResultSet keys = bookingStmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        booking.setId(keys.getInt(1));
                    } else {
                        throw new DatabaseOperationException("Creating booking failed, no ID obtained");
                    }
                }

                flightStmt.setInt(1, booking.getId());
                flightStmt.setString(2, booking.getFlightNumber());
                flightStmt.setString(3, booking.getOrigin());
                flightStmt.setString(4, booking.getDestination());
                flightStmt.setString(5, booking.getSeatClass().name());
                flightStmt.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("create", "FlightBooking", e);
        }
    }

    private void createHotelBooking(HotelBooking booking) {
        String bookingSql = "INSERT INTO bookings (booking_date, total_price, status, customer_id, type) VALUES (?, ?, ?, ?, ?)";
        String hotelSql = "INSERT INTO hotel_bookings (booking_id, hotel_name, room_type, nights) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement bookingStmt = conn.prepareStatement(bookingSql, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement hotelStmt = conn.prepareStatement(hotelSql)) {

                bookingStmt.setDate(1, Date.valueOf(booking.getBookingDate()));
                bookingStmt.setDouble(2, booking.getTotalPrice());
                bookingStmt.setString(3, booking.getStatus().name());
                bookingStmt.setInt(4, booking.getCustomerId());
                bookingStmt.setString(5, "HOTEL");
                bookingStmt.executeUpdate();

                try (ResultSet keys = bookingStmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        booking.setId(keys.getInt(1));
                    } else {
                        throw new DatabaseOperationException("Creating booking failed, no ID obtained");
                    }
                }

                hotelStmt.setInt(1, booking.getId());
                hotelStmt.setString(2, booking.getHotelName());
                hotelStmt.setString(3, booking.getRoomType().name());
                hotelStmt.setInt(4, booking.getNights());
                hotelStmt.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("create", "HotelBooking", e);
        }
    }

    @Override
    public List<Booking> getAll() {
        List<Booking> result = new ArrayList<>();
        result.addAll(getAllFlightBookings());
        result.addAll(getAllHotelBookings());
        return result;
    }

    private List<FlightBooking> getAllFlightBookings() {
        String sql = "SELECT b.*, fb.flight_number, fb.origin, fb.destination, fb.seat_class FROM bookings b JOIN flight_bookings fb ON b.id = fb.booking_id";
        List<FlightBooking> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapFlight(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("getAll", "FlightBooking", e);
        }
        return list;
    }

    private List<HotelBooking> getAllHotelBookings() {
        String sql = "SELECT b.*, hb.hotel_name, hb.room_type, hb.nights FROM bookings b JOIN hotel_bookings hb ON b.id = hb.booking_id";
        List<HotelBooking> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapHotel(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("getAll", "HotelBooking", e);
        }
        return list;
    }

    @Override
    public Booking getById(int id) {
        try {
            return getFlightById(id);
        } catch (ResourceNotFoundException e) {
            return getHotelById(id);
        }
    }

    private FlightBooking getFlightById(int id) {
        String sql = "SELECT b.*, fb.flight_number, fb.origin, fb.destination, fb.seat_class FROM bookings b JOIN flight_bookings fb ON b.id = fb.booking_id WHERE b.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new ResourceNotFoundException("FlightBooking", id);
                }
                return mapFlight(rs);
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("getById", "FlightBooking", e);
        }
    }

    private HotelBooking getHotelById(int id) {
        String sql = "SELECT b.*, hb.hotel_name, hb.room_type, hb.nights FROM bookings b JOIN hotel_bookings hb ON b.id = hb.booking_id WHERE b.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new ResourceNotFoundException("HotelBooking", id);
                }
                return mapHotel(rs);
            }
        } catch (SQLException e) {
            throw new DatabaseOperationException("getById", "HotelBooking", e);
        }
    }

    @Override
    public void update(int id, Booking booking) {
        String bookingSql = "UPDATE bookings SET booking_date = ?, total_price = ?, status = ?, customer_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(bookingSql)) {
                ps.setDate(1, Date.valueOf(booking.getBookingDate()));
                ps.setDouble(2, booking.getTotalPrice());
                ps.setString(3, booking.getStatus().name());
                ps.setInt(4, booking.getCustomerId());
                ps.setInt(5, id);
                ps.executeUpdate();
            }

            if (booking instanceof FlightBooking) {
                String flightSql = "UPDATE flight_bookings SET flight_number = ?, origin = ?, destination = ?, seat_class = ? WHERE booking_id = ?";
                try (PreparedStatement fps = conn.prepareStatement(flightSql)) {
                    FlightBooking fb = (FlightBooking) booking;
                    fps.setString(1, fb.getFlightNumber());
                    fps.setString(2, fb.getOrigin());
                    fps.setString(3, fb.getDestination());
                    fps.setString(4, fb.getSeatClass().name());
                    fps.setInt(5, id);
                    fps.executeUpdate();
                }
            } else if (booking instanceof HotelBooking) {
                String hotelSql = "UPDATE hotel_bookings SET hotel_name = ?, room_type = ?, nights = ? WHERE booking_id = ?";
                try (PreparedStatement hps = conn.prepareStatement(hotelSql)) {
                    HotelBooking hb = (HotelBooking) booking;
                    hps.setString(1, hb.getHotelName());
                    hps.setString(2, hb.getRoomType().name());
                    hps.setInt(3, hb.getNights());
                    hps.setInt(4, id);
                    hps.executeUpdate();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            throw new DatabaseOperationException("update", "Booking", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM bookings WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseOperationException("delete", "Booking", e);
        }
    }

    private FlightBooking mapFlight(ResultSet rs) throws SQLException {
        return new FlightBooking(
                rs.getInt("id"),
                rs.getDate("booking_date").toLocalDate(),
                rs.getDouble("total_price"),
                BookingStatus.valueOf(rs.getString("status")),
                rs.getInt("customer_id"),
                rs.getString("flight_number"),
                rs.getString("origin"),
                rs.getString("destination"),
                SeatClass.valueOf(rs.getString("seat_class"))
        );
    }

    private HotelBooking mapHotel(ResultSet rs) throws SQLException {
        return new HotelBooking(
                rs.getInt("id"),
                rs.getDate("booking_date").toLocalDate(),
                rs.getDouble("total_price"),
                BookingStatus.valueOf(rs.getString("status")),
                rs.getInt("customer_id"),
                rs.getString("hotel_name"),
                RoomType.valueOf(rs.getString("room_type")),
                rs.getInt("nights")
        );
    }
}
