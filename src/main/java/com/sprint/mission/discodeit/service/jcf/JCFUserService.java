package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.dto.UserRequestCreateDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.Validators;

import java.util.*;

public class JCFUserService implements UserService {
    private final UserRepository userRepository;

    public JCFUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(UserRequestCreateDto request) {
        Validators.validationUser(userName, userEmail);
        validateDuplicationUserName(userName);
        validateDuplicationEmail(userEmail);

        UserRequestCreateDto.ProfileImageParam profile = request.profileImage();

        User user;
        if (profile != null) {
            if(profile.data() == null || profile.data().length == 0) {
                throw new IllegalArgumentException("프로필 이미지 데이터가 비어있습니다.");
            }
            if(profile.contentType() == null || profile.contentType().isBlank()) {
                throw new IllegalArgumentException("contentType은 필수입니다.");
            }

            BinaryContent binaryContent = new BinaryContent(profile.data(), profile.contentType());
            user = new User(request.userName(), request.userEmail(), binaryContent.getId());
        } else {
            user = new User(request.userName(), request.userEmail(), null);
        }
        User savedUser = userRepository.save(user);
        new UserStatus(savedUser.getId(), null);
        return savedUser;
    }

    @Override
    public User find(UUID id) {
        return validateExistenceUser(id); // 비즈니스 로직
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    } // 저장 로직

    @Override
    public User update(UUID id, String userName, String userEmail) {
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

    private void validateDuplicationUserName(String userName) {
        if(userRepository.findAll().stream()
                .anyMatch(user -> userName.equals(user.getUserName()))) {
            throw new IllegalArgumentException("이미 존재하는 이름입니다.");
        }
    }

    private User validateExistenceUser(UUID id) {
        Validators.requireNonNull(id, "id는 null이 될 수 없습니다."); // 비즈니스 로직
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("유저 id가 존재하지 않습니다."));
    } // 비즈니스 로직 + 저장 로직
}
