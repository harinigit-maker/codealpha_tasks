import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Room implements Serializable {
    int roomNumber;
    String category;
    boolean isAvailable;
    double pricePerNight;

    public Room(int roomNumber, String category, double pricePerNight) {
        this.roomNumber = roomNumber;
        this.category = category;
        this.isAvailable = true;
        this.pricePerNight = pricePerNight;
    }
}

class Booking implements Serializable {
    String guestName;
    int roomNumber;
    String category;
    int nights;
    double totalAmount;

    public Booking(String guestName, Room room, int nights) {
        this.guestName = guestName;
        this.roomNumber = room.roomNumber;
        this.category = room.category;
        this.nights = nights;
        this.totalAmount = room.pricePerNight * nights;
    }
}

public class HotelReservation {
    private static List<Room> rooms = new ArrayList<>();
    private static List<Booking> bookings = new ArrayList<>();
    private static final String FILE_NAME = "bookings.txt";

    public static void main(String[] args) {
        
        initializeRooms();
        
        loadBookingsFromFile();

        Scanner scanner = new Scanner(System.in);
        System.out.println("==================================================");
        System.out.println(" CodeAlpha Advanced Hotel Reservation System ");
        System.out.println("==================================================");

        while (true) {
            System.out.println("\n=== HOTEL MANAGEMENT MENU ===");
            System.out.println("1. Search Available Rooms");
            System.out.println("2. Make a Reservation & Pay");
            System.out.println("3. Cancel a Reservation");
            System.out.println("4. View Active Booking Details");
            System.out.println("5. Exit System");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); 

            switch (choice) {
                case 1:
                    searchRooms();
                    break;
                case 2:
                    makeReservation(scanner);
                    break;
                case 3:
                    cancelReservation(scanner);
                    break;
                case 4:
                    viewBookings();
                    break;
                case 5:
                    saveBookingsToFile();
                    System.out.println("Data saved successfully. Thank you for using our system!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    private static void initializeRooms() {
        rooms.add(new Room(101, "Standard (Single Bed)", 1500.0));
        rooms.add(new Room(102, "Standard (Single Bed)", 1500.0));
        rooms.add(new Room(201, "Deluxe (Double Bed)", 3000.0));
        rooms.add(new Room(202, "Deluxe (Double Bed)", 3000.0));
        rooms.add(new Room(301, "Suite (Luxury)", 6000.0));
    }

    private static void searchRooms() {
        System.out.println("\n--- Current Available Rooms & Categorization ---");
        boolean found = false;
        for (Room room : rooms) {
            if (room.isAvailable) {
                System.out.println("Room No: " + room.roomNumber + " | Category: " + room.category + " | Price/Night: Rs." + room.pricePerNight);
                found = true;
            }
        }
        if (!found) {
            System.out.println("Sorry, all rooms are fully booked!");
        }
    }

    private static void makeReservation(Scanner scanner) {
        searchRooms();
        System.out.print("\nEnter Guest Full Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Room Number to Book: ");
        int roomNo = scanner.nextInt();

        Room selectedRoom = null;
        for (Room room : rooms) {
            if (room.roomNumber == roomNo && room.isAvailable) {
                selectedRoom = room;
                break;
            }
        }

        if (selectedRoom == null) {
            System.out.println("Error: Invalid Room Number or Room is already occupied!");
            return;
        }

        System.out.print("Enter number of nights to stay: ");
        int nights = scanner.nextInt();
        if (nights <= 0) {
            System.out.println("Stay must be 1 or more nights!");
            return;
        }

        double finalCost = selectedRoom.pricePerNight * nights;
        
        
        System.out.println("\n--- Payment Simulation Window ---");
        System.out.printf("Total Payable Amount: Rs.%.2f\n", finalCost);
        System.out.print("Processing Payment... Press Enter to confirm payment.");
        try { System.in.read(); } catch (Exception e) {}

        selectedRoom.isAvailable = false;
        Booking newBooking = new Booking(name, selectedRoom, nights);
        bookings.add(newBooking);
        saveBookingsToFile(); // Dynamic File Save

        System.out.println("\n Booking & Payment Successful! Details logged to database.");
    }

    private static void cancelReservation(Scanner scanner) {
        if (bookings.isEmpty()) {
            System.out.println("\nNo active reservations found to cancel.");
            return;
        }

        System.out.print("\nEnter Guest Name to cancel reservation: ");
        String name = scanner.nextLine();

        Booking bookingToRemove = null;
        for (Booking b : bookings) {
            if (b.guestName.equalsIgnoreCase(name)) {
                bookingToRemove = b;
                break;
            }
        }

        if (bookingToRemove == null) {
            System.out.println("No reservation found under the name: " + name);
            return;
        }

        
        for (Room r : rooms) {
            if (r.roomNumber == bookingToRemove.roomNumber) {
                r.isAvailable = true;
                break;
            }
        }

        bookings.remove(bookingToRemove);
        saveBookingsToFile(); 
        System.out.println(" Reservation for " + name + " has been successfully canceled. Refund processed!");
    }

    private static void viewBookings() {
        System.out.println("\n--- Current Database Bookings ---");
        if (bookings.isEmpty()) {
            System.out.println("No active reservations in the system database.");
            return;
        }
        for (Booking b : bookings) {
            System.out.println("Guest: " + b.guestName + " | Room: " + b.roomNumber + " (" + b.category + ") | Stay: " + b.nights + " nights | Total Bill: Rs." + b.totalAmount);
        }
    }

    
    private static void saveBookingsToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(bookings);
        } catch (IOException e) {
            System.out.println("Error saving booking data to file database.");
        }
    }

    
    @SuppressWarnings("unchecked")
    private static void loadBookingsFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            bookings = (List<Booking>) ois.readObject();
            // Sync room availability based on loaded bookings
            for (Booking b : bookings) {
                for (Room r : rooms) {
                    if (r.roomNumber == b.roomNumber) {
                        r.isAvailable = false;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading existing system data.");
        }
    }
}