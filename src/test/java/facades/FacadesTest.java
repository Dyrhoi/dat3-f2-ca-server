package facades;

import dtos.CityInfoDTO;
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
import java.util.ArrayList;
import java.util.List;

class FacadesTest {

    private static EntityManagerFactory emf;
    private static PersonFacade personFacade;
    private static HobbyFacade hobbyFacade;
    private static CityInfoFacade cityInfoFacade;
    private static PersonDTO p1, p2, p3;
    private static Hobby h1, h2, h3, h4;
    private static CityInfo c1, c2;

    private static void removeUsers() {
        EntityManager em = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            // Do this since we need to cascade delete...?
            em.createQuery("SELECT p from Person p", Person.class).getResultStream().forEach(person -> {
                person.removeAllHobbies();
                if(person.getAddress() != null) {
                    CityInfo cityInfo = em.find(CityInfo.class, person.getAddress().getCityInfo().getPostalCode());
                    cityInfo.removeAddress(person.getAddress());
                    em.merge(cityInfo);
                }
                em.remove(person);

            });
            em.getTransaction().commit();
        }finally {
            em.close();
        }
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        personFacade = PersonFacade.getPersonFacade(emf);
        hobbyFacade = HobbyFacade.getHobbyFacade(emf);
        cityInfoFacade = CityInfoFacade.getCityInfoFacade(emf);

        EntityManager em = emf.createEntityManager();
        // We're not using our insert script in our tests...

        em.getTransaction().begin();
        em.createQuery("DELETE FROM CityInfo").executeUpdate();
        em.createQuery("DELETE FROM Hobby").executeUpdate();
        h1 = new Hobby("Humor", "Generel", "Indendørs", "wikilink.com");
        h2 = new Hobby("Videospil", "Generel", "Indendørs", "wikilink.com");
        h3 = new Hobby("Videoredigering", "Generel", "Indendørs", "wikilink.com");
        h4 = new Hobby("Vævning", "Generel", "Indendørs", "wikilink.com");
        em.persist(h1);
        em.persist(h2);
        em.persist(h3);
        em.persist(h4);
        c1 = new CityInfo("4000", "Roskilde");
        c2 = new CityInfo("2300", "Amager");
        em.persist(c1);
        em.persist(c2);
        em.getTransaction().commit();

    }

    @AfterAll
    public static void tearDownClass() {
        removeUsers();
    }

    @BeforeEach
    public void setUp() {
        removeUsers();
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

        p1 = personFacade.save("Carsten", "Jensen", a1, pList1, "test1@test.dk", hobbyDTOList1);
        p2 = personFacade.save("Thomas", "Andersen", a2, pList2, "test2@test.dk", hobbyDTOList2);
        p3 = personFacade.save("Anna", "Lauersen", a1, pList3, "test3@test.dk", hobbyDTOList3);
    }

    @Test
    void personSave() {
        System.out.println("Save Test");
        PersonDTO.AddressDTO a1 = new PersonDTO.AddressDTO("Langegade 14", "4000", "Roskilde");
        PersonDTO.PhoneDTO ph1 = new PersonDTO.PhoneDTO(45678231, "Hjemme");
        List<PersonDTO.PhoneDTO> pList1 = new ArrayList<>();
        pList1.add(ph1);
        PersonDTO.HobbyDTO h1 = new PersonDTO.HobbyDTO("Humor", "Generel", "Indendørs");
        List<PersonDTO.HobbyDTO> hobbyDTOList1 = new ArrayList<>();
        hobbyDTOList1.add(h1);
        PersonDTO tmpPerson = personFacade.save("Louise", "Mølgård", a1, pList1, "test4@test.dk", hobbyDTOList1);
        assertEquals(4, personFacade.getAll().size());
    }

    @Test
    void personGetById() {
        System.out.println("Get Person by ID Test");
        long id = p1.getId();
        assertEquals(id, personFacade.getById(id).getId());
    }

    @Test
    void personGetAll() {
        System.out.println("Get all Persons Test");
        assertEquals(3, personFacade.getAll().size());
    }

    @Test
    void personGetByPostalCode() {
        System.out.println("Get Persons by Postal code Test");
        assertEquals(2, personFacade.getByPostalCode("4000").size());
    }

    @Test
    void personGetByHobby() {
        System.out.println("Get Persons by Hobby Test");
        System.out.println(personFacade.getByHobby("vævning"));
        assertEquals(1, personFacade.getByHobby("Vævning").size());
        assertEquals(1, personFacade.getByHobby("Humor").size());
        assertEquals(1, personFacade.getByHobby("Videospil").size());
    }

    @Test
    void personUpdate() {
        System.out.println("Update");
        PersonDTO personDto = personFacade.getById(p1.getId());
        personDto.setEmail("updated_test@gmail.com");
        personDto.setAddress(p2.getAddress());

        personFacade.update(p1.getId(), personDto);
        List<PersonDTO> peopleK = personFacade.getByPostalCode("2300");
        List<PersonDTO> peopleR = personFacade.getByPostalCode("4000");

        assertEquals(2, personFacade.getByPostalCode("2300").size());
        assertEquals(1, personFacade.getByPostalCode("4000").size());
        assertEquals("updated_test@gmail.com", personFacade.getById(p1.getId()).getEmail());
    }

    @Test
    void personGetByNumber() {
        System.out.println("Get Person by Number Test");
        int number = p1.getPhone().get(0).getNumber();
        assertEquals(number, personFacade.getByNumber(number).getPhone().get(0).getNumber());
    }

    @Test
    void personDelete() {
        System.out.println("Update");
        long id = p1.getId();
        personFacade.delete(id);
        assertEquals(2, personFacade.getAll().size());
    }

    @Test
    void hobbyGetAll() {
        assertEquals(4, hobbyFacade.getAll().size());
    }

    @Test
    void hobbyGetName() {
        assertEquals(h1.getName(), hobbyFacade.getByName(h1.getName()).getName());
    }

    @Test
    void hobbySearchName() {
        assertEquals(2, hobbyFacade.searchByName("video").size());
    }

    @Test
    void cityInfoGetAll() {
        assertEquals(2, cityInfoFacade.getAll().size());
    }

    @Test
    void cityInfoGetByPostalCode() {
        assertEquals(c1.getCity(), cityInfoFacade.getByPostalCode(c1.getPostalCode()).getCity());
    }

}