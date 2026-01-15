package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
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
    public User createUser(String email, String userName, String nickName, String password, String birthday) {
        // email `null` or `blank` 검증
        ValidationMethods.validateNullBlankString(email, "email");
        // email 중복 확인
        validateDuplicateEmail(email);
        // userName, userName, password, birthday `null` or `blank` 검증
        ValidationMethods.validateNullBlankString(userName, "userName");
        ValidationMethods.validateNullBlankString(nickName, "nickName");
        ValidationMethods.validateNullBlankString(password, "password");
        ValidationMethods.validateNullBlankString(birthday, "birthday");

        User user = new User(email, userName, nickName, password, birthday);
        data.put(user.getId(), user);
        return user;
    }

    // R. 읽기
    @Override
    public Optional<User> findUserById(UUID userId) {
        // User ID null 검증
        ValidationMethods.validateId(userId);

        return Optional.ofNullable(data.get(userId));
    }

    // R. 모두 읽기
    // 모든 사용자
    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(data.values());
    }

    // 특정 사용자가 참여한 모든 채널
    public List<UUID> readJoinChannelIds(UUID userId) {
    // 로그인 되어있는 user ID null / user 객체 존재 확인
        User user = validateAndGetUserByUserId(userId);

        return user.getJoinChannelIds().stream().toList();
    }

    // 특정 사용자가 작성한 모든 메시지
    @Override
    public List<Message> readUserMessagesByUserId(UUID userId) {
        // 로그인 되어있는 user ID null / user 객체 존재 확인
        User user = validateAndGetUserByUserId(userId);

        return user.getWriteMessageList().stream().toList();
    }

    // U. 수정
    // 로그인 정보를 가져온다고 가정하면 `requestUserId`와 `targetUserId`로 나눌 필요는 없음
    // email, password, userName, nickName, birthday 수정
    public User updateUserInfo(UUID userId, String email, String password, String userName, String nickName, String birthday) {
        // 로그인 되어있는 user ID null / user 객체 존재 확인
        User user = validateAndGetUserByUserId(userId);
        // email or pawword or userName 등이 "전부" 입력되지 않았거나 "전부" 이전과 동일하다면 exception 발생시킴
        if ((email == null || user.getEmail().equals(email))
                && (password == null || user.getPassword().equals(password))
                && (nickName == null || user.getNickName().equals(nickName))
                && (userName == null || user.getUserName().equals(userName))
                && (birthday == null || user.getBirthday().equals(birthday))) {
            throw new IllegalArgumentException("변경사항이 없습니다. 입력 값을 다시 확인하세요.");
        }

        // filter로 중복 확인 후 업데이트(중복 확인 안하면 동일한 값을 또 업데이트함)
        Optional.ofNullable(email)
                .filter(e -> !user.getEmail().equals(e)) // !false(중복 아닌 값) -> true
                .ifPresent(e -> user.updateEmail(e));
        Optional.ofNullable(password)
                .filter(p -> !user.getPassword().equals(p))
                .ifPresent(p -> user.updatePassword(p));
        Optional.ofNullable(nickName)
                .filter(n -> !user.getNickName().equals(n))
                .ifPresent(n -> user.updateNickName(n));
        Optional.ofNullable(userName)
                .filter(n -> !user.getUserName().equals(n))
                .ifPresent(n -> user.updateUserName(n));
        Optional.ofNullable(birthday)
                .filter(b -> !user.getBirthday().equals(b))
                .ifPresent(b -> user.updateBirthday(b));

        return user;
    }

    // D. 삭제
    @Override
    public void deleteUser(UUID userId) {
        if (data.remove(userId) == null) {
            throw new NoSuchElementException("존재하지 않는 사용자입니다.");
        }
    }
//        // 연관 관계 정리
//        // 해당 유저가 작성한(author) 메시지 모두 삭제
//        user.getWriteMessageList()
//                .forEach(message ->
//                        messageService.deleteMessage(message.getAuthor().getId(), message.getId()));
//        // 해당 유저가 owner인 모든 채널 삭제하기 + 채널 관련 삭제
//        user.getOwnerChannelList()
//                .forEach(channel ->
//                        channelService.deleteChannel(user.getId(), channel.getId()));
//    // 참여 채널에 존재하는 해당 유저 흔적 지우기
//        user.getJoinChannelList().forEach(channel -> channel.removeChannelUser(user));
//    // owner인 채널에 존재하는 해당 유저 흔적 지우기

    //// validation
    // 로그인 되어있는 user ID null / user 객체 존재 확인
    public User validateAndGetUserByUserId(UUID userId) {
        return findUserById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 없습니다."));
    }

    // email 중복 확인
    private void validateDuplicateEmail(String email) {
        if (this.data.values().stream()
                .anyMatch(user -> user.getEmail().equals(email))) {
            throw new IllegalArgumentException("동일한 email이 존재합니다");
        }

    }
}
