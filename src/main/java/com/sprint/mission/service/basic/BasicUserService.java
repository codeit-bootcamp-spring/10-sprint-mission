package com.sprint.mission.service.basic;

import com.sprint.mission.entity.User;
import com.sprint.mission.repository.UserRepository;
import com.sprint.mission.service.UserService;

import java.util.List;
import java.util.UUID;

public class BasicUserService implements UserService {
    private final UserRepository userRepository;

    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(String name, String email) {
        User user = new User(name, email);
        return userRepository.save(user);
    }

    @Override
    public User update(UUID userId, String name) {
        User user = getUserOrThrow(userId);
        validateChangeNameExist(userId, name);
        user.updateName(name);

        return userRepository.save(user);
    }

    @Override
    public User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));
    }

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(UUID userId) {
        User user = getUserOrThrow(userId);
        userRepository.deleteById(userId);
    }

    @Override
    public List<User> getChannelUsers(UUID channelId) {
        return userRepository.findByChannelId(channelId);
    }

    private void validateChangeNameExist(UUID userId, String name) {
        String trimmedNickName = name.trim();
        boolean exist = userRepository.findAll().stream()
                .anyMatch(user -> !user.getId().equals(userId) && user.getName().equals(trimmedNickName));

        if (exist) {
            throw new IllegalArgumentException("존재하는 닉네임입니다. 다른이름을 선택해주세요");
        }
    }
}
