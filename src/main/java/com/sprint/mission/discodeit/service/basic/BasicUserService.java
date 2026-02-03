package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
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
        user.setUserStatus(new UserStatus(user.getId()));
        return userMapper.convertToResponse(userRepository.save(user));
    }

    @Override
    public UserResponse find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
        return userMapper.convertToResponse(user);
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::convertToResponse)
                .toList();
    }

    @Override
    public UserResponse update(UUID userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        String name = Optional.ofNullable(request.username()).orElse(user.getUsername());
        String email = Optional.ofNullable(request.email()).orElse(user.getEmail());
        String password = Optional.ofNullable(request.password()).orElse(user.getPassword());
        BinaryContentDto profileImage = Optional.ofNullable(request.profileImage()).orElse(user.getProfileImage());

        user.update(name,email,password,profileImage);
        return userMapper.convertToResponse(userRepository.save(user));
    }

    @Override
    public void delete(UUID userId) {
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

    }
}


