package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.PersonDTO;
import facades.PersonFacade;
import utils.EMF_Creator;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@Path("/people")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PersonResource {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final PersonFacade PERSON_FACADE = PersonFacade.getPersonFacade(EMF_Creator.createEntityManagerFactory());

    public PersonResource() {}

    @GET
    public Response getPeople() {
        return Response.ok().entity(GSON.toJson(peopleResponseData(PERSON_FACADE.getAll()))).build();
    }

    @GET
    @Path("/{id}")
    public Response getPeopleById(@PathParam("id") long id) {
        return Response.ok().entity(GSON.toJson(PERSON_FACADE.getById(id))).build();
    }

    @GET
    @Path("/hobby/{hobby}")
    public Response getPeopleByHobby(@PathParam("hobby") String name) {
        return Response.ok().entity(GSON.toJson(peopleResponseData(PERSON_FACADE.getByHobby(name)))).build();
    }

    @GET
    @Path("/postalcode/{postalcode}")
    public Response getPeopleByPostalCode(@PathParam("postalcode") String postalcode) {
        return Response.ok().entity(GSON.toJson(peopleResponseData(PERSON_FACADE.getByPostalCode(postalcode)))).build();
    }

    @GET
    @Path("/phone/{phonenumber}")
    public Response getPeopleByPhoneNumber(@PathParam("phonenumber") int phoneNumber) {
        return Response.ok().entity(GSON.toJson(PERSON_FACADE.getByNumber(phoneNumber))).build();
    }

    @POST
    public Response savePerson(String JsonPerson) {
        PersonDTO p = GSON.fromJson(JsonPerson, PersonDTO.class);
        p = PERSON_FACADE.save(p);
        return Response.ok().entity(GSON.toJson(p)).build();
    }

    @PUT
    @Path("/{id}")
    public Response updatePerson(@PathParam("id") long id, String JsonPerson) {
        PersonDTO p = GSON.fromJson(JsonPerson, PersonDTO.class);
        p = PERSON_FACADE.update(id, p);
        return Response.ok().entity(GSON.toJson(p)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deletePerson(@PathParam("id") long id) {
        PersonDTO p = PERSON_FACADE.delete(id);
        return Response.ok().entity(GSON.toJson(p)).build();
    }

    private Map<String, List<PersonDTO>> peopleResponseData(List<PersonDTO> people) {
        return Collections.singletonMap("data", people);
    }
}
