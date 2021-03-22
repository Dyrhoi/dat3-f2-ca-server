package entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
public class Phone implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private int number;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    private Person person;

    public Phone() {
    }

    public Phone(int number, String description) {
        this.number = number;
        this.description = description;
    }

    public long getId() {
        return id;
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

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Phone phone = (Phone) o;
        return id == phone.id && number == phone.number;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, number);
    }
}
