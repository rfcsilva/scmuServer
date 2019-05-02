package pt.agroSmart.resources.User;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import pt.agroSmart.StorableObject;


public class UserStats  extends StorableObject {

    private static final String USER = "user";
    private static final String USER_STATS_LOGIN = "user_stats_logins";
    private static final String USER_STATS_FAILED = "user_stats_failed";
    private static final String LAST_LOGIN = "user_last_login";

    public static final String TYPE = "UserStats";
    private static final int INITIAL = 0;

    private String user;
    private int user_stats_logins;
    private int user_stats_failed;
    private long user_last_login;

    public UserStats(String user){
        super(TYPE, generateKey(user));
        this.user = user;
        user_stats_logins = INITIAL;
        user_stats_failed = INITIAL;
        user_last_login = INITIAL;
    }

    public UserStats(String user, int user_stats_logins, int user_stats_failed, long lastLogin){
        super(TYPE, generateKey(user));
        this.user_stats_logins = user_stats_logins;
        this.user_stats_failed = user_stats_failed;
        this.user_last_login = lastLogin;
    }

    public static Key generateKey(String user){
        return KeyFactory.createKey(TYPE, user);
    }
    public void incrementLogins(){ user_stats_logins+=1L; }

    public void incrementFails(){ user_stats_failed+=1L; }

    public void setLastLogin(long date){ user_last_login = date; }

    @Override
    protected Entity encodeEntity() {

        Entity entity = new Entity(TYPE, user);
        entity.setIndexedProperty(USER, user);
        entity.setUnindexedProperty(USER_STATS_LOGIN, user_stats_logins);
        entity.setUnindexedProperty(USER_STATS_FAILED, user_stats_failed);
        entity.setUnindexedProperty(LAST_LOGIN, user_last_login);


        return entity;
    }

    public static UserStats fromEntity(Entity e) {

        return new UserStats(
                (String) e.getProperty(USER),
                (int) e.getProperty(USER_STATS_LOGIN),
                (int) e.getProperty(USER_STATS_FAILED),
                (long) e.getProperty(LAST_LOGIN)
        );

    }
}
