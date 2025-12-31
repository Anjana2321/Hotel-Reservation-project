package menu;

import api.AdminResource;
import api.HotelResource;
import model.Customer;
import model.IRoom;
import model.Room;
import model.RoomType;

import java.util.Collection;
import java.util.Scanner;

public class AdminMenu {

    private static final Scanner scanner = new Scanner(System.in);
    private static final AdminResource adminService = AdminResource.getInstance();
    private static final HotelResource hotelService = HotelResource.getInstance();

    public static void start() {
        boolean running = true;

        while (running) {
            printMenu();
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> showAllCustomers();
                case "2" -> showAllRooms();
                case "3" -> addRoomFlow();
                case "4" -> adminService.displayAllReservations();
                case "5" -> running = false;
                default -> System.out.println("Invalid selection. Try again.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n=== ADMIN MENU ===");
        System.out.println("1. List customers");
        System.out.println("2. List rooms");
        System.out.println("3. Create a room");
        System.out.println("4. Show reservations");
        System.out.println("5. Back to main menu");
        System.out.print("Enter choice: ");
    }

    private static void showAllCustomers() {
        Collection<Customer> customers = adminService.getAllCustomers();

        if (customers == null || customers.isEmpty()) {
            System.out.println("No customers found.");
            return;
        }

        customers.forEach(System.out::println);
    }

    private static void showAllRooms() {
        Collection<IRoom> rooms = adminService.getAllRooms();

        if (rooms == null || rooms.isEmpty()) {
            System.out.println("No rooms available.");
            return;
        }

        rooms.forEach(System.out::println);
    }



    private static void addRoomFlow() {

        System.out.print("Enter room number: ");
        String roomNumber = scanner.nextLine().trim();

        if (roomNumber.isBlank()) {
            System.out.println("Room number is required.");
            return;
        }

        if (hotelService.getRoom(roomNumber) != null) {
            System.out.println("This room already exists.");
            return;
        }


        double price;
        while (true) {
            try {
                System.out.print("Enter room price: ");
                price = Double.parseDouble(scanner.nextLine());

                if (price < 0) {
                    System.out.println("Price cannot be negative.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid price. Please enter a valid number.");
            }
        }


        RoomType type = requestRoomType();
        if (type == null) return;

        IRoom room = new Room(roomNumber, price, type);
        adminService.addRoom(java.util.List.of(room));

        System.out.println("Room added successfully.");
    }

    private static RoomType requestRoomType() {
        while (true) {
            System.out.print("Room type (SINGLE / DOUBLE): ");
            String input = scanner.nextLine().trim().toUpperCase();

            try {
                return RoomType.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid room type. Please enter SINGLE or DOUBLE.");
            }
        }
    }
}
