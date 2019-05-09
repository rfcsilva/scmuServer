package pt.agroSmart.resources;

import com.google.appengine.api.datastore.*;
import com.google.gson.Gson;
import pt.agroSmart.resources.GreenHouse.Config;
import pt.agroSmart.resources.GreenHouse.GreenHouse;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;
import java.util.logging.Logger;

@Path("/config")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ConfigResource {

    private static final Logger LOG = Logger.getLogger(UsersResource.class.getName());
    private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private static final Gson gson = new Gson();

    public ConfigResource() { }

    @POST
    @Path("/{greenhouse}/new")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setConfig(@PathParam("greenhouse") String greenHouseId, Config argConfig){


        try {


            GreenHouse greenHouse = new GreenHouse(greenHouseId);
            greenHouse = GreenHouse.fromEntity(greenHouse.ds_get());

            Query query = new Query(Config.TYPE).setAncestor(greenHouse.key);
            Entity e = datastore.prepare(query).asSingleEntity();
            if(e != null) LOG.info("Updating config");

            String configId = UUID.randomUUID().toString();
            Config config = new Config(argConfig.avgTemperature, argConfig.tempDeviation, argConfig.avgAirHumidity, argConfig.airHumidityDeviation,
            argConfig.avgSoilHumidity, argConfig.soilHumidityDeviation, argConfig.avgLuminosity, argConfig.luminosityDeviation,
            argConfig.avgSteam, argConfig.steamDeviation, configId, greenHouseId);

            if(config.ds_save())
                return Response.ok().build();


        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }



}
