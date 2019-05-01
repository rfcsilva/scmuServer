package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.util.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.util.LoginData;
import pt.unl.fct.di.apdc.firstwebapp.util.RegisterData;

@Path("/register")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RegisterResource {

	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	public RegisterResource() { } //Nothing to be done here...

	@POST
	@Path("/v1")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registerUserV1(LoginData data) {
		LOG.fine("Attempt to register user: " + data.username);
		
		Entity user = new Entity("User", data.username);
		user.setProperty("user_pwd", DigestUtils.sha512Hex(data.password));
		user.setUnindexedProperty("user_creation_time", new Date());
		datastore.put(user);
		LOG.info("User registered " + data.username);
		return Response.ok().build();
	}
	
	@POST
	@Path("/v2")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response registerUserV2(RegisterData data) {
		LOG.fine("Attempt to register user: " + data.username);
		
		if( ! data.validRegistration() ) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		
		try {
			// If the entity does not exist an Exception is thrown. Otherwise,
			Key userKey = KeyFactory.createKey("User", data.username);
			Entity user = datastore.get(userKey);
			return Response.status(Status.BAD_REQUEST).entity("User already exists.").build(); 
		} catch (EntityNotFoundException e) {
			Entity user = new Entity("User", data.username);
			user.setProperty("user_name", data.name);
			user.setProperty("user_pwd", DigestUtils.sha512Hex(data.password));
			user.setProperty("user_email", data.email);
			user.setUnindexedProperty("user_creation_time", new Date());
			datastore.put(user);
			LOG.info("User registered " + data.username);
			return Response.ok().build();
		}
	}
	
	@POST
	@Path("/v3")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response registerUserV3(RegisterData data) {
		LOG.fine("Attempt to register user: " + data.username);
		
		if( ! data.validRegistration() ) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}
		
		Transaction txn = datastore.beginTransaction();
		try {
			// If the entity does not exist an Exception is thrown. Otherwise,
			Key userKey = KeyFactory.createKey("User", data.username);
			Entity user = datastore.get(userKey);
			txn.rollback();
			return Response.status(Status.BAD_REQUEST).entity("User already exists.").build(); 
		} catch (EntityNotFoundException e) {
			Entity user = new Entity("User", data.username);
			user.setProperty("user_name", data.name);
			user.setProperty("user_pwd", DigestUtils.sha512Hex(data.password));
			user.setProperty("user_email", data.email);
			user.setUnindexedProperty("user_creation_time", new Date());
			datastore.put(txn,user);
			LOG.info("User registered " + data.username);
			txn.commit();
			return Response.ok().build();
		} finally {
			if (txn.isActive() ) {
				txn.rollback();
			}
		}
	}
	


}
