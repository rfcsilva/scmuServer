package pt.agroSmart.resources.Sensor;

import com.google.appengine.api.datastore.Entity;
import pt.agroSmart.StorableObject;

public class Sensor extends StorableObject {

    private static final String TYPE = "Sensor";

    private String id;
    public String type;
    public String greenHouse;

    public Sensor(String id, String greenHouse){
        super(TYPE, generateKey());
    }



    @Override
    protected Entity encodeEntity() {
        return null;
    }
}
