package pt.agroSmart.resources;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.appengine.api.datastore.*;
import com.google.gson.Gson;
import pt.agroSmart.resources.GreenHouse.Config;
import pt.agroSmart.resources.GreenHouse.GreenHouse;
import pt.agroSmart.resources.User.AuthToken;
import pt.agroSmart.resources.User.User;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import java.util.UUID;
import java.util.logging.Logger;

@Path("/withtoken/config")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ConfigResource {

	private static final Logger LOG = Logger.getLogger(UsersResource.class.getName());
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private static final Gson gson = new Gson();

	public ConfigResource() { }

	@POST
	@Path("/new/{greenhouse}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setConfig(@Context HttpHeaders headers, @PathParam("greenhouse") String greenHouseId, Config argConfig){

		try {

			String configId;

			GreenHouse greenHouse = new GreenHouse(greenHouseId);
			greenHouse = GreenHouse.fromEntity(greenHouse.ds_get());

			DecodedJWT token = AuthToken.getDecodedToken(AuthToken.getTokenFromHeaders(headers));
			String userName = token.getClaim(User.USERNAME).asString();

			if(!greenHouse.creatorUserName.equals(userName))
				return Response.status(Status.FORBIDDEN).build();

			Key greenHouseKey = GreenHouse.generateKey(greenHouseId);
			Query query = new Query(Config.TYPE).setAncestor(greenHouseKey);
			Entity e = datastore.prepare(query).asSingleEntity();
			if(e != null) {
				LOG.info("Updating config");
				configId = (String) e.getProperty(Config.ID); 
			}else {
				LOG.info("New Config");
				configId = UUID.randomUUID().toString();
			}

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

	@GET
	@Path("/{greenhouse}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response getConfig(@Context HttpHeaders headers, @PathParam("greenhouse") String greenhouseId) {

		try {
			
			GreenHouse greenHouse = new GreenHouse(greenhouseId);
			greenHouse = GreenHouse.fromEntity(greenHouse.ds_get());

			DecodedJWT token = AuthToken.getDecodedToken(AuthToken.getTokenFromHeaders(headers));
			String userName = token.getClaim(User.USERNAME).asString();

			if(!greenHouse.creatorUserName.equals(userName))
				return Response.status(Status.FORBIDDEN).build();

			Key greenHouseKey = GreenHouse.generateKey(greenhouseId);
			Query query = new Query(Config.TYPE).setAncestor(greenHouseKey);
			Entity e = datastore.prepare(query).asSingleEntity();
			
			return Response.ok(gson.toJson(Config.fromEntity(e))).build();
		
		} catch (EntityNotFoundException e1) {
			return Response.status(Status.NOT_FOUND).build();
		}

	}
}