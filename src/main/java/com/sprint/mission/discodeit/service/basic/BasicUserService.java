package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserPatchDto;
import com.sprint.mission.discodeit.dto.UserPostDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;

    @Override
    public UserDto create(UserPostDto userPostDto, MultipartFile profile) {
        log.trace("[USER] create 메서드 호출: email={}, username={}", userPostDto.getEmail(),
            userPostDto.getUsername());

        // username과 email이 다른 유저와 같으면 안 된다.
        if (isUserNameDuplicated(userPostDto.getUsername()) ||
            isEmailDuplicated(userPostDto.getEmail())) {
            throw new BusinessLogicException(ExceptionCode.USER_INFO_DUPLICATED,
                userPostDto.getEmail(), userPostDto.getUsername());
        }

        // 새 user 객체 생성
        User newUser = userMapper.toEntity(userPostDto);

        // 프로필 정보를 선택적으로 저장
        if (profile != null && !profile.isEmpty()) {
            log.trace("[USER] 프로필 저장 시작: fileName = {}", profile.getOriginalFilename());

            try {
                BinaryContent binaryContent = new BinaryContent(
                    profile.getOriginalFilename(),
                    (int) profile.getSize(),
                    profile.getContentType()
                );

                binaryContentRepository.save(binaryContent);
                binaryContentStorage.put(binaryContent.getId(), profile.getBytes());

                newUser.updateProfile(binaryContent); // user에 프로필 정보 업데이트

                log.trace(
                    "[USER] 프로필 파일 저장 및 binaryContent 영속화 완료: fileName = {}, binaryContentId = {}",
                    profile.getOriginalFilename(), binaryContent.getId());

            } catch (IOException e) {
                throw new BusinessLogicException(ExceptionCode.ATTACHMENT_SAVE_EXCEPTION, e);
            }
        }

        // UserStatus를 같이 생성 및 저장
        UserStatus newUserStatus = new UserStatus(newUser);
        newUser.updateStatus(newUserStatus);

        userRepository.save(newUser);
        userStatusRepository.save(newUserStatus);

        log.info("[USER] 유저 생성 완료: id = {}", newUser.getId());

        return userMapper.toDto(newUser);
    }

    public boolean isUserNameDuplicated(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean isEmailDuplicated(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findById(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND, userId));

        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findByUsername(String username) {
        log.trace("[USER] findByUsername 메서드 호출: username={}", username);

        User user = userRepository.findByUsername(username)
            .orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.USER_NAME_NOT_FOUND, username)
            );

        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        log.trace("[USER] findAll 메서드 호출");

        // UserStatus의 isLoggedIn을 활용하여 온라인 상태 반환
        return userRepository.findAll().stream()
            .map(userMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public UserDto updateUser(UUID userId, UserPatchDto userPatchDto, MultipartFile profile) {
        log.debug("[USER] update 메서드 호출: newEmail={}, newUsername={}", userPatchDto.newEmail(),
            userPatchDto.newUsername());

        User updatedUser = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND, userId));

        // 유저 정보 업데이트
        Optional.ofNullable(userPatchDto.newUsername())
            .ifPresent(updatedUser::updateUsername);
        Optional.ofNullable(userPatchDto.newEmail())
            .ifPresent(updatedUser::updateEmail);
        Optional.ofNullable(userPatchDto.newPassword())
            .ifPresent(updatedUser::updatePassword);
        if (profile != null && !profile.isEmpty()) {

            log.trace("[USER] 프로필 저장 시작: fileName = {}", profile.getOriginalFilename());

            try {
                BinaryContent binaryContent = new BinaryContent(
                    profile.getOriginalFilename(),
                    (int) profile.getSize(),
                    profile.getContentType()
                );

                binaryContentRepository.save(binaryContent);
                binaryContentStorage.put(binaryContent.getId(), profile.getBytes());

                updatedUser.updateProfile(binaryContent); // user에 프로필 정보 업데이트

                log.trace(
                    "[USER] 프로필 파일 저장 및 binaryContent 영속화 완료: fileName = {}, binaryContentId = {}",
                    profile.getOriginalFilename(), binaryContent.getId());

            } catch (IOException e) {
                throw new BusinessLogicException(ExceptionCode.ATTACHMENT_SAVE_EXCEPTION, e);
            }
        }

        log.info("[USER] 유저 수정 완료: id = {}", updatedUser.getId());

        return userMapper.toDto(userRepository.save(updatedUser));
    }

    @Override
    public void delete(UUID userId) {
        log.trace("[USER] delete 메서드 호출: id = {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND, userId);
        }

        userRepository.deleteById(userId);

        log.info("[USER] 유저 삭제 완료: id = {}", userId);
    }
}
