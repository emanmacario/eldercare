package au.edu.unimelb.eldercare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import au.edu.unimelb.eldercare.user.UserSearchUI;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {

    private CircleImageView mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mButton = findViewById(R.id.image_message_profile);
    }

    public void openUserSearch(View view) {
        Intent intent = new Intent(HomeActivity.this, UserSearchUI.class);
        startActivity(intent);
    }
}
