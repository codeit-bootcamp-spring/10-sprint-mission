package com.sprint.mission.discodeit.entity;

public class User extends BaseEntity {
    private String userName;
    private String userEmail;

    public User(String userName, String userEmail) {
        validateUser(userName, userEmail);
        this.userName = userName;
        this.userEmail = userEmail;
    }

    public void updateUser(String userName, String userEmail) {
        validateUser(userName, userEmail);
        this.userName = userName;
        this.userEmail = userEmail;
        super.update();
    }

    // 유저 생성 및 수정 시 준수해야 할 비즈니스 정책 (Fail-Fast)
    // 나중에 필드가 늘어난다면 헬퍼 메서드나 정규 표현식으로 변환 예정
    private void validateUser(String userName, String userEmail) {

        // null, Blank 체크
        if (userName == null || userName.isBlank()) throw new IllegalArgumentException("이름은 필수이며, 비어있을 수 없습니다.");
        if (userEmail == null || userEmail.isBlank()) throw new IllegalArgumentException("이메일은 필수이며, 비어있을 수 없습니다.");

        // 공백(" ") 체크
        if (userName.contains(" ")) throw new IllegalArgumentException("이름은 공백을 포함할 수 없습니다.");
        if (userEmail.contains(" ")) throw new IllegalArgumentException("이메일은 공백을 포함할 수 없습니다.");

        // 유저 이름 길이 체크 (2자 이상, 10자 이하)
        if (userName.length() < 2 || userName.length() > 10) throw new IllegalArgumentException("이름은 2자 이상, 10자 이하로 설정하세요.");

        // 추후 정규 표현식을 이용하여 이메일 검증 로직 구현 예정
    }

    @Override
    public String toString() {
        return String.format("User[이름: %s, 이메일: %s, User ID: %s]", userName, userEmail, getId());
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }
}