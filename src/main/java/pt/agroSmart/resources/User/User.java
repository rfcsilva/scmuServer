package pt.agroSmart.resources.User;


import com.google.appengine.api.datastore.*;
import pt.agroSmart.StorableObject;

public class User extends StorableObject {

    public static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String ROLE = "role";

    public static final String TYPE = "User";

    public String username;
    public String password;
    public String confirmation_password;
    public String name;
    public String email;
    public String role;

    public User(){ }

    public User(String username, String password, String confirmation_password, String name, String email, String role){
        super(TYPE, generateKey(username)) ;
        this.username = username;
        this.password = password;
        this.confirmation_password = confirmation_password;
        this.name = name;
        this.email = email;
        this.role = role;

    }

    public User(String username){
        super(TYPE,generateKey(username));
        this.username = username;
    }

    protected User(String username, String name, String email, String role){
        this.username = username;
        this.name = name;
        this.email = email;
        this.role = role;
    }
    
    public String getPassword(){ return  password; }

    @Override
    protected Entity encodeEntity() {

        Entity userEntity = new Entity(TYPE, username);
        userEntity.setProperty(USERNAME, username);
        userEntity.setUnindexedProperty(PASSWORD, password);
        userEntity.setUnindexedProperty(NAME, name);
        userEntity.setUnindexedProperty(EMAIL, email);
        userEntity.setProperty(ROLE, role);

        return userEntity;

    }

   
    public static User fromEntity(Entity e) {

        return new User(
                (String) e.getProperty(USERNAME),
                (String) e.getProperty(NAME),
                (String) e.getProperty(EMAIL),
                (String) e.getProperty(ROLE)
                
        	);

    }

    
    public static String getPassword(Entity e) {
    	return (String) e.getProperty(PASSWORD);
    }
    
    public static Key generateKey(String userName) {
        return KeyFactory.createKey(TYPE, userName);
    }

    public void updateInfo(User data) {

        if(data.name != null)
            name = data.name;

        if(data.email!= null)
            email = data.email;

    }
}
