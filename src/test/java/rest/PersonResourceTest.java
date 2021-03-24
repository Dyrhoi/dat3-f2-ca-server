package rest;

import dtos.PersonDTO;
import entities.CityInfo;
import entities.Hobby;
import entities.Person;
import facades.PersonFacade;
import io.restassured.RestAssured;
import io.restassured.parsing.Parser;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.*;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class PersonResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    private static PersonDTO p1, p2;

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;
    private static PersonFacade facade;


    static HttpServer startServer(){
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

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
        EMF_Creator.startREST_TestWithDB();
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

        httpServer = startServer();

        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer(){
        removeUsers();

        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdown();
    }

    @BeforeEach
    public void setUp(){
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
        PersonDTO.AddressDTO a1 = new PersonDTO.AddressDTO("Langegade 14", "4000", "Roskilde");
        PersonDTO.AddressDTO a2 = new PersonDTO.AddressDTO("Amagergade 12", "2300", "Amager");
        PersonDTO.PhoneDTO ph1 = new PersonDTO.PhoneDTO(12345678, "Hjemme");
        PersonDTO.PhoneDTO ph2 = new PersonDTO.PhoneDTO(87654321, "Arbejde");
        List<PersonDTO.PhoneDTO> pList1 = new ArrayList<>();
        List<PersonDTO.PhoneDTO> pList2 = new ArrayList<>();
        pList1.add(ph1);
        pList2.add(ph2);
        PersonDTO.HobbyDTO h1 = new PersonDTO.HobbyDTO("Humor", "Generel", "Indendørs");
        PersonDTO.HobbyDTO h2 = new PersonDTO.HobbyDTO("Videospil", "Generel", "Indendørs");
        List<PersonDTO.HobbyDTO> hobbyDTOList1 = new ArrayList<>();
        List<PersonDTO.HobbyDTO> hobbyDTOList2 = new ArrayList<>();
        hobbyDTOList1.add(h1);
        hobbyDTOList2.add(h2);

        p1 = facade.save("Carsten", "Jensen", a1, pList1, "test1@test.dk", hobbyDTOList1);
        p2 = facade.save("Thomas", "Andersen", a2, pList2, "test2@test.dk", hobbyDTOList2);
    }

    @Test
    public void testServerIsUp(){
        System.out.println("Testing if server is UP");
        given().when().get("/people").then().statusCode(200);
    }

    @Test
    void getPeople() {
        given()
                .contentType("application/json")
                .get("/people").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("data.size()", equalTo(2));
    }

    @Test
    void getPersonById() {
        given()
                .contentType("application/json")
                .get("/people/" + p1.getId()).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("id", equalTo((int) p1.getId()));
    }

    @Test
    void getPeopleByHobby() {
        given()
                .contentType("application/json")
                .get("people/hobby/" + p1.getHobbies().get(0).getName()).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("data.size()", equalTo(1));
    }

    @Test
    void getPeopleByPostalCode() {
        given()
                .contentType("application/json")
                .get("people/postalcode/4000").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("data.size()", equalTo(1));
    }

    @Test
    void savePerson() {
    }

    @Test
    void updatePerson() {
    }
}