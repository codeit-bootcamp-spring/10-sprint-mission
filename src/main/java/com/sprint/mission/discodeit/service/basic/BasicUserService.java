package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserMapper userMapper;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserResponseDto create(UserCreateDto userCreateDto) {
        // 유저 객체 생성
        User user = new User(userCreateDto.getName(),
                userCreateDto.getEmail(),
                userCreateDto.getPassword());

        // 프로필 이미지 추가 선택
        if(userCreateDto.getProfileImageId() != null){
            user.addProfileImage(userCreateDto.getProfileImageId());
        }
        // 저장
        save(user);
        // 저장후 유저 상태 생성
        UserStatus userStatus = new UserStatus(user.getId());
        // 추후 메시지상태도 저장

        return new UserResponseDto(user.getId()
                ,user.getName()
                ,user.getEmail()
                ,user.getProfileImageId()
                ,user.getMessageList()
                ,user.getChannelList()
                ,user.getFriendsList()
                ,true);
    }

    @Override
    public UserResponseDto findUser(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NoSuchElementException("해당 사용자가 없습니다."));
         UserStatus userStatus = userStatusRepository.findById(userId);

         // 추후 유저상태 서비스에서 가져오는 식으로 바꿔야할듯
        boolean online = false;
        if(userStatus != null){
            online = isOnline(userStatus.getLastOnlineAt());
        }

        return new UserResponseDto(user.getId()
                ,user.getName()
                ,user.getEmail()
                ,user.getProfileImageId()
                ,user.getMessageList()
                ,user.getChannelList()
                ,user.getFriendsList(),
                online);
    }

    @Override
    public List<UserResponseDto> findAllUsers() {
        List<User> userList = new ArrayList<>(userRepository.findAll());

        // 가져온 객체들을 dto로 변환
        List<UserResponseDto> dtoList = userList.stream()
                .map(userMapper::toDto)
                .toList();

        System.out.println("[유저 전체 조회]");
        dtoList.forEach(System.out::println);

        return dtoList;
    }

    @Override
    public UserResponseDto addFriend(UUID senderId, UUID receiverId) {
        User sender = userMapper.toEntity(findUser(senderId));
        User receiver = userMapper.toEntity(findUser(receiverId));

        sender.addFriend(receiverId);
        receiver.addFriend(senderId);
        save(sender);
        save(receiver);

        return findUser(senderId);
    }

    @Override
    public List<UserResponseDto> findFriends(UUID userId) {
        User user = userMapper.toEntity(findUser(userId));

        List<UserResponseDto> friendList = user.getFriendsList().stream()
                .map(id -> findUser(id))
                .filter(Objects::nonNull).toList();

        System.out.println("[친구 목록 조회]");
        friendList.forEach(System.out::println);
        return friendList;
    }

    @Override
    public UserResponseDto update(UUID userId, String newName,String email, String password,UUID profileImageId) {
        User user = userMapper.toEntity(findUser(userId));

        user.updateUser(newName,email,password,profileImageId);
        save(user);

        return findUser(userId);
    }

    @Override
    public void delete(UUID userId) {
        User user = userMapper.toEntity(findUser(userId));

        // 유저가 속한 채널에서 유저 id지우기
        List<UUID> channelList = new ArrayList<>(user.getChannelList());
        for (UUID channelId : channelList) {
            Channel channel = channelRepository.findById(channelId);
            if (channel != null) {
                channel.getUserList().remove(userId);
                channelRepository.save(channelId, channel);
            }
        }

        // 유저가 쓴 메시지 삭제
        List<UUID> messageList = new ArrayList<>(user.getMessageList());
        for (UUID messageId : messageList){
            messageRepository.delete(messageId);
        }

        // 친구 목록에서 유저 지우기
        List<UUID> friendList = new ArrayList<>(user.getFriendsList());
        for (UUID friendId : friendList) {
            User friend = userMapper.toEntity(findUser(friendId));
            friend.getFriendsList().remove(userId); // 친구의 친구 목록에서 나 삭제
            save(friend); // 변경된 친구저장
        }

        // 유저를 데이터에서 삭제
        userRepository.delete(userId);
    }

    @Override
    public void save(User user) {
        userRepository.save(user.getId(),user);
    }

    private boolean isOnline(Instant lastOnlineAt){
        // 만약 최종접속시간이 현재시간의 5분전 이내라면 참 반환
        return lastOnlineAt.isAfter(Instant.now().minus(Duration.ofMinutes(5)));
    }
}
