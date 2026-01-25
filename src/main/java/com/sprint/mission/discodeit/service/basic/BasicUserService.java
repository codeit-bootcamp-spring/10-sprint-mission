package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.Validators;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BasicUserService implements UserService {
    private final UserRepository userRepository;

    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(String userName, String userEmail) {
        Validators.validationUser(userName, userEmail);
        validateDuplicationEmail(userEmail);
        User user = new User(userName, userEmail);
        return userRepository.save(user);
    }

    @Override
    public User readUser(UUID id) {
        return validateExistenceUser(id);
    }

    @Override
    public List<User> readAllUser() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(UUID id, String userName, String userEmail) {
        User user = validateExistenceUser(id);
        Optional.ofNullable(userName)
                .ifPresent(name -> {Validators.requireNotBlank(name, "userName");
                    user.updateUserName(name);
                });
        Optional.ofNullable(userEmail)
                .ifPresent(email -> {Validators.requireNotBlank(email, "userEmail");
                    validateDuplicationEmail(email);
                    user.updateUserEmail(email);
                });

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(UUID userId) {
        validateExistenceUser(userId);
        userRepository.deleteById(userId);
    }



    @Override
    public List<User> readUsersByChannel(UUID channelId) {
        return userRepository.findAll().stream()
                .filter(user -> user.getJoinedChannels().stream()
                        .anyMatch(ch -> channelId.equals(ch.getId())))
                .toList();
    }

    private void validateDuplicationEmail(String userEmail) {
        if(userRepository.findAll().stream()
                .anyMatch(user -> userEmail.equals(user.getUserEmail()))) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
    }


    private User validateExistenceUser(UUID id) {
        Validators.requireNonNull(id, "id는 null이 될 수 없습니다.");
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("유저 id가 존재하지 않습니다."));
    }
}
