package au.edu.unimelb.eldercare;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangeDNameActivity extends AppCompatActivity {

    TextView currentDisplayName;
    EditText newDisplayName;
    FirebaseUser user;
    DatabaseReference mDatabase;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //sets correct layout file
        setContentView(R.layout.change_d_name_activity);

        currentDisplayName = findViewById(R.id.DisplayName);
        newDisplayName = findViewById(R.id.NewDisplayName);

        //Get User and Database reference
        this.user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(this.user.getUid());

        //Set Current Display Name
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Assigns the current users display name to the TextView
                User user = dataSnapshot.getValue(User.class);
                String dName = user.getDisplayName();
                currentDisplayName.setText(dName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void updateDisplayName(View view){
        String newDName = newDisplayName.getText().toString();
        mDatabase.child("displayName").setValue(newDName);
    }
}
