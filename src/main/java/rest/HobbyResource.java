package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.CityInfoDTO;
import dtos.HobbyDTO;
import facades.HobbyFacade;
import utils.EMF_Creator;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Path("/hobbies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HobbyResource {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final HobbyFacade HOBBY_FACADE = HobbyFacade.getHobbyFacade(EMF_Creator.createEntityManagerFactory());

    private HobbyResource() {}

    @GET
    public Response getAll() {
        return Response.ok().entity(GSON.toJson(hobbiesResponseData(HOBBY_FACADE.getAll()))).build();
    }

    @GET
    @Path("/name/{name}")
    public Response getByPostalCode(@PathParam("name") String name) {
        return Response.ok().entity(GSON.toJson(HOBBY_FACADE.getByName(name))).build();
    }

    @GET
    @Path("/search/{q}")
    public Response searchByName(@PathParam("q") String q) {
        return Response.ok().entity(GSON.toJson(hobbiesResponseData(HOBBY_FACADE.searchByName(q)))).build();
    }

    private Map<String, List<HobbyDTO>> hobbiesResponseData(List<HobbyDTO> hobbies) {
        return Collections.singletonMap("data", hobbies);
    }
}
