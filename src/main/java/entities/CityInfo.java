package entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class CityInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(length = 4)
    private Long zipCode;
    @Column(length = 35)
    private String city;

    public CityInfo() {
    }

    public Long getZipCode() {
        return zipCode;
    }

    public String getCity() {
        return city;
    }
}
