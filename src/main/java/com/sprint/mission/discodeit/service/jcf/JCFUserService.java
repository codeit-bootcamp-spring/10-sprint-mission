package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.Validators;

import java.util.*;

public class JCFUserService implements UserService {
    private final UserRepository userRepository;

    public JCFUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(String userName, String userEmail) {
        Validators.validationUser(userName, userEmail); // 비즈니스 로직
        validateDuplicationEmail(userEmail); // 비즈니스 로직
        User user = new User(userName, userEmail);
        return userRepository.save(user); // 저장 로직
    }

    @Override
    public User readUser(UUID id) {
        return validateExistenceUser(id); // 비즈니스 로직
    }

    @Override
    public List<User> readAllUser() {
        return userRepository.findAll();
    } // 저장 로직

    @Override
    public User updateUser(UUID id, String userName, String userEmail) {
        User user = validateExistenceUser(id); // 비즈니스 로직
        Optional.ofNullable(userName)
                .ifPresent(name -> {Validators.requireNotBlank(name, "userName");
            user.updateUserName(name);
        }); //비즈니스 로직 + 저장 로직
        Optional.ofNullable(userEmail)
                .ifPresent(email -> {Validators.requireNotBlank(email, "userEmail");
            validateDuplicationEmail(email);
            user.updateUserEmail(email);
        }); // 비즈니스 로직 + 저장 로직

        return user;
    }

    @Override
    public void deleteUser(UUID id) {
        validateExistenceUser(id); //비즈니스 로직
        userRepository.deleteById(id); // 저장 로직
    }


    @Override
    public List<User> readUsersByChannel(UUID channelId) {
        return userRepository.findAll().stream()
                .filter(user -> user.getJoinedChannels().stream()
                        .anyMatch(ch -> channelId.equals(ch.getId())))
                .toList();
    } // 비즈니스 로직 + 저장 로직

    private void validateDuplicationEmail(String userEmail) {
        if(userRepository.findAll().stream()
                .anyMatch(user -> userEmail.equals(user.getUserEmail()))) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
    } // 비즈니스 로직 + 저장 로직


    private User validateExistenceUser(UUID id) {
        Validators.requireNonNull(id, "id는 null이 될 수 없습니다."); // 비즈니스 로직
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("유저 id가 존재하지 않습니다."));
    } // 비즈니스 로직 + 저장 로직
}
