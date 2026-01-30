package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    // 계정 생성
    @Override
    public UserResponse createAccount(UserCreateRequest request){
        // username, email 중복 체크
        validateDuplicateName(request.name());
        validateDuplicateEmail(request.email());

        // 유저 생성
        User user = new User(
                request.name(),
                request.nickname(),
                request.email(),
                request.password()
        );

        // 프로필 이미지 설정
        if (request.profileImageId() != null) {
            binaryContentRepository.findById(request.profileImageId())
                    .ifPresent(content -> user.updateProfileImage(content.getId())); // 존재하면 프로필 사진 셋팅
        }

        userRepository.save(user);

        // 유저 상태 저장
        UserStatus status = new UserStatus(user.getId(), Instant.now());
        userStatusRepository.save(status);

        return convertToResponse(user);
    }

    // 단건 조회
    @Override
    public UserResponse getAccountById(UUID id) {
        return convertToResponse(findUserById(id));
    }

    // 전체 조회
    @Override
    public List<UserResponse> getAllAccounts() {
        return userRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // 계정 정보 수정
    @Override
    public UserResponse updateAccount(UUID id, UserUpdateRequest request){
        User user = findUserById(id);

        // 이름 수정 + 중복 체크
        if (request.name() != null && !user.getName().equals(request.name())) {
            validateDuplicateName(request.name());
            user.updateName(request.name());
        }

        // 닉네임 수정
        if (request.nickname() != null) {
            user.updateNickname(request.nickname());
        }

        // 이메일 수정 + 중복 체크
        if (request.email() != null && !user.getEmail().equals(request.email())) {
            validateDuplicateEmail(request.email());
            user.updateEmail(request.email());
        }

        // 프로필 이미지 수정
        if (request.profileImageId() != null) {
            binaryContentRepository.findById(request.profileImageId())
                    .ifPresent(content -> user.updateProfileImage(content.getId()));
        }

        userRepository.save(user);

        return convertToResponse(user);
    }

    // 계정 삭제
    @Override
    public void deleteAccount(UUID id) {
        User user = findUserById(id);
        userStatusRepository.deleteByUserId(id);
        userRepository.delete(user);
    }


    // 엔티티 -> DTO 변환
    private UserResponse convertToResponse(User user) {
        boolean isOnline = userStatusRepository.findByUserId(user.getId())
                .map(UserStatus::isOnline)
                .orElse(false);

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getNickname(),
                user.getEmail(),
                user.getProfileImage(),
                isOnline,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    // 유저 검증
    private User findUserById(UUID id) {
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
}
