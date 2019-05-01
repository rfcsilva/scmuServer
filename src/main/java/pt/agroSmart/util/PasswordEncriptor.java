package pt.agroSmart.util;


import com.google.api.client.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class PasswordEncriptor {

    public static String get_sha256_HMAC_SecurePassword(String passwordToHash){

        String generatedPassword;
        generatedPassword = null;
        String secret = "secret";

        try {

            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            generatedPassword = Base64.encodeBase64String(sha256_HMAC.doFinal(passwordToHash.getBytes()));

        }
        catch (NoSuchAlgorithmException | InvalidKeyException e){
            e.printStackTrace();

        }

        return generatedPassword;
    }
}