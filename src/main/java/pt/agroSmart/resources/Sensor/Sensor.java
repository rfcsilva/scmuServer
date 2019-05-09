package pt.agroSmart.resources.Sensor;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import pt.agroSmart.StorableObject;

public class Sensor extends StorableObject {

    private static final String TYPE = "Sensor";

    private String id;
    public String physical_phenomenon;


    public Sensor(String id, Key greenHouseKey){
        super(TYPE, generateKey(id, greenHouseKey));
    }

    public Sensor(String id, String physical_phenomenon, Key greenHouseKey){
        super(TYPE, generateKey(id, greenHouseKey));
        this.id = id;
        this.physical_phenomenon = physical_phenomenon;
    }

    public static Key generateKey(String id, Key greenHouseKey){
        return KeyFactory.createKey(greenHouseKey, TYPE, id);
    }


    @Override
    protected Entity encodeEntity() {
        return null;
    }
}
