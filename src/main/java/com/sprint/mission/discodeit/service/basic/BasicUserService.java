package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDto;
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
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserDto.Response create(UserDto.CreateRequest request) {
        // 프로필 이미지 처리(선택적)
        UUID imageId = null;
        if (request.profileImage() != null) {
            BinaryContent image = new BinaryContent(
                    request.profileImage().data(),
                    request.profileImage().fileType(),
                    request.profileImage().fileName());
            imageId = binaryContentRepository.save(image).getId();
        }

        // 중복 검증
        if (userRepository.usernameExistence(request.username())) throw new RuntimeException("이미 존재하는 이름입니다.");
        if (userRepository.emailExistence(request.email())) throw new RuntimeException("이미 존재하는 이메일입니다.");

        // 유저 등록
        User user = new User(request.username(), request.email(), request.password());
        if (imageId != null) {
            user.setProfileImageId(imageId);
        }
        User savedUser = userRepository.save(user);
        userStatusRepository.save(new UserStatus(savedUser.getId()));

        return UserDto.Response.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .isOnline(true)
                .profileImageId(savedUser.getProfileImageId())
                .build();


    }

    @Override
    public UserDto.Response find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        // 유저 온라인 상태 정보 포함(패스워드 제외)
        boolean isOnline = userStatusRepository.findByUserId(userId)
                .map(UserStatus::isOnline)
                .orElse(false);

        return UserDto.Response.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .profileImageId(user.getProfileImageId())
                .isOnline(isOnline)
                .build();
    }

    @Override
    public List<UserDto.Response> findAll() {
        return userRepository.findAll().stream()
                .map(user -> {
                    boolean isOnline = userStatusRepository.findByUserId(user.getId())
                            .map(UserStatus::isOnline)
                            .orElse(false);

                    return UserDto.Response.builder()
                            .id(user.getId())
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .profileImageId(user.getProfileImageId())
                            .isOnline(isOnline)
                            .build();
                })
                .toList();
    }

    @Override
    public UserDto.Response update(UserDto.UpdateRequest updateRequest) {
        User user = userRepository.findById(updateRequest.id())
                .orElseThrow(() -> new NoSuchElementException("User with id " + updateRequest.id()+ " not found"));

        // 프로필 이미지 대체 (선택)
        Optional.ofNullable(updateRequest.profileImage())
                .ifPresent(imageDto -> {
                    if (user.getProfileImageId() != null) {
                        binaryContentRepository.deleteById(user.getProfileImageId());
                    }
                    BinaryContent newImage = new BinaryContent(
                            imageDto.data(), imageDto.fileType(), imageDto.fileName()
                    );
                    user.setProfileImageId(binaryContentRepository.save(newImage).getId());
                });

        user.update(updateRequest.username(), updateRequest.email(), updateRequest.password());
        userRepository.save(user);
        return find(user.getId());
    }

    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));

        // BinaryContent 삭제
        if (user.getProfileImageId() != null) {
            binaryContentRepository.deleteById(user.getProfileImageId());
        }

        // UserStatus 삭제
        userStatusRepository.deleteById(userId);

        // 유저 삭제
        userRepository.deleteById(userId);
    }
}
