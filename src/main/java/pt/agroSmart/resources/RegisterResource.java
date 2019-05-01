package pt.agroSmart.resources;

import com.google.appengine.api.datastore.*;
import pt.agroSmart.resources.User.User;
import pt.agroSmart.util.InformationChecker;
import pt.agroSmart.util.Strings;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.Date;
import java.util.logging.Logger;

import static pt.agroSmart.util.InformationChecker.validEmail;
import static pt.agroSmart.util.InformationChecker.validPassword;

@Path("/register")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RegisterResource {

	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	public RegisterResource() { } //Nothing to be done here...


	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response registerUserV3(User data) {
		LOG.fine("Attempt to register user: " + data.userName);

		if( !InformationChecker.validRegistration(data.userName, data.password, data.confirmationPassword,  data.email, data.role) ) {

			return Response.status(Status.BAD_REQUEST).entity(Strings.FAILED_REQUIERED_PARAMS).build();
		}

		Transaction txn = datastore.beginTransaction(TransactionOptions.Builder.withXG(true));
		User user;
		Key key = User.generateKey(data.userName);
		Entity specializedRole;
		Date creationDate = new Date();

		try {
			// If the entity does not exist an Exception is thrown. Otherwise,
			datastore.get(key);
			txn.rollback();
			return Response.status(Status.BAD_REQUEST).entity(Strings.ALREADY_EXISTS).build();

		} catch (EntityNotFoundException e) {

			if(!validPassword(data.password, data.confirmationPassword  )){
				LOG.info("ERROR: The password is not valid.");
				txn.rollback();
				return Response.status(Status.UNAUTHORIZED).entity("New pass is not valid").build();
			}


			if(data.email!=null) {
				if(!validEmail(data.email)) {
					LOG.warning("ERROR: INVALID EMAIL");
					txn.rollback();
					return Response.status(Status.BAD_REQUEST).entity(Strings.FAILED_REQUIERED_PARAMS).build();
				}
			}


			user = new User(data.userName, data.password, data.confirmationPassword, data.name, data.email, data.phoneNumber, data.role, data.company );
			if (user.ds_save(txn))
				return Response.status(Status.OK).build();

			txn.rollback();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();

		} finally {
			if (txn.isActive() ) {
				txn.rollback();
			}
		}
	}



}
