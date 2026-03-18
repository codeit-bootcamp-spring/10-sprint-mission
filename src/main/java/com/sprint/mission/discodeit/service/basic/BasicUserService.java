package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final UserMapper mapper;

    @Override
    public UserDto createUser(UserDto.UserCreateRequest userReq, MultipartFile profileImage) throws IOException {
        validateDuplicateUsername(userReq.username());
        validateDuplicateEmail(userReq.email());

        User user = new User(userReq.username(), userReq.password(), userReq.email());

        // userStatus 관련
        UserStatus status = new UserStatus();
        user.updateStatus(status);

        // profile 이미지를 같이 추가하면
        processUpdateProfile(user, profileImage);
        userRepository.save(user);

        return toDto(user);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDto).toList();
    }

    @Override
    public UserDto updateUser(UUID uuid, UserDto.UserUpdateRequest userReq, MultipartFile profileImage) throws IOException{
        User user = userRepository.findById(uuid)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));

        // username과 mail 중복성 검사
        if (userReq.newUsername() != null && !Objects.equals(user.getUsername(), userReq.newUsername()))
            validateDuplicateUsername(userReq.newUsername());
        if (userReq.newEmail() != null && !Objects.equals(user.getEmail(), userReq.newEmail()))
            validateDuplicateEmail(userReq.newEmail());

        Optional.ofNullable(userReq.newUsername()).ifPresent(user::updateUserName);
        Optional.ofNullable(userReq.newPassword()).ifPresent(user::updatePassword);
        Optional.ofNullable(userReq.newEmail()).ifPresent(user::updateEmail);

        // 변경되는 프로필 이미지가 있으면
        processUpdateProfile(user, profileImage);

        userRepository.save(user);

        return toDto(user);
    }

    @Override
    public void deleteUser(UUID uuid) {
        userRepository.findById(uuid)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
        userRepository.deleteById(uuid);
    }

    private void validateDuplicateUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new BusinessLogicException(ErrorCode.DUPLICATE_USERNAME);
        }
    }

    private void validateDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessLogicException(ErrorCode.DUPLICATE_EMAIL);
        }
    }

    private UserDto toDto(User user) {
        return mapper.toDto(user);
    }

    private void processUpdateProfile(User user, MultipartFile profileImage) throws IOException {
        if (profileImage == null) return;

        // BinaryContent 생성
        BinaryContent content = new BinaryContent(
                profileImage.getOriginalFilename(), profileImage.getSize(), profileImage.getContentType());
        binaryContentRepository.save(content);
        binaryContentStorage.put(content.getId(), profileImage.getBytes());

        user.updateProfile(content);
    }
}
