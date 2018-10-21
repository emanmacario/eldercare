package au.edu.unimelb.eldercare.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import au.edu.unimelb.eldercare.user.User;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides an interface for user access
 */
public class UserService {
    private static final String TAG = "UserService";
    private static UserService ourInstance = new UserService();
    private final DatabaseReference databaseReference;

    /**
     * Get the singleton User Service
     * @return The singleton instance of the User Service
     */
    public static UserService getInstance() {
        return ourInstance;
    }

    private UserService() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users/");
    }

    /**
     * Gets all known users in the database
     * @param sender The UserAccessor that is attempting to retrieve data
     */
    public void getAllUsers(final UserAccessor sender) {
        Log.d(TAG, "getAllUsers: beginning query");
        databaseReference.orderByChild("displayName").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: got data change");
                List<User> userList = new ArrayList<>();
                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    User firebaseUser = user.getValue(User.class);
                    assert firebaseUser != null;
                    firebaseUser.setUserId(user.getKey());
                    userList.add(firebaseUser);
                    Log.d(TAG, "onDataChange: adding user");
                }
                Log.d(TAG, "onDataChange: invoking callback");
                sender.userListLoaded(userList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Do nothing
            }
        });
    }

    /**
     * Gets a specific user from the database
     * @param userId The uid of the user
     * @param sender The UserAccessor that is attempting to retrieve data
     */
    public void getSpecificUser(String userId, final UserAccessor sender) {
        databaseReference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User firebaseUser = dataSnapshot.getValue(User.class);
                assert firebaseUser != null;
                firebaseUser.setUserId(dataSnapshot.getKey());
                sender.userLoaded(firebaseUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Persist a specific user into the database
     * @param thisUser The user being persisted
     */
    public void saveUser(User thisUser) {
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(thisUser.getUserId(), thisUser);
        databaseReference.updateChildren(updateMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    throw new RuntimeException(databaseError.toException());
                }
            }
        });
    }
}
