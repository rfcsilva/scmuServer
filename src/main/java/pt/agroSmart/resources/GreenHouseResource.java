package pt.agroSmart.resources;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.*;
import com.google.gson.Gson;
import pt.agroSmart.resources.GreenHouse.GreenHouse;
import pt.agroSmart.resources.User.AuthToken;
import pt.agroSmart.resources.User.User;
import pt.agroSmart.util.InformationChecker;
import pt.agroSmart.util.Strings;

import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


@Path("/withtoken/greenhouse")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class GreenHouseResource {

    private static final Logger LOG = Logger.getLogger(UsersResource.class.getName());
    private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private static final Gson gson = new Gson();

    private static final String ALREADY_EXISTS = "ALREADY EXISTS";
    private static final String ATTEMPT_TO_CREATE_GREENHOUSE = "Attempt to create greenhouse";
    private static final String INVALID_COORDINATES = "INVALID COORDINATES";
    private static final String GREENHOUSE_ALREADY_EXISTS = "GREENHOUSE_ALREADY_EXISTS";
    private static final String GREEN_HOUSE_CREATED = "GreenHouse Created";

    public GreenHouseResource(){ }

    @POST
    @Path("/new")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createGreenHouse(GreenHouse gh){

        String id = GreenHouse.generateStringId(gh.center_coordinates);
        LOG.info(ATTEMPT_TO_CREATE_GREENHOUSE + id);

        if( !InformationChecker.validPosition(new GeoPt[]{gh.center_coordinates, gh.bottomLeft, gh.topLeft, gh.bottomRight, gh.topRight} )) {

            LOG.warning(INVALID_COORDINATES);
            return Response.status(Status.BAD_REQUEST).entity(Strings.FAILED_REQUIERED_PARAMS).build();
        }

        Transaction txn = datastore.beginTransaction(TransactionOptions.Builder.withXG(true));
        GreenHouse greenHouse = new GreenHouse(id);

        try {
            // If the entity does not exist an Exception is thrown. Otherwise,
            greenHouse.ds_get();
            txn.rollback();
            LOG.warning(GREENHOUSE_ALREADY_EXISTS);
            return Response.status(Status.BAD_REQUEST).entity(ALREADY_EXISTS).build();

        } catch (EntityNotFoundException e) {

            greenHouse = new GreenHouse(id, gh.creatorUserName, gh.name, gh.center_coordinates, gh.topLeft, gh.bottomLeft, gh.topRight, gh.bottomRight);

            if (greenHouse.ds_save(txn)) {
                LOG.fine(GREEN_HOUSE_CREATED);
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

    @GET
    @Path("/{greenhouse}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInfo(HttpHeaders headers, @PathParam("greenhouse") String greenHouse){

        DecodedJWT token = AuthToken.getDecodedToken(AuthToken.getTokenFromHeaders(headers));
        String userName = token.getClaim(User.TYPE).asString();

        try {
            User user = new User(userName);
            user.ds_get();

            GreenHouse gh = new GreenHouse(greenHouse);
            Entity e = gh.ds_get();
            gh = GreenHouse.fromEntity(e);

            return Response.ok(gson.toJson(gh)).build();

        } catch (EntityNotFoundException e) {
            return Response.status(Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listGreenHouses(HttpHeaders headers) {

        DecodedJWT token = AuthToken.getDecodedToken(AuthToken.getTokenFromHeaders(headers));
        String userName = token.getClaim(User.TYPE).asString();

        Filter f1 = new FilterPredicate(GreenHouse.CREATOR_USERNAME, FilterOperator.EQUAL, userName);
        Query query = new Query(GreenHouse.TYPE);
        query.setFilter(f1);

        List<Entity> ghEntities = datastore.prepare(query).asList(FetchOptions.Builder.withDefaults());
        List<GreenHouse> greenHouses = new ArrayList<>(ghEntities.size());

        for (Entity e : ghEntities)
            greenHouses.add(GreenHouse.fromEntity(e));

        return Response.ok(gson.toJson(greenHouses)).build();

    }
}
