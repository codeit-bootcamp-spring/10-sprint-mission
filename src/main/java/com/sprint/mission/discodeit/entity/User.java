package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class User extends BaseEntity {
    private static final String USERNAME_REGEX = "^[a-zA-Z0-9가-힣_-]+$";
    private static final Pattern USERNAME_PATTERN = Pattern.compile(USERNAME_REGEX);

    // id, createdAt, updatedAt 상속 받음
    private String username;     // 로그인에 쓰이는 고유 ID (중복 불가능), 시간당 2회 변경 제한
//  private String email;        // email 또는 phoneNumber로 가입 가능
//  private String displayName;  // 남들에게 보이는 이름 (중복 허용)
//  private String password;     // 암호화해서 저장 필요
//  private String dateOfBrith   // 생년월일 -> Date 클래스 사용이 더 좋을 듯

    // 1:N 관계 (User : ChannelUserRole)
    private List<ChannelUserRole> channelUserRoles = new ArrayList<>();

    // 유효성 검증 메서드 분리
    // JCFUserService.java에 해당 로직을 넣지 않는 이유는, 생성자 때에도 같은 로직을 사용해야하기 때문에
    private void validateUsername(String username) {
        // 규칙이 많고 updateUsername 에서도 로직 똑같이 사용해서 따로 빼야할 듯?
        // 애초에 entity가 아니라 service로 보내버려야 하나?

        // username 규칙 (1) - null 안됨. 빈 문자열("") 안됨
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("사용자 이름은 필수");
        }
        // username 규칙 (2) - 2자 이상 ~ 20자 이하
        if (username.length() < 2 || username.length() > 20) {
            throw new IllegalArgumentException("2자 이상 20자 이하");
        }
        // username 규칙 (3) - 영문, 한글, 숫자 (0-9), 밑줄(_), 하이픈(-)만 사용 가능
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new IllegalArgumentException("사용자 이름은 영문, 한글, 숫자, _, - 만 사용할 수 있음");
        }
    }

    // BaseEntity() 상속 받음
    public User(String username) {
        super(); // id, createdAt, updatedAt -> 생성자로 초기화;
        validateUsername(username);
        this.username = username;
    }

    // Getter
    public String getUsername() {
        return username;
    }
    public List<ChannelUserRole> getChannelUserRoles() {
        return channelUserRoles;
    }
    // getId(), getCreatedAt(), getUpdatedAt()은 상속 받음

    // update
    public void updateUsername(String newUsername) {
        validateUsername(newUsername);
        this.username = newUsername;
        this.updateTimestamp();
    }
    public void addChannelUserRole(ChannelUserRole role) { // 연관관계 편의 메서드
        this.channelUserRoles.add(role);
    }
    public void removeChannelUserRole(ChannelUserRole role) { // 탈퇴 시 리스트에서도 제거
        this.channelUserRoles.remove(role);
    }
    // updateTimestamp()는 상속받음

//    @Override
//    public String toString() {
//        return "User{" +
//                "id=" + id +
//                ", createdAt=" + createdAt +
//                ", updatedAt=" + updatedAt +
//
//                ", username='" + username +
//                '}';
//    }
}