package dtos;

import entities.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PersonDTO {
    private long id;
    private String firstname;
    private String lastname;
    private Address address;
    private List<Phone> phone;
    private String email;
    private List<Hobby> hobbies;

    public PersonDTO(String firstname, String lastname, Address address, List<Phone> phone, String email, List<Hobby> hobbies) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.hobbies = hobbies;
    }

    public PersonDTO(Person person) {
        PersonDTO.Address address = new Address(
                person.getAddress().getStreet(),
                person.getAddress().getCityInfo().getZipCode(),
                person.getAddress().getCityInfo().getCity());

        List<Phone> phoneList = new ArrayList<>();
        person.getPhones().forEach(pe -> {
            phoneList.add(new Phone(pe.getNumber(), pe.getDescription()));
        });

        List<Hobby> hobbyList = new ArrayList<>();
        person.getHobbies().forEach(he -> {
            hobbyList.add(new Hobby(he.getName(), he.getCategory(), he.getType()));
        });

        this.id = person.getId();
        this.firstname = person.getFirstName();
        this.lastname = person.getLastName();
        this.address = address;
        this.phone = phoneList;
        this.email = person.getEmail();
        this.hobbies = hobbyList;
    }

    public static List<PersonDTO> toList(List<Person> people) {
        return people.stream().map(PersonDTO::new).collect(Collectors.toList());
    }

    public long getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<Phone> getPhone() {
        return phone;
    }

    public void setPhone(List<Phone> phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Hobby> getHobbies() {
        return hobbies;
    }

    public void setHobbies(List<Hobby> hobbies) {
        this.hobbies = hobbies;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonDTO personDTO = (PersonDTO) o;
        return id == personDTO.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static class Address {
        private String street;
        private int postalcode;
        private String city;

        public Address(String street, int postalcode, String city) {
            this.street = street;
            this.postalcode = postalcode;
            this.city = city;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public int getPostalcode() {
            return postalcode;
        }

        public void setPostalcode(int postalcode) {
            this.postalcode = postalcode;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }
    }

    public static class Phone {
        private int number;
        private String description;

        public Phone(int number, String description) {
            this.number = number;
            this.description = description;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class Hobby {
        private String name;
        private String category;
        private String type;

        public Hobby(String name, String category, String type) {
            this.name = name;
            this.category = category;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

}
