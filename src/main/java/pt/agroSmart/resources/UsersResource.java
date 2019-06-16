package pt.agroSmart.resources;

import static pt.agroSmart.util.Strings.USER_NOT_FOUND;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.gson.Gson;

import pt.agroSmart.StorableObject;
import pt.agroSmart.resources.User.User;


/**
 *This Resource is responsible for logging users to out app.
 *
 * @author Ruben Silva
 *
 */
@Path("/withtoken/users")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class UsersResource {

	/**
	 * A logger object.
	 */
	private static final Logger LOG = Logger.getLogger(UsersResource.class.getName());
	private static final Gson gson = new Gson();

	private static final String RETRIEVING_USER_INFO = "Retrieving user info.";
	private static final String SENDING_DATA = "Sending request response.";
	private static final String REQUEST_TO_UPDATE_USER_INFO = "Request to update user info.";
	private static final String LISTING_USERS = "Listing Users";

	private static final String USER_INFO_UPDATED = "User Info Updated";
	public UsersResource() { } //Nothing to be done here...



	//TODO: Delete all greenhouses and sensors
	//public Response deleteUser()

	@GET
	@Path("/{user}")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response getUserPersonalInfo(@PathParam("user") String username){
	
		LOG.info(RETRIEVING_USER_INFO);

		User user = new User(username);
		try {
			Entity userEntity = user.ds_get();
			LOG.fine(SENDING_DATA);
			return Response.ok().entity(gson.toJson(User.fromEntity(userEntity))).build();
		} catch (EntityNotFoundException e) {
			LOG.warning(USER_NOT_FOUND);
			return Response.status(Status.NOT_FOUND).build();
		}
	}


	@PUT
	@Path("/{user}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateUser(@PathParam("user") String username,  User data){

		LOG.info(REQUEST_TO_UPDATE_USER_INFO);

		User user = new User(username);
		Entity userEntity;
		try {

			userEntity = user.ds_get();
			user = User.fromEntity(userEntity);
			user.updateInfo(data);
			user.ds_save();

			LOG.fine(USER_INFO_UPDATED);

			return Response.ok().build();

		} catch (EntityNotFoundException e) {

			LOG.warning(USER_NOT_FOUND);
			return Response.status(Status.NOT_FOUND).build();
		}

	}

	@GET
	@Path("/withtoken/")
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response listUsers(){


		LOG.info(LISTING_USERS);
		List<Entity> entity_users = StorableObject.list(User.TYPE, null );
		List<User> users = new ArrayList<>(entity_users.size());
		for(Entity uEntity : entity_users)
			users.add(User.fromEntity(uEntity));

		return Response.ok(gson.toJson(users)).build();
	}

}