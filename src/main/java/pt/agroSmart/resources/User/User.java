package pt.agroSmart.resources.User;


import com.google.appengine.api.datastore.*;
import pt.agroSmart.StorableObject;

public class User extends StorableObject {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String PHONE_NUMBER = "phone number";
    private static final String ROLE = "role";
    private static final String COMPANY = "company";


    private static final String TYPE = "User";

    public String userName;
    public String password;
    public String confirmationPassword;
    public String name;
    public String email;
    public int phoneNumber;
    public String role;
    public String company;

    public User(){ super(); }

    public User(String userName, String password, String confirmationPassword, String name, String email, int phoneNumber, String role, String company){
        super(TYPE, generateKey(userName)) ;
        this.userName = userName;
        this.password = password;
        this.confirmationPassword = confirmationPassword;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.company = company;
    }

    protected User(String userName, String password, String name, String email, int phoneNumber, String role, String company){
        super(TYPE, generateKey(userName));
        this.userName = userName;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.company = company;
    }

    @Override
    protected Entity encodeEntity() {

        Entity userEntity = new Entity(TYPE,userName);
        userEntity.setProperty(USERNAME, userName);
        userEntity.setUnindexedProperty(PASSWORD, password);
        userEntity.setUnindexedProperty(NAME, name);
        userEntity.setUnindexedProperty(EMAIL, email);
        userEntity.setUnindexedProperty(PHONE_NUMBER, phoneNumber);
        userEntity.setProperty(ROLE, role);
        userEntity.setProperty(COMPANY, company);

        return userEntity;

    }

    @Override
    protected void fromEntity(Entity e) {

        new User(
                (String) e.getProperty(USERNAME),
                (String) e.getProperty(PASSWORD),
                (String) e.getProperty(NAME),
                (String) e.getProperty(EMAIL),
                (int) e.getProperty(PHONE_NUMBER),
                (String) e.getProperty(ROLE),
                (String) e.getProperty(COMPANY)
        );

    }

    public static Key generateKey(String userName) {
        return KeyFactory.createKey(TYPE, userName);
    }

}
