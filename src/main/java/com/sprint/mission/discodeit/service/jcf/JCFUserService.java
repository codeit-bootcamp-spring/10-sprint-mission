package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

public class JCFUserService implements UserService {

    @Override
   public User createUser(String userName, String userEmail, String userPassword) {
        return new User(userName, userEmail, userPassword);
    }
}
