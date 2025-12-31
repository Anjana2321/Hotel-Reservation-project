package model;

import java.util.Objects;

public class Customer {

    private final String givenName;
    private final String familyName;
    private final String emailAddress;

    public Customer(String givenName, String familyName, String emailAddress) {
        if (!validateEmail(emailAddress)) {
            throw new IllegalArgumentException("Provided email address is invalid");
        }
        this.givenName = givenName;
        this.familyName = familyName;
        this.emailAddress = emailAddress;
    }

    private boolean validateEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email != null && email.matches(emailPattern);
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Customer)) {
            return false;
        }
        Customer other = (Customer) object;
        return emailAddress.equalsIgnoreCase(other.emailAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(emailAddress.toLowerCase());
    }

    @Override
    public String toString() {
        return "Customer Details -> Name: " + givenName + " " + familyName +
                ", Email: " + emailAddress;
    }
}
