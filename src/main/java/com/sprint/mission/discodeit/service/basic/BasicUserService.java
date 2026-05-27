package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.user.response.UserDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.storage.FileStorageException;
import com.sprint.mission.discodeit.exception.user.AlreadyExistsEmailException;
import com.sprint.mission.discodeit.exception.user.AlreadyExistsNameException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class BasicUserService implements UserService {
    // 필드
    private final UserRepository userRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final UserMapper userMapper;
    private final BinaryContentRepository binaryContentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDTO create(UserCreateRequest userRequest, Optional<MultipartFile> profile) {
        // 이름, 이메일 유효성 검증
        validateName(userRequest.username());
        validateEmail(userRequest.email());

        // user 생성 with DTO
        User user = new User(userRequest.username(), userRequest.email(), passwordEncoder.encode(userRequest.password()), null);

        // 선택적으로 프로필 등록
        profile.ifPresent(file -> postProfile(profile, user));

        userRepository.save(user);
        log.info("사용자 생성 성공 - userId: {}", user.getId());
        return userMapper.toDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO find(UUID userId) {
        return userMapper.toDTO(userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> findAll() {
        return userRepository.findAllWithProfileAndStatus().stream()
                .map(userMapper::toDTO)
                .toList();
    }

    // 이름. 프로필 선택적 업데이트
    @Override
    @Transactional
    public UserDTO update(UUID userID, UserUpdateRequest request, Optional<MultipartFile> profile) {
        User user = userRepository.findById(userID)
                .orElseThrow(() -> new UserNotFoundException(userID));

        // user 이름 선택적 업데이트
        Optional.ofNullable(request.newUsername()).ifPresent(name -> {
            validateName(name);
            user.updateName(name);
        });

        // user 이메일 선택적 업데이트
        Optional.ofNullable(request.newEmail()).ifPresent(email -> {
            validateEmail(email);
            user.updateEmail(email);
        });

        // user 비밀번호 선택적 업데이트
        Optional.ofNullable(request.newPassword()).ifPresent(newPassword -> user.updatePassword(passwordEncoder.encode(newPassword)));

        // user의 프로필 선택적 업데이트
        profile.ifPresent(file -> {
            // 기존에 프로필 존재 시 삭제
            if (user.getProfile() != null) {
                binaryContentRepository.delete(user.getProfile());
            }
            postProfile(profile, user);
        });

        log.info("사용자 수정 성공 - userId: {}", userID);
        return userMapper.toDTO(user);
    }

    // user가 해당 ch에서 보낸 msg 삭제 반영 X
    @Override
    @Transactional
    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        userRepository.delete(user);
        log.info("사용자 삭제 성공 - userId: {}", userId);
    }

    // User 이름 유효성 검증
    public void validateName(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new AlreadyExistsNameException(username);
        }
    }

    // 이메일 유효성 검증
    public void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new AlreadyExistsEmailException(email);
        }
    }

    // 프로필 등록
    public void postProfile(Optional<MultipartFile> profile, User user) {
        profile.ifPresent(file -> {
            try {
                log.debug("프로필 이미지 저장 - fileName: {}", file.getOriginalFilename());
                BinaryContent bc = new BinaryContent(
                        file.getOriginalFilename(),
                        file.getContentType(),
                        file.getSize()
                );
                binaryContentRepository.save(bc);
                binaryContentStorage.put(bc.getId(), file.getBytes());
                user.updateProfile(bc);
                log.debug("프로필 이미지 저장 성공 - fileName: {}", file.getOriginalFilename());
            } catch (IOException e) {
                throw new FileStorageException(file.getOriginalFilename());
            }
        });
    }
}
