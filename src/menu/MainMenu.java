package menu;

import api.HotelResource;
import model.Customer;
import model.IRoom;
import model.Reservation;
import model.Room;
import model.FreeRoom;
import model.RoomType;
import service.ReservationService;

import java.text.SimpleDateFormat;
import java.util.*;

public class MainMenu {

    private static final Scanner scanner = new Scanner(System.in);
    private static final HotelResource hotelService = HotelResource.getInstance();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    private static Date activeCheckIn;
    private static Date activeCheckOut;

    public static void start() {
        dateFormat.setLenient(false);


        ReservationService service = ReservationService.getInstance();
        if (hotelService.getRoom("101") == null) {
            service.addRoom(new Room("101", 120.0, RoomType.SINGLE));
            service.addRoom(new FreeRoom("102", RoomType.DOUBLE));
        }

        boolean running = true;
        while (running) {
            renderMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> reserveRoomProcess();
                case "2" -> showCustomerReservations();
                case "3" -> registerCustomer();
                case "4" -> AdminMenu.start();
                case "5" -> running = false;
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void renderMenu() {
        System.out.println("\n=== HOTEL RESERVATION MENU ===");
        System.out.println("1. Reserve a room");
        System.out.println("2. View my reservations");
        System.out.println("3. Register new customer");
        System.out.println("4. Admin access");
        System.out.println("5. Exit");
        System.out.print("Choice: ");
    }



    private static void registerCustomer() {
        System.out.print("Email: ");
        String email = getValidEmail();

        System.out.print("First name: ");
        String first = scanner.nextLine().trim();

        System.out.print("Last name: ");
        String last = scanner.nextLine().trim();

        try {
            hotelService.createACustomer(email, first, last);
            System.out.println("Customer registered successfully.");
        } catch (Exception e) {
            System.out.println("Customer already exists.");
        }
    }

    private static void showCustomerReservations() {
        System.out.print("Enter your registered email: ");
        String email = getValidEmail();

        Collection<Reservation> reservations =
                hotelService.getCustomersReservations(email);

        if (reservations == null || reservations.isEmpty()) {
            System.out.println("No reservations found.");
            return;
        }

        reservations.forEach(System.out::println);
    }



    private static void reserveRoomProcess() {

        Date arrival = requestDate("Check-in date (MM/dd/yyyy): ");
        Date departure = requestDate("Check-out date (MM/dd/yyyy): ");

        if (!departure.after(arrival)) {
            System.out.println("Check-out date must be after check-in.");
            return;
        }

        Collection<IRoom> availableRooms =
                searchAvailableRooms(arrival, departure);

        if (availableRooms.isEmpty()) {
            System.out.println("Reservation cancelled.");
            return;
        }

        availableRooms.forEach(System.out::println);

        System.out.print("Enter room number to book: ");
        String roomNumber = scanner.nextLine().trim();
        IRoom room = hotelService.getRoom(roomNumber);

        if (room == null) {
            System.out.println("Invalid room number.");
            return;
        }

        System.out.print("Customer email: ");
        String email = getValidEmail();
        Customer customer = hotelService.getCustomer(email);

        if (customer == null) {
            System.out.println("Customer not registered.");
            return;
        }

        if (activeCheckIn == null || activeCheckOut == null) {
            System.out.println("Booking dates not set. Cannot proceed.");
            return;
        }

        Reservation reservation =
                hotelService.bookARoom(email, room, activeCheckIn, activeCheckOut);

        if (reservation == null) {
            System.out.println("Room unavailable for selected dates.");
            return;
        }

        System.out.println("Room successfully reserved!");
        System.out.println(reservation);
    }



    private static Collection<IRoom> searchAvailableRooms(Date in, Date out) {

        Collection<IRoom> rooms = hotelService.findARoom(in, out);
        if (!rooms.isEmpty()) {
            activeCheckIn = in;
            activeCheckOut = out;
            return rooms;
        }

        System.out.println("No rooms available. Checking alternative dates (+7 days)...");

        Calendar cal = Calendar.getInstance();

        cal.setTime(in);
        cal.add(Calendar.DAY_OF_MONTH, 7);
        Date newIn = cal.getTime();

        cal.setTime(out);
        cal.add(Calendar.DAY_OF_MONTH, 7);
        Date newOut = cal.getTime();

        Collection<IRoom> recommendedRooms =
                hotelService.findARoom(newIn, newOut);

        if (recommendedRooms.isEmpty()) {
            System.out.println("No alternative rooms available.");
            return Collections.emptyList();
        }

        System.out.println("Recommended dates:");
        System.out.println("Check-in:  " + dateFormat.format(newIn));
        System.out.println("Check-out: " + dateFormat.format(newOut));
        System.out.print("Do you want to book these dates? (y/n): ");

        String choice = scanner.nextLine().trim();
        if (!choice.equalsIgnoreCase("y")) {
            return Collections.emptyList();
        }

        activeCheckIn = newIn;
        activeCheckOut = newOut;
        return recommendedRooms;
    }



    private static Date requestDate(String message) {
        while (true) {
            try {
                System.out.print(message);
                Date date = dateFormat.parse(scanner.nextLine());

                if (date.before(new Date())) {
                    System.out.println("Past dates are not allowed.");
                    continue;
                }
                return date;
            } catch (Exception e) {
                System.out.println("Invalid date format. Use MM/dd/yyyy.");
            }
        }
    }

    private static String getValidEmail() {
        while (true) {
            String email = scanner.nextLine().trim();
            if (email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                return email;
            }
            System.out.println("Invalid email. Try again:");
        }
    }
}
