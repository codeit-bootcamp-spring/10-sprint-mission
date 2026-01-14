package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.validation.ValidationMethods;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;
    private final ChannelService channelService;
    private final MessageService messageService;

    public JCFUserService(Map<UUID, User> data, ChannelService channelService, MessageService messageService) {
        this.data = data;
        this.channelService = channelService;
        this.messageService = messageService;
    }

    @Override
    public String toString() {
        return "JCFUserService{" +
//                "data = " + data + ", " +
                "data key = " + data.keySet() + ", " +
                "data values = " + data.values() + ", " +
                "data size = " + data.size() +
                '}';
    }

    // C. 생성: User 생성 후 User 객체 반환
    @Override
    public User createUser(String email, String nickName, String userName, String password, String birthday) {
        // email 검증
        ValidationMethods.validateString(email, "email");
        // 이메일 중복 확인
        ValidationMethods.duplicateEmail(data, email);
        // userName, password 검증
        ValidationMethods.validateString(userName, "userName");
        ValidationMethods.validateString(password, "password");

        User user = new User(email, nickName, userName, password, birthday);
        data.put(user.getId(), user);
        return user;
    }

    // R. 읽기
    // 이메일+비번(로그인?)
    @Override
    public Optional<User> readUserByEmailAndPw(String email, String password) {
        // email, password 검증
        ValidationMethods.validateString(email, "email");
        ValidationMethods.validateString(password, "password");

        return data.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .filter(user -> user.getPassword().equals(password))
                .findAny();
    }

    // 본인?
    @Override
    public Optional<User> readUserById(UUID userId) {
        // User ID null 검증
        ValidationMethods.validateUserId(userId);

        return Optional.ofNullable(data.get(userId));
    }

    // 특정 사용자가 참여한 채널 중에서 특정 채널 검색
    @Override
    public List<Channel> searchUserChannelByChannelName(UUID userId, String partialChannelName) {
        // User ID null 검증
        ValidationMethods.validateChannelId(userId);
        // partialChannelName 검증
        ValidationMethods.validateString(partialChannelName, "partialChannelName");

        User user = data.get(userId);
        if (user == null) {
            return Collections.emptyList();
        }

        return user.getJoinChannelList().stream()
                .filter(channel -> channel.getChannelName().contains(partialChannelName))
                .toList();
    }

    // R. 모두 읽기
    // 모든 사용자
    @Override
    public List<User> readAllUsers() {
        return new ArrayList<>(data.values());
    }

    // 전체 검색으로 특정 사용자 찾기
    @Override
    public List<User> searchAllUsersByPartialName(String partialName) {
        // partialName 검증
        ValidationMethods.validateString(partialName, "partialName");

        return data.values().stream()
                .filter(user -> user.getUserName().contains(partialName) ||
                        user.getNickName().contains(partialName))
                .toList();
    }

    // 특정 사용자가 참여한 모든 채널
    public List<Channel> readUserJoinChannelsByUserId(UUID userId) {
        // User ID null 검증
        ValidationMethods.validateUserId(userId);

        User user = data.get(userId);
        // 유저 없으면 빈 리스트 반환
        if (user == null) {
            return Collections.emptyList();
        }

        return user.getJoinChannelList().stream().toList();
    }

    // 특정 사용자가 owner인 모든 채널
    @Override
    public List<Channel> readUserOwnChannelsByUserId(UUID userId) {
        // User ID null 검증
        ValidationMethods.validateUserId(userId);

        User user = data.get(userId);
        // 유저 없으면 빈 리스트 반환
        if (user == null) {
            return Collections.emptyList();
        }

        return user.getOwnerChannelList().stream().toList();
    }

    // 특정 사용자가 작성한 모든 메시지
    @Override
    public List<Message> readUserMessagesByUserId(UUID userId) {
        // User ID null 검증
        ValidationMethods.validateUserId(userId);

        User user = data.get(userId);
        // 유저 없으면 빈 리스트 반환
        if (user == null) {
            return Collections.emptyList();
        }

        return user.getWriteMessageList().stream().toList();
    }

    // U. 수정
    // ID null 검증 / req ID와 target ID의 동일한지 확인 / user 객체 존재 확인
    public static User validateMethods(Map<UUID, User> data, UUID requestId, UUID targetId) {
        // request, target ID null 검증
        ValidationMethods.validateUserId(requestId);
        ValidationMethods.validateUserId(targetId);
        // requestId와 targetId가 동일한지 검증
        ValidationMethods.validateSameId(requestId, targetId);

        User user = data.get(targetId);
        ValidationMethods.existUser(user);

        return user;
    }
    // 이메일 수정
    @Override
    public User updateEmail(UUID requestId, UUID targetId, String email) {
        // email 검증
        ValidationMethods.validateString(email, "email");
        // 이메일 중복 확인
        ValidationMethods.duplicateEmail(data, email);
        // ID null 검증 / req ID와 target ID의 동일한지 확인 / user 객체 존재 확인
        User user = validateMethods(data, requestId, targetId);

        user.updateEmail(email);
        return user;
    }

    // 비밀번호 수정
    @Override
    public User updatePassword(UUID requestId, UUID targetId, String password) {
        // password 검증
        ValidationMethods.validateString(password, "password");
        // ID null 검증 / req ID와 target ID의 동일한지 확인 / user 객체 존재 확인
        User user = validateMethods(data, requestId, targetId);

        user.updatePassword(password);
        return user;
    }

    // 별명 수정
    @Override
    public User updateNickName(UUID requestId, UUID targetId, String nickName) {
        // ID null 검증 / req ID와 target ID의 동일한지 확인 / user 객체 존재 확인
        User user = validateMethods(data, requestId, targetId);

        user.updateNickName(nickName);
        return user;
    }

    // 사용자 이름 수정
    @Override
    public User updateUserName(UUID requestId, UUID targetId, String userName) {
        // userName 검증
        ValidationMethods.validateString(userName, "userName");

        // ID null 검증 / req ID와 target ID의 동일한지 확인 / user 객체 존재 확인
        User user = validateMethods(data, requestId, targetId);

        user.updateUserName(userName);
        return user;
    }

    // 생년월일 수정
    @Override
    public User updateBirthday(UUID requestId, UUID targetId, String birthday) {
        // ID null 검증 / req ID와 target ID의 동일한지 확인 / user 객체 존재 확인
        User user = validateMethods(data, requestId, targetId);

        user.updateBirthday(birthday);
        return user;
    }

    // 채널 참여
    @Override
    public User joinChannel(UUID requestId, UUID targetId, Channel channel) {
        // ID null 검증 / req ID와 target ID의 동일한지 확인 / user 객체 존재 확인
        User user = validateMethods(data, requestId, targetId);
        // channel null 검증
        ValidationMethods.validateObject(channel, "channel");
        // 이미 참여한 채널인지 검증
        ValidationMethods.validateAlreadyParticipation(targetId, channel);

        user.joinChannel(channel);
        return user;
    }

    // 채널 탈퇴
    @Override
    public User leaveChannel(UUID requestId, UUID targetId, Channel channel) {
        // ID null 검증 / req ID와 target ID의 동일한지 확인 / user 객체 존재 확인
        User user = validateMethods(data, requestId, targetId);
        // channel null 검증
        ValidationMethods.validateObject(channel, "channel");

        user.leaveChannel(channel);
        return user;
    }

    // D. 삭제
    @Override
    public void deleteUser(UUID requestId, UUID targetId) {
        // ID null 검증 / req ID와 target ID의 동일한지 확인 / user 객체 존재 확인
        User user = validateMethods(data, requestId, targetId);

        // 연관 관계 정리
        // 해당 유저가 작성한(author) 메시지 모두 삭제
        user.getWriteMessageList()
                .forEach(message ->
                        messageService.deleteMessage(message.getAuthor().getId(), message.getId()));
        // 해당 유저가 owner인 모든 채널 삭제하기 + 채널 관련 삭제
        user.getOwnerChannelList()
                .forEach(channel ->
                        channelService.deleteChannel(user.getId(), channel.getId()));

        // 참여 채널에 존재하는 해당 유저 흔적 지우기
        user.getJoinChannelList().forEach(channel -> channel.removeChannelMembers(user));
        // owner인 채널에 존재하는 해당 유저 흔적 지우기

        data.remove(targetId);
    }
}
