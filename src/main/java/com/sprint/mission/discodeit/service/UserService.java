package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;


public interface UserService {

    User userCreate(String userName, String userEmail);

    User userFind(UUID userId);

    List<User> userFindAll();

    User userUpdate(UUID id, String userName, String userEmail);

    void userDelete(UUID userId);

}
