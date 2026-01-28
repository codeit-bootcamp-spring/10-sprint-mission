package com.sprint.mission.discodeit.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimeFormatter {
    private TimeFormatter() {} // 프라이빗으로 생성자를 만든이유는 객체로 만들지 말라고
                               // 객체로 만들면 설계 의도가 흐려짐

    public static String format(long millis) {     // 밀리초
        return LocalDateTime.ofInstant(           // 날짜 + 시간
                Instant.ofEpochMilli(millis),    // 1970년 기준부터 지난시간을  UTC 기준으로 시간바꾸기
                ZoneId.systemDefault()           // 내 컴퓨터가 쓰는 시간대 사용
        ).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}