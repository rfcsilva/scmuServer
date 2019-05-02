package pt.agroSmart.resources;

import java.util.*;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.google.appengine.api.datastore.*;

import pt.agroSmart.resources.User.*;
import pt.agroSmart.util.InformationChecker;
import pt.agroSmart.util.PasswordEncriptor;
import pt.agroSmart.util.Strings;

import static pt.agroSmart.util.InformationChecker.validEmail;
import static pt.agroSmart.util.InformationChecker.validPassword;


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

	public UsersResource() { } //Nothing to be done here...


	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response registerUserV3(User data) {
		LOG.fine("Attempt to register user: " + data.username);
		if( !InformationChecker.validRegistration(data.username, data.password, data.confirmation_password,  data.email, data.role) ) {

			return Response.status(Status.BAD_REQUEST).entity(Strings.FAILED_REQUIERED_PARAMS).build();
		}

		Transaction txn = datastore.beginTransaction(TransactionOptions.Builder.withXG(true));
		User user;
		Key key = User.generateKey(data.username);

		try {
			// If the entity does not exist an Exception is thrown. Otherwise,
			datastore.get(key);
			txn.rollback();
			return Response.status(Status.BAD_REQUEST).entity(Strings.ALREADY_EXISTS).build();

		} catch (EntityNotFoundException e) {

			if(!validPassword(data.password, data.confirmation_password)){
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

			user = new User(data.username, PasswordEncriptor.get_sha256_HMAC_SecurePassword(data.password), PasswordEncriptor.get_sha256_HMAC_SecurePassword(data.confirmation_password), data.name, data.email, data.phoneNumber, data.role, data.company );

			if (user.ds_save(txn)) {
				txn.commit();
				return Response.status(Status.OK).build();
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
		Key userKey = User.generateKey(data.username);
		try {
			Entity user = datastore.get(userKey);

			// Obtain the user login statistics
			Query ctrQuery = new Query(UserStats.TYPE).setAncestor(userKey);
			List<Entity> results = datastore.prepare(ctrQuery).asList(FetchOptions.Builder.withDefaults());
			UserStats ustats ;
			if (results.isEmpty()) {
				ustats = new UserStats( data.username );
			} else {
				ustats = UserStats.fromEntity(results.get(0));
			}

			String hashedPWD = (String) user.getProperty(User.PASSWORD);
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
					query.setAncestor(userKey);
					token_entity = datastore.prepare(query).asSingleEntity();
					token = (String) token_entity.getProperty(Strings.TOKEN);
					AuthToken.verifier.verify(token);

				}catch(JWTVerificationException e  ) {

					//Creating the token
                    AuthToken authToken = new AuthToken(data.username, userKey);
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

}