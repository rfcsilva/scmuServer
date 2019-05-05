package pt.agroSmart.resources.User;

import pt.agroSmart.resources.Sensor.Regist.Regist;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/regist")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RegistResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addRead(Regist r){




        Regist regist = new Regist(r.greenHouseId, r.temperature, r.soilHumidity, r.airHumidity, r.waterLevel, r.luminosity, r.steam, r.timeStamp);
        regist.ds_save();

        return null;
    }



}
