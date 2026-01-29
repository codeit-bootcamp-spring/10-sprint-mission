package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
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
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public UserResponseDto create(UserCreateDto userCreateDto) {
        // 유저 객체 생성
        User user = new User(userCreateDto.getName(),
                userCreateDto.getEmail(),
                userCreateDto.getPassword());

        // 유저 dto 내부 받아온 파일 dto
        BinaryContentDto binaryContentDto = userCreateDto.getProfileImg();
        // 프로필 등록 여부 & binaryContent객체 생성
        if(userCreateDto.getProfileImg() != null){
            BinaryContent binaryContent =
                    new BinaryContent(user.getId(),
                            null,
                            binaryContentDto.getFileData(),
                            binaryContentDto.getName(),
                            binaryContentDto.getFileType());
            // binarycontent 저장
            binaryContentRepository.save(binaryContent.getId(),binaryContent);
            // 연관성 주입
            user.addProfileImage(binaryContent.getId());
        }

        // 유저정보 저장
        userRepository.save(user.getId(),user);
        // 유저 상태 생성
        UserStatus userStatus = new UserStatus(user.getId());
        // 추후 메시지상태도 저장
        userStatusRepository.save(userStatus.getId(), userStatus);

        return userMapper.toDto(user,true);
    }

    @Override
    public UserResponseDto findUser(UUID userId) {
        User user = checkNull(userId);
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException());

        // 추후 유저상태 서비스에서 가져오는 식으로 바꿔야할듯
        boolean online = false;
        if(userStatus != null){
            online = isOnline(userStatus.getLastOnlineAt());
        }

        return userMapper.toDto(user,online);
    }

    @Override
    public List<UserResponseDto> findAllUsers() {
        List<User> userList = new ArrayList<>(userRepository.findAll());
        // 유저 상태들을 유저 아이디를 키로한 맵으로 가져옴
        Map<UUID, UserStatus> userStatusMap = userStatusRepository.findAll();
        // 가져온 객체들을 dto로 변환
        List<UserResponseDto> dtoList = userList.stream()
                .map(user -> {
                    boolean online = isOnline(userStatusMap.get(user.getId()).getLastOnlineAt());
                    return userMapper.toDto(user, online);
                })
                .toList();

        System.out.println("[유저 전체 조회]");
        dtoList.forEach(System.out::println);

        return dtoList;
    }

    @Override
    public UserResponseDto addFriend(UUID senderId, UUID receiverId) {
        User sender = checkNull(senderId);
        User receiver = checkNull(receiverId);

        sender.addFriend(receiverId);
        receiver.addFriend(senderId);
        userRepository.save(senderId,sender);
        userRepository.save(receiverId,receiver);

        return findUser(senderId);
    }

    @Override
    public List<UserResponseDto> findFriends(UUID userId) {
        User user = checkNull(userId);

        List<UserResponseDto> friendList = user.getFriendsList().stream()
                .map(this::findUser)
                .filter(Objects::nonNull).toList();

        System.out.println("[친구 목록 조회]");
        friendList.forEach(System.out::println);
        return friendList;
    }

    @Override
    public UserResponseDto update(UUID userId, UserUpdateDto userUpdateDto) {
        User user = checkNull(userId);


        if(userUpdateDto.getName() != null){
            user.updateName(userUpdateDto.getName());
        }
        if(userUpdateDto.getEmail() != null){
            user.updateEmail(userUpdateDto.getEmail());
        }
        if(userUpdateDto.getPassword() != null){
            user.updatePassword(userUpdateDto.getPassword());
        }
        if(userUpdateDto.getBinaryContentDto() != null){
            BinaryContentDto newBinaryContentDto = userUpdateDto.getBinaryContentDto();

            UUID oldProfileImageId = user.getProfileImageId();
            if(oldProfileImageId != null){
                binaryContentRepository.delete(oldProfileImageId);
            }

            BinaryContent newBinaryContent = new BinaryContent(user.getId(),
                                null,
                                    newBinaryContentDto.getFileData(),
                                    newBinaryContentDto.getName(),
                                    newBinaryContentDto.getFileType());
            binaryContentRepository.save(newBinaryContent.getId(),newBinaryContent);

            user.updateProfileImg(newBinaryContent.getId());
        }
        user.updateTimeStamp();
        userRepository.save(userId,user);

        return findUser(userId);
    }

    @Override
    public void delete(UUID userId) {
        User user = checkNull(userId);

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
            User friend = checkNull(friendId);
            friend.getFriendsList().remove(userId); // 친구의 친구 목록에서 나 삭제
            userRepository.save(friendId,friend); // 변경된 친구저장
        }

        // 유저 상태 삭제
        userStatusRepository.deleteByUserId(user.getId());

        //유저의 binarycontent 삭제
        binaryContentRepository.delete(user.getProfileImageId());

        // 유저를 데이터에서 삭제
        userRepository.delete(userId);
    }

    // 추후 UserStatucService로 이전
    private boolean isOnline(Instant lastOnlineAt){
        // 만약 최종접속시간이 현재시간의 5분전 이내라면 참 반환
        return lastOnlineAt.isAfter(Instant.now().minus(Duration.ofMinutes(5)));
    }

    private User checkNull(UUID userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 없습니다."));
    }
}
