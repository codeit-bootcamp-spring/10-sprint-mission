package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.response.UserResponse;
import com.sprint.mission.discodeit.dto.user.response.UserStatusResponse;
import com.sprint.mission.discodeit.dto.user.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {
    private final ChannelService channelService;
    // 필드
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public User create(UserCreateRequest request) {
        // 이름, 이메일 같으면 안되는지 확인
        userRepository.findAll().stream()
                .filter(user -> user.getName().equals(request.name()))
                .findFirst()
                .ifPresent(
                        u -> {
                            throw new IllegalArgumentException("Already Present name");
                        });

        userRepository.findAll().stream()
                .filter(user -> user.getEmail().equals(request.email()))
                .findFirst()
                .ifPresent(
                        u -> {
                            throw new IllegalArgumentException("Already Present email");
                        }
                );

        // 선택적으로 프로필 등록
        UUID profileImageID = null;
        if (request.profileImage() != null) {
            BinaryContent profileImage = new BinaryContent(request.profileImage(), request.type());
            profileImageID = profileImage.getId();
            binaryContentRepository.save(profileImage);
        }

        // user 생성 with DTO
        User user = new User(request.name(), request.email(), request.password(), profileImageID);

        // userStatus 생성
        UserStatus userStatus = new UserStatus(user.getId());
        // userStatusRepository에 저장
        userStatusRepository.save(userStatus);

        return userRepository.save(user);
    }

    @Override
    public UserResponse find(UUID id) {
        // user 조회
        User user = userRepository.find(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        // UserStatusRepo status 조회
        UserStatus status = userStatusRepository.find(id);
        return new UserResponse(user.getId(), user.getName(), new UserStatusResponse(status.isOnline()));
    }

    @Override
    public List<UserResponse> findAll() {
        List<User> users = new ArrayList<>(userRepository.findAll());
        return users.stream()
                .map(
                        user -> {
                            UserStatus status = userStatusRepository.find(user.getId());
                            return new UserResponse(user.getId(), user.getName(), new UserStatusResponse(status.isOnline()));
                        })
                .toList();
    }

    // 이름 업데이트가 맞나?
    @Override
    public User updateName(UserUpdateRequest request) {
        // user 조회
        User user = userRepository.find(request.userID())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.userID()));

        // 동명이인 확인
        userRepository.findAll().stream()
                .filter(u -> u.getName().equals(request.name()))
                .findFirst()
                .ifPresent(
                        u -> {
                            throw new IllegalArgumentException("Already Present name");
                        });

        // user에서 이름 update
        user.updateName(request.name());

        Set<UUID> channelIDs = new HashSet<>();
        // user의 channel, memberList에서 user이름 업데이트
        for (Channel channel : user.getChannelsList()) {
            for (User u : channel.getMembersList()) {
                if (u.getId().equals(request.userID())) {
                    u.updateName(request.name());
                    channelIDs.add(channel.getId());
                }
            }
        }

        // -------------------------------------------

        // 추가 수정
        for (UUID channelID : channelIDs) {
            channelService.updateName(channelID, request.name());
        }

        // message의 sender 이름 변경
        Set<UUID> messageIDs = new HashSet<>();
        for (Message message : user.getMessageList()) {
            message.getSender().updateName(request.name());
            messageIDs.add(message.getId());
        }

        // messageRepository save()
        for (UUID messageID : messageIDs) {
            messageRepository.save(messageRepository.find(messageID)
                    .orElseThrow(() -> new IllegalArgumentException("Message not found: " + messageID)));
        }

        // [저장] 변경사항 저장
        return userRepository.save(user);
    }

    // user가 해당 ch에서 보낸 msg 삭제 반영 X
    @Override
    public void deleteUser(UUID userID) {
        // 존재하는 user인지 검증
        User user = userRepository.find(userID)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userID));

        // messageRepository에서 user가 보낸 message 삭제
        List<Message> messages = new ArrayList<>(user.getMessageList());
        for (Message message : messages) {
            if (message.getSender() != null && message.getSender().getId().equals(userID)) {
                messageRepository.deleteMessage(message.getId());
            }
        }

        // 삭제할 User의 channel 모두 탈퇴
        List<UUID> channelIDs = new ArrayList<>();
        for (Channel channel : user.getChannelsList()) {
            channelIDs.add(channel.getId());
        }

        for (UUID channelID : channelIDs) {
            channelService.leaveChannel(userID, channelID);
        }

        // userStatusRepo에서 삭제
        userStatusRepository.deleteUserStatus(user.getId());

        // binaryContentRepo에서 삭제
        if(user.getProfileImageID() != null){
            binaryContentRepository.delete(user.getProfileImageID());
        }

        // [저장]
        userRepository.deleteUser(user);
    }

    @Override
    public List<Channel> findJoinedChannels(UUID userID) {
        // user find 검증
        User user = userRepository.find(userID)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userID));
        List<Channel> channels = new ArrayList<>(user.getChannelsList());
        return channels;
    }
}
