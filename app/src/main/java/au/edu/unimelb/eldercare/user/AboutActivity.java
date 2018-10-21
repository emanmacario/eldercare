package au.edu.unimelb.eldercare.user;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import au.edu.unimelb.eldercare.R;
import au.edu.unimelb.eldercare.service.UserAccessor;
import au.edu.unimelb.eldercare.service.UserService;

public class AboutActivity extends AppCompatActivity implements UserAccessor {

    //Variable Text Views
    private TextView AboutNameUser;
    private TextView AboutEmailUser;
    private TextView AboutUserTypeUser;

    //Database and User references
    private FirebaseUser user;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);

        //Get the current users id
        final String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        UserService.getInstance().getSpecificUser(userID, this);

        //Variable Text Views
        AboutNameUser = findViewById(R.id.AboutNameUser);
        AboutEmailUser = findViewById(R.id.AboutEmailUser);
        AboutUserTypeUser = findViewById(R.id.AboutUserTypeUser);

    }

    @Override
    public void userListLoaded(List<User> users) {
        //not used
    }

    @Override
    public void userLoaded(User value) {
        String mDisplayName= value.getDisplayName();
        AboutNameUser.setText(mDisplayName);

        String mEmail = value.getEmail();
        AboutEmailUser.setText(mEmail);

        String mUserType = value.getUserType();
        AboutUserTypeUser.setText(mUserType);
    }
}
