package au.edu.unimelb.eldercare.helpers;

import android.util.Patterns;

public class EmailValidifier {

    public static boolean isEmailValid(CharSequence email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
