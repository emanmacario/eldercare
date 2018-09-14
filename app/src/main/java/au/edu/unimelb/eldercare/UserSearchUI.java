package au.edu.unimelb.eldercare;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import au.edu.unimelb.eldercare.usersearch.SearchAdaptor;

public class UserSearchUI extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search_ui);

        RecyclerView rvSearch = findViewById(R.id.rvSearchResults);
        SearchAdaptor adapter = new SearchAdaptor();

        rvSearch.setAdapter(adapter);

        // Set layout manager to position the items
        rvSearch.setLayoutManager(new LinearLayoutManager(this));
    }
}
