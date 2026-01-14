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

    public JCFUserService() {
        this.data = new HashMap<>();
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
        duplicateEmail(email);
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
        ValidationMethods.validateId(userId);

        return Optional.ofNullable(data.get(userId));
    }

    // 특정 사용자가 참여한 채널 중에서 특정 채널 검색
    @Override
    public List<Channel> searchUserChannelByChannelName(UUID userId, String partialChannelName) {
        // User ID null 검증
        ValidationMethods.validateId(userId);
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
        ValidationMethods.validateId(userId);

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
        ValidationMethods.validateId(userId);

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
        ValidationMethods.validateId(userId);

        User user = data.get(userId);
        // 유저 없으면 빈 리스트 반환
        if (user == null) {
            return Collections.emptyList();
        }

        return user.getWriteMessageList().stream().toList();
    }

    // U. 수정
    // 이메일 수정
    @Override
    public User updateEmail(UUID userId, String email) {
        // email 검증
        ValidationMethods.validateString(email, "email");
        // 기존 email과 변경할 email이 동일하지 않을 때
        if (!email.equals(data.get(userId).getEmail())) {
            // 이메일 중복 확인
            duplicateEmail(email);
        }
        // 로그인 되어있는 user ID null / user 객체 존재 확인
        User user = validateMethods(userId);

        user.updateEmail(email);
        return user;
    }

    // 비밀번호 수정
    @Override
    public User updatePassword(UUID userId, String password) {
        // password 검증
        ValidationMethods.validateString(password, "password");
        // 로그인 되어있는 user ID null / user 객체 존재 확인
        User user = validateMethods(userId);

        user.updatePassword(password);
        return user;
    }

    // 별명 수정
    @Override
    public User updateNickName(UUID userId, String nickName) {
        // 로그인 되어있는 user ID null / user 객체 존재 확인
        User user = validateMethods(userId);

        user.updateNickName(nickName);
        return user;
    }

    // 사용자 이름 수정
    @Override
    public User updateUserName(UUID userId, String userName) {
        // userName 검증
        ValidationMethods.validateString(userName, "userName");
        // 로그인 되어있는 user ID null / user 객체 존재 확인
        User user = validateMethods(userId);

        user.updateUserName(userName);
        return user;
    }

    // 생년월일 수정
    @Override
    public User updateBirthday(UUID userId, String birthday) {
        // 로그인 되어있는 user ID null / user 객체 존재 확인
        User user = validateMethods(userId);

        user.updateBirthday(birthday);
        return user;
    }

    // 채널 참여
    @Override
    public User joinChannel(UUID userId, Channel channel) {
        // 로그인 되어있는 user ID null / user 객체 존재 확인
        User user = validateMethods(userId);
        // channel null 검증
        ValidationMethods.validateObject(channel, "channel");
        // 이미 참여한 채널인지 검증
        validateAlreadyParticipation(userId, channel);

        user.joinChannel(channel);
        return user;
    }

    // 채널 탈퇴
    @Override
    public User leaveChannel(UUID userId, Channel channel) {
        // 로그인 되어있는 user ID null / user 객체 존재 확인
        User user = validateMethods(userId);
        // channel null 검증
        ValidationMethods.validateObject(channel, "channel");

        user.leaveChannel(channel);
        return user;
    }

    // D. 삭제
    @Override
    public void deleteUser(UUID userId) {
        // 로그인 되어있는 user ID null / user 객체 존재 확인
        User user = validateMethods(userId);

//        // 연관 관계 정리
//        // 해당 유저가 작성한(author) 메시지 모두 삭제
//        user.getWriteMessageList()
//                .forEach(message ->
//                        messageService.deleteMessage(message.getAuthor().getId(), message.getId()));
//        // 해당 유저가 owner인 모든 채널 삭제하기 + 채널 관련 삭제
//        user.getOwnerChannelList()
//                .forEach(channel ->
//                        channelService.deleteChannel(user.getId(), channel.getId()));

        // 참여 채널에 존재하는 해당 유저 흔적 지우기
        user.getJoinChannelList().forEach(channel -> channel.removeChannelUser(user));
        // owner인 채널에 존재하는 해당 유저 흔적 지우기

        data.remove(userId);
    }

    // validation
    // 로그인 되어있는 user ID null / user 객체 존재 확인
    public User validateMethods(UUID userId) {
        // 로그인 정보를 가져온다고 가정하면 `requestUserId`와 `targetUserId`를 나눌 필요는 없음
        // 로그인 되어있는 user ID null 검증
        ValidationMethods.validateId(userId);

        User user = data.get(userId);
        existUser(user);

        return user;
    }

    // email `null` 또는 `blank` 확인, 중복 확인
    private void duplicateEmail(String email) {
        this.data.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findAny()
                .ifPresent(user -> {
                    throw new IllegalStateException("동일한 email이 존재합니다");
                });
    }

    // 이미 참여한 채널인지 검증
    private void validateAlreadyParticipation(UUID userId, Channel channel) {
        Boolean validateAlreadyParticipation = channel.getChannelUsersList().stream()
                .noneMatch(user -> user.getId().equals(userId));
        // false -> 중복 존재 O
        if (!validateAlreadyParticipation) {
            throw new IllegalStateException("이미 참여한 channel입니다.");
        }
    }

    // 존재하는 유저인지 확인
    public void existUser(User user) {
        if (user == null) {
            throw new NoSuchElementException("해당 사용자가 없습니다.");
        }
    }
}
