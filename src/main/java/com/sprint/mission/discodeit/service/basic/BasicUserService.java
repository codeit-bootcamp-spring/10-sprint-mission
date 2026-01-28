package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentCreateDTO;
import com.sprint.mission.discodeit.dto.user.UserCreateDTO;
import com.sprint.mission.discodeit.dto.user.UserResponseDTO;
import com.sprint.mission.discodeit.dto.user.UserUpdateDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public UserResponseDTO createUser(UserCreateDTO dto) {
        if (userRepository.existsByUsername(dto.username())) {
            throw new IllegalArgumentException("이미 사용중인 username입니다.");
        }

        if (userRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("이미 사용중인 email입니다.");
        }

        // userId를 받아오기 위해 우선 객체 생성
        User user = new User(dto.username(), dto.email(), dto.password(), null);
        userRepository.save(user);

        if (dto.profileImage() != null) {
            BinaryContentCreateDTO img = dto.profileImage();

            BinaryContent binaryContent = new BinaryContent(
                    user.getId(),
                    img.data(),
                    img.contentType(),
                    img.filename()
            );

            binaryContentRepository.save(binaryContent);

            // 프로필 사진이 있으면 갱신하기
            user.updateProfileImage(binaryContent.getId());
            userRepository.save(user);
        }

        UserStatus status = new UserStatus(user.getId(), Instant.now());
        userStatusRepository.save(status);

        return UserMapper.toResponse(user, status);
    }

    @Override
    public List<UserResponseDTO> getUserList() {
        List<User> users = userRepository.findAll();
        Map<UUID, UserStatus> userStatusMap = indexStatusByUserId(userStatusRepository.findAll());

        return responseDTOList(userStatusMap, users);
    }

    @Override
    public List<UserResponseDTO> getUsersByChannel(UUID channelId) {
        if (channelRepository.findById(channelId).isEmpty()) {
            throw new NoSuchElementException("해당 id를 가진 채널이 존재하지 않습니다.");
        }

        List<User> users = userRepository.findAll().stream()
                .filter(user -> user.getJoinedChannels().stream()
                        .anyMatch(channel -> channel.getId().equals(channelId)))
                .toList();
        Map<UUID, UserStatus> userStatusMap = indexStatusByUserId(userStatusRepository.findAll());

        return responseDTOList(userStatusMap, users);
    }

    @Override
    public UserResponseDTO getUserInfoByUserId(UUID userId) {
        return UserMapper.toResponse(
                findUserInfoById(userId), userStatusRepository.findByUserId(userId)
        );
    }

    @Override
    public UserResponseDTO updateUser(UserUpdateDTO dto) {
        if (dto.username() != null) {
            updateUserName(dto);
        }
        if (dto.statusType() != null) {
            updateUserStatus(dto);
        }
        if (dto.profileImage() != null) {
            updateUserProfileImage(dto);
        }

        return UserMapper.toResponse(
                findUserInfoById(dto.userId()),
                userStatusRepository.findByUserId(dto.userId())
        );
    }

    @Override
    public void deleteUser(UUID userId) {
        UUID binaryContentId = findUserInfoById(userId).getProfileImageId();

        userStatusRepository.deleteById(userStatusRepository.findByUserId(userId).getId());
        if (binaryContentId != null){
            binaryContentRepository.deleteById(binaryContentId);
        }
        userRepository.deleteById(userId);
    }

    // === 여기부터 내부 메서드 ===

    private User findUserInfoById(UUID userId) {
        Objects.requireNonNull(userId, "userId는 null 값일 수 없습니다.");

        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 id를 가진 유저가 존재하지 않습니다."));
    }

    private void updateMessagesUsername(UUID userId, String newName) {
        messageRepository.findAll().stream()
                .filter(message -> message.getSentUser().getId().equals(userId))
                .forEach(message -> {
                    message.getSentUser().updateUsername(newName);
                    messageRepository.save(message);
                });
    }

    private void updateChannelsUsername(UUID userId, String newName) {
        channelRepository.findAll().forEach( channel -> {
            boolean updated = false;

            for (User joinedUser : channel.getJoinedUsers()) {
                if (joinedUser.getId().equals(userId)) {
                    joinedUser.updateUsername(newName);
                    updated = true;
                }
            }
            // 갱신 되면 저장
            if (updated) {
                channelRepository.save(channel);
            }
        });
    }

    private Map<UUID, UserStatus> indexStatusByUserId(List<UserStatus> statuses) {
        Map<UUID, UserStatus> map = new HashMap<>();
        for (UserStatus status: statuses) {
            map.put(status.getUserId(), status);
        }
        return map;
    }

    private List<UserResponseDTO> responseDTOList(Map<UUID, UserStatus> statusMap, List<User> users) {
        List<UserResponseDTO> userResponseDTOS = new ArrayList<>();

        for (User user: users) {
            UserStatus status = statusMap.get(user.getId());

            if (status == null) {
                throw new IllegalStateException(
                        "UserStatus가 없습니다.: userId=" + user.getId()
                );
            }

            userResponseDTOS.add(UserMapper.toResponse(user, status));
        }

        return userResponseDTOS;
    }

    private void updateUserName(UserUpdateDTO dto) {
        User user = findUserInfoById(dto.userId());

        if (!user.getUsername().equals(dto.username())){
            if (userRepository.existsByUsername(dto.username())) {
                throw new IllegalArgumentException("이미 사용중인 username입니다.");
            }
        }

        user.updateUsername(dto.username());
        userRepository.save(user);

        // 메시지 파일 업데이트
        updateMessagesUsername(dto.userId(), dto.username());
        // 채널 파일 업데이트
        updateChannelsUsername(dto.userId(), dto.username());
    }

    private void updateUserStatus(UserUpdateDTO dto) {
        UserStatus status = userStatusRepository.findByUserId(dto.userId());
        status.updateStatusType(dto.statusType());
        userStatusRepository.save(status);
    }

    private void updateUserProfileImage(UserUpdateDTO dto) {
        if (dto.profileImage() == null) {
            throw new IllegalArgumentException("profile 값이 존재하지 않습니다.");
        }

        User user = findUserInfoById(dto.userId());
        BinaryContentCreateDTO img = dto.profileImage();

        BinaryContent binaryContent = new BinaryContent(
                user.getId(),
                img.data(),
                img.contentType(),
                img.filename()
        );

        binaryContentRepository.save(binaryContent);

        // 프로필 사진이 있으면 갱신하기
        user.updateProfileImage(binaryContent.getId());
        userRepository.save(user);
    }
}
