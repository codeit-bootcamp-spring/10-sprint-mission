package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequest;
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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
  // @Component의 자식이므로 빈으로 자동으로 등록해 줌
  // 해당 클래스가 service 계층임을 명시
  // 스프링은 Service 계층을 트랜잭션의 시작과 끝으로 봄
@RequiredArgsConstructor
  // final이 붙은 필드를 초기화하는 생성자를 자동으로 생성한다. -> Lombok 적용
  // Lombok이 알아서 만들어 주기 때문에 Repository가 하나 더 늘어나도 생성자 크드를 수정할 필요가 없다.
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserResponse create(UserCreateRequest request) {
        if(userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("이미 존재하는 사용자 이름(username)입니다: " + request.username());
        }
        if(userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 사용중인 이메일(email)입니다: " + request.email());
        }

        // 프로필 이미지 처리 (있으면 저정, 없으면 null)
        UUID profileImageId = null;
        if(request.profileImage() != null) {
            BinaryContent binaryContent = createBinaryContent(request.profileImage());
            binaryContentRepository.save(binaryContent);
            profileImageId = binaryContent.getId();
        }

        // User 엔티티 생성 (있으면 저장, 없으면 null)
        User user = new User(
                request.username(),
                request.email(),
                request.password(),
                profileImageId
        );

        User savedUser = userRepository.save(user);

        // UserStatus 생성
        UserStatus userStatus = new UserStatus(savedUser.getId());
        // TODO: 트랜잭션 롤백 필요성 존재 -> 추후 단계에서 고민
        userStatusRepository.save(userStatus);

        return toResponse(savedUser, userStatus);

        // TODO: username 유효성 검사 로직 추가
        // TODO: email 중복 불가능 검사 로직 추가
        // TODO: password 유효성 검사 로직 추가
    }

    @Override
    public UserResponse find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 id의 유저가 존재하지 않습니다. (userId: " + userId + " )"));

        // UserStatus 조회 (데이터 무결성을 위해 없으면 예외 처리 - 트랜잭션 없는 상황에서는 발생할 일 없음)
        UserStatus status = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("유저 상태(접속 정보)데이터가 누락되었습니다. (userId: " + userId + " )"));

        return toResponse(user, status);
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(user -> {
                    // 각 유저의 상태 정보 조회 // TODO: N+1 문제로 최적화 필요. 현재는 단순 조회 (Later)
                    UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
                            .orElseThrow(() -> new NoSuchElementException("UserStatus를 찾을 수 없습니다."));
                    return toResponse(user, userStatus);
                }).collect(Collectors.toList());
    }

    @Override
    public UserResponse update(UUID userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다. id: " + userId));

        // 새 프로필 이미지가 전달되었다면 저장 - 이미지 수정 안했을 경우 기존 id 유지
        UUID newProfileImageId = user.getProfileImageId();

        if(request.profileImage() != null) {
            // 기존에 설정된 프로필 이미지가 있다면 삭제
            // TODO: 현재 profileId가 null일 경우 기본 프로필 설정으므로 / 기존 설정된 프로필 이미지가 기본 프로필인지 구분하기 위해
            //   기본 프로필 설정을 null이 아니라 명시적으로 따른 사진으로 지정해 줄 필요성 있어보임 -> entity 수정 필요
            if(user.getProfileImageId() != null) {
                binaryContentRepository.deleteById(user.getProfileImageId());
            }

            // 새 이미지 생성 및 저장
            BinaryContent binaryContent = createBinaryContent(request.profileImage());
            binaryContentRepository.save(binaryContent);

            // 새 ID로 교체
            newProfileImageId = binaryContent.getId();
        }

        // 유저 정보 수정
        user.update(
                request.username(),
                request.email(),
                request.password(),
                newProfileImageId
        );
        User updatedUser = userRepository.save(user);

        UserStatus status = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("유저 상태 데이터가 누락되었습니다. id: " + userId));

        return toResponse(updatedUser, status);
    }

    @Override
    public void delete(UUID userId) {
        // TODO: findById() 활용해서 중복되는 부분 제거할 수 있는지 확인 필요
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다. id: " + userId));

        // 연관된 UserStatus 삭제
        userStatusRepository.deleteByUserId(userId);

        // 연관된 BinaryContent (프로필 이미지) 삭제
        if (user.getProfileImageId() != null) {
            binaryContentRepository.deleteById(user.getProfileImageId()); // TODO: deleteByUserId 사용 고려 요망
        }

        // User 삭제
        userRepository.deleteById(userId);
    }

    // ===

    // DTO -> BinaryContent 변환 로직
    private BinaryContent createBinaryContent(BinaryContentRequest request) {
        return new BinaryContent(
                request.fileName(),
                request.contentType(),
                request.bytes()
        );
    }

    // Entity -> DTO 변환 로직
    private UserResponse toResponse(User user, UserStatus userStatus) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileImageId(),
                userStatus.isOnline()
        );
    }
}
