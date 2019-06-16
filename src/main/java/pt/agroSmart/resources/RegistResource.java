package pt.agroSmart.resources;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.gson.Gson;
import pt.agroSmart.resources.GreenHouse.GreenHouse;
import pt.agroSmart.resources.Sensor.Regist.Regist;
import pt.agroSmart.resources.UsersResource;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Path("/regist")
@Consumes(MediaType.APPLICATION_JSON)
public class RegistResource {
	
	//Physical variables codes
	private static final char TEMPERATURE_CODE = 97;
	private static final char AIR_HUMIDITY_CODE = 98;
	private static final char HEAT_CODE = 99;
	private static final char  SOIL_HUMIDITY_CODE = 100;
	private static final char  LUMINOSITY_CODE = 101;
	private static final char  WATER_LEVEL_CODE = 102;

    private static final Logger LOG = Logger.getLogger(UsersResource.class.getName());
    private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private static final Gson gson = new Gson();
	private static final String GRENNHOUSEID = "GreenHouse@2.000000,2.000000";

    @POST
    @Path("/new")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response addRead(String values){

        GreenHouse greenHouse = new GreenHouse(GRENNHOUSEID);
        try {
            greenHouse.ds_get();
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
                
       // Regist regist = new Regist(GRENNHOUSEID, Float.valueOf(readings.get(key)), r.soilHumidity, r.airHumidity, r.waterLevel, r.luminosity, r.steam, r.timeStamp);
       // regist.ds_save();

        return Response.ok().build();
    }

    private Map<String, String> parseInput(String values) {
		String[] readings = values.split("\n");
		Map<String, String> toReturn = new HashMap<>(readings.length);
		for(String read: readings) {
			String[] readSplit = read.split(":");
			toReturn.put(readSplit[0], readSplit[1]);
		}
		return toReturn;
	}

	@GET
    @Path("/{greenhouse}/{sensor}/")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response greenHouseReadings(@PathParam("greenhouse") String ghId, @PathParam("sensor") String sensorId, @QueryParam("t1") long init, @QueryParam("t2") long end){

        Key greenHouseKey = GreenHouse.generateKey(ghId);


        Filter f1 = new FilterPredicate(Regist.TIMESTAMP, FilterOperator.GREATER_THAN_OR_EQUAL, init);
        Filter f2 = new FilterPredicate(Regist.TIMESTAMP, FilterOperator.LESS_THAN_OR_EQUAL, end);
        CompositeFilter cf1 = new CompositeFilter(CompositeFilterOperator.AND, Arrays.asList(f1, f2));

        Query query = new Query(Regist.TYPE).setAncestor(greenHouseKey);
        query.setFilter(cf1);

        List<Entity> qResults = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
        List<Regist> results = new ArrayList<>(qResults.size());
        for(Entity e: qResults)
            results.add(Regist.fromEntity(e));

        return Response.ok(gson.toJson(results)).build();

    }
    
    @GET
    @Path("/{greenhouse}/{sensor}/current")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public Response currentValues(@PathParam("greenhouse") String ghId, @PathParam("sensor") String sensorId) {
    	
    	Key greenHouseKey = GreenHouse.generateKey(ghId);
    	Query query = new Query(Regist.TYPE).setAncestor(greenHouseKey);
    	

        List<Entity> qResults = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
        List<Regist> results = new ArrayList<>(qResults.size());
        for(Entity e: qResults)
            results.add(Regist.fromEntity(e));

        return Response.ok(gson.toJson(results)).build();
    	
    }	
}
