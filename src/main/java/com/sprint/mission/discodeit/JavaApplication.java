package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;

import java.util.List;

public class JavaApplication {
    public static void main(String[] args) {
        JCFUserService userService = new JCFUserService();
        JCFChannelService channelService = new JCFChannelService();
        JCFMessageService messageService = new JCFMessageService();

        System.out.println("========= [1. User 도메인 테스트] =========");
        // 1. 등록
        User user1 = new User("김철수", "kcs@example.com");
        User user2 = new User("김영희", "kyh@example.com");
        User user3 = new User("홍길동", "hkd@example.com");
        userService.create(user1);
        userService.create(user2);
        userService.create(user3);

        // 2. 조회 (단건, 다건)
        User foundUser = userService.readById(user1.getId());
        List<User> allUsers = userService.readAll();
        System.out.println("[단건 조회]: " + foundUser.getName());
        System.out.println("[다건 조회] 전체 유저 수: " + allUsers.size());

        // 3. 수정
        user1.update("고철수", "go@example.com");
        userService.update(user1);
        System.out.println("[수정] 실행");

        // 4. 수정된 데이터 조회
        System.out.println("[수정 데이터 확인]: " + userService.readById(user1.getId()).getName() + "(" + userService.readById(user1.getId()).getEmail() + ")" );

        // 5. 삭제
        userService.delete(user1.getId());
        System.out.println("[삭제] 실행");

        // 6. 조회를 통해 삭제 확인
        System.out.println("[삭제 확인]: " + (userService.readById(user1.getId()) == null ? "user1 삭제 성공" : "user1 삭제 실패"));


        System.out.println("\n========= [2. Channel 도메인 테스트] =========");
        // 1. 등록
        Channel channel1 = new Channel("코딩 공부방", "공부합시다");
        Channel channel2 = new Channel("공지방", "공지방입니다");
        channelService.create(channel1);
        channelService.create(channel2);

        // 2. 조회 (단건, 다건)
        System.out.println("[단건 조회]: " + channelService.readById(channel1.getId()).getName());
        System.out.println("[다건 조회] 전체 채널 수: " + channelService.readAll().size());

        // 3. 수정
        channel1.update("자바 열공방", "열공합시다");
        channelService.update(channel1);
        System.out.println("[수정] 실행");

        // 4. 수정된 데이터 조회
        System.out.println("[수정 데이터 확인]: " + channelService.readById(channel1.getId()).getName() + "(" + channelService.readById(channel1.getId()).getDescription() + ")");

        // 5. 삭제
        channelService.delete(channel1.getId());
        System.out.println("[삭제] 실행");

        // 6. 조회를 통해 삭제 확인
        System.out.println("[삭제 확인]: " + (channelService.readById(channel1.getId()) == null ? "channel1 삭제 성공" : "channel1 삭제 실패"));


        System.out.println("\n========= [3. Message 도메인 테스트] =========");
        // 1. 등록
        Message message1 = new Message("안녕하세요!", user2.getId(), channel2.getId());
        Message message2 = new Message("반갑습니다!", user3.getId(), channel2.getId());
        messageService.create(message1);
        messageService.create(message2);

        // 2. 조회 (단건, 다건)
        System.out.println("[단건 조회]: " + messageService.readById(message1.getId()).getContent());
        System.out.println("[다건 조회] 전체 메시지 수: " + messageService.readAll().size());

        // 3. 수정
        message1.update("반가워요");
        messageService.update(message1);
        System.out.println("[수정] 실행");

        // 4. 수정된 데이터 조회
        System.out.println("[수정 데이터 확인]: " + messageService.readById(message1.getId()).getContent());

        // 5. 삭제
        messageService.delete(message1.getId());
        System.out.println("[삭제] 실행");

        // 6. 조회를 통해 삭제 확인
        System.out.println("[삭제 확인]: " + (messageService.readById(message1.getId()) == null ? "message1 삭제 성공" : "message1 삭제 실패"));

        System.out.println("===== 모든 서비스 테스트 완료 =====");
    }
}