package pt.agroSmart.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.commons.validator.routines.EmailValidator;

public class InformationChecker {


    private static final int CC_SIZE = 10;
    private static final int NIF_SIZE = 9;
    private static final int PHONE_NUMBER_SIZE = 9;
    private static final int PASSWORD_MIN_LENGTH = 6;

    public static boolean validRegistration(String username, String password, String confirmation_password,
                                            String email, String role){

        return nonEmptyField(username) && nonEmptyField(password) && nonEmptyField(confirmation_password) &&
                nonEmptyField(email) && validEmail(email) && validPassword( password,  confirmation_password)
                && nonEmptyField(role);
    }

    public static boolean validEmail(String email) {

        return  EmailValidator.getInstance().isValid(email);

    }

    public static boolean validPhoneNumber(String phoneNumber) {

        if(phoneNumber.length() != PHONE_NUMBER_SIZE) return false;

        Pattern pattern = Pattern.compile("^[0-9]+$");
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.find();

    }

    public static boolean validNIF(String nif) {


        if(nif.length() != NIF_SIZE) return false;

        Pattern pattern = Pattern.compile("^[0-9]+$");
        Matcher matcher = pattern.matcher(nif);
        return matcher.find();

    }

    public static boolean validCC(String cc) {

        if(cc.length() != CC_SIZE) return false;

        Pattern pattern = Pattern.compile("[0-9]{6}[-][a-zA-Z]{3}");
        Matcher matcher = pattern.matcher(cc);
        return matcher.find();

    }

    public static boolean validPassword(String password, String confirmationPassword) {

        if(password == null || confirmationPassword == null)
            return false;

        if(password.length() < PASSWORD_MIN_LENGTH || confirmationPassword.length() < PASSWORD_MIN_LENGTH)
            return false;

        return PasswordEncriptor.get_sha256_HMAC_SecurePassword(password).contentEquals(PasswordEncriptor.get_sha256_HMAC_SecurePassword(confirmationPassword));

    }

    private static boolean nonEmptyField(String field) {
        return field != null && !field.isEmpty();
    }

}