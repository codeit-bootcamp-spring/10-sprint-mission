package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    private final ReadStatusRepository readStatusRepository;
    private final UserStatusService userStatusService;

    @Override
    public UserResponseDto create(UserCreateDto dto) {
        // 유저 객체 생성
        User user = new User(dto.getName(),
                dto.getEmail(),
                dto.getPassword());

        // 유저 dto 내부 받아온 파일 dto
        MultipartFile file = dto.getProfileImg();
        // 프로필 등록 여부 & binaryContent객체 생성
        if(file != null){
            try{
                BinaryContent binaryContent =
                        new BinaryContent(user.getId(),
                                null,
                                file.getBytes(),
                                file.getOriginalFilename(),
                                file.getContentType());
                // binarycontent 저장
                binaryContentRepository.save(binaryContent);
                // 연관성 주입
                user.addProfileImage(binaryContent.getId());
            } catch (Exception e) {
                throw new RuntimeException("파일 업로드 오류가 발생했습니다",e);
            }
        }
        // 유저정보 저장
        userRepository.save(user);
        // 유저 상태 생성
        UserStatus userStatus = new UserStatus(user.getId());
        // 추후 메시지상태도 저장
        userStatusRepository.save(userStatus);

        return userMapper.toDto(user,true);
    }

    @Override
    public UserResponseDto findUser(UUID userId) {
        User user = getUser(userId);
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 사용자 상태가 없습니다."));

        // 추후 유저상태 서비스에서 가져오는 식으로 바꿔야할듯
        boolean online = false;
        if(userStatus != null){
            online = isOnline(userStatus.getLastOnlineAt());
        }
        System.out.println(user);
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
                    boolean online = userStatusRepository.findByUserId(user.getId())
                                            .map(UserStatus::getLastOnlineAt)
                                            .map(this::isOnline)
                                            .orElseThrow(() -> new NoSuchElementException("해당 유저상태는 없습니다."));
                    return userMapper.toDto(user, online);
                })
                .toList();

        System.out.println("[유저 전체 조회]");

        return dtoList;
    }

    @Override
    public UserResponseDto addFriend(UUID senderId, UUID receiverId) {
        User sender = getUser(senderId);
        User receiver = getUser(receiverId);

        sender.addFriend(receiverId);
        receiver.addFriend(senderId);
        userRepository.save(sender);
        userRepository.save(receiver);

        return findUser(senderId);
    }

    @Override
    public List<UserResponseDto> findFriends(UUID userId) {
        User user = getUser(userId);

        List<UserResponseDto> friendList = user.getFriendsList().stream()
                .map(this::findUser)
                .filter(Objects::nonNull).toList();

        System.out.println("[친구 목록 조회]");
        friendList.forEach(System.out::println);
        return friendList;
    }

    @Override
    public UserResponseDto update(UUID userId, UserUpdateDto dto) {
        User user = getUser(userId);
        // 이름 수정
        if(dto.getName() != null){
            user.updateName(dto.getName());
        }
        // 이메일 수정
        if(dto.getEmail() != null){
            user.updateEmail(dto.getEmail());
        }
        // 비밀번호 수정
        if(dto.getPassword() != null){
            user.updatePassword(dto.getPassword());
        }
        // 프로필 수정(기존에 있던 binaryContent를 삭제하고 업데이트 dto에 있는 binaryContent를 생성
        if(dto.getProfileImg() != null){
            MultipartFile newProfileImg = dto.getProfileImg();

            UUID oldProfileImageId = user.getProfileImageId();
            if(oldProfileImageId != null){
                binaryContentRepository.deleteByUserId(userId);
            }
            try{
                BinaryContent newBinaryContent = new BinaryContent(user.getId(),
                        null,
                        newProfileImg.getBytes(),
                        newProfileImg.getOriginalFilename(),
                        newProfileImg.getContentType());
                binaryContentRepository.save(newBinaryContent);

                user.updateProfileImg(newBinaryContent.getId());
            } catch (Exception e) {
                throw new RuntimeException("파일 업로드 오류가 발생했습니다",e);
            }
        }
        user.updateTimeStamp();
        userRepository.save(user);

        return findUser(userId);
    }


    public void updateOnlineStatus(UUID userId){
        User user = getUser(userId);
        userStatusService.update(userId);
    }

    @Override
    public void delete(UUID userId) {
        User user = getUser(userId);

        // 유저를 가지고 있는 readStatus를 통해서 채널 조회
        List<UUID> channelList = readStatusRepository.findAll().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .map(ReadStatus::getChannelId)
                .toList();
        // 채널에서 유저삭제
        channelList.stream()
                .map(channelId -> channelRepository.findById(channelId)
                        .orElseThrow(() -> new NoSuchElementException("해당 채널이 없습니다.")))
                .forEach(channel ->
                        {   channel.getUserList().remove(userId);
                            channelRepository.save(channel);
                        });

        // 유저가 쓴 메시지 삭제
        List<UUID> messageList = new ArrayList<>(user.getMessageList());
        for (UUID messageId : messageList){
            messageRepository.delete(messageId);
        }

        // 친구 목록에서 유저 지우기
        List<UUID> friendList = new ArrayList<>(user.getFriendsList());
        for (UUID friendId : friendList) {
            User friend = getUser(friendId);
            friend.getFriendsList().remove(userId); // 친구의 친구 목록에서 나 삭제
            userRepository.save(friend); // 변경된 친구저장
        }

        // 유저 상태 삭제
        userStatusRepository.deleteByUserId(user.getId());

        //유저의 binarycontent 삭제
        binaryContentRepository.deleteByUserId(userId);

        // 유저를 데이터에서 삭제
        userRepository.delete(userId);
    }

    // 추후 UserStatucService로 이전
    private boolean isOnline(Instant lastOnlineAt){
        // 만약 최종접속시간이 현재시간의 5분전 이내라면 참 반환
        return lastOnlineAt.isAfter(Instant.now().minus(Duration.ofMinutes(5)));
    }

    // 유효성 검사
    private User getUser(UUID userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 없습니다."));
    }
}
