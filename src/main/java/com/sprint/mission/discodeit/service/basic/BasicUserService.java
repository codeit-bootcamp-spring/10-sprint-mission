package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public UserDto.UserResponse create(UserDto.UserRequest request, String filePath) {
        Objects.requireNonNull(request.name(), "유저 이름은 필수 항목입니다.");
        Objects.requireNonNull(request.email(), "이메일은 필수 항목입니다.");

        // 중복 검사
        if (userRepository.findAll().stream().anyMatch(user -> user.getName().equals(request.name()))) {
            throw new IllegalArgumentException("이미 존재하는 이름입니다.");
        }
        if (userRepository.findAll().stream().anyMatch(user -> user.getEmail().equals(request.email()))) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        // 유저 상태, 프로필 이미지 객체

        BinaryContent binaryContent = new BinaryContent(new BinaryContentDto.BinaryContentRequest(filePath, "image"));

        User user = new User(request, binaryContent.getId());
        UserStatus userStatus = new UserStatus(user.getId());

        // 레포 저장
        userRepository.save(user);
        userStatusRepository.save(userStatus);
        binaryContentRepository.save(binaryContent);

        return UserDto.UserResponse.from(user, userStatus);
    }

    @Override
    public UserDto.UserResponse findById(UUID userId) {
        Objects.requireNonNull(userId, "유저 Id가 유효하지 않습니다.");
        User user = Objects.requireNonNull(userRepository.findById(userId), "해당 유저가 존재하지 않습니다.");
        UserStatus status = Objects.requireNonNull(userStatusRepository.findByUserId(userId), "유저 상태 정보가 없습니다.");

        return UserDto.UserResponse.from(user, status);
    }

    @Override
    public List<UserDto.UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserStatus status = Objects.requireNonNull(userStatusRepository.findByUserId(user.getId()), "유저 상태 정보가 없습니다.");
                    return UserDto.UserResponse.from(user, status);
                })
                .toList();
    }

    @Override
    public UserDto.UserResponse update(UUID userId, UserDto.UserRequest request, String filePath) {
        User user = Objects.requireNonNull(userRepository.findById(userId), "해당 유저가 존재하지 않습니다.");
        UserStatus status = Objects.requireNonNull(userStatusRepository.findByUserId(user.getId()), "유저 상태 정보가 없습니다.");
        Optional.ofNullable(request.name()).ifPresent(user::updateName);
        Optional.ofNullable(request.email()).ifPresent(user::updateEmail);
        Optional.ofNullable(request.password()).ifPresent(user::updatePassword);
        // 새로운 content 객체를 생성하여 대체
        Optional.ofNullable(filePath).ifPresent(path -> {
                    BinaryContent binaryContent = new BinaryContent(new BinaryContentDto.BinaryContentRequest(path, "image"));
                    user.updateBinaryContentId(binaryContent.getId());
                    binaryContentRepository.save(binaryContent);
                });
        userRepository.save(user);
        return UserDto.UserResponse.from(user, status);
    }

    @Override
    public void delete(UUID userId) {
        // 입력값 검증
        User user = Objects.requireNonNull(userRepository.findById(userId), "해당 유저가 존재하지 않습니다.");
        // 유저가 참가했던 채널들에서 해당 유저 삭제
        user.getChannels().forEach(channel -> channel.removeUser(userRepository.findById(userId)));

        // userStatus, binaryContent 객체 삭제
        userStatusRepository.deleteById(userStatusRepository.findByUserId(userId).getId());
        binaryContentRepository.deleteById(user.getBinaryContentId());

        userRepository.delete(userId);
    }

    public void joinChannel(UUID userId, UUID channelId) {
        User user = userRepository.findById(userId);
        Channel channel = channelRepository.findById(channelId);
        // 유저에 채널 추가
        user.joinChannel(channel);
        // 채널에 유저 추가
        channel.addUser(user);
    }

    public void leaveChannel(UUID userId, UUID channelId) {
        User user = userRepository.findById(userId);
        Channel channel = channelRepository.findById(channelId);
        // 유저에서 채널 삭제
        user.leaveChannel(channel);
        // 채널에서 유저 삭제
        channel.removeUser(user);
    }
}