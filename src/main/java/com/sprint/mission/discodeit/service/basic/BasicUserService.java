package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    private ChannelService channelService;
    private MessageService messageService;

    public void setChannelService(ChannelService channelService) {
        this.channelService = channelService;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public UserDto.Response create(UserDto.CreateRequest request) {
        validateDuplicateEmail(request.email());
        validateDuplicateUserName(request.username());

        User newUser = new User(
                request.username(),
                request.email(),
                request.password(),
                request.profileId()
        );
        UserStatus status = new UserStatus(newUser.getId());
        newUser.setStatus(status);

        if (userStatusRepository != null) userStatusRepository.save(status);

        userRepository.save(newUser);

        return convertToResponse(newUser);
    }

    @Override
    public UserDto.Response findById(UUID id) {
        return convertToResponse(findUserEntityById(id));
    }

    @Override
    public List<UserDto.Response> findAll() {
        return userRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findUsersByChannelId(UUID channelId) {
        return channelService.findById(channelId).getMembers();
    }

    @Override
    public UserDto.Response update(UserDto.UpdateRequest request) {
        User user = findUserEntityById(request.id());

        if (request.email() != null && !request.email().equals(user.getEmail())) {
            validateDuplicateEmail(request.email());
        }

        user.update(
                request.username(),
                request.email(),
                request.password(),
                request.profileId()
        );

        userRepository.save(user);
        return convertToResponse(user);
    }

    @Override
    public void delete(UUID userId) {
        User user = findUserEntityById(userId);

        messageRepository.findAll().stream()
                .filter(m -> m.getUser().getId().equals(userId))
                .forEach(m -> {
                    if (m.getChannel() != null) {
                        m.getChannel().removeMessage(m);
                        channelRepository.save(m.getChannel());
                    }
                    messageRepository.deleteById(m.getId());
                });

        channelRepository.findAll().forEach(channel -> {
            if (channel.getMembers().contains(user)) {
                channel.removeMember(user);
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

    private UserDto.Response convertToResponse(User user) {
        return new UserDto.Response(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileId(),
                user.getStatus() != null && user.getStatus().isOnline()
        );
    }
}
