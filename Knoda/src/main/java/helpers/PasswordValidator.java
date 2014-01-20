package helpers;

/**
 * Created by nick on 1/20/14.
 */
public class PasswordValidator {

    public static Integer passwordMinCharacters = 6;
    public static Integer passwordMaxCharacters = 20;

    public static boolean validate(String password) {
        if (password.length() < passwordMinCharacters || password.length() > passwordMaxCharacters)
            return false;

        return true;
    }

}
