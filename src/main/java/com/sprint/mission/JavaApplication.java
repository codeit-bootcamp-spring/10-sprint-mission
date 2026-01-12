package com.sprint.mission;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.*;
import java.util.*;


public class JavaApplication {
    private static void testCreateUser(String testName, String input) {
        System.out.print("TEST [" + testName + "] input: '" + input + "' -> ");
        try {
            new User(input); // 여기서 에러가 나야 정상
            // 에러가 안 났다는 뜻 (테스트 실패)
            System.out.println("❌ 실패 (예외가 발생했어야 함)");

        } catch (IllegalArgumentException e) {
            // 우리가 원하던 에러인지 확인하고 성공 메시지 출력
            System.out.println("✅ 통과 (예외 메시지: " + e.getMessage() + ")");
        } catch (Exception e) {
            System.out.println("❓ 예상치 못한 에러: " + e.getClass().getName());
        }
    }
    public static void main(String[] args) {
        User user1 = new User("홍길동");
        Channel ch1 = new Channel("채팅방", user1);
        Message message1 = new Message("첫 메시지", user1, ch1);

        System.out.println("\n====== [User, Channel, Message - Getter 테스트 시작] ======");
        System.out.println("====== [테스트 종료] ======");

        System.out.println("\n====== [User, Channel, Message - update 테스트 시작] ======");
        System.out.println("====== [테스트 종료] ======");

        System.out.println("\n====== [User CRUD 테스트 시작] ======");
        System.out.println("====== [테스트 종료] ======");

        System.out.println("\n====== [Channel CRUD 테스트 시작] ======");
        System.out.println("====== [테스트 종료] ======");

        System.out.println("\n====== [Message CRUD 테스트 시작] ======");
        System.out.println("====== [테스트 종료] ======");

        System.out.println("\n====== [User 유효성 검증 테스트 시작] ======");
        // username 규칙 (1) | null 안됨. 빈 문자열("") 안됨 | 위반
        testCreateUser("Null 입력", null);
        testCreateUser("빈 문자열", "");
        // username 규칙 (2) | 4자 이상 ~ 20자 이하 | 위반
        testCreateUser("길이 미달(1자)", "a");
        testCreateUser("길이 초과(21자)", "012345678901234567890");
        // username 규칙 (3) | 영문, 한글, 숫자 (0-9), 밑줄(_), 하이픈(-)만 사용 가능 | 위반
        testCreateUser("허용되지 않은 특수문자", "한글aBc123_-!@#$%");
        System.out.println("====== [테스트 종료] ======");

        // 등록

        // 조회(단건, 다건)

        // 수정

        // 수정된 데이터 조회

        // 삭제

        // 조회를 통해 삭제되었는지 확인
    }
}
