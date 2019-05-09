package pt.agroSmart.util;

import com.google.appengine.api.datastore.GeoPt;
import org.apache.commons.validator.routines.EmailValidator;

public class InformationChecker {


    private static final int PASSWORD_MIN_LENGTH = 6;

    public static boolean validRegistration(String username, String password, String confirmation_password,
                                            String email, String role){

        return nonEmptyField(username) && nonEmptyField(password) && nonEmptyField(confirmation_password) &&
                nonEmptyField(email) && validEmail(email) && validPassword( password,  confirmation_password)
                && nonEmptyField(role);
    }

    private static boolean validEmail(String email) {

        return  EmailValidator.getInstance().isValid(email);

    }

     private static boolean validPassword(String password, String confirmationPassword) {

        if(password == null || confirmationPassword == null)
            return false;

        if(password.length() < PASSWORD_MIN_LENGTH || confirmationPassword.length() < PASSWORD_MIN_LENGTH)
            return false;

        return PasswordEncriptor.get_sha256_HMAC_SecurePassword(password).contentEquals(PasswordEncriptor.get_sha256_HMAC_SecurePassword(confirmationPassword));

    }

    public static boolean validPosition(GeoPt[] points){

        for(GeoPt point: points)
            if(point.getLatitude() > 90 || (point.getLatitude() < -90) || (point.getLongitude() > 180) || (point.getLongitude() < -180))
                return false;

        return true;
    }

    private static boolean nonEmptyField(String field) {
        return field != null && !field.isEmpty();
    }

}