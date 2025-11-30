package Classes;

import java.time.LocalDateTime;

public class Reservation {
    private int id;
    private int tripId;
    private int passengerId;
    private int numberOfSeats;
    private String status;
    private LocalDateTime reservationDate;
    private double totalPrice;

    // Constructors
    public Reservation() {}

    public Reservation(int tripId, int passengerId, int numberOfSeats, double totalPrice) {
        this.tripId = tripId;
        this.passengerId = passengerId;
        this.numberOfSeats = numberOfSeats;
        this.totalPrice = totalPrice;
        this.status = "confirme";
        this.reservationDate = LocalDateTime.now();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTripId() { return tripId; }
    public void setTripId(int tripId) { this.tripId = tripId; }

    public int getPassengerId() { return passengerId; }
    public void setPassengerId(int passengerId) { this.passengerId = passengerId; }

    public int getNumberOfSeats() { return numberOfSeats; }
    public void setNumberOfSeats(int numberOfSeats) { this.numberOfSeats = numberOfSeats; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getReservationDate() { return reservationDate; }
    public void setReservationDate(LocalDateTime reservationDate) { this.reservationDate = reservationDate; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
}