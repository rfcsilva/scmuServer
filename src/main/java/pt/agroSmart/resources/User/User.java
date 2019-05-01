package pt.agroSmart.resources.User;


import com.google.appengine.api.datastore.*;
import pt.agroSmart.StorableObject;
import pt.agroSmart.resources.LoginResource;

import java.util.logging.Logger;

public class User extends StorableObject {

    private static final Logger LOG = Logger.getLogger(User.class.getName());
    private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    public static final String TYPE = "User";

    public String userName;
    public String name;
    public String email;
    public int phoneNumber;

    public User(String userName, String name, String email, int phoneNumber){
        super(TYPE, generateKey(userName)) ;
        this.userName = userName;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    @Override
    protected Entity encodeEntity() {

        Entity userEntity = new Entity(TYPE,userName);


        return userEntity;

    }

    @Override
    protected void fromEntity(Entity e) {


    }

    public static Key generateKey(String userName) {
        return KeyFactory.createKey(TYPE, userName);
    }
}
