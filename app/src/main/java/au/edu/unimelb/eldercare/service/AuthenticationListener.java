package au.edu.unimelb.eldercare.service;

import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseUser;

public interface AuthenticationListener {
    void userAuthenticated(FirebaseUser user);

    void authenticationFailed(IdpResponse response);
}
