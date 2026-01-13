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
        ChannelService channelService = new JCFChannelService(userService);
        MessageService messageService = new JCFMessageService(userService, channelService);

        // ================= [1. USER TEST] =================
        System.out.println(">>> [1. USER TEST: 닉네임 수정 가능 / 이메일 수정 불가]");
        try {
            // [C] 생성 및 [R] 다건 조회
            User u1 = userService.create("김진우", "zinu@naver.com");
            User u2 = userService.create("김호야", "hoya@naver.com");
            System.out.println("성공 - 유저 생성 완료: " + userService.findAll());

            // [R] 단건 조회
            System.out.println("성공 - 단건 조회: " + userService.findById(u1.getId()));

            // [U] 수정 (닉네임만)
            userService.update(u1.getId(), "진우업뎃");
            System.out.println("성공 - 닉네임 수정: " + userService.findById(u1.getId()).getUserNickname());

            // [E] 예외 테스트: 엔티티 정책 (공백, 길이)
            runTest("User - 닉네임 공백", "   ", () -> userService.create("   ", "fail@test.com"));
            runTest("User - 닉네임 길이", "김", () -> userService.create("김", "fail@test.com"));
            runTest("User - 닉네임 내 공백", "김 진 우", () -> userService.create("김 진 우", "fail@test.com"));

            // [E] 예외 테스트: 서비스 정책 (이메일 중복)
            runTest("User - 이메일 중복", "zinu@naver.com", () -> userService.create("중복맨", "zinu@naver.com"));

            // [D] 삭제
            userService.delete(u2.getId());
            System.out.println("성공 - 유저 삭제 후 수: " + userService.findAll().size());

        } catch (Exception e) {
            System.out.println("CRITICAL ERROR: " + e.getMessage());
        }
        System.out.println("-".repeat(80));

        // ================= [2. CHANNEL TEST] =================
        System.out.println(">>> [2. CHANNEL TEST: 채널명 중복 허용]");
        try {
            // [C] 생성 (이름이 같아도 ID가 다르므로 생성 가능해야 함)
            Channel c1 = channelService.create("자바공부");
            Channel c2 = channelService.create("자바공부");
            System.out.println("성공 - 채널 생성 완료 (중복명 허용): " + channelService.findAll());

            // [U] 수정
            channelService.update(c1.getId(), "스프링공부");
            System.out.println("성공 - 채널명 수정: " + channelService.findById(c1.getId()).getChannelName());

            // [E] 예외 테스트: 채널명 정책 (엔티티 validate)
            runTest("Channel - 이름 미달", "방", () -> channelService.create("방"));
            runTest("Channel - 이름 공백", " ", () -> channelService.create(" "));

            // [D] 삭제
            channelService.delete(c2.getId());
            System.out.println("성공 - 채널 삭제 후 수: " + channelService.findAll().size());

        } catch (Exception e) {
            System.out.println("CRITICAL ERROR: " + e.getMessage());
        }
        System.out.println("-".repeat(80));

        // ================= [3. MESSAGE TEST] =================
        System.out.println(">>> [3. MESSAGE TEST: 참조 무결성 검증]");
        try {
            User user = userService.findAll().get(0);
            Channel channel = channelService.findAll().get(0);

            // [C] 생성
            Message m1 = messageService.create(user.getId(), channel.getId(), "안녕하세요!");
            System.out.println("성공 - 메시지 생성: " + m1.getContent());

            // [U] 수정
            messageService.update(m1.getId(), "내용 수정됨");
            System.out.println("성공 - 메시지 수정: " + messageService.findById(m1.getId()).getContent());

            // [E] 예외 테스트: 존재하지 않는 참조 (중요!)
            UUID fakeId = UUID.randomUUID();
            runTest("Message - 가짜 유저 ID", fakeId.toString(), () -> messageService.create(fakeId, channel.getId(), "Hello"));
            runTest("Message - 가짜 채널 ID", fakeId.toString(), () -> messageService.create(user.getId(), fakeId, "Hello"));

            // [E] 예외 테스트: 메시지 내용 정책
            runTest("Message - 내용 빈값", " ", () -> messageService.create(user.getId(), channel.getId(), "  "));

            // [D] 삭제
            messageService.delete(m1.getId());
            System.out.println("성공 - 메시지 삭제 완료. 전체 수: " + messageService.findAll().size());

        } catch (Exception e) {
            System.out.println("CRITICAL ERROR: " + e.getMessage());
        }

        System.out.println("=".repeat(80));
        System.out.println("모든 도메인 기능 및 예외 테스트 완료.");
    }

    private static void runTest(String title, String input, Runnable action) {
        try {
            action.run();
            System.out.println("[실패] " + title + " : 예외가 발생해야 합니다.");
        } catch (Exception e) {
            System.out.printf("[예외발생 확인] %-20s | 입력값: [%s] | 원인: %s%n", title, input, e.getMessage());
        }
    }
}
