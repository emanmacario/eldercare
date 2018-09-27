package au.edu.unimelb.eldercare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SelectUserTypeActivity extends AppCompatActivity {

    Button confirmButton;
    RadioGroup UserTypeRadio;
    RadioButton CarerRadio;
    RadioButton DependantRadio;
    private DatabaseReference mDatabase;
    private FirebaseUser user;

    private static final int CarerRadioID = 101;
    private static final int DependantRadioID = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_user_type_activity);

        confirmButton = findViewById(R.id.ConfirmUserTypeButton);
        UserTypeRadio = findViewById(R.id.UserTypeRadio);
        CarerRadio = findViewById(R.id.CarerUserRadio);
        CarerRadio.setId(CarerRadioID);
        DependantRadio = findViewById(R.id.DependantUserRadio);
        DependantRadio.setId(DependantRadioID);

        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(this.user.getUid());
    }

    public void openHomeScreen(View view){
        //Make sure that a user type has been selected
        if(UserTypeRadio.getCheckedRadioButtonId() == -1){
            //No User Type has been selected
            return;
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
            Intent intent = new Intent(SelectUserTypeActivity.this, HomeScreen.class);
            startActivity(intent);
        }

    }
}
