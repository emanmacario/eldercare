package au.edu.unimelb.eldercare.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import au.edu.unimelb.eldercare.R;
import au.edu.unimelb.eldercare.usersearch.SearchAdaptor;

public class UserSearchUI extends AppCompatActivity {

    private static final String TAG = "UserSearchUI";

    public void onUserProfileClick(View view) {
        Intent intent = new Intent(UserSearchUI.this, UserProfileUI.class);
        intent.putExtra("targetUser", (String)view.getTag());
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search_ui);

        RecyclerView rvSearch = findViewById(R.id.rvSearchResults);
        SearchAdaptor adapter = new SearchAdaptor();

        rvSearch.setAdapter(adapter);

        // Set layout manager to position the items
        rvSearch.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equalsIgnoreCase(intent.getAction())) {
            Log.d(TAG, "onCreate: searching!");
        }
    }
}
