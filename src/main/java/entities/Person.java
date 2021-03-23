package entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
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
    @ManyToOne(cascade = CascadeType.PERSIST)
    private Address address;

    @ManyToMany(mappedBy = "people", cascade = CascadeType.PERSIST)
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
        this.address = address;
        if(address != null)
            address.addPerson(this);
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
