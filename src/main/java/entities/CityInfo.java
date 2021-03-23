package entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class CityInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(length = 4)
    private int zipCode;
    @Column(length = 35)
    private String city;

    @OneToMany(mappedBy = "cityInfo")
    private List<Address> addresses = new ArrayList<>();

    public CityInfo() {
    }

    public int getZipCode() {
        return zipCode;
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

    public List<Address> getAddresses() {
        return addresses;
    }
}
