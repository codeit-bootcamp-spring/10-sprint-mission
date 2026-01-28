package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.CreateUserRequest;
import com.sprint.mission.discodeit.dto.FileUploadDto;
import com.sprint.mission.discodeit.dto.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public UserDto create(CreateUserRequest request) {
        if(userRepository.findAll()
                .stream()
                .anyMatch(user -> user.getUsername().equals(request.getUsername()))) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }
        if(userRepository.findAll()
                .stream()
                .anyMatch(user -> user.getEmail().equals(request.getEmail()))) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        User user = new User(request.getUsername(), request.getEmail(), request.getPassword());

        if(request.getProfileImage() != null) {
            FileUploadDto img = request.getProfileImage();
            BinaryContent content = new BinaryContent(
                    img.getFileName(),
                    img.getContentType(),
                    img.getSize(),
                    img.getContents()
            );
            binaryContentRepository.save(content);
            user.updateProfileImageId(content.getId());
        }
        userRepository.save(user);

        UserStatus status = new UserStatus(user.getId());
        userStatusRepository.save(status);

        return new UserDto(user, status.isOnline());
    }

    @Override
    public UserDto find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
        boolean isOnline = userStatusRepository.findByUserId(userId)
                .map(UserStatus::isOnline)
                .orElse(false);

        return new UserDto(user, isOnline);
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(user -> {
                    boolean isOnline = userStatusRepository.findByUserId(user.getId())
                            .map(UserStatus::isOnline)
                            .orElse(false);
                    return new UserDto(user, isOnline);
                })
                .collect(Collectors.toList());
    }

    @Override
    public UserDto update(UUID userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
        user.update(request.getUsername(), request.getEmail(), request.getPassword());

        if(request.getNewProfileImage() != null) {
            FileUploadDto img = request.getNewProfileImage();
            BinaryContent content = new BinaryContent(
                    img.getFileName(),
                    img.getContentType(),
                    img.getSize(),
                    img.getContents()
            );
            binaryContentRepository.save(content);
            user.updateProfileImageId(content.getId());
        }
        userRepository.save(user);
        boolean isOnline = userStatusRepository.findByUserId(user.getId())
                .map(UserStatus::isOnline)
                .orElse(false);
        return new UserDto(user, isOnline);
    }

    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(
                        "User with id " + userId + " not found")
                );
        // 유저 상태 삭제
        userStatusRepository.findByUserId(userId)
                .ifPresent(status -> userStatusRepository.deleteById(status.getId()));
        userRepository.deleteById(userId);

        // 유저 프로필 사진 삭제
        if(user.getProfileImageId() != null) {
            binaryContentRepository.deleteById(user.getProfileImageId());
        }

        // 유저 삭제
        userRepository.deleteById(userId);

    }
}
