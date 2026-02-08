package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.response.UserResponse;
import com.sprint.mission.discodeit.dto.user.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.user.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {
    // 필드
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    private final UserMapper userMapper;

    @Override
    public UserResponse create(UserCreateRequest userRequest, Optional<BinaryContentCreateRequest> profileRequest) {
        // 이름, 이메일 유효성 검증
        validateName(userRequest.name());
        validateEmail(userRequest.email());

        // 선택적으로 프로필 등록
        UUID profileImageID = profileRequest
                .map(pr -> {
                    BinaryContent profile = new BinaryContent(
                            pr.fileName(),
                            pr.bytes(),
                            pr.contentType()
                    );
                    return binaryContentRepository.save(profile).getId();
                })
                .orElse(null);


        // user 생성 with DTO
        User user = userMapper.toEntity(userRequest, profileImageID);
        User savedUser = userRepository.save(user);

        // userStatus 생성
        UserStatus userStatus = new UserStatus(savedUser.getId());
        userStatusRepository.save(userStatus);

        return userMapper.toResponse(savedUser, userStatus);
    }

    @Override
    public UserResponse find(UUID id) {
        // user 조회
        User user = userRepository.find(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        // UserStatusRepo status 조회
        UserStatus status = userStatusRepository.find(id)
                .orElseThrow(() -> new IllegalArgumentException("UserStatus not found: " + id));
        return userMapper.toResponse(user, status);
    }

    @Override
    public List<UserResponse> findAll() {
        List<User> users = new ArrayList<>(userRepository.findAll());
        return users.stream()
                .map(
                        user -> {
                            UserStatus status = userStatusRepository.find(user.getId())
                                    .orElseThrow(() -> new IllegalArgumentException("UserStatus not found: " + user.getId()));
                            return userMapper.toResponse(user, status);
                        })
                .toList();
    }

    // 이름. 프로필 선택적 업데이트
    // 업데이트 원하지 않는 경우 null을 전달하는게 맞나??
    @Override
    public UserResponse update(UUID userID, UserUpdateRequest request, Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
        // user 조회
        User user = userRepository.find(userID)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userID));

        // user 이름 선택적 업데이트
        Optional.ofNullable(request.newUserName()).ifPresent(name -> {
            validateName(name);
            user.updateName(name);
        });

        // user 이메일 선택적 업데이트
        Optional.ofNullable(request.newEmail()).ifPresent(email -> {
            validateEmail(email);
            user.updateEmail(email);
        });

        // user의 프로필 선택적 업데이트
        UUID profileID = optionalProfileCreateRequest
                .map(pr ->{
                    BinaryContent profile = new BinaryContent(pr.fileName(), pr.bytes(), pr.contentType());
                    return binaryContentRepository.save(profile).getId();
                })
                .orElse(null);

        user.updateProfileImageID(profileID);

        UserStatus userStatus = userStatusRepository.findByUserID(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("UserStatus not found: " + user.getId()));

        User savedUser = userRepository.save(user);

        Set<UUID> channelIDs = new HashSet<>();
        // user가 가입한 channel의 memberList에서 user이름 및 프로필 업데이트
        for (Channel channel : user.getChannelsList()) {
            for (User u : channel.getMembersList()) {
                if (u.getId().equals(userID)) {
                    u.updateName(request.newUserName());
                    u.updateProfileImageID(savedUser.getProfileImageID());
                    channelIDs.add(channel.getId());
                }
            }
        }

        // channelRepository save()
        for (UUID channelID : channelIDs) {
            channelRepository.save(channelRepository.find(channelID));
        }

        // message의 sender 이름 변경
        Set<UUID> messageIDs = new HashSet<>();
        for (Message message : user.getMessageList()) {
            message.getSender().updateName(request.newUserName());
            message.getSender().updateProfileImageID(savedUser.getProfileImageID());
            messageIDs.add(message.getId());
        }

        // messageRepository save()
        for (UUID messageID : messageIDs) {
            messageRepository.save(messageRepository.find(messageID)
                    .orElseThrow(() -> new IllegalArgumentException("Message not found: " + messageID)));
        }

        return userMapper.toResponse(savedUser, userStatus);
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
            channel.removeMember(user);
            channelIDs.add(channel.getId());
        }

        // 변경사항 저장
        for (UUID channelID : channelIDs) {
            channelRepository.save(channelRepository.find(channelID));
        }

        // userStatusRepo에서 삭제
        UserStatus userStatus = userStatusRepository.findByUserID(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("UserStatus not found: " + user.getId()));
        userStatusRepository.deleteUserStatus(userStatus.getId());

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

    // Repo쪽으로 이관해야함.
    // User 이름 유효성 검증
    @Override
    public void validateName(String name){
        userRepository.findAll().stream()
                .filter(user -> user.getName().equals(name))
                .findFirst()
                .ifPresent(u -> {
                    throw new IllegalArgumentException("Already Present name: " + name);
                });
    }

    // 이메일 유효성 검증
    @Override
    public void validateEmail(String name){
        userRepository.findAll().stream()
                .filter(user -> user.getEmail().equals(name))
                .findFirst()
                .ifPresent(u -> {
                    throw new IllegalArgumentException("Already Present email: " + name);
                });
    }
}
