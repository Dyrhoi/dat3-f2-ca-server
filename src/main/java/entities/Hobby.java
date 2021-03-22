package entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class Hobby implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(length = 50)
    private String name;

    private String wikiLink;
    private String category;
    private String type;

    public Hobby() {
    }

    public String getName() {
        return name;
    }

    public String getWikiLink() {
        return wikiLink;
    }

    public String getCategory() {
        return category;
    }

    public String getType() {
        return type;
    }
}
