package pt.agroSmart.resources;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.google.appengine.api.datastore.*;
import pt.agroSmart.resources.User.*;
import pt.agroSmart.util.InformationChecker;
import pt.agroSmart.util.PasswordEncriptor;
import pt.agroSmart.util.Strings;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import static pt.agroSmart.util.Strings.USER_NOT_FOUND;


/**
 *This Resource is responsible for logging users to out app.
 *
 * @author Ruben Silva
 *
 */
@Path("/users")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class UsersResource {

	/**
	 * A logger object.
	 */
	private static final Logger LOG = Logger.getLogger(UsersResource.class.getName());
	private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private static final String ATTEMPT_TO_REGISTER_USER = "Attempt to register user: ";
	private static final String ALREADY_EXISTS = "ERROR: User already exists.";
	private static final String USER_RESISTED = "User registered ";
	private static final String INVALID_PARAMS = "Register data is not valid.";
	private static final String USER_ALREADY_EXISTS_ERROR = "User already exists. Aborting register.";
	private static final String RETRIEVING_USER_INFO = "Retrieving user info.";
	private static final String SENDING_DATA = "Sending request response.";
	private static final String REQUEST_TO_UPDATE_USER_INFO = "Request to update user info.";
	private static final String USER_INFO_UPDATED = "User Info Updated";

	public UsersResource() { } //Nothing to be done here...


	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response registerUserV3(User data) {

		LOG.info(ATTEMPT_TO_REGISTER_USER + data.username);

		if( !InformationChecker.validRegistration(data.username, data.password, data.confirmation_password,  data.email, data.phoneNumber, data.role) ) {

			LOG.warning(INVALID_PARAMS);
			return Response.status(Status.BAD_REQUEST).entity(Strings.FAILED_REQUIERED_PARAMS).build();
		}

		Transaction txn = datastore.beginTransaction(TransactionOptions.Builder.withXG(true));
		User user = new User(data.username);

		try {
			// If the entity does not exist an Exception is thrown. Otherwise,
			user.ds_get();
			txn.rollback();
			LOG.warning(USER_ALREADY_EXISTS_ERROR);
			return Response.status(Status.BAD_REQUEST).entity(ALREADY_EXISTS).build();

		} catch (EntityNotFoundException e) {

			user = new User(data.username, PasswordEncriptor.get_sha256_HMAC_SecurePassword(data.password), PasswordEncriptor.get_sha256_HMAC_SecurePassword(data.confirmation_password), data.name, data.email, data.phoneNumber, data.role, data.company );

			if (user.ds_save(txn)) {
				LOG.fine(USER_RESISTED);
				txn.commit();
				return Response.ok().build();
			}

			txn.rollback();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();

		} finally {
			if (txn.isActive() ) {
				txn.rollback();
			}
		}
	}




	/**
	 * To Login you have to  use HTTP POST request and send a JSON object with your username and password.
	 * @param data JSON obj with username and password.
	 * @param request - HTTP request.
	 * @param headers - HTTP Request headers
	 * @return Authentication token - HTTP Header "Authorization".
	 */
	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doLoginV2(LoginData data,
							  @Context HttpServletRequest request,
							  @Context HttpHeaders headers) {

		LOG.info(Strings.ABOUT_TO_LOGIN + data.username);

		Transaction txn = datastore.beginTransaction(TransactionOptions.Builder.withXG(true));
		User user = new User(data.username);

		try {

			Entity userEntity = user.ds_get();
			user = User.fromEntity(userEntity);

			// Obtain the user login statistics
			List<Entity> results = UserStats.list(UserStats.TYPE, userEntity.getKey());
			UserStats ustats;
			if (results.isEmpty()) {
				ustats = new UserStats( data.username );
			} else {
				ustats = UserStats.fromEntity(results.get(0));
			}

			String hashedPWD = user.getPassword();
			if (hashedPWD.equals(PasswordEncriptor.get_sha256_HMAC_SecurePassword(data.password))) {

				// Password correct
				LOG.info(LoginLog.PASSWORD_CORRECT);

				// Construct the logs
				long loginDate = System.currentTimeMillis();
				LoginLog log = new LoginLog(request.getRemoteAddr(), request.getRemoteHost(), headers.getHeaderString("X-AppEngine-CityLatLong"),
						headers.getHeaderString("X-AppEngine-City"),  headers.getHeaderString("X-AppEngine-Country"), loginDate);

				// Get the user statistics and updates it
				ustats.incrementLogins();
				ustats.setLastLogin(loginDate);

				Entity token_entity;
				String token;
				try {

					Query query = new Query(AuthToken.TYPE);
					query.setAncestor(userEntity.getKey());
					token_entity = datastore.prepare(query).asSingleEntity();
					token = (String) token_entity.getProperty(Strings.TOKEN);
					AuthToken.verifier.verify(token);

				}catch(JWTVerificationException e  ) {

					//Creating the token
                    AuthToken authToken = new AuthToken(UUID.randomUUID().toString(), data.username, userEntity.getKey());
                    token = authToken.issueToken();
                    authToken.ds_save(txn);

				}

				log.ds_save(txn);
				ustats.ds_save(txn);


				LOG.info("User " + data.username + "' logged in sucessfully.");
				txn.commit();
				return Response.ok().header(HttpHeaders.AUTHORIZATION, "Bearer " + token).build();
			} else {

				// Incorrect password
				ustats.incrementFails();
				ustats.ds_save(txn);
				txn.commit();

				LOG.warning(Strings.WRONG_PASSWORD + data.username);
				return Response.status(Status.FORBIDDEN).entity(Strings.WRONG_PASSWORD).build();
			}

		} catch (EntityNotFoundException e) {
			// Username does not exist
			txn.rollback();
			LOG.warning("Failed login attempt for username: " + data.username);
			return Response.status(Status.NOT_FOUND).build();

		} catch (JWTCreationException exception){
			//Invalid Signing configuration / Couldn't convert Claims.
			txn.rollback();
			return Response.status(Status.BAD_REQUEST).build();
		}catch(Exception e) {
			e.printStackTrace();
			txn.rollback();
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}

	}

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
			return Response.ok().entity(User.fromEntity(userEntity)).build();
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

}