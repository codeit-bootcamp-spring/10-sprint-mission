package com.sprint.mission.discodeit.service.basic;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.utils.Validation;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class BasicUserService implements UserService {
    private final UserRepository userRepository;

    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(String userName, String alias) {
        Validation.notBlank(userName, "이름");
        Validation.notBlank(alias, "별명");
        // 중복 검사
        Validation.noDuplicate(
                userRepository.findAll(),
                u -> u.getAlias().equals(alias),
                "이미 존재하는 별명입니다: " + alias
        );

        User user = new User(userName, alias);
        userRepository.save(user);
        return user;
    }

    @Override
    public List<User> getUserAll() {
        return userRepository.findAll();
    }

    @Override
    public User getUserByAlias(String alias) {
        return userRepository.findAll().stream()
                .filter(user->user.getAlias().equals(alias))
                .findFirst()
                .orElseThrow(()->new NoSuchElementException("해당 별명을 가진 유저가 없습니다: " + alias));

    }
    @Override
    public List<User> getUserByName(String userName) {
        List<User> matches =  userRepository.findAll().stream()
                .filter(user -> user.getUserName().equals(userName))
                .toList();
        if(matches.isEmpty()) {
            throw new NoSuchElementException("해당 이름의 유저가 없습니다: " + userName);
        }
        return matches;
    }
    @Override
    public User findUserById(UUID id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new NoSuchElementException("해당 ID의 유저가 없습니다: " + id);
        }
        return user;
    }



    @Override
    public void deleteUser(UUID uuid){
        userRepository.delete(uuid);
    }
    @Override
    public User updateUser(UUID uuid, String newName, String newAlias) {
        User existing = userRepository.findById(uuid);
        Validation.notBlank(newName, "이름");
        Validation.notBlank(newAlias, "별명");

        existing.changeUserName(newName);
        existing.changeAlias(newAlias);
        userRepository.save(existing);
        return existing;
    }
}
