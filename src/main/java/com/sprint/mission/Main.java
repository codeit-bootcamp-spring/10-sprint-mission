package com.sprint.mission;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
//TIP 코드를 <b>실행</b>하려면 <shortcut actionId="Run"/>을(를) 누르거나
// 에디터 여백에 있는 <icon src="AllIcons.Actions.Execute"/> 아이콘을 클릭하세요.
public class Main {
    public static void main(String[] args) {
        User user1 = new User("김철수", "kcs@example.com");
        User user2 = new User("김영희", "kyh@example.com");

        Channel channel1 = new Channel("자바 스터디");

        Message message1 = new Message("안녕하세요", user1.getId(), channel1.getId());

        System.out.println("사용자1 정보: " + user1);
        System.out.println("사용자2 정보: " + user2);
        System.out.println("채널1 정보: " + channel1);
        System.out.println("메시지1 정보: " + message1);

    }
}