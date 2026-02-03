package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.CreateUserRequest;
import com.sprint.mission.discodeit.dto.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto create(CreateUserRequest request) {

        validateDuplicateUser(request.getUsername(), request.getEmail());

        User user = new User(request.getUsername(), request.getEmail(), request.getPassword());

        uploadProfileImage(user, request.getProfileImage());

        userRepository.save(user);

        UserStatus status = new UserStatus(user.getId());
        userStatusRepository.save(status);

        return userMapper.toDto(user);
    }

    @Override
    public Optional<UserDto> findById(UUID userId) {
        return userRepository.findById(userId)
                .map(userMapper::toDto);
    }

    @Override
    public UserDto find(UUID userId) {
        User user = getUserEntity(userId);
        return userMapper.toDto(user);
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public UserDto update(UUID userId, UpdateUserRequest request) {
        User user = getUserEntity(userId);

        user.update(request.getUsername(), request.getEmail(), request.getPassword());

        if (request.getNewProfileImage() != null) {
            uploadProfileImage(user, request.getNewProfileImage());
        }

        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public void delete(UUID userId) {
        User user = getUserEntity(userId);

        userStatusRepository.findByUserId(userId)
                .ifPresent(status -> userStatusRepository.deleteById(status.getId()));

        if (user.getProfileImageId() != null) {
            binaryContentRepository.deleteById(user.getProfileImageId());
        }

        // 유저 삭제
        userRepository.deleteById(userId);
    }

    // --------- 내부 메서드 구현 ---------
    // 유저 엔티티 조회
    private User getUserEntity(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
    }


    // 프로필 이미지 업로드 및 유저 정보 업데이트
    private void uploadProfileImage(User user, BinaryContentDto contentDto) {
        if (contentDto == null) return;

        BinaryContent content = new BinaryContent(
                contentDto.getFileName(),
                contentDto.getContentType(),
                contentDto.getSize(),
                contentDto.getContents()
        );
        binaryContentRepository.save(content);
        user.updateProfileImageId(content.getId());
    }

    // 중복 가입 검증
    private void validateDuplicateUser(String username, String email) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
        boolean emailExists = userRepository.findAll().stream()
                .anyMatch(u -> u.getEmail().equals(email));
        if (emailExists) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }
    }
}