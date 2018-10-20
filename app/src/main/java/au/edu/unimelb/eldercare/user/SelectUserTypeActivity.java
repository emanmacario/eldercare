package au.edu.unimelb.eldercare.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import au.edu.unimelb.eldercare.HomeActivity;
import au.edu.unimelb.eldercare.R;

public class SelectUserTypeActivity extends AppCompatActivity {

    private Button confirmButton;
    private RadioGroup UserTypeRadio;
    private RadioButton CarerRadio;
    private RadioButton DependantRadio;

    //Firebase References
    private DatabaseReference mDatabase;

    //These IDs identify which radio button is selected
    private static final int CarerRadioID = 101;
    private static final int DependantRadioID = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //sets the correct layout
        setContentView(R.layout.select_user_type_activity);

        //Gets the reference for each variable
        confirmButton = findViewById(R.id.ConfirmUserTypeButton);
        UserTypeRadio = findViewById(R.id.UserTypeRadio);
        CarerRadio = findViewById(R.id.CarerUserRadio);
        DependantRadio = findViewById(R.id.DependantUserRadio);
        //Sets the IDs
        CarerRadio.setId(CarerRadioID);
        DependantRadio.setId(DependantRadioID);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String userType = dataSnapshot.child("userType").getValue(String.class);
                if(userType == null){
                    Toast toast = Toast.makeText(SelectUserTypeActivity.this, "Please set user type", Toast.LENGTH_LONG);
                    toast.show();
                }
                else{
                    //If there already exists a user type, make sure that radio button is selected
                    switch (userType){
                        case "Dependant":
                            DependantRadio.toggle();
                            break;
                        case "Carer":
                            CarerRadio.toggle();
                            break;
                        default:
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Sets the user type value on the database before moving to the home screen
     * @param view
     */
    public void openHomeScreen(View view){
        //Make sure that a user type has been selected
        if(UserTypeRadio.getCheckedRadioButtonId() == -1){
            //No User Type has been selected
        }
        else{
            //Get the radio button that is clicked at set the value in the database
            int UserTypeID = UserTypeRadio.getCheckedRadioButtonId();
            switch (UserTypeID) {
                case CarerRadioID:
                    //Set User Type to Carer
                    mDatabase.child("userType").setValue("Carer");
                    break;
                case DependantRadioID:
                    //Set User Type to Dependant
                    mDatabase.child("userType").setValue("Dependant");
                    break;
            }

            //Now open Home Screen
            Intent intent = new Intent(SelectUserTypeActivity.this, HomeActivity.class);
            startActivity(intent);
        }

    }
}
