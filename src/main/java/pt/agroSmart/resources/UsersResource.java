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

import com.google.appengine.api.datastore.*;

import pt.agroSmart.resources.User.AuthToken;
import pt.agroSmart.resources.User.LoginData;
import pt.agroSmart.resources.User.User;
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
			Query ctrQuery = new Query(Strings.USER_STATS_KIND).setAncestor(userKey);
			List<Entity> results = datastore.prepare(ctrQuery).asList(FetchOptions.Builder.withDefaults());
			Entity ustats ;
			if (results.isEmpty()) {
				ustats = new Entity(Strings.USER_STATS_KIND, user.getKey() );
				ustats.setProperty(Strings.USER_STATS_LOGIN, 0L);
				ustats.setProperty(Strings.USER_STATS_FAILED, 0L);
			} else {
				ustats = results.get(0);
			}

			String hashedPWD = (String) user.getProperty(User.PASSWORD);
			System.out.println(hashedPWD == null);
			if (hashedPWD.equals(PasswordEncriptor.get_sha256_HMAC_SecurePassword(data.password))) {

				// Password correct
				LOG.info(Strings.PASSWORD_CORRECT);
				// Construct the logs
				Entity log = new Entity(Strings.USER_LOG, user.getKey());
				log.setProperty(Strings.USER_IP, request.getRemoteAddr());
				log.setProperty(Strings.USER_HOST, request.getRemoteHost());
				log.setProperty(Strings.USER_LATLON, headers.getHeaderString("X-AppEngine-CityLatLong"));
				log.setProperty(Strings.LOGIN_LOGIN_CITY, headers.getHeaderString("X-AppEngine-City"));
				log.setProperty(Strings.USER_LOGIN_COUNTRY, headers.getHeaderString("X-AppEngine-Country"));
				log.setProperty(Strings.LOGIN_DATE, new Date());
				// Get the user statistics and updates it
				ustats.setProperty(Strings.USER_STATS_LOGIN, 1L + (long) ustats.getProperty("user_stats_logins"));
				ustats.setProperty(Strings.USER_STATS_FAILED, 0L );
				ustats.setProperty(Strings.USER_STATS_LASTLOGIN, new Date());

				List<Entity> toStore = Arrays.asList(log,ustats);

				Algorithm algorithm = Algorithm.HMAC256(Strings.SECRET);

				Entity token_entity ;
				String token;
				try {

					JWTVerifier verifier = JWT.require(algorithm)
							.withIssuer("agroSmart")
							.build();

					Query query = new Query(AuthToken.TYPE);
					query.setAncestor(userKey);
					token_entity = datastore.prepare(query).asSingleEntity();
					token = (String) token_entity.getProperty(Strings.TOKEN);
					verifier.verify(token);
					//TODO ver por id
				}catch(Exception e  ) {

					//Creating the token
					Date expiration = new Date(System.currentTimeMillis() + Strings.EXPIRATION_TIME);
					String token_id = UUID.randomUUID().toString();
					token = JWT.create()
							.withExpiresAt(expiration)
							.withIssuer("maisverde")
							.withClaim(Strings.USERNAME, data.username)
							.withIssuedAt(new Date())
							.withJWTId(token_id)
							.sign(algorithm)
					;

					token_entity = new Entity(AuthToken.TYPE, token_id, userKey );
					token_entity.setUnindexedProperty(Strings.TOKEN_ID, token_id);
					token_entity.setProperty(Strings.USERNAME, data.username);
					token_entity.setUnindexedProperty(Strings.TOKEN, token);
					token_entity.setProperty(Strings.EXPIRATION_TIME_MSG, expiration);

					toStore = Arrays.asList(log,ustats,token_entity);
				}

				datastore.put(txn,toStore);

				LOG.info("User " + data.username + "' logged in sucessfully.");
				txn.commit();
				return Response.ok().header(HttpHeaders.AUTHORIZATION, "Bearer " + token).build();
			} else {
				// Incorrect password
				ustats.setProperty(Strings.USER_STATS_FAILED, 1L + (long) ustats.getProperty(Strings.USER_STATS_FAILED));
				datastore.put(txn,ustats);
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