package pt.agroSmart.resources.GreenHouse;

import com.google.appengine.api.datastore.*;
import pt.agroSmart.StorableObject;


public class GreenHouse extends StorableObject {

    public static final String TYPE = "GreenHouse" ;

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String LATLNG = "Coordinates";
    public static final String CREATOR_USERNAME = "creator username";
    private static final String BOTTOM_LEFT = "Bottom Left";
    private static final String TOP_LEFT = "Top Left";
    private static final String TOP_RIGHT = "Top Right";
    private static final String BOTTOM_RIGHT = "Bottom Right";

    private String id;
    public String name;
    public GeoPt center_coordinates;
    public String creatorUserName;
    public GeoPt topLeft;
    public GeoPt bottomLeft;
    public GeoPt topRight;
    public GeoPt bottomRight;

    public GreenHouse() { }

    public GreenHouse(String id, String creatorUserName, String name, GeoPt center_coordinates, GeoPt topLeft, GeoPt bottomLeft, GeoPt topRight, GeoPt bottomRight){
        
        this.id = id;
        this.name = name;
        this.center_coordinates = center_coordinates;
        this.topLeft = topLeft;
        this.bottomLeft = bottomLeft;
        this.topRight = topRight;
        this.bottomRight = bottomRight;
        this.creatorUserName = creatorUserName;
    }
   

    public GreenHouse(String greenHouseId) {
        super(TYPE, generateKey(greenHouseId));
    }

    public static String generateStringId(GeoPt coordinates){

        return TYPE.concat("@").concat(coordinates.toString());
    }

    public static Key generateKey(String id){

        return KeyFactory.createKey(TYPE, id);
    }


    @Override
    protected Entity encodeEntity() {

        Entity entity = new Entity(TYPE, id);
        entity.setProperty(ID, id);
        entity.setUnindexedProperty(NAME, name);
        entity.setIndexedProperty(LATLNG, center_coordinates);
        entity.setIndexedProperty(BOTTOM_LEFT, bottomLeft);
        entity.setIndexedProperty(TOP_LEFT, topLeft);
        entity.setIndexedProperty(BOTTOM_RIGHT, bottomRight);
        entity.setIndexedProperty(TOP_RIGHT, topRight);
        entity.setIndexedProperty(CREATOR_USERNAME, creatorUserName);
        return entity;
    }

    public static GreenHouse fromEntity(Entity e){

        return new GreenHouse(
                (String) e.getProperty(ID),
                (String) e.getProperty(CREATOR_USERNAME),
                (String) e.getProperty(NAME),
                (GeoPt) e.getProperty(LATLNG),
                (GeoPt) e.getProperty(TOP_LEFT),
                (GeoPt) e.getProperty(BOTTOM_LEFT),
                (GeoPt) e.getProperty(TOP_RIGHT),
                (GeoPt) e.getProperty(BOTTOM_RIGHT)
        );

   }

}
