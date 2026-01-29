package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserRequestCreateDto;
import com.sprint.mission.discodeit.dto.UserRequestUpdateDto;
import com.sprint.mission.discodeit.dto.UserResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.Validators;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserResponseDto create(UserRequestCreateDto request) {
        Validators.validationUser(request.userName(), request.userEmail());
        validateDuplicationUserName(request.userName());
        validateDuplicationEmail(request.userEmail());

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
        UserStatus userStatus = new UserStatus(savedUser.getId(), null);
        boolean online = userStatus.isOnline();
        return toDto(savedUser, online);
    }

    @Override
    public UserResponseDto find(UUID id) {
        User user = validateExistenceUser(id);
        boolean online = resolveOnline(id);
        return toDto(user, online);
    }

    @Override
    public List<UserResponseDto> findAll() {
        return userRepository.findAll().stream()
                .map(u -> toDto(u, resolveOnline(u.getId())))
                .toList();
    }

    @Override
    public UserResponseDto update(UserRequestUpdateDto request) {
        Validators.requireNonNull(request, "request");
        User user = validateExistenceUser(request.id());
        Optional.ofNullable(request.userName())
                .ifPresent(name -> {Validators.requireNotBlank(name, "userName");
                    validateDuplicationUserName(name);
                    user.updateUserName(name);
                });
        Optional.ofNullable(request.userEmail())
                .ifPresent(email -> {Validators.requireNotBlank(email, "userEmail");
                    validateDuplicationEmail(email);
                    user.updateUserEmail(email);
                });

        User savedUser = userRepository.save(user);
        boolean online = resolveOnline(savedUser.getId());
        return toDto(savedUser, online);
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

    private void validateDuplicationUserName(String userName) {
        if(userRepository.findAll().stream()
                .anyMatch(user -> userName.equals(user.getUserName()))) {
            throw new IllegalArgumentException("이미 존재하는 이름입니다.");
        }
    }


    private User validateExistenceUser(UUID id) {
        Validators.requireNonNull(id, "id는 null이 될 수 없습니다.");
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("유저 id가 존재하지 않습니다."));

    }

    public static UserResponseDto toDto(User user, boolean online) {
        return new UserResponseDto(
                user.getId(),
                user.getUserName(),
                user.getUserEmail(),
                user.getProfileId(),
                online
        );
    }

    private boolean resolveOnline(UUID userId) {
        return false;
    }
}
