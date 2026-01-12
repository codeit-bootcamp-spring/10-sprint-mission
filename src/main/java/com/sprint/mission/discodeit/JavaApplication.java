package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {
        // 1. 서비스 초기화
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();
        MessageService messageService = new JCFMessageService(userService, channelService);

        // [USER TEST]
        System.out.println(">>> [USER TEST]");
        try {
            User user1 = new User("김진우", "wsx104301@naver.com");
            userService.create(user1);
            User user2 = new User("김호야", "hoya@naver.com");
            userService.create(user2);

            System.out.println("[출력] 다건 조회: " + userService.findAll());

            // 예외 케이스 테스트
            runTest("유저 이름 공백", "   ", () -> new User("   ", "error@test.com"));
            runTest("이름 내 공백 포함", "김 진우", () -> new User("김 진우", "error@test.com"));
            runTest("이메일 중복 (Service)", "wsx104301@naver.com", () -> userService.create(new User("중복맨", "wsx104301@naver.com")));

            user1.updateUser("김진짜누", "realzinu@gmail.com");
            userService.update(user1.getId(), user1);
            System.out.println("[출력] 수정 후 정보: " + user1);
        } catch (Exception e) {
            System.out.println("[예외발생] 유저 섹션 내부 오류 | 원인: " + e.getMessage());
        }
        System.out.println("=".repeat(100));

        // [CHANNEL TEST]
        System.out.println(">>> [CHANNEL TEST]");
        try {
            Channel channel1 = new Channel("자바 공부방");
            channelService.create(channel1);

            runTest("채널 이름 미달", "자", () -> new Channel("자"));
            System.out.println("[출력] 단건 조회: " + channelService.findById(channel1.getId()));
        } catch (Exception e) {
            System.out.println("[예외발생] 채널 섹션 내부 오류 | 원인: " + e.getMessage());
        }
        System.out.println("=".repeat(100));

        // [MESSAGE TEST]
        System.out.println(">>> [MESSAGE TEST]");
        try {
            // 안전하게 존재하는 데이터 참조
            User activeUser = userService.findAll().get(0);
            Channel activeChannel = channelService.findAll().get(0);

            System.out.println("[메세지 생성 시도]");
            messageService.create(activeUser.getId(), activeChannel.getId(), "인녕하세요!");

            UUID unknownId = UUID.randomUUID();
            runTest("존재하지 않는 유저 참조", unknownId.toString(), () -> {
                messageService.create(unknownId, activeChannel.getId(), "누구세요?");
            });

            runTest("메시지 내용 빈값", "공백", () -> {
                new Message(activeUser, activeChannel, "  ");
            });

            System.out.println("[출력] 메시지 목록: " + messageService.findAll());

            Message savedMsg = messageService.findAll().get(0);
            System.out.println("[출력] 첫 번째 메시지 작성자: " + savedMsg.getUser().getUserName());

            System.out.println("[출력] 메시지 목록: " + messageService.findAll());
        } catch (Exception e) {
            System.out.println("[예외발생] 메시지 섹션 내부 오류 | 원인: " + e.getMessage());
        }

        System.out.println("=".repeat(100));
        System.out.println("[출력] 모든 테스트가 완료되었습니다.");
    }

    /**
     * 예외 테스트용 헬퍼 메서드
     */
    private static void runTest(String title, String input, Runnable action) {
        try {
            action.run();
            System.out.println("[실패] " + title + " : 예외가 발생하지 않았습니다.");
        } catch (Exception e) {
            System.out.printf("[예외발생] %-20s | 입력값: [%s] | 원인: %s%n", title, input, e.getMessage());
        }
    }
}
