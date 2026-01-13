package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.validation.ValidationMethods;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data = new HashMap<>();

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

    // R. 모두 읽기
    // 모든 사용자
    @Override
    public List<User> readAllUsers() {
        return new ArrayList<>(data.values());
    }

    // 전체 검색으로 특정 사용자 찾기
    @Override
    public List<User> searchAllUsersByPartialName(String partialName) {
        return data.values().stream()
                .filter(user -> user.getUserName().contains(partialName) ||
                        user.getNickName().contains(partialName))
                .toList();
    }

    // 특정 사용자가 참여한 모든 채널
    public List<Channel> readAllJoinChannelsAtUserByUserId(UUID userId) {
        // User ID null 검증
        ValidationMethods.validateUserId(userId);

        User user = data.get(userId);
        // 유저 없으면 빈 리스트 반환
        if (user == null) {
            return Collections.emptyList();
        }

        return user.getJoinChannelList().stream().toList();
    }

    // 특정 사용자가 작성한 모든 메시지


    // U. 수정
    // 이메일 수정
    @Override
    public Optional<User> updateEmail(UUID userId, String email) {
        // User ID null 검증
        ValidationMethods.validateUserId(userId);
        // email 검증
        ValidationMethods.validateString(email, "email");
        // 이메일 중복 확인
        ValidationMethods.duplicateEmail(data, email);

        return Optional.ofNullable(data.get(userId))
                        .map(user -> {
                            user.updateEmail(email);
                            return user;
                        });
    }

    // 비밀번호 수정
    @Override
    public Optional<User> updatePassword(UUID userId, String password) {
        // User ID null 검증
        ValidationMethods.validateUserId(userId);
        // password 검증
        ValidationMethods.validateString(password, "password");

        return Optional.ofNullable(data.get(userId))
                        .map(user -> {
                            user.updatePassword(password);
                            return user;
                        });
    }

    // 별명 수정
    @Override
    public Optional<User> updateNickName(UUID userId, String nickName) {
        // User ID null 검증
        ValidationMethods.validateUserId(userId);

        return Optional.ofNullable(data.get(userId))
                        .map(user -> {
                            user.updateNickName(nickName);
                            return user;
                        });
    }

    // 사용자 이름 수정
    @Override
    public Optional<User> updateUserName(UUID userId, String userName) {
        // User ID null 검증
        ValidationMethods.validateUserId(userId);
        // userName 검증
        ValidationMethods.validateString(userName, "userName");

        return Optional.ofNullable(data.get(userId))
                .map(user -> {
                    user.updateUserName(userName);
                    return user;
                });
    }

    // 생년월일 수정
    @Override
    public Optional<User> updateBirthday(UUID userId, String birthday) {
        // User ID null 검증
        ValidationMethods.validateUserId(userId);

        return Optional.ofNullable(data.get(userId))
                .map(user -> {
                    user.updateBirthday(birthday);
                    return user;
                });
    }

    // 채널 참여
    @Override
    public Optional<User> joinChannel(UUID userId, Channel channel) {
        // User ID null 검증
        ValidationMethods.validateUserId(userId);

        return Optional.ofNullable(data.get(userId))
                .map(user -> {
                    user.joinChannel(channel);
                    return user;
                });
    }

    // 채널 탈퇴
    @Override
    public Optional<User> leaveChannel(UUID userId, Channel channel) {
        // User ID null 검증
        ValidationMethods.validateUserId(userId);

        return Optional.ofNullable(data.get(userId))
                .map(user -> {
                    user.leaveChannel(channel);
                    return user;
                });
    }

    // 메시지 작성 - 수정 중...
    @Override
    public Optional<User> writeMessage(UUID userId, String messageContent, Channel channel) {
        // User ID null 검증
        ValidationMethods.validateUserId(userId);

        return Optional.empty();
    }

    // D. 삭제
    @Override
    public void deleteUser(UUID userId) {
        // User ID null 검증
        ValidationMethods.validateUserId(userId);

        data.remove(userId);
    }
}
