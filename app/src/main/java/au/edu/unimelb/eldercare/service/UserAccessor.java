package au.edu.unimelb.eldercare.service;

import au.edu.unimelb.eldercare.user.User;

import java.util.List;

public interface UserAccessor {
    void userListLoaded(List<User> users);

    void userLoaded(User value);
}
