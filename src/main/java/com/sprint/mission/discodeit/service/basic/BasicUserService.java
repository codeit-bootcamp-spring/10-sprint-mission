package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;
    private final ReadStatusRepository readStatusRepository;

    // 계정 생성
    @Override
    public UserResponse create(UserCreateRequest request){
        // username, email 중복 체크
        validateDuplicateName(request.name());
        validateDuplicateEmail(request.email());

        // 프로필 사진 설정
        UUID profileId = null;
        if (request.profileImage() != null) {
            BinaryContent profileImage = new BinaryContent(
                    request.profileImage().fileName(),
                    request.profileImage().data()
            );
            binaryContentRepository.save(profileImage);
            profileId = profileImage.getId();
        }

        // 유저 생성
        User user = new User(
                request.name(),
                request.nickname(),
                request.email(),
                request.password(),
                profileId
        );
        userRepository.save(user);

        // 유저 상태 생성
        UserStatus status = new UserStatus(user.getId(), true, Instant.now());
        userStatusRepository.save(status);

        return convertToResponse(user, status);
    }

    // 단건 조회
    @Override
    public UserResponse getUserById(UUID id) {
        User user = validateUserExists(id);
        UserStatus status = getUserStatus(id);
        return convertToResponse(user, status);
    }

    // 전체 조회
    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> convertToResponse(user, getUserStatus(user.getId())))
                .toList();
    }

    // 계정 정보 수정
    @Override
    public UserResponse updateUser(UUID id, UserUpdateRequest request){
        User user = validateUserExists(id);

        // 이름 수정 + 중복 체크
        Optional.ofNullable(request.name())
                .filter(name -> !name.equals(user.getName()))
                .ifPresent(name -> {
                    validateDuplicateName(name);
                    user.updateName(name);
                });

        // 닉네임 수정
        Optional.ofNullable(request.nickname()).ifPresent(user::updateNickname);

        // 이메일 수정 + 중복 체크
        Optional.ofNullable(request.email())
                .filter(email -> !email.equals(user.getEmail()))
                .ifPresent(email -> {
                    validateDuplicateEmail(email);
                    user.updateEmail(email);
                });

        // 프로필 사진 수정
        if (request.profileImage() != null) {
            if (user.getProfileId() != null){
                binaryContentRepository.deleteById(user.getProfileId());
            }
            BinaryContent newImage = new BinaryContent(
                    request.profileImage().fileName(),
                    request.profileImage().data()
            );
            binaryContentRepository.save(newImage);
            user.updateProfileImage(newImage.getId());
        }

        userRepository.save(user);
        UserStatus status = getUserStatus(id);

        return convertToResponse(user, status);
    }

    // 계정 삭제
    @Override
    public void deleteById(UUID id) {
        User user = validateUserExists(id);

        // 유저가 참여하고 있는 채널 삭제
        readStatusRepository.deleteById(id);

        // 유저 상태 삭제
        userStatusRepository.deleteByUserId(id);

        // 프로필 사진 삭제
        if (user.getProfileId() != null) {
            binaryContentRepository.deleteById(user.getProfileId());
        }

        // 유저 삭제
        userRepository.deleteById(id);
    }


    // 유저 상태 조회
    private UserStatus getUserStatus(UUID userId) {
        return userStatusRepository.findByUserId(userId).orElse(null);
    }

    // 유저 검증
    private User validateUserExists(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));
    }

    // 사용자명 중복 체크
    private void validateDuplicateName(String name) {
        if (userRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자명입니다.");
        }
    }

    // 이메일 중복 체크
    private void validateDuplicateEmail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
    }

    // 엔티티 -> DTO 변환
    private UserResponse convertToResponse(User user, UserStatus status) {
        boolean isOnline = (status != null) && status.isOnline();

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getNickname(),
                user.getEmail(),
                user.getProfileId(),
                isOnline,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
