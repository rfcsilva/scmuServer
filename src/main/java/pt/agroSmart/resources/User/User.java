package pt.agroSmart.resources.User;


import com.google.appengine.api.datastore.*;
import pt.agroSmart.StorableObject;

public class User extends StorableObject {

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String PHONE_NUMBER = "phone number";
    public static final String ROLE = "role";
    public static final String COMPANY = "company";


    private static final String TYPE = "User";

    public String username;
    public String password;
    public String confirmation_password;
    public String name;
    public String email;
    public String phoneNumber;
    public String role;
    public String company;

    public User(){ super(); }

    public User(String username, String password, String confirmation_password, String name, String email, String phoneNumber, String role, String company){
        super(TYPE, generateKey(username)) ;
        this.username = username;
        this.password = password;
        this.confirmation_password = confirmation_password;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.company = company;
    }

    public User(String username){
        super(TYPE,generateKey(username));
        this.username = username;
    }

    protected User(String username, String password, String name, String email, String phoneNumber, String role, String company){
        super(TYPE, generateKey(username));
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.company = company;
    }

    public String getPassword(){ return  password; }

    @Override
    protected Entity encodeEntity() {

        Entity userEntity = new Entity(TYPE, username);
        userEntity.setProperty(USERNAME, username);
        userEntity.setUnindexedProperty(PASSWORD, password);
        userEntity.setUnindexedProperty(NAME, name);
        userEntity.setUnindexedProperty(EMAIL, email);
        userEntity.setUnindexedProperty(PHONE_NUMBER, phoneNumber);
        userEntity.setProperty(ROLE, role);
        userEntity.setProperty(COMPANY, company);

        return userEntity;

    }


    public static User fromEntity(Entity e) {

        return new User(
                (String) e.getProperty(USERNAME),
                (String) e.getProperty(PASSWORD),
                (String) e.getProperty(NAME),
                (String) e.getProperty(EMAIL),
                (String) e.getProperty(PHONE_NUMBER),
                (String) e.getProperty(ROLE),
                (String) e.getProperty(COMPANY)
        );

    }

    private static Key generateKey(String userName) {
        return KeyFactory.createKey(TYPE, userName);
    }

    public void updateInfo(User data) {

        if(data.name != null)
            name = data.name;

        if(data.email!= null)
            email = data.email;

        if(data.phoneNumber != null)
            phoneNumber = data.phoneNumber;

        if(data.company != null)
            company = data.company;
    }
}
