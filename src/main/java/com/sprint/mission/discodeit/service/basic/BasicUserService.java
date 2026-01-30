package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserServiceDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserPresence;
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

    private User find(UUID userId, UserPresence presence) {
        User user = find(userId);
        UserStatus userStatus = getUserStatus(user.getUserStatusId());
        if (!userStatus.isSamePresence(presence)) {
            throw new NoSuchElementException(
                    "No matching user found, id: %s, presence: %s".formatted(userId, presence));
        }
        return user;
    }

    @Override
    public User find(UserServiceDTO.UserFinding model) {
        return find(model.userId(), model.presence());
    }

    @Override
    public List<User> findAll(UserPresence presence) {
        return userRepository.findAll()
                .stream()
                .map(user -> find(user.getId(), presence))
                .toList();
    }

    @Override
    public User create(UserServiceDTO.UserCreation model) {
        BinaryContent profile = new BinaryContent(model.profileImageUrl());
        profileRepository.save(profile);
        UserStatus userStatus = new UserStatus();
        userStatusRepository.save(userStatus);
        User user = new User(model.username(), model.email(),
                model.password(), profile.getId(), userStatus.getId());
        return userRepository.save(user);
    }

    @Override
    public User find(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(
                        ID_NOT_FOUND.formatted("User", userId)));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(UserServiceDTO.UserInfoUpdate model) {
        User user = userRepository.findById(model.userId())
                .orElseThrow(() -> new NoSuchElementException(
                        ID_NOT_FOUND.formatted("User", model.userId())));
        user.update(model.newUsername(), model.newEmail(), model.newPassword());
        BinaryContent profile = profileRepository.findById(model.userId())
                .orElseThrow(() -> new NoSuchElementException(
                        ID_NOT_FOUND.formatted("Profile", model.userId())));
        profile.setUrl(model.newUrl());
        profileRepository.save(profile);
        return userRepository.save(user);
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
