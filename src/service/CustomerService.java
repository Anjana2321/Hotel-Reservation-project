package service;

import model.Customer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CustomerService {

   private static final CustomerService instance=new CustomerService();
   private static final Map<String, Customer> customerStore =new HashMap<>();

   private CustomerService(){}

    public static CustomerService getInstance(){
       return instance;
    }

    public void addCustomer(String email,String firstName,String lastName){
        Customer newCustomer = new Customer(firstName,lastName,email);
        customerStore.put(email,newCustomer);
    }
    public Customer getCustomer(String customerEmail) {
        return customerStore.get(customerEmail);

   }


    public Collection<Customer> getAllCustomers(){
        return customerStore.values();
    }

}
