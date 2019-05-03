package pt.agroSmart.resources.GreenHouse;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.repackaged.com.google.type.LatLng;
import pt.agroSmart.StorableObject;
import pt.agroSmart.resources.Sensor.Sensor;
import pt.agroSmart.resources.User.User;

import java.util.UUID;

public class GreenHouse extends StorableObject {

    private static final String TYPE = "GreenHouse" ;

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String LATLNG = "Coordinates";
    private static final String CREATOR_USERNAME = "creator username";

    public String id;
    public String name;
    public GeoPt coordinates;
    public String creatorUserName;
    public Sensor[] sensors;

    public GreenHouse() { super(); }

    public GreenHouse(String id, String creatorUserName){

        super(TYPE, generateKey(id, creatorUserName));
        this.id = id;
        this.creatorUserName = creatorUserName;
    }

    public GreenHouse(String id, String name, GeoPt coordinates, String creatorUserName){
        super(TYPE, generateKey(id,creatorUserName));
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creatorUserName = creatorUserName;
    }

    private GreenHouse(String id, String name, GeoPt coordinates, String creatorUserName, Sensor[] sensors){
        super(TYPE, generateKey(id,creatorUserName));
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creatorUserName = creatorUserName;
        this.sensors = sensors;
    }

    public static String generateStringId(GeoPt coordinates){
        return TYPE.concat("@").concat(coordinates.toString());
    }

    public static Key generateKey(String id, String creatorUserName){

        return KeyFactory.createKey(User.generateKey(creatorUserName), TYPE, id);
    }


    @Override
    protected Entity encodeEntity() {

        Entity entity = new Entity(TYPE, id, User.generateKey(creatorUserName));
        entity.setProperty(ID, id);
        entity.setUnindexedProperty(NAME, name);
        entity.setIndexedProperty(LATLNG, coordinates);
        entity.setIndexedProperty(CREATOR_USERNAME, creatorUserName);

        return entity;
    }

    public static GreenHouse fromEntity(Entity e){
        return null;
    }

}
