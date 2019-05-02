package pt.agroSmart.resources.User;

import com.google.appengine.api.datastore.Entity;
import pt.agroSmart.StorableObject;

import java.util.UUID;

public class AuthToken extends StorableObject {

	public static final String TYPE = "token";

	public static final long EXPIRATION_TIME = 1000*60*60*2; //2h
	
	public String username;
	public String tokenID;
	public long creationData;
	public long expirationData;

	public AuthToken(String username) {
		this.username = username;
		this.tokenID = UUID.randomUUID().toString();
		this.creationData = System.currentTimeMillis();
		this.expirationData = this.creationData + AuthToken.EXPIRATION_TIME;
	}

	@Override
	protected Entity encodeEntity() {
		return null;
	}


}
