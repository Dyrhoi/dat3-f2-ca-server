package facades;

import dtos.PersonDTO;
import entities.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;
import static org.junit.jupiter.api.Assertions.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

class PersonFacadeTest {

    private static EntityManagerFactory emf;
    private static PersonFacade facade;
    private static PersonDTO p1, p2, p3;

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = PersonFacade.getPersonFacade(emf);

        EntityManager em = emf.createEntityManager();
        // We're not using our insert script in our tests...

        em.getTransaction().begin();
        em.createQuery("DELETE FROM CityInfo").executeUpdate();
        em.createQuery("DELETE FROM Hobby").executeUpdate();

        em.persist(new Hobby("Humor", "Generel", "Indendørs", "wikilink.com"));
        em.persist(new Hobby("Videospil", "Generel", "Indendørs", "wikilink.com"));
        em.persist(new Hobby("Vævning", "Generel", "Indendørs", "wikilink.com"));

        em.persist(new CityInfo("4000", "Roskilde"));
        em.persist(new CityInfo("2300", "Amager"));
        em.getTransaction().commit();

    }

    @AfterAll
    public static void tearDownClass() {
//        Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
    }

    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            // Do this since we need to cascade delete...?
            em.createQuery("SELECT p from Person p", Person.class).getResultStream().forEach(person -> {
                person.getHobbies().forEach(hobby -> {
                    hobby.removePerson(person);
                });
                em.remove(person);
            });
            em.getTransaction().commit();
        }finally {
            em.close();
        }
        PersonDTO.AddressDTO a1 = new PersonDTO.AddressDTO("Langegade 14", "4000", "Roskilde");
        PersonDTO.AddressDTO a2 = new PersonDTO.AddressDTO("Amagergade 12", "2300", "Amager");
        PersonDTO.PhoneDTO ph1 = new PersonDTO.PhoneDTO(12345678, "Hjemme");
        PersonDTO.PhoneDTO ph2 = new PersonDTO.PhoneDTO(87654321, "Arbejde");
        PersonDTO.PhoneDTO ph3 = new PersonDTO.PhoneDTO(15764328, "Mobil");
        List<PersonDTO.PhoneDTO> pList1 = new ArrayList<>();
        List<PersonDTO.PhoneDTO> pList2 = new ArrayList<>();
        List<PersonDTO.PhoneDTO> pList3 = new ArrayList<>();
        pList1.add(ph1);
        pList2.add(ph2);
        pList3.add(ph3);
        PersonDTO.HobbyDTO h1 = new PersonDTO.HobbyDTO("Humor", "Generel", "Indendørs");
        PersonDTO.HobbyDTO h2 = new PersonDTO.HobbyDTO("Videospil", "Generel", "Indendørs");
        PersonDTO.HobbyDTO h3 = new PersonDTO.HobbyDTO("Vævning", "Generel", "Indendørs");
        List<PersonDTO.HobbyDTO> hobbyDTOList1 = new ArrayList<>();
        List<PersonDTO.HobbyDTO> hobbyDTOList2 = new ArrayList<>();
        List<PersonDTO.HobbyDTO> hobbyDTOList3 = new ArrayList<>();
        hobbyDTOList1.add(h1);
        hobbyDTOList2.add(h2);
        hobbyDTOList3.add(h3);

        p1 = facade.save("Carsten", "Jensen", a1, pList1, "test1@test.dk", hobbyDTOList1);
        p2 = facade.save("Thomas", "Andersen", a2, pList2, "test2@test.dk", hobbyDTOList2);
        p3 = facade.save("Anna", "Lauersen", a1, pList3, "test3@test.dk", hobbyDTOList3);
    }

    @Test
    void save() {
        System.out.println("Save Test");
        PersonDTO.AddressDTO a1 = new PersonDTO.AddressDTO("Langegade 14", "4000", "Roskilde");
        PersonDTO.PhoneDTO ph1 = new PersonDTO.PhoneDTO(45678231, "Hjemme");
        List<PersonDTO.PhoneDTO> pList1 = new ArrayList<>();
        pList1.add(ph1);
        PersonDTO.HobbyDTO h1 = new PersonDTO.HobbyDTO("Humor", "Generel", "Indendørs");
        List<PersonDTO.HobbyDTO> hobbyDTOList1 = new ArrayList<>();
        hobbyDTOList1.add(h1);
        PersonDTO tmpPerson = facade.save("Louise", "Mølgård", a1, pList1, "test4@test.dk", hobbyDTOList1);
        assertEquals(4, facade.getAll().size());
    }

    @Test
    void getById() {
        System.out.println("Get Person by ID Test");
        long id = p1.getId();
        assertEquals(id, facade.getById(id).getId());
    }

    @Test
    void getAll() {
        System.out.println("Get all Persons Test");
        assertEquals(3, facade.getAll().size());
    }

    @Test
    void getByPostalCode() {
        System.out.println("Get Persons by Postal code Test");
        assertEquals(2, facade.getByPostalCode("4000").size());
    }

    @Test
    void getPersonsByHobby() {
        System.out.println("Get Persons by Hobby Test");
        System.out.println(facade.getByHobby("vævning"));
        assertEquals(1, facade.getByHobby("Vævning").size());
        assertEquals(1, facade.getByHobby("Humor").size());
        assertEquals(1, facade.getByHobby("Videospil").size());
    }

    @Test
    void getAllZipCodes() {
        System.out.println("Get all Zipcodes Test");
        assertEquals(2, facade.getAllZipCodes().size());
    }
}