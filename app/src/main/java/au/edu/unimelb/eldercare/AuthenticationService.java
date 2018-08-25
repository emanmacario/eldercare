package au.edu.unimelb.eldercare;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class AuthenticationService {
    private static final String TAG_AUTHENTICATION = "ElderCare_Authentication";

    public static final int RC_SIGN_IN = 2606;

    private static final List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.PhoneBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build(),
            new AuthUI.IdpConfig.FacebookBuilder().build()
    );

    private static AuthenticationService instance;

    public static AuthenticationService getAuthenticationService() {
        if (instance == null) {
            instance = new AuthenticationService();
        }
        return instance;
    }

    private FirebaseUser user;

    public FirebaseUser getUser(Authenticator sender) {
        if (user == null) {
            startAuthentication(sender);
            return null;
        } else {
            return user;
        }
    }

    public void startAuthentication(Authenticator sender) {
        sender.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    public void handleAuthenticationRequestCallback(int resultCode, @Nullable Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (resultCode == RESULT_OK && (user = FirebaseAuth.getInstance().getCurrentUser()) != null) {
            Log.i(TAG_AUTHENTICATION, String.format("User %s (id %s) signed in ", user.getDisplayName(), user.getUid()));
        } else {
            if (response == null || response.getError() == null) {
                Log.w(TAG_AUTHENTICATION, "User cancelled authentication");
            } else {
                Log.w(TAG_AUTHENTICATION, String.format("Authentication failed with error %s", response.getError().getErrorCode()), response.getError());
            }
        }
    }

}
