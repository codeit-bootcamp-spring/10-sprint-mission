package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserInfoDto;
import com.sprint.mission.discodeit.dto.UserCreateDto;
import com.sprint.mission.discodeit.dto.UserUpdateDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.ClearMemory;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService, ClearMemory {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserMapper userMapper;

    @Override
    public User create(UserCreateDto request) {
        userRepository.findByName(request.userName())
                .ifPresent(u -> {
                    throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
                });
        userRepository.findByEmail(request.email())
                .ifPresent(u -> {
                    throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
                });
        User user;
        if (request.imageBytes() != null) {  // 프로필 있을 때
            BinaryContent profileImg = new BinaryContent(request.imageBytes()); // 프로필 이미지 생성
            binaryContentRepository.save(profileImg);
            user = new User(request.userName(), request.email(), request.password(), profileImg.getId());
        } else {
            user = new User(request.userName(), request.email(), request.password(), null);
        }
        UserStatus userStatus = new UserStatus(user);
        userStatusRepository.save(userStatus);
        return userRepository.save(user);
    }

    @Override
    public UserInfoDto findById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(()
                -> new NoSuchElementException("실패 : 존재하지 않는 사용자 ID입니다."));
        UserStatus userStatus = userStatusRepository.findByUserId(user.getId()).orElse(null);
        return userMapper.userToUserInfoDto(user, userStatus);
    }

    @Override
    public List<UserInfoDto> findAll() {
        List<User> users = userRepository.readAll();
        List<UserStatus> userStatuses = userStatusRepository.readAll();
        Map<UUID, UserStatus> statusMap
                = userStatuses.stream().collect(Collectors.toMap(UserStatus::getUserId, us -> us));
        List<UserInfoDto> infoList
                = users.stream()
                .map(u -> {
                    UserStatus userStatus = statusMap.get(u.getId());
                    StatusType status = (userStatus == null) ? StatusType.OFFLINE : userStatus.getStatusType();
                    return new UserInfoDto(u.getName(), u.getId(), status, u.getEmail(), u.getProfileId());
                })
                .toList();
        return infoList;
    }

    @Override
    public User update(UserUpdateDto request) {
        User user = userRepository.findById(request.userId()).orElseThrow();
        user.updateName(request.newName()); // 이름 변경

        if (request.imageBytes() != null) {  // 프로필 변경
            if(user.getProfileId() != null) {
                binaryContentRepository.delete(user.getProfileId());    // 기존 프로필 삭제
            }
            BinaryContent newProfileImg = new BinaryContent(request.imageBytes());
            binaryContentRepository.save(newProfileImg);
            user.updateProfileId(newProfileImg.getId());
        }
        updateLastActiveTime(request.userId());   // 마지막 접속 시간 갱신
        return userRepository.save(user);
    }

    @Override
    public List<Message> getUserMessages(UUID id) {
        findById(id);
        return messageRepository.findAllByUserId(id).stream()
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .toList();
    }

    @Override
    public List<Channel> getUserChannels(UUID id) {
        findById(id);
        return channelRepository.findAll().stream()
                .filter(ch -> ch.getUserIds().stream().anyMatch(uId -> uId.equals(id)))
                .toList();
    }

    @Override
    public void delete(UUID id) {
        UserInfoDto userInfoDto = findById(id);

        // 프로필 삭제
        if(userInfoDto.profileId() != null) {
            binaryContentRepository.delete(userInfoDto.profileId());
        }

        // UserStatus 삭제
        userStatusRepository.deleteByUserId(userInfoDto.userId());

        // 사용자가 등록되어 있는 채널들
        List<Channel> joinedChannels = channelRepository.findAll().stream()
                .filter(ch -> ch.getUserIds().stream()
                        .anyMatch(uId -> uId.equals(id)))
                .toList();

        for (Channel ch : joinedChannels) {
            // 내가 방장인 채널 - 채널 자체 삭제
            if (ch.getOwnerId().equals(id)) {
                channelRepository.delete(ch.getId());
            }
            // 참여한 채널 - 멤버 명단에서 나만 삭제
            else {
                ch.getUserIds().removeIf(uId -> uId.equals(id));
                channelRepository.save(ch);
            }
        }

        // 사용자가 작성한 메시지 삭제
        messageRepository.deleteByUserId(id);

        // 사용자의 ReadStatus 삭제
        readStatusRepository.deleteByUserId(id);

        userRepository.delete(id);
    }

    @Override
    public void clear() {
        userRepository.clear();
    }

    @Override
    public void updateLastActiveTime(UUID id) {
        Optional<UserStatus> userStatus = userStatusRepository.findByUserId(id);
        userStatus.ifPresent(us -> {
            us.updateLastActiveTime();
            userStatusRepository.save(us);
        });
    }


}
