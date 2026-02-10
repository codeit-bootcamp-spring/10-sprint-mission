package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.helper.EntityFinder;
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
    private final EntityFinder entityFinder;
    private final ReadStatusRepository readStatusRepository;
    // 검증 헬퍼 메서드

    @Override
    public UserDto.UserResponse create(UserDto.UserRequest request, String filePath) {
        Objects.requireNonNull(request.name(), "유저 이름은 필수 항목입니다.");
        Objects.requireNonNull(request.email(), "이메일은 필수 항목입니다.");

        // 중복 검사
        userRepository.findAll().stream()
                .filter(user -> user.getName().equals(request.name()) || user.getEmail().equals(request.email()))
                .findFirst()
                .ifPresent(user -> {
                    if (user.getName().equals(request.name())) throw new IllegalArgumentException("이미 존재하는 이름입니다.");
                    if (user.getEmail().equals(request.email())) throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
                });

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
        User user = entityFinder.getUser(userId);
        UserStatus status = entityFinder.getStatusByUser(userId);

        return UserDto.UserResponse.from(user, status);
    }

    @Override
    public List<UserDto.FindAllUserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserStatus status = entityFinder.getStatusByUser(user.getId());
                    return UserDto.FindAllUserResponse.from(user, status);
                })
                .toList();
    }

    @Override
    public UserDto.UserResponse update(UUID userId, UserDto.UserRequest request, String filePath) {
        User user = entityFinder.getUser(userId);
        UserStatus status = entityFinder.getStatusByUser(userId);

        // 자기 자신을 제외한 중복 검사
        userRepository.findAll().stream()
                .filter(user1 -> !user1.getId().equals(userId))
                .filter(user1 -> user1.getName().equals(request.name()) || user1.getEmail().equals(request.email()))
                .findFirst()
                .ifPresent(user1 -> {
                    if (user1.getName().equals(request.name())) throw new IllegalArgumentException("이미 존재하는 이름입니다.");
                    if (user1.getEmail().equals(request.email())) throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
                });

        Optional.ofNullable(request.name()).ifPresent(user::updateName);
        Optional.ofNullable(request.email()).ifPresent(user::updateEmail);
        Optional.ofNullable(request.password()).ifPresent(user::updatePassword);
        // 새로운 content 객체를 생성하여 대체
        Optional.ofNullable(filePath).ifPresent(path -> {
            BinaryContent binaryContent = new BinaryContent(new BinaryContentDto.BinaryContentRequest(path, "image"));
            binaryContentRepository.deleteById(user.getBinaryContentId());
            user.updateBinaryContentId(binaryContent.getId());
            binaryContentRepository.save(binaryContent);
        });
        userRepository.save(user);
        return UserDto.UserResponse.from(user, status);
    }

    @Override
    public void delete(UUID userId) {
        // 입력값 검증
        User user = entityFinder.getUser(userId);
        // 유저가 참가했던 채널들에서 해당 유저 삭제
        new ArrayList<>(user.getChannels()).forEach(channel -> {
            entityFinder.getChannel(channel).removeUser(user);
            channelRepository.save(entityFinder.getChannel(channel));
        });

        // userStatus, binaryContent 객체 삭제
        userStatusRepository.delete(entityFinder.getStatusByUser(userId).getId());
        binaryContentRepository.deleteById(user.getBinaryContentId());
        readStatusRepository.findAllByUserId(userId).forEach(readStatus -> readStatusRepository.delete(readStatus.getId()));
        userRepository.delete(userId);
    }

    @Override
    public void joinChannel(UUID userId, UUID channelId) {
        User user = entityFinder.getUser(userId);
        Channel channel = entityFinder.getChannel(channelId);
        // 유저에 채널 추가
        user.joinChannel(channel);
        // 채널에 유저 추가
        channel.addUser(user);
        // 레포에 반영
        userRepository.save(user);
        channelRepository.save(channel);
    }

    @Override
    public void leaveChannel(UUID userId, UUID channelId) {
        User user = entityFinder.getUser(userId);
        Channel channel = entityFinder.getChannel(channelId);
        // 유저에서 채널 삭제
        user.leaveChannel(channel);
        // 채널에서 유저 삭제
        channel.removeUser(user);
        // 레포에 반영
        userRepository.save(user);
        channelRepository.save(channel);
    }

    @Override
    // 유저 체크 헬퍼 메서드
    public User checkUser(String name, String password) {
        return userRepository.findAll().stream()
                .filter(user -> user.getName().equals(name) && user.getPassword().equals(password))
                .findFirst().orElseThrow(() -> new RuntimeException("해당하는 유저가 없습니다."));
    }

    @Override
    public User checkUserByName(String name){
        return userRepository.findAll().stream()
                .filter(user -> user.getName().equals(name))
                .findFirst().orElseThrow(() -> new RuntimeException("해당하는 유저가 없습니다."));
    }
}