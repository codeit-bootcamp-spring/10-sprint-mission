package com.sprint.mission.discodeit.service.basic;


import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class BasicUserService implements UserService {

    private final UserRepository userRepository;

    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(String userName, String userEmail) {
        if(userName == null || userName.isEmpty()){
            throw new UserNotFoundException();
        }
        if (userEmail == null || userEmail.isEmpty()){
            throw new UserNotFoundException();
        }
        User user = new User(userName, userEmail);
        return userRepository.createUser(user);
    }

    @Override
    public User findUser(UUID userId) {
        return userRepository.findUser(userId);
    }

    @Override
    public List<User> findAllUser() {
        return userRepository.findAllUser();
    }

    @Override
    public User updateUser(UUID userId, String userName, String userEmail) {
        return userRepository.updateUser(userId, userName, userEmail);
    }

    @Override
    public User deleteUser(UUID userId) {
        return userRepository.deleteUser(userId);
    }
}