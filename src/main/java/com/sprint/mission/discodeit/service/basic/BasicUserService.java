package com.sprint.mission.discodeit.service.basic;
import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.utils.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    // User 저장 및 조회용
    private final UserRepository userRepository;
    // UserStatus 저장/조회 -> 현재 로그인중인지.
    private final UserStatusRepository userStatusRepository;
    // BinaryContent 저장/조회 (프로필 이미지..)
    private final BinaryContentRepository binaryContentRepository;

    // Mapper 클래스 따로
    private final UserMapper userMapper;



    // DTO로 입력받고, UserStatus 같이 생성.
    @Override
    public UserResponse createUser(UserCreateRequest request) {
        // 공백 검사 (수정)
        request.validate();

        // 중복 검사
        Validation.noDuplicate(
                userRepository.findAll(),
                u -> u.getAlias().equals(request.alias()),
                "이미 존재하는 별명입니다: " + request.alias()
        );
        Validation.noDuplicate(
                userRepository.findAll(),
                u -> u.getEmail().equals(request.email()),
                "이미 존재하는 이메일입니다: " + request.email()
        );

        //User 생성/저장
        User user = new User(
                request.userName(),
                request.alias(),
                request.email(),
                request.password()
        );

        // 프로필 이미지가 있으면 BinaryContent 저장 후 profileId 연결
        if (request.profileImage() != null) {
            BinaryContent binaryContent = userMapper.toBinaryContent(request.profileImage());
            binaryContentRepository.save(binaryContent);
            user.changeProfileId(binaryContent.getId());
        }
        userRepository.save(user);

        // UserStatus 생성/저장
        UserStatus userStatus = new UserStatus(user.getId());
        userStatusRepository.save(userStatus);

        // 응답 DTO로 반환 ->
        return userMapper.toResponse(user, userStatus.isOnline());
    }


    @Override
    public List<UserResponse> getUserAll() {
        return userRepository.findAll().stream()
                .map(user -> userMapper.toResponse(user, findOnlineByUserId(user.getId())))
                .toList();
    }

    @Override
    public User getUserByAlias(String alias) {
        return userRepository.findAll().stream()
                .filter(user -> user.getAlias().equals(alias))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 별명을 가진 유저가 없습니다: " + alias));
    }

    @Override
    public List<UserResponse> getUserByName(String userName) {
        //username으로 repo에서 찾은 후에
        List<User> matches = userRepository.findAll().stream()
                .filter(user -> user.getUserName().equals(userName))
                .toList();
        if (matches.isEmpty()) {
            throw new NoSuchElementException("해당 이름의 유저가 없습니다: " + userName);
        }

        // response DTO로 반환.
        return matches.stream()
                .map(user -> userMapper.toResponse(user, findOnlineByUserId(user.getId())))
                .toList();
    }

    @Override
    public UserResponse findUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException("해당 ID의 유저가 없습니다: " + id));

        boolean online = findOnlineByUserId(id);
        return userMapper.toResponse(user, online);
    }


    @Override
    public UserResponse updateUser(UserUpdateRequest request) {
        if (request.userId() == null) {
            throw new IllegalArgumentException("userId는 null일 수 없습니다.");
        }
        // 수정 대상 조회
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NoSuchElementException("수정할 유저가 없습니다."));

        // 이름 변경
        if (request.userName() != null) {
            Validation.notBlank(request.userName(), "이름");
            user.changeUserName(request.userName());
        }
        //별명 변경
        if (request.alias() != null) {
            Validation.notBlank(request.alias(), "별명");
            Validation.noDuplicate(
                    userRepository.findAll(),
                    u -> !u.getId().equals(user.getId()) && u.getAlias().equals(request.alias()),
                    "이미 존재하는 별명입니다: " + request.alias()
            );
            user.changeAlias(request.alias());
        }

        // 이메일 변경
        if (request.email() != null) {
            Validation.notBlank(request.email(), "이메일");
            Validation.noDuplicate(
                    userRepository.findAll(),
                    u -> !u.getId().equals(user.getId()) && u.getEmail().equals(request.email()),
                    "이미 존재하는 이메일입니다." + request.email()

            );
            user.changeEmail(request.email());
        }

        // 비밀번호 변경...?
        if (request.password() != null) {
            Validation.notBlank(request.password(), "비밀번호");
            user.changerPassword(request.password());
        }
        // 프로필 이미지 변경
        if(request.ProfileImage() != null){
            // 기존 이미지 삭제
            if(user.getProfileId() != null) {
                binaryContentRepository.delete(user.getProfileId());
            }
            BinaryContent newProfile = userMapper.toBinaryContent(request.ProfileImage());
            binaryContentRepository.save(newProfile);

            // 유저한테 새로운 profileID 반영
            user.changeProfileId(newProfile.getId());
        }
        userRepository.save(user);
        // 유저 정보 변경 후, 이 유저가 들어있는 채널들을 다시 저장해서 파일 반영
        for (Channel ch : channelRepository.findAll()) {
            boolean contains = ch.getParticipants().stream()
                    .anyMatch(u -> u.getId().equals(user.getId()));
            if (contains) {
                channelRepository.save(ch);
            }
        }

        boolean online = findOnlineByUserId(user.getId());
        return userMapper.toResponse(user, online);
    }



    @Override
    public void deleteUser(UUID userId){
        //  없는 ID면 예외
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("삭제할 유저가 없습니다: " + userId));
        // 참가한 채널 모두 퇴장 + 메모리 반영
        List<Channel> joined = new ArrayList<>(user.getJoinedChannels());
        for(Channel ch: joined) {
            user.leaveChannel(ch);
            channelRepository.save(ch);
        }
        userRepository.save(user);

        // 유저의 메세지 전부 삭제 (수정)
        List<Message> myMessages = messageRepository.findAll().stream()
                .filter(m -> m.getSenderId().equals(userId))
                .toList();

        for (Message m : myMessages) {
            UUID channelId = m.getChannelId();
            UUID messageId = m.getId();

            // 채널 쪽 messageIds에서도 제거(채널이 들고 있다면)
            Channel ch = channelRepository.findById(channelId)
                    .orElseThrow(() -> new NoSuchElementException("채널이 없습니다: " + channelId));
            ch.getMessageIds().remove(m.getId());
            channelRepository.save(ch);

            // 유저쪽도 messageIds 에서 삭제
            user.getMessageIds().remove(messageId);

            // 메시지 삭제
            messageRepository.delete(m.getId());
        }

        //프로필 이미지 있으면 삭제
        if(user.getProfileId() != null){
            binaryContentRepository.delete(user.getProfileId());
        }
        // userStatus도 같이 삭제.
        userStatusRepository.findByUserId(userId)
                .ifPresent(status ->
                        userStatusRepository.deleteById(status.getId()));

        // 최종 User 삭제
        userRepository.delete(userId);
    }


    // 메서드 모음

//    private BinaryContent toBinaryContent(BinaryContentCreateRequest request) {
//        return new BinaryContent(request.fileName(), request.contentType(), request.bytes());
//    }
//
//    // 생성, 수정 시 reponse DTO 반영하는
//    public UserResponse toResponse(User user, boolean online){
//        return new UserResponse(
//                user.getId(),
//                user.getUserName(),
//                user.getAlias(),
//                user.getEmail(),
//                online,
//                user.getProfileId()
//        );
//    }
    // use
    private boolean findOnlineByUserId(UUID userId) {
        return userStatusRepository.findByUserId(userId)
                .map(UserStatus::isOnline)
                .orElse(false);
    }

}
