package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.exception.common.InvalidInputException;
import com.sprint.mission.discodeit.exception.common.NoChangeValueException;
import com.sprint.mission.discodeit.exception.user.*;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserMapper userMapper;
    private final BinaryContentStorage binaryContentStorage;

    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher applicationEventPublisher;

    @CacheEvict(value = "userList", allEntries = true)
    @Override
    public UserDto create(
            UserCreateRequest request,
            MultipartFile profile
    ) {
        log.debug("[USER_CREATE] 사용자 등록 시작: email={}, username={}",
                request.email(), request.username());

        // newEmail, newUsername 중복 확인
        validateDuplicateEmail(request.email());
        validateDuplicateUserName(request.username());

        String email = request.email();
        String username = request.username();

        BinaryContent binaryContent = null;
        byte[] bytes;
        if (profile != null && !profile.isEmpty()) {
            try {
                bytes = profile.getBytes();
                binaryContent = new BinaryContent(
                        profile.getOriginalFilename(),
                        profile.getContentType(),
                        profile.getSize()
                );
                binaryContentRepository.save(binaryContent); // 없으면 UUID가 생성 안됨

                UUID binaryContentId = binaryContent.getId();
                applicationEventPublisher.publishEvent(
                        new BinaryContentCreatedEvent(
                                binaryContentId,
                                bytes
                        )
                );

                log.info("[USER_CREATE_PROFILE_UPLOAD_EVENT_PUBLISH] 프로필 업로드 이벤트 발행: profileId={}, fileName={}, contentType={}, count={}",
                        binaryContentId, binaryContent.getFileName(), binaryContent.getContentType(), binaryContent.getSize());

            } catch (IOException e) {
                throw new ProfileUploadFailedException(email, username, e);
            }
        }

        // PasswordEncoder를 이용해 비밀번호 해시 처리
        String encodedPassword = passwordEncoder.encode(request.password());
        User user = new User(email, username, encodedPassword, binaryContent);

        userRepository.save(user);

        log.info("[USER_CREATE] 사용자 등록 완료: userId={}, profileId={}",
                user.getId(), user.getProfile() != null ? user.getProfile().getId() : null);

        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto find(UUID userId) {
        log.debug("[USER_FIND] 사용자 조회 시작");

        User user = validateAndGetUserByUserId(userId);

        log.debug("[USER_FIND] 사용자 조회 완료: userId={}, email={}, username={}, profileId={}",
                user.getId(), user.getEmail(), user.getUsername(), user.getProfile() != null ? user.getProfile().getId() : null);

        return userMapper.toDto(user);
    }

    @Cacheable(value = "userList", unless = "#result.isEmpty()")
    @Transactional(readOnly = true)
    @Override
    public List<UserDto> findAll() {
        log.debug("[USER_LIST_FIND] 사용자 목록 조회 시작");

        List<UserDto> userDtoList = userRepository.findAllWithProfile().stream()
                .map(user -> userMapper.toDto(user))
                .toList();

        log.debug("[USER_LIST_FIND] 사용자 목록 조회 완료: count={}", userDtoList.size());

        return userDtoList;
    }

    @Caching(evict = {
            @CacheEvict(value = "userList", allEntries = true),
            @CacheEvict(value = "channelList", allEntries = true) // 채팅창 내 사용자 정보
    })
    @PreAuthorize("#userId != null and #userId.equals(authentication.principal.userDto.id)")
    @Override
    public UserDto update(
            UUID userId,
            UserUpdateRequest request,
            MultipartFile profile
    ) {
        log.debug("[USER_UPDATE] 사용자 정보 수정 시작: userId={}, newEmail={}, newUsername={}, isInputNewPassword={}",
                userId, request.newEmail(), request.newUsername(), request.newPassword() != null);

        // 로그인 되어있는 user ID null / user 객체 존재 확인
        User user = validateAndGetUserByUserId(userId);

        // 입력값과 현재 값을 비교해서 같으면 null, 새롭게 입력된 값이면 입력값
        String newEmail = changedString(
                request.newEmail(),
                user.getEmail()
        );
        String newUsername = changedString(
                request.newUsername(),
                user.getUsername()
        );
        String newPassword = changedPassword(
                request.newPassword(),
                user.getPassword()
        );

        // 새로운 BinaryContent가 들어왔다면 true / 들어왔는데 기존과 동일하다면 false / 안들어왔다면 false
        byte[] bytes = null;
        boolean binaryContentChanged = false;
        if (profile != null && !profile.isEmpty()) {
            try {
                bytes = profile.getBytes();
                binaryContentChanged = isProfileChanged(bytes, user.getProfile());
            } catch (IOException e) {
                throw new ProfileUploadFailedException(userId, e);
            }
        }

        log.debug("[USER_UPDATE] 사용자 수정 입력값 변경 여부: isChangedEmail={}, isChangedUsername={}, isChangedPassword={}, isChangedProfile={}",
                newEmail != null, newUsername != null, newPassword != null, binaryContentChanged);

        // 전부 입력 X이거나 전부 현재 값과 동일(전부 null)할 때 검증
        validateAllRequestExistingOrNull(newEmail, newUsername, newPassword, binaryContentChanged);

        // 다른 사용자들과 중복 확인
        validateDuplicateUsernameForUpdate(userId, newUsername);
        validateDuplicateEmailForUpdate(userId, newEmail);

        BinaryContent newProfile = null;
        if (binaryContentChanged) {
            newProfile = new BinaryContent(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    (long) bytes.length
            );
            binaryContentRepository.save(newProfile); // 없으면 UUID가 생성 안됨
            applicationEventPublisher.publishEvent(
                    new BinaryContentCreatedEvent(
                            newProfile.getId(),
                            bytes
                    )
            );

            log.info("[USER_UPDATE_PROFILE_UPLOAD_EVENT_PUBLISH] 프로필 업로드 이벤트 발행: profileId={}, fileName={}, contentType={}, count={}",
                    newProfile.getId(), newProfile.getFileName(), newProfile.getContentType(), newProfile.getSize());
        }

        user.update(newUsername, newEmail, newPassword, newProfile);

        log.info("[USER_UPDATE] 사용자 정보 수정 완료: userId={}, profileId={}",
                user.getId(), user.getProfile() != null ? user.getProfile().getId() : null);

        return userMapper.toDto(user);
    }

    @CacheEvict(value = "userList", allEntries = true)
    @PreAuthorize("#userId != null and #userId.equals(authentication.principal.userDto.id)")
    @Override
    public void delete(UUID userId) {
        log.debug("[USER_DELETE] 사용자 삭제 시작: userId={}", userId);

        // 로그인 되어있는 user ID null / user 객체 존재 확인
        User user = validateAndGetUserByUserId(userId);

        userRepository.delete(user);

        log.info("[USER_DELETE] 사용자 삭제 완료: userId={}", userId);
    }

    //// validation
    // user ID null & user 객체 존재 확인
    private User validateAndGetUserByUserId(UUID userId) {
        if (userId == null) {
            throw new InvalidInputException("userId", null);
        }

        return userRepository.findByIdWithProfile(userId)
                .orElseThrow(() -> new UserNotFoundException("userId", userId));
    }

    // email이 이미 존재하는지 확인
    private void validateDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicatedEmailException(email);
        }
    }

    // userName이 이미 존재하는지 확인
    private void validateDuplicateUserName(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new DuplicatedUsernameException(username);
        }
    }

    // 입력값과 현재 값을 비교해서 같으면 null, 새롭게 입력된 값이면 입력값
    private String changedString(String requestValue, String userValue) {
        return requestValue != null && !requestValue.equals(userValue)
                ? requestValue
                : null;
    }
    private String changedPassword(String newPassword, String userPassword) {
        return (newPassword != null
                && !passwordEncoder.matches(newPassword, userPassword))
                ? passwordEncoder.encode(newPassword)
                : null;
    }

    // 전부 입력 X이거나 전부 현재 값과 동일(전부 null)할 때 검증
    private void validateAllRequestExistingOrNull(
            String email,
            String username,
            String password,
            boolean binaryContentChanged
    ) {
        if (email == null
                && username == null
                && password == null
                && !binaryContentChanged
        ) {
            throw new NoChangeValueException("All UpdateRequestField", null);
        }
    }

    // 새로운 BinaryContent가 들어왔다면 true / 들어왔는데 기존과 동일하다면 false / 안들어왔다면 false
    private boolean isProfileChanged(
            byte[] bytes,
            BinaryContent profile
    ) {
        if (profile == null) { // 기존에 BinaryContent 없을 때
            return true; // 새로운 BinaryContent 들어옴
        }

        //기존 프로필이 존재
        UUID profileId = profile.getId();
        BinaryContent oldProfile = binaryContentRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException(profileId));
        UUID oldProfileId = oldProfile.getId();

        // 새로 들어온 BinaryContent와 비교
        // 같으면 -> false -> change 되지 않음
        try {
            byte[] oldBytes = binaryContentStorage.get(oldProfileId).readAllBytes();
            return !Arrays.equals(oldBytes, bytes);
        } catch (IOException e) {
            throw new ProfileReadFailedException(oldProfileId, e);
        }
    }

    // 나를 제외한 newEmail 중에 중복된 값이 있는지 확인
    private void validateDuplicateEmailForUpdate(UUID userId, String email) {
        if (userRepository.isEmailUsedByOther(userId, email)) {
            throw new DuplicatedEmailException(userId, email);
        }
    }

    // 나를 제외한 newUsername 중에 중복된 값이 있는지 확인
    private void validateDuplicateUsernameForUpdate(UUID userId, String username) {
        if (userRepository.isUsernameUsedByOther(userId, username)) {
            throw new DuplicatedUsernameException(userId, username);
        }
    }
}
