package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserServiceDTO.UserCreation;
import com.sprint.mission.discodeit.dto.UserServiceDTO.UserInfoUpdate;
import com.sprint.mission.discodeit.dto.UserServiceDTO.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    @Qualifier("JCFUserRepository")
    private final UserRepository userRepository;
    @Qualifier("JCFBinaryContentRepository")
    private final BinaryContentRepository profileRepository;
    @Qualifier("JCFUserStatusRepository")
    private final UserStatusRepository userStatusRepository;
    private final String ID_NOT_FOUND = "%s with id %s not found";

    private UserStatus getUserStatus(UUID userStatusId) {
        return userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new NoSuchElementException(
                        ID_NOT_FOUND.formatted("UserStatus", userStatusId)));
    }

    private User getUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(
                        ID_NOT_FOUND.formatted("User", userId)));
    }

    private UserResponse getUserResponse(User user, UserStatus userStatus) {
        return UserResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .isActive(userStatus.isActive())
                .build();
    }

    @Override
    public UserResponse find(String username, String password) {
        User user = userRepository.findAll().stream().filter(user1 -> user1.getUsername().equals(username))
                .filter(user1 -> user1.isCorrectPassword(password))
                .findFirst().orElseThrow(() -> new NoSuchElementException("incorrect username or password"));
        UserStatus userStatus = getUserStatus(user.getUserStatusId());
        return getUserResponse(user, userStatus);
    }

    @Override
    public UserResponse find(UUID userId) {
        User user = getUser(userId);
        UserStatus userStatus = getUserStatus(user.getUserStatusId());
        return getUserResponse(user, userStatus);
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll()
                .stream()
                .map(user -> find(user.getId()))
                .toList();
    }

    @Override
    public UserResponse create(UserCreation model) {
        BinaryContent profile = new BinaryContent(model.profileImageUrl());
        profileRepository.save(profile);
        UserStatus userStatus = new UserStatus();
        userStatusRepository.save(userStatus);
        User user = new User(model.username(), model.email(),
                model.password(), profile.getId(), userStatus.getId());
        userRepository.save(user);
        return getUserResponse(user, userStatus);
    }

    @Override
    public UserResponse update(UserInfoUpdate model) {
        User user = userRepository.findById(model.userId())
                .orElseThrow(() -> new NoSuchElementException(
                        ID_NOT_FOUND.formatted("User", model.userId())));
        user.update(model.newUsername(), model.newEmail(), model.newPassword());
        BinaryContent profile = profileRepository.findById(model.userId())
                .orElseThrow(() -> new NoSuchElementException(
                        ID_NOT_FOUND.formatted("Profile", model.userId())));
        profile.setUrl(model.newUrl());
        profileRepository.save(profile);
        userRepository.save(user);
        UserStatus userStatus = getUserStatus(user.getUserStatusId());
        return getUserResponse(user, userStatus);
    }

    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(
                        ID_NOT_FOUND.formatted("User", userId)));
        if (!profileRepository.existsById(user.getProfileId())) {
            throw new NoSuchElementException(
                    ID_NOT_FOUND.formatted("Profile", user.getProfileId()));
        }
        profileRepository.deleteById(user.getProfileId());
        if (!userStatusRepository.existsById(user.getUserStatusId())) {
            throw new NoSuchElementException(
                    ID_NOT_FOUND.formatted("UserStatus", user.getUserStatusId()));
        }
        userStatusRepository.deleteById(user.getUserStatusId());
        userRepository.deleteById(userId);
    }
}
