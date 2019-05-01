package pt.agroSmart.resources.User;

import com.google.appengine.api.datastore.Entity;
import pt.agroSmart.StorableObject;

public class LoginLog extends StorableObject {


    public LoginLog(){
        super();
    }

    @Override
    protected Entity encodeEntity() {
        return null;
    }

    @Override
    protected void fromEntity(Entity e) {

    }

}
