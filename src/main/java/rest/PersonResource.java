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
        return Response.ok().entity(GSON.toJson(PERSON_FACADE.getPersonById(id))).build();
    }

    @GET
    @Path("/hobby/{hobby}")
    public Response getPeopleByHobby(@PathParam("hobby") String name) {
        return Response.ok().entity(GSON.toJson(peopleResponseData(PERSON_FACADE.getPersonsByHobby(name)))).build();
    }

    @GET
    @Path("/postalcode/{postalcode}")
    public Response getPeopleByPostalCode(@PathParam("postalcode") String postalcode) {
        return Response.ok().entity(GSON.toJson(peopleResponseData(PERSON_FACADE.getPersonsByCity(postalcode)))).build();
    }

    @POST
    public Response savePerson(String JsonPerson) {
        PersonDTO p = GSON.fromJson(JsonPerson, PersonDTO.class);
        //p = PERSON_FACADE.create(p);
        return Response.ok().entity(GSON.toJson(p)).build();
    }

    @PUT
    public Response updatePerson(String JsonPerson) {
        PersonDTO p = GSON.fromJson(JsonPerson, PersonDTO.class);
        //p = PERSON_FACADE.update(p);
        return Response.ok().entity(GSON.toJson(p)).build();
    }

    private Map<String, List<PersonDTO>> peopleResponseData(List<PersonDTO> people) {
        return Collections.singletonMap("data", people);
    }
}
