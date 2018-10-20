package au.edu.unimelb.eldercare.helpers;

import android.util.Patterns;

public class EmailValidator {

    /**
     * Validates that an email address is valid according to RFC5321
     * @param email The email address to validate
     * @return True if the email address conforms to RFC5321, false otherwise
     */
    public static boolean isEmailValid(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
