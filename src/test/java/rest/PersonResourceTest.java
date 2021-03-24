package rest;

import dtos.PersonDTO;
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

    static HttpServer startServer(){
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();

        httpServer = startServer();

        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer(){
        //System.in.read();

        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdown();
    }

    @BeforeEach
    public void setUp(){
        EntityManager em = emf.createEntityManager();
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

        p1 = new PersonDTO("Carsten", "Jensen", a1, pList1, "test1@test.dk", hobbyDTOList1);
        p2 = new PersonDTO("Thomas", "Andersen", a2, pList2, "test2@test.dk", hobbyDTOList2);

        try{
            em.getTransaction().begin();
            em.persist(p1);
            em.persist(p2);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
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
                .body("count", equalTo(2));
    }

    @Test
    void getPeopleById() {
        given()
                .contentType("application/json")
                .get("/people/{id}").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("id", hasItem(p1.getId()));
    }

    @Test
    void getPeopleByHobby() {
    }

    @Test
    void getPeopleByPostalCode() {
    }

    @Test
    void savePerson() {
    }

    @Test
    void updatePerson() {
    }
}