package au.edu.unimelb.eldercare.user;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import au.edu.unimelb.eldercare.R;

public class AboutActivity extends AppCompatActivity {

    //Static Text Views
    TextView AboutPageHeading;
    TextView AboutNameStatic;
    TextView AboutEmailStatic;
    TextView AboutUserTypeStatic;

    //Variable Text Views
    TextView AboutNameUser;
    TextView AboutEmailUser;
    TextView AboutUserTypeUser;

    //Database and User references
    FirebaseUser user;
    DatabaseReference mDatabase;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set the correct Layout
        setContentView(R.layout.about_activity);

        //Static Text Views
        AboutPageHeading = findViewById(R.id.AboutPageHeading);
        AboutNameStatic = findViewById(R.id.AboutNameStatic);
        AboutEmailStatic = findViewById(R.id.AboutEmailStatic);
        AboutUserTypeStatic = findViewById(R.id.AboutUserTypeStatic);

        //Variable Text Views
        AboutNameUser = findViewById(R.id.AboutNameUser);
        AboutEmailUser = findViewById(R.id.AboutEmailUser);
        AboutUserTypeUser = findViewById(R.id.AboutUserTypeUser);

        //Database and User references
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(this.user.getUid());

        //This Listener gets values from the database snapshot and sets the appropriate text views
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                String userName = user.getDisplayName();
                String userEmail = user.getEmail();
                String userType = user.getUserType();

                AboutNameUser.setText(userName);
                AboutEmailUser.setText(userEmail);
                AboutUserTypeUser.setText(userType);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
