package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    // TODO: 추후 각 파일 리포지토리 고도화 시에 @Autowired 제거
    @Autowired(required = false)
    private UserStatusRepository  userStatusRepository;

    @Autowired(required = false)
    private BinaryContentRepository  binaryContentRepository;

    @Override
    public UserDto.Response create(UserDto.CreateRequest request) {
        validateDuplicateEmail(request.email());
        validateDuplicateUserName(request.username());

        UUID profileImageId = processImage(null, request.profileImage());

        User newUser = new User(
                request.username(),
                request.email(),
                request.password(),
                profileImageId
        );
        userRepository.save(newUser);

        if (userStatusRepository != null)  {
            UserStatus status = new UserStatus(newUser.getId());
            userStatusRepository.save(status);
        }

        return convertToResponse(newUser);
    }

    @Override
    public UserDto.Response findById(UUID id) {
        User user = findUserEntityById(id);
        return convertToResponse(user);
    }

    @Override
    public List<UserDto.Response> findAll() {
        return userRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findUsersByChannelId(UUID channelId) {
        List<UUID> memberIds = channelRepository.findById(channelId)
                .map(channel -> channel.getMemberIds())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 채널입니다."));
        return memberIds.stream()
                .map(this::findUserEntityById)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto.Response update(UserDto.UpdateRequest request) {
        User user = findUserEntityById(request.id());

        if (request.email() != null && !request.email().equals(user.getEmail())) {
            validateDuplicateEmail(request.email());
        }

        UUID newProfileId = processImage(user.getProfileId(), request.profileImage());

        user.update(
                request.username(),
                request.email(),
                request.password(),
                newProfileId
        );

        userRepository.save(user);
        return convertToResponse(user);
    }

    @Override
    public void delete(UUID userId) {
        User user = findUserEntityById(userId);

        messageRepository.findAll().stream()
                .filter(m -> userId.equals(m.getAuthorId()))
                        .forEach(m -> messageRepository.deleteById(m.getId()));

        channelRepository.findAll().forEach(channel -> {
            if (channel.getMemberIds().contains(userId)) {
                channel.removeMember(userId);
                channelRepository.save(channel);
            }
        });

        if (user.getProfileId() != null && binaryContentRepository != null) {
            binaryContentRepository.delete(user.getProfileId());
        }

        if (userStatusRepository != null) {
            userStatusRepository.delete(user.getId());
        }

        userRepository.deleteById(userId);
    }

    private User findUserEntityById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 사용자 입니다. ID: " + id));
    }

    // 이메일 중복 시 예외를 던져 가입 중단 (Fail-Fast)
    private void validateDuplicateEmail(String userEmail) {
        if (userRepository.findByEmail(userEmail).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + userEmail);
        }
    }

    // 이름 중복 시 예외를 던져 가입 중단 (Fail-Fast)
    private void validateDuplicateUserName(String username) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이름입니다: " + username);
        }
    }

    // [헬퍼 메서드]: 엔티티를 클라이언트 응답용 DTO로 변환 및 데이터 가공
    private UserDto.Response convertToResponse(User user) {
        boolean isOnline = false;
        if (userStatusRepository != null) {
            isOnline = userStatusRepository.findById(user.getId())
                    .map(status -> status.isOnline())
                    .orElse(false);
        }
        return new UserDto.Response(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileId(),
                isOnline
        );
    }

    // [헬퍼 메서드]: 이미지 생성(createPublicChannel) 및 기존 이미지 수정(update)
    private UUID processImage(UUID existingId, UserDto.BinaryContentDto imageDto) {
        if (imageDto == null || binaryContentRepository == null) return existingId;

        // 기존 이미지가 있으면 삭제 (Update)
        if (existingId != null) {
            binaryContentRepository.delete(existingId);
        }

        // 새 이미지 저장
        BinaryContent newImage = new BinaryContent(
                UUID.randomUUID(),
                imageDto.fileName(),
                imageDto.data(),
                Instant.now()
        );
        binaryContentRepository.save(newImage);
        return newImage.getId();
    }
}
