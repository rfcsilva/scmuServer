package pt.agroSmart.resources;

import com.google.appengine.api.datastore.*;
import pt.agroSmart.resources.GreenHouse.GreenHouse;
import pt.agroSmart.util.InformationChecker;
import pt.agroSmart.util.Strings;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;
import javax.ws.rs.core.Response.Status;


@Path("/greenhouse")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class GreenHouseResource {

    private static final String ALREADY_EXISTS = "ALREADY EXISTS";
    private static final Logger LOG = Logger.getLogger(UsersResource.class.getName());
    private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private static final String ATTEMPT_TO_CREATE_GREENHOUSE = "Attempt to create greenhouse";
    private static final String INVALID_COORDINATES = "INVALID COORDINATES";
    private static final String GREENHOUSE_ALREADY_EXISTS = "GREENHOUSE_ALREADY_EXISTS";
    private static final String GREEN_HOUSE_CREATED = "GreenHouse Created";

    public GreenHouseResource(){ }

    @POST
    @Path("/new")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createGreenHouse(GreenHouse gh){

        String id = GreenHouse.generateStringId(gh.coordinates);
        LOG.info(ATTEMPT_TO_CREATE_GREENHOUSE + id);

        if( !InformationChecker.validPosition(gh.coordinates) ) {

            LOG.warning(INVALID_COORDINATES);
            return Response.status(Status.BAD_REQUEST).entity(Strings.FAILED_REQUIERED_PARAMS).build();
        }

        Transaction txn = datastore.beginTransaction(TransactionOptions.Builder.withXG(true));
        GreenHouse greenHouse = new GreenHouse(id, gh.creatorUserName);

        try {
            // If the entity does not exist an Exception is thrown. Otherwise,
            greenHouse.ds_get();
            txn.rollback();
            LOG.warning(GREENHOUSE_ALREADY_EXISTS);
            return Response.status(Status.BAD_REQUEST).entity(ALREADY_EXISTS).build();

        } catch (EntityNotFoundException e) {

            greenHouse = new GreenHouse(id, gh.name, gh.coordinates, gh.creatorUserName );

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



}
