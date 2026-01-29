package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    @Override
    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.findAll().stream().anyMatch(u -> u.getName().equals(request.getName()))) {
            throw new IllegalArgumentException("이미 존재하는 이름입니다: " + request.getName());
        }
        if (userRepository.findAll().stream().anyMatch(u -> u.getEmail().equals(request.getEmail()))) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + request.getEmail());
        }

        UUID profileId = null;
        if (request.getProfileContent() != null && request.getProfileContent().length > 0) {
            validateContentType(request.getContentType());
            BinaryContent content = new BinaryContent(
                    request.getProfileContent(),
                    request.getProfileFileName(),
                    request.getContentType()
            );
            binaryContentRepository.save(content);
            profileId = content.getId();
        }

        User user = new User(request.getName(), request.getEmail(), request.getPassword(), profileId);
        userRepository.save(user);

        UserStatus userStatus = new UserStatus(user.getId(), LocalDateTime.now());
        userStatusRepository.save(userStatus);

        return toResponse(user);
    }

    @Override
    public UserResponse getUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        return toResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse updateUser(UserUpdateRequest request) {
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        if (request.getName() != null && !request.getName().isBlank()) {
            user.updateName(request.getName());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            user.updateEmail(request.getEmail());
        }

        if (request.getProfileContent() != null && request.getProfileContent().length > 0) {
            validateContentType(request.getContentType());
            
            if (user.getProfileId() != null) {
                binaryContentRepository.delete(user.getProfileId());
            }
            BinaryContent content = new BinaryContent(
                    request.getProfileContent(),
                    request.getProfileFileName(),
                    request.getContentType()
            );
            binaryContentRepository.save(content);
            user.updateProfileId(content.getId());
        }

        userRepository.save(user);

        for (Channel c : new ArrayList<>(user.getChannels())) {
            channelRepository.findById(c.getId()).ifPresent(channelRepository::save);
        }
        
        return toResponse(user);
    }

    @Override
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        for (Message message : new ArrayList<>(user.getMessages())) {
            messageRepository.delete(message.getId());
        }

        for (Channel c : new ArrayList<>(user.getChannels())) {
            channelRepository.findById(c.getId()).ifPresent(channel -> {
                channel.removeUser(user);
                channelRepository.save(channel);
            });
        }

        if (user.getProfileId() != null) {
            binaryContentRepository.delete(user.getProfileId());
        }

        userStatusRepository.findByUserId(id)
                .ifPresent(status -> userStatusRepository.delete(status.getId()));

        userRepository.delete(id);
    }

    private UserResponse toResponse(User user) {
        boolean isOnline = userStatusRepository.findByUserId(user.getId())
                .map(UserStatus::isOnline)
                .orElse(false);

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                isOnline,
                user.getProfileId()
        );
    }

    private void validateContentType(String contentType) {
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("허용되지 않는 파일 형식입니다. (허용: jpg, png, gif, webp)");
        }
    }
}
