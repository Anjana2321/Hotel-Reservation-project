package service;

import model.Customer;
import model.Reservation;
import model.IRoom;
import java.util.*;

public class ReservationService {

    private static final ReservationService instance = new ReservationService();
    private static final Map<String, IRoom> roomStore = new HashMap<>();
    private static final List<Reservation> reservationStore = new ArrayList<>();

    private ReservationService() {
    }

    public static ReservationService getInstance() {
        return instance;
    }

    public void addRoom(IRoom room) {

        roomStore.putIfAbsent(room.getRoomNumber(), room);
    }

    public IRoom getARoom(String roomId) {
        return roomStore.get(roomId);
    }

    public Reservation reserveARoom(Customer customer, IRoom room, Date checkInDate, Date checkOutDate) {

        for(Reservation reservation : reservationStore){
            if(reservation.getRoom().equals(room)){
                if(!(checkOutDate.before(reservation.getCheckInDate())||checkInDate.after(reservation.getCheckOutDate()))){
    return null;
                }
            }
        }
        Reservation reservation= new Reservation(customer,room,checkInDate,checkOutDate);
        reservationStore.add(reservation);
        return reservation;
    }

    public Collection<IRoom> findRooms(Date checkInDate, Date checkOutDate) {
        Collection<IRoom> availableRooms = new ArrayList<>(roomStore.values());

        for (Reservation reservation : reservationStore) {
            if(!(checkOutDate.before(reservation.getCheckInDate())||checkInDate.after(reservation.getCheckOutDate()))){
    availableRooms.remove(reservation.getRoom());
            }
        }
        if(availableRooms.isEmpty()){
            return findRecommendedRooms(checkInDate,checkOutDate);
        }
        return availableRooms;
    }

    void logReservationCount() {
        System.out.println("Total reservation:" + reservationStore.size());
    }

    public Collection<IRoom> getAllRooms() {
        return roomStore.values();
    }

    public void printAllReservations(){
        if(reservationStore.isEmpty()){
            System.out.println("No reservations found");
            return;
        }
        for(Reservation reservation:reservationStore){
            System.out.println(reservation);
        }
    }

    public Collection<IRoom> findRecommendedRooms(Date checkIn, Date checkOut) {
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(checkIn);
        calendar.add(Calendar.DATE,7);
        Date newCheckIn =calendar.getTime();

        calendar.setTime(checkOut);
        calendar.add(Calendar.DATE,7);
        Date newCheckout = calendar.getTime();

        Collection<IRoom> recommendedRooms = new ArrayList<>(roomStore.values());

        for(Reservation reservation:reservationStore){
            if(!(newCheckout.before(reservation.getCheckInDate())||newCheckIn.after(reservation.getCheckOutDate()))){
    recommendedRooms.remove(reservation.getRoom());
            }
        }

        return recommendedRooms;
    }
    public Collection<Reservation> getCustomersReservation(Customer customer) {
        Collection<Reservation> customerReservations = new ArrayList<>();

        for (Reservation reservation : reservationStore) {
            if (reservation.getCustomer().equals(customer)) {
                customerReservations.add(reservation);
            }
        }

        return customerReservations;
    }



    private boolean datesOverlap(Date start1, Date end1,
                                 Date start2, Date end2) {
        return start1.before(end2) && end1.after(start2);
    }
}
