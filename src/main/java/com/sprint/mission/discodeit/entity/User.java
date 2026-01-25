package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User extends BaseEntity {
    private static final long serialVersionUID = 1L;

    // 개인정보보호를 생각하여서 이름 -> 닉네임으로 변경
    // 또한 업데이트 로직 적용 대상을 닉네임으로 적용하고 싶어서 변경
    private String userNickname;

    // 이메일 같은 경우는 UUID 처럼 개인의 "아이디" 라고 생각하면 됨
    // 업데이트 시 수정 불가를 위하여 final로 선언
    private final String userEmail;

    private final List<Channel> myChannels = new ArrayList<>();
    private final List<Message> myMessages = new ArrayList<>();

    public User(String userNickname, String userEmail) {
        validateUser(userNickname, userEmail);
        this.userNickname = userNickname;
        this.userEmail = userEmail;
    }

    public void updateNickname(String userNickname) {
        validateUser(userNickname, this.userEmail);
        this.userNickname = userNickname;
        super.update();
    }

    // 유저의 채널 목록에 새 채널을 추가
    public void addMyChannel(Channel channel) {
        if (channel != null && !this.myChannels.contains(channel)) this.myChannels.add(channel);
    }

    // 유저의 채널 목록에서 특정 채널을 삭제
    public void removeMyChannel(Channel channel) {
        this.myChannels.remove(channel);
    }

    // 유저가 작성한 메세지를 리스트에 추가
    public void addMyMessages(Message message) {
        if (message != null) this.myMessages.add(message);
    }

    public void removeMyMessages(Message message) {
        if (message != null) this.myMessages.remove(message);
    }

    // 유저 생성 및 수정 시 준수해야 할 비즈니스 정책 (Fail-Fast)
    // 나중에 필드가 늘어난다면 헬퍼 메서드나 정규 표현식으로 변환 예정
    private void validateUser(String userNickname, String userEmail) {

        // null, Blank 체크
        if (userNickname == null || userNickname.isBlank())
            throw new IllegalArgumentException("닉네임은 필수이며, 비어있을 수 없습니다.");
        if (userEmail == null || userEmail.isBlank()) throw new IllegalArgumentException("이메일은 필수이며, 비어있을 수 없습니다.");

        // 공백(" ") 체크
        if (userNickname.contains(" ")) throw new IllegalArgumentException("닉네임은 공백을 포함할 수 없습니다.");
        if (userEmail.contains(" ")) throw new IllegalArgumentException("이메일은 공백을 포함할 수 없습니다.");

        // 유저 이름 길이 체크 (2자 이상, 10자 이하)
        if (userNickname.length() < 2 || userNickname.length() > 10)
            throw new IllegalArgumentException("닉네임은 2자 이상, 10자 이하로 설정하세요.");
    }

    @Override
    public String toString() {
        return String.format("User[닉네임: %s, 이메일: %s, User ID: %s]", userNickname, userEmail, getId());
    }

    public String getUserNickname() {
        return userNickname;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public List<Message> getMyMessages() {
        return new ArrayList<>(myMessages);
    }

    public List<Channel> getMyChannels() {
        return new ArrayList<>(myChannels);
    }
}