package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
<<<<<<< HEAD
=======

>>>>>>> 3a7b55e457e0d55f5042c220079e6b60cb0acc7f
import java.util.List;
import java.util.UUID;

public interface UserService {
<<<<<<< HEAD
    User createUser(String username, String email);
    User findById(UUID id);
    List<User> findAll();

    User update(UUID id, String username, String email);

    void delete(UUID id);
=======
    User createUser(String username, String password, String email);

    User findUserById(UUID id);

    List<User> findAllUsers();

    User updateUserInfo(UUID id, String newUsername, String newEmail);

    User changePassword(UUID id, String newPassword);

    void deleteUser(UUID id);

    List<User> findParticipants(UUID channelID);

>>>>>>> 3a7b55e457e0d55f5042c220079e6b60cb0acc7f
}
