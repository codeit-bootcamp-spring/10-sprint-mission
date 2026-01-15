package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class User extends BaseEntity {
    // === 0 상수 ===
    private static final String USERNAME_REGEX = "^[a-zA-Z0-9가-힣_-]+$";
    private static final Pattern USERNAME_PATTERN = Pattern.compile(USERNAME_REGEX);
    // Pattern 클래스: 정규 표현식이 컴퓨일된 클래스.
    // 사용할 정규표현식을 Pattern 객체로 미리 만들어놓고 사용하게 되면, 불필요한 Pattern객체 생성을 막고 메모리를 효율적으로 사용할 수 있음

    // === 1 필드 ===
    // id, createdAt, updatedAt 상속 받음
    private String username;     // 로그인에 쓰이는 고유 ID (중복 불가능) // 시간당 2회 변경 제한 (추후 구현)
    // 1:N 관계 (User : ChannelUserRole) -> 한명의 User는 여러개의 ChannelUserRole을 가질 수 있다.
    private List<ChannelUserRole> channelUserRoles = new ArrayList<>();
//  private String email;        // email 또는 phoneNumber로 가입 가능
//  private String displayName;  // 남들에게 보이는 이름 (중복 허용)
//  private String password;     // 암호화해서 저장 필요
//  private String dateOfBrith   // 생년월일 -> Date 클래스 사용이 더 좋을 듯

    // === 2 생성자 ===
    // BaseEntity() 상속 받음
    public User(String username) {
        super(); // id, createdAt, updatedAt -> 생성자로 초기화;
        validateUsername(username);
        this.username = username;
    }

    // === 3 비즈니스 로직 ===
    // 유효성 검증 메서드 분리
    // JCFUserService.java에 해당 로직을 넣지 않는 이유는, 생성자 때에도 같은 로직을 사용해야하기 때문에
    private void validateUsername(String username) {
        // username 규칙 (1) - null 안됨. 빈 문자열("") 안됨
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("사용자 이름은 필수입니다.");
        }
        // username 규칙 (2) - 2자 이상 ~ 20자 이하
        if (username.length() < 2 || username.length() > 20) {
            throw new IllegalArgumentException("사용자 이름은 2자 이상 20자 이하만 가능합니다.");
        }
        // username 규칙 (3) - 영문, 한글, 숫자 (0-9), 밑줄(_), 하이픈(-)만 사용 가능
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new IllegalArgumentException("사용자 이름은 영문, 한글, 숫자, _, - 만 사용할 수 있습니다. (공백 불가능)");
        }
    }

    // === 4 Getter ===
    public String getUsername() {
        return username;
    }
    public List<ChannelUserRole> getChannelUserRoles() {
        return channelUserRoles;
    }
    // getId(), getCreatedAt(), getUpdatedAt()은 상속 받음

    // === 5 update ===
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
}