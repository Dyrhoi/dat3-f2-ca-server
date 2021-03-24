package entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Entity
public class Person implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String email;
    private String firstName;
    private String lastName;
    @ManyToOne(cascade = CascadeType.ALL)
    private Address address;

    @ManyToMany(cascade = CascadeType.PERSIST, mappedBy = "people")
    private List<Hobby> hobbies = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "person", orphanRemoval = true)
    private List<Phone> phones = new ArrayList<>();

    public Person() {
    }

    public Person(String email, String firstName, String lastName) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
/*        if(this.address != null) {
            address.removePerson(this);
            if(address.getPeople().size() == 0)
                address.getCityInfo().removeAddress(this.address);
        }*/
        this.address = address;

        if(address != null) {
            address.addPerson(this);
            address.getCityInfo().addAddress(address);
        }
    }

    public List<Hobby> getHobbies() {
        return hobbies;
    }

    public void setHobbies(List<Hobby> hobbies) {
        this.hobbies = hobbies;
    }

    public void addHobby(Hobby hobby) {
        hobbies.add(hobby);
        hobby.addPerson(this);
    }

    public void removeHobby(Hobby hobby) {
        hobbies.remove(hobby);
        hobby.removePerson(this);
    }

    public void removeAllHobbies() {
        //this.hobbies.forEach(this::removeHobby);
        // Avoiding concurrent exception...
        for (Iterator<Hobby> iterator = this.getHobbies().iterator(); iterator.hasNext();) {
            Hobby hobby = iterator.next();
            iterator.remove();
            hobby.removePerson(this);
        }
    }

    public List<Phone> getPhones() {
        return phones;
    }

    public void setPhones(List<Phone> phones) {
        this.phones = phones;
    }

    public void addPhone(Phone phone) {
        phones.add(phone);
        phone.setPerson(this);
    }

    public void removeAllPhone() {
        //this.phones.forEach(this::removePhone);
        // Avoiding concurrent exception...
        for (Iterator<Phone> iterator = this.getPhones().iterator(); iterator.hasNext();) {
            Phone phone = iterator.next();
            iterator.remove();
            phone.setPerson(null);
        }
    }

    public void removePhone(Phone phone) {
        phones.remove(phone);
        phone.setPerson(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return id == person.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
