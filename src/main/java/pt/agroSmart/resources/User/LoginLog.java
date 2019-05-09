package pt.agroSmart.resources.User;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import pt.agroSmart.StorableObject;

public class LoginLog extends StorableObject {


    public static final String PASSWORD_CORRECT = "Password is correct";

    public static final String TYPE = "UserLog";

    private static final String USER_IP = "user_login_ip";
    private static final String USER_HOST = "user_login_host";
    private static final String USER_LATLON = "user_login_latlon";
    private static final String LOGIN_LOGIN_CITY = "user_login_city";
    private static final String USER_LOGIN_COUNTRY = "user_login_country";
    private static final String LOGIN_DATE = "user_login_time";


    private String userIp;
    private String userHost;
    private String userLatLon;
    private String loginCity;
    private String loginCountry;
    private long loginDate;

    public LoginLog() { }
    
    public LoginLog(String userIp, String userHost, String userLatLon, String loginCity, String loginCountry, long loginDate){
        super(TYPE, generateKey(loginDate));
        this.userIp = userIp;
        this.userHost = userHost;
        this.userLatLon = userLatLon;
        this.loginCity = loginCity;
        this.loginCountry = loginCountry;
        this.loginDate = loginDate;
    }
    
    private static Key generateKey(long loginDate) {

        return KeyFactory.createKey(TYPE, loginDate);

    }

    @Override
    protected Entity encodeEntity() {

        Entity entity = new Entity(TYPE, loginDate);
        entity.setUnindexedProperty(USER_IP, userIp);
        entity.setUnindexedProperty(USER_HOST, userHost);
        entity.setUnindexedProperty(USER_LATLON, userLatLon);
        entity.setUnindexedProperty(LOGIN_LOGIN_CITY, loginCity);
        entity.setUnindexedProperty(USER_LOGIN_COUNTRY, loginCountry);
        entity.setUnindexedProperty(LOGIN_DATE, loginDate);

        return entity;
    }


    public static LoginLog fromEntity(Entity e) {

        return new LoginLog(
            (String) e.getProperty(USER_IP),
            (String) e.getProperty(USER_HOST),
            (String) e.getProperty(USER_LATLON),
            (String) e.getProperty(LOGIN_LOGIN_CITY),
            (String) e.getProperty(USER_LOGIN_COUNTRY),
            (long) e.getProperty(LOGIN_DATE)
        );

    }

}
