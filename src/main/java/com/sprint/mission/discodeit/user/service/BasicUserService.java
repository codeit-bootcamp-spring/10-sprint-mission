package com.sprint.mission.discodeit.user.service;

import com.sprint.mission.discodeit.binarycontent.dto.BinaryContentResponse;
import com.sprint.mission.discodeit.binarycontent.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.user.dto.UserCreateRequest;
import com.sprint.mission.discodeit.user.dto.UserResponse;
import com.sprint.mission.discodeit.user.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.user.entity.User;
import com.sprint.mission.discodeit.user.mapper.UserMapper;
import com.sprint.mission.discodeit.user.repository.UserRepository;
import com.sprint.mission.discodeit.user.entity.UserStatus;
import com.sprint.mission.discodeit.user.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse create(UserCreateRequest request) {
        java.util.List<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            if (user.getUsername().equals(request.username())) {
                throw new IllegalArgumentException("이미 존재하는 유저네임입니다.");
            }
            if (user.getEmail().equals(request.email())) {
                throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
            }
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        User user = new User(
                request.username(),
                request.email(),
                encodedPassword,
                request.profileImage()
        );

        User savedUser = userRepository.save(user);
        userStatusRepository.save(new UserStatus(savedUser.getId()));
        return userMapper.convertToResponse(userRepository.save(user));
    }

    @Override
    public UserResponse find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
        userStatusRepository.findByUserId(userId)
                .ifPresent(user::setUserStatus);
        return userMapper.convertToResponse(user);
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(user -> {
                    userStatusRepository.findByUserId(user.getId())
                            .ifPresent(user::setUserStatus);
                    return userMapper.convertToResponse(user);
                })
                .toList();
    }

    @Override
    public UserResponse update(UUID userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        String name = Optional.ofNullable(request.username()).orElse(user.getUsername());
        String email = Optional.ofNullable(request.email()).orElse(user.getEmail());
        String password = Optional.ofNullable(request.password()).orElse(user.getPassword());
        BinaryContentResponse profileImage = Optional.ofNullable(request.profileImage()).orElse(user.getProfileImage());

        user.update(name,email,password,profileImage);
        return userMapper.convertToResponse(userRepository.save(user));
    }

    @Override
    public UserResponse delete(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User with id " + userId + " not found");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        userStatusRepository.findByUserId(userId)
                .ifPresent(status -> userStatusRepository.deleteById(status.getId()));


        if (user.getProfileImage() != null) {
            binaryContentRepository.deleteById(user.getProfileId());
        }
        userRepository.deleteById(user.getId());
        return userMapper.convertToResponse(user);

    }
}


