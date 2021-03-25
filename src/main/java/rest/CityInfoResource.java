package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.CityInfoDTO;
import dtos.PersonDTO;
import facades.CityInfoFacade;
import utils.EMF_Creator;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Path("/postalcodes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CityInfoResource {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final CityInfoFacade CITY_INFO_FACADE = CityInfoFacade.getCityInfoFacade(EMF_Creator.createEntityManagerFactory());

    public CityInfoResource() {}

    @GET
    public Response getAll() {
        return Response.ok().entity(GSON.toJson(cityInfoResponseData(CITY_INFO_FACADE.getAll()))).build();
    }

    @GET
    @Path("/{postalcode}")
    public Response getByPostalCode(@PathParam("postalcode") String postalcode) {
        return Response.ok().entity(GSON.toJson(CITY_INFO_FACADE.getByPostalCode(postalcode))).build();
    }

    private Map<String, List<CityInfoDTO>> cityInfoResponseData(List<CityInfoDTO> cityInfos) {
        return Collections.singletonMap("data", cityInfos);
    }
}
