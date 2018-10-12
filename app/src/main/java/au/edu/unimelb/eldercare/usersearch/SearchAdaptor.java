package au.edu.unimelb.eldercare.usersearch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import au.edu.unimelb.eldercare.R;
import au.edu.unimelb.eldercare.service.AuthenticationService;
import au.edu.unimelb.eldercare.user.User;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class SearchAdaptor extends RecyclerView.Adapter<SearchAdaptor.ViewHolder> implements UserAccessor {
    private static final String TAG = "Search";
    private List<User> userList;
    private FirebaseUser user;

    private final View.OnClickListener clicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            User addedUser = (User) view.getTag();
            User thisUser = findLoggedInUser(user.getUid());
            thisUser.getFriends().add(addedUser.getUserId());
            Button button = (Button)view;
            button.setEnabled(false);
            button.setText(R.string.friendAddButtonRequested);
            UserService.getInstance().saveUser(thisUser);
            Log.d(TAG, "onClick: clicked!" + addedUser.getDisplayName());
        }
    };

    private User findLoggedInUser(String uid) {
        for (User currentUser : userList) {
            if (uid.equalsIgnoreCase(currentUser.getUserId())) {
                return currentUser;
            }
        }
        return null;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.user_search_cell, viewGroup, false);

        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        User user = userList.get(i);
        TextView textView = viewHolder.nameTextView;
        textView.setText(user.getDisplayName());
        Button button = viewHolder.addButton;
        if (user.getUserId().equalsIgnoreCase(this.user.getUid())) {
            button.setText(R.string.friendAddButtonSelf);
            button.setEnabled(false);
        } else if (findLoggedInUser(this.user.getUid()).getFriends().contains(user.getUserId())) {
            if (user.getFriends().contains(this.user.getUid())) {
                button.setText(R.string.friendAddButtonAlreadyFriends);
            } else {
                button.setText(R.string.friendAddButtonRequested);
            }
            button.setEnabled(false);
        } else {
            button.setText(R.string.addFriendText);
            button.setEnabled(true);
            button.setOnClickListener(clicked);
            button.setTag(user);
        }
    }

    @Override
    public int getItemCount() {
        if (userList == null) {
            UserService.getInstance().getAllUsers(this);
            this.user = AuthenticationService.getAuthenticationService().getUser();
            return 0;
        } else {
            return userList.size();
        }
    }

    @Override
    public void userListLoaded(List<User> users) {
        this.userList = users;
        this.notifyDataSetChanged();
        Log.d(TAG, "userListLoaded: updating list");
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;

        Button addButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.user_name);
            addButton = itemView.findViewById(R.id.add_button);
        }
    }
}
