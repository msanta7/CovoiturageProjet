package Classes;

import java.time.LocalDate;

public class Trajets {
    private int id; // Add this field
    public Double Num;
    public String Firstname;
    public String Lastname;
    public String Phonenumber;
    public String Email;
    public String Password;
    public int MemberSince;

    // Trip-specific fields
    private String depart;
    private String destination;
    private LocalDate date;
    private String driverName;
    private int price;
    private int places;
    private int driverId; // Add driver ID to link with users table
    private int vehicleId; // Add vehicle ID

    // Updated constructors
    public Trajets(String depart, String destination, LocalDate date, String driverName, int price, int places) {
        this.depart = depart;
        this.destination = destination;
        this.date = date;
        this.driverName = driverName;
        this.price = price;
        this.places = places;
    }

    public Trajets(int id, String depart, String destination, LocalDate date, String driverName, int price, int places, int driverId) {
        this.id = id;
        this.depart = depart;
        this.destination = destination;
        this.date = date;
        this.driverName = driverName;
        this.price = price;
        this.places = places;
        this.driverId = driverId;
    }

    // Simple constructor for testing (if needed)
    public Trajets(String depart, String destination, LocalDate date, int price) {
        this.depart = depart;
        this.destination = destination;
        this.date = date;
        this.price = price;
        // Set default values for missing parameters
        this.driverName = "Conducteur " + (char)('A' + (int)(Math.random() * 26)); // Random driver name
        this.places = (int)(Math.random() * 4) + 1; // Random seats between 1-4
    }

    // Add getRating() method to fix the error
    public String getRating() {
        // Return a random rating between 4.0 and 5.0 for demo purposes
        double rating = 4.0 + (Math.random() * 1.0); // Random between 4.0-5.0
        return String.format("%.1f", rating);
    }

    // Getters and Setters for new fields
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDepart() { return depart; }
    public void setDepart(String depart) { this.depart = depart; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getDriverName() { return driverName; }
    public void setDriverName(String driverName) { this.driverName = driverName; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public int getPlaces() { return places; }
    public void setPlaces(int places) { this.places = places; }

    public int getDriverId() { return driverId; }
    public void setDriverId(int driverId) { this.driverId = driverId; }

    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }

    // Keep your existing getters and setters for other fields
    public String getFirstname() {
        return Firstname;
    }

    public void setFirstname(String firstname) {
        Firstname = firstname;
    }

    public String getLastname() {
        return Lastname;
    }

    public void setLastname(String lastname) {
        Lastname = lastname;
    }

    public String getPhonenumber() {
        return Phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        Phonenumber = phonenumber;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public int getMemberSince() {
        return MemberSince;
    }

    public void setMemberSince(int memberSince) {
        MemberSince = memberSince;
    }
}