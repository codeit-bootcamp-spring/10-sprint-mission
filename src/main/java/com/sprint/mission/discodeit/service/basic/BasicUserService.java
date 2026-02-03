package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserResponse create(UserCreateRequest request) {
        // 유저 생성을 위한 필수 검증
        validateCreateRequest(request);

        // 프로필 이미지 등록 여부
        UUID profileId = null;
        if (request.profile() != null) {
            // 프로필 이미지 저장
            profileId = saveProfile(request.profile()).getId();
        }

        // 유저 생성
        User user = new User(
                request.email(),
                request.password(),
                request.nickname(),
                profileId
        );

        // 유저 저장
        userRepository.save(user);

        // 유저 상태 저장, 기본값 오프라인
        UserStatus userStatus = new UserStatus(user.getId(), UserStatus.Status.OFFLINE);
        userStatusRepository.save(userStatus);

        return UserResponse.from(user, userStatus);
    }

    @Override
    public UserResponse findById(UUID userId) {
        // 조회 대상 유저가 존재하는지 검증
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));

        // 유저 상태 조회
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("유저 상태가 존재하지 않습니다."));


        return UserResponse.from(user, userStatus);
    }

    @Override
    public List<UserResponse> findAll() {
        // 전체 유저 조회
        List<User> users = userRepository.findAll();
        List<UserResponse> responses = new ArrayList<>();

        for (User user : users) {
            // 유저 상태 조회
            UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("유저 상태가 존재하지 않습니다."));
            responses.add(UserResponse.from(user, userStatus));
        }

        return responses;
    }

    @Override
    public UserResponse update(UserUpdateRequest request) {
        // DTO 검증
        if (request == null || request.id() == null) {
            throw new RuntimeException("유저가 존재하지 않습니다.");
        }

        // 수정 대상 유저가 존재하는지 검증
        User user = userRepository.findById(request.id())
                .orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));

        // 수정 DTO 데이터 확인
        String newPassword = request.newPassword();
        String newNickname = request.newNickname();
        BinaryContentCreateRequest newProfile = request.newProfile();

        // 새로운 비밀번호가 blank면 예외
        if (newPassword != null && newPassword.isBlank()) {
            throw new RuntimeException("비밀번호가 필요합니다.");
        }

        // 새로운 닉네임이 blank면 예외
        if (newNickname != null) {
            if (newNickname.isBlank()) {
                throw new RuntimeException("닉네임이 필요합니다.");
            }
            // null, blank 모두 아니면 기존 닉네임과 일치하는지 확인
            if (!newNickname.equals(user.getNickname())) {
                // 새로운 닉네임이라면 중복 체크
                validateUpdateUniqueness(user.getId(), newNickname);
            }
        }

        // null이 아니면 새로운 데이터 주입, null이면 기존 데이터 유지
        String resolvedPassword = (newPassword != null) ? newPassword : user.getPassword();
        String resolvedNickname = (newNickname != null) ? newNickname : user.getNickname();

        // null이 아니면 새로운 데이터 주입, null이면 기존 데이터 유지
        UUID oldProfileId = user.getProfileId();
        UUID resolvedProfileId = oldProfileId;
        if (newProfile != null) {
            // 새로운 프로필을 프로필 저장소에 저장
            BinaryContent saved = saveProfile(newProfile);
            resolvedProfileId = saved.getId();
        }

        // 유저 정보 수정 및 저장
        user.update(resolvedPassword, resolvedNickname, resolvedProfileId);
        userRepository.save(user);

        // 프로필 교체 시에만 기존 파일 삭제
        if (newProfile != null && oldProfileId != null && !oldProfileId.equals(resolvedProfileId)) {
            binaryContentRepository.delete(oldProfileId);
        }

        // 유저 상태 조회
        UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("유저 상태가 존재하지 않습니다."));

        return UserResponse.from(user, userStatus);
    }

    @Override
    public void delete(UUID userId) {
        // 삭제 대상 유저가 존재하는지 검증
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));

        // 유저 프로필 이미지 조회 후 삭제
        if (user.getProfileId() != null) {
            binaryContentRepository.delete(user.getProfileId());
        }

        // 유저 상태 조회
        UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("유저 상태가 존재하지 않습니다."));

        // 유저 상태 삭제
        userStatusRepository.delete(userStatus.getId());

        // 유저 삭제
        userRepository.delete(user.getId());
    }

    private void validateCreateRequest(UserCreateRequest request) {
        if (request == null) {
            throw new RuntimeException("요청이 필요합니다.");
        }
        if (request.email() == null || request.email().isBlank()) {
            throw new RuntimeException("이메일이 필요합니다.");
        }
        if (request.password() == null || request.password().isBlank()) {
            throw new RuntimeException("비밀번호가 필요합니다.");
        }
        if (request.nickname() == null || request.nickname().isBlank()) {
            throw new RuntimeException("닉네임이 필요합니다.");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("이미 사용 중인 이메일입니다.");
        }
        if (userRepository.existsByNickname(request.nickname())) {
            throw new RuntimeException("이미 사용 중인 닉네임입니다.");
        }
    }

    private void validateUpdateUniqueness(UUID userId, String newNickname) {
        if (userRepository.existsByNicknameExceptUserId(newNickname, userId)) {
            throw new RuntimeException("이미 사용 중인 닉네임입니다.");
        }
    }

    private BinaryContent saveProfile(BinaryContentCreateRequest request) {
        if (request.bytes() == null || request.bytes().length == 0) {
            throw new RuntimeException("이미지가 없습니다.");
        }
        BinaryContent binaryContent = new BinaryContent(
                request.fileName(),
                request.contentType(),
                request.bytes()
        );
        return binaryContentRepository.save(binaryContent);
    }
}
