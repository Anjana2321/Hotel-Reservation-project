package api;

import model.Customer;
import model.IRoom;
import model.Reservation;
import java.util.Collection;
import java.util.Date;
import service.CustomerService;
import service.ReservationService;



public class HotelResource {

    private static final HotelResource instance=new HotelResource();
    private static final CustomerService customerService = CustomerService.getInstance();
    private static final ReservationService reservationService = ReservationService.getInstance();

    private HotelResource() {
    }

    public static HotelResource getInstance() {
        return instance;
    }

    public void createACustomer(String email,String firstName,String lastName){
        customerService.addCustomer(email,firstName,lastName);
    }
    public Reservation bookARoom(String email,IRoom room, Date checkInDate, Date checkOutDate){
        Customer customer=customerService.getCustomer(email);
        if(customer==null){
            throw new IllegalArgumentException("Customer not found");
        }
        if(checkInDate==null || checkOutDate==null|| checkOutDate.before(checkInDate)){
            throw new IllegalArgumentException("Invalid check-in or check-out date");
        }

        Reservation reservation = reservationService.reserveARoom(customer,room,checkInDate,checkOutDate);
        if(reservation==null){
            throw new IllegalStateException("Room is already booked for the selected dates");
        }

        return reservation;
    }
    public Customer getCustomer(String email) {
        return customerService.getCustomer(email);
    }
    public IRoom getRoom(String roomNumber){
        return reservationService.getARoom(roomNumber);
    }

    public Collection<IRoom> findARoom(Date checkIn, Date checkOut){
        return reservationService.findRooms(checkIn,checkOut);
    }
    public Collection<Reservation> getCustomersReservations(String email) {
        Customer customer = customerService.getCustomer(email);
        return reservationService.getCustomersReservation(customer);
    }

}
