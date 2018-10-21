package au.edu.unimelb.eldercare.service;

import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseUser;

/**
 * Used as a callback for views that require user authentication
 */
public interface AuthenticationListener {
    /**
     * Called if a user successfully authenticates
     *
     * @param user The authenticated user object
     */
    void userAuthenticated(FirebaseUser user);

    /**
     * Called if user authentication fails
     *
     * @param response An object with more information from the IDP around why authentication failed
     */
    void authenticationFailed(IdpResponse response);
}
