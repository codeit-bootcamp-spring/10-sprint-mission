package com.sprint.mission.discodeit.entity;

public class Channel extends BaseEntity {
    // 1. 필드는 private으로 보호 (캡슐화)
    private String name;

    // 2. 생성자: 채널 생성 시 반드시 이름이 있어야 함을 강제
    public Channel(String name) {
        super(); // 부모(BaseEntity)의 생성자 호출 -> ID, CreatedAt 생성됨
        this.name = name;
    }

    // 3. Getter
    public String getName() {
        return name;
    }

    // 4. 비즈니스 로직: 단순 Setter가 아닌 의미 있는 변경 메소드
    public void updateName(String newName) {
        this.name = newName;
        // 중요: 부모 클래스에 정의해둔 타임스탬프 업데이트 기능 활용
        this.updateTimestamp();
    }

    // (선택사항) 디버깅을 위해 객체 정보를 문자열로 표현
    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}