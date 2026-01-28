package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Getter
public class Basic implements Serializable {

    private static final long serialVersionUID = 1L;
    // 공통 도메인 작업 위치.
    protected final UUID id;
    protected final Instant createdAt;
    protected Instant updatedAt;


    //생성자에서 id와 createAt 초기화
    protected Basic() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now(); // 객체가 언제 생성되었느지를 기록하는 메서드라고 한다.
        this.updatedAt = Instant.now(); // 객체가 초기 생성 시에도 업데이트는 기록!
    }


    // 위에 객체 설정 시간처럼... 현재 시간 새로 기록.
    protected void update() {
        this.updatedAt = Instant.now();
    }


    // 생성시간이나 업데이트 시간 가져올떈 보기 편하게!!!
    public String getCreatedAt() {
        return format(createdAt);
    }

    public String getUpdatedAt() {
//        if (updatedAt == null) return "X"; // 아직 수정된 적 없으면 표시만
//        return new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
//                .format(new java.util.Date(instant));
//    }
        return updatedAt == null ? "X" : format(updatedAt);
    }

    private String format(Instant instant) {
        return DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
                .withZone(ZoneId.systemDefault())
                .format(instant);
    }
}
