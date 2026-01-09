package com.sprint.mission;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.*;
import java.util.*;

public class JavaApplication {
    public static void main(String[] args) {
        User user1 = new User("홍길동");
        Channel ch1 = new Channel("채팅방", user1.getId());
        Message message1 = new Message("첫 메시지", user1.getId(), ch1.getId());

        System.out.println(user1.toString());
        System.out.println(ch1.toString());
        System.out.println(message1.toString());

        // 등록

        // 조회(단건, 다건)

        // 수정

        // 수정된 데이터 조회

        // 삭제

        // 조회를 통해 삭제되었는지 확인
    }
}