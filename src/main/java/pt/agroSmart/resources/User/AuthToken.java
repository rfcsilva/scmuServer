package pt.agroSmart.resources.User;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import pt.agroSmart.StorableObject;

import java.util.Date;
import java.util.UUID;

public class AuthToken extends StorableObject {

    public static final String TYPE = "token";
    private static final long EXPIRATION_TIME = 1000*60*60*2; //2h

    private static final String TOKEN_ID = "id";
    private static final String USERNAME = "username";
    private static final String EXPIRATION_TIME_PROP = "expiration_time";

	//TODO: Better way to save this
    private static final String SECRET = "1449F2B6E6710251CC37506AF4C84A11F09DB25DB52CAFC4529D7F476552E90A";
    private static final String ISSUER = "agroSmart";
    private static final Algorithm algorithm = Algorithm.HMAC256(SECRET);
    public static final JWTVerifier verifier = JWT.require(AuthToken.algorithm).withIssuer(AuthToken.ISSUER).build();

    private String username;
    private Key userKey;
    private String id;
	private Date creationDate;
	private Date expirationDate;
    private String token;

	public AuthToken(String username, Key userKey) {
		this.username = username;
		this.userKey = userKey;
		this.id = UUID.randomUUID().toString();
		this.creationDate = new Date(System.currentTimeMillis());
		this.expirationDate = new Date( this.creationDate.getTime() + AuthToken.EXPIRATION_TIME);
	}

	@Override
	protected Entity encodeEntity() {

        Entity token_entity = new Entity(AuthToken.TYPE, id, userKey );
        token_entity.setUnindexedProperty(TOKEN_ID, id);
        token_entity.setProperty(USERNAME,username);
        token_entity.setUnindexedProperty(TYPE, token);
        token_entity.setProperty(EXPIRATION_TIME_PROP, expirationDate);

        return token_entity;
	}

	public String issueToken(){

	    token =  JWT.create()
                .withExpiresAt(expirationDate)
                .withIssuer(ISSUER)
                .withClaim(User.USERNAME, username)
                .withIssuedAt(creationDate)
                .withJWTId(id)
                .sign(algorithm);

        return token;

	}

}
