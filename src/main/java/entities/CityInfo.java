package entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Entity
public class CityInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(length = 4)
    private String postalCode;
    @Column(length = 35)
    private String city;

    @OneToMany(mappedBy = "cityInfo")
    private List<Address> addresses = new ArrayList<>();

    public CityInfo() {
    }

    public CityInfo(String postalCode, String city) {
        this.postalCode = postalCode;
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCity() {
        return city;
    }

    public void addAddress(Address address) {
        addresses.add(address);
        address.setCityInfo(this);
    }

    public void removeAddress(Address address) {
        addresses.remove(address);
        address.setCityInfo(null);
    }

    public void removeAllAddresses() {
        //this.hobbies.forEach(this::removeHobby);
        // Avoiding concurrent exception...
        for (Iterator<Address> iterator = this.getAddresses().iterator(); iterator.hasNext();) {
            Address address = iterator.next();
            iterator.remove();
            address.setCityInfo(null);
        }
    }

    public List<Address> getAddresses() {
        return addresses;
    }
}
