package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.factory.*;

import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {
        UserService userService = ServiceFactory.getUserService();
        ChannelService channelService = ServiceFactory.getChannelService();
        MessageService messageService = ServiceFactory.getMessageService();

        System.out.println(">>> [FINAL ULTIMATE SCENARIO 시작]");
        System.out.println("설정: 유저(U1, U2, U3), 채널(A: U1&U2, B: U2&U3), 인당 메시지 3개");

        try {
            // ---------------------------------------------------------
            // 1. [CREATE & READ] 기초 데이터 생성 및 전체 조회
            // ---------------------------------------------------------
            User u1 = userService.create("유저1", "user1@test.com");
            User u2 = userService.create("유저2", "user2@test.com");
            User u3 = userService.create("유저3", "user3@test.com");

            Channel channelA = channelService.create("채널A");
            Channel channelB = channelService.create("채널B");

            System.out.println("Step 1: 유저 3명, 채널 2개 생성 완료");
            System.out.println("- 전체 유저 수: " + userService.findAll().size());
            System.out.println("- 전체 채널 수: " + channelService.findAll().size());

            // ---------------------------------------------------------
            // 2. [RELATION & EXCEPTION] 가입 시나리오 및 예외 검증
            // ---------------------------------------------------------
            System.out.println("\n--- [Step 2: 가입 및 권한 예외 검증] ---");

            // 채널A: U1, U2 가입
            channelService.addMember(channelA.getId(), u1.getId());
            channelService.addMember(channelA.getId(), u2.getId());

            // 채널B: U2, U3 가입
            channelService.addMember(channelB.getId(), u2.getId());
            channelService.addMember(channelB.getId(), u3.getId());

            // [예외] 미가입자(U1)가 채널B에 메시지 작성 시도
            runTest("E-1: 미가입자(U1) 채널B 발송 차단", "U1 -> ChannelB",
                    () -> messageService.create(u1.getId(), channelB.getId(), "나도 껴줘!"));

            // [예외] 존재하지 않는 유저가 채널A 가입 시도
            UUID fakeId = UUID.randomUUID();
            runTest("E-2: 가짜 유저 ID 가입 차단", "FakeID",
                    () -> channelService.addMember(channelA.getId(), fakeId));

            // ---------------------------------------------------------
            // 3. [BUSINESS LOGIC] 1인당 메시지 3개씩 작성 (총 9개)
            // ---------------------------------------------------------
            System.out.println("\n--- [Step 3: 메시지 발송 (인당 3개)] ---");

            // 유저1 (채널A)
            for (int i = 1; i <= 3; i++) messageService.create(u1.getId(), channelA.getId(), "U1 메시지 " + i);
            // 유저2 (채널A에 2개, 채널B에 1개)
            messageService.create(u2.getId(), channelA.getId(), "U2 채널A 메시지 1");
            messageService.create(u2.getId(), channelA.getId(), "U2 채널A 메시지 2");
            messageService.create(u2.getId(), channelB.getId(), "U2 채널B 메시지 1");
            // 유저3 (채널B)
            for (int i = 1; i <= 3; i++) messageService.create(u3.getId(), channelB.getId(), "U3 메시지 " + i);

            System.out.println("Step 3: 총 9개의 메시지 생성 완료");

            // ---------------------------------------------------------
            // 4. [DOMAIN RELATION] 모든 관계 조회 메서드 검증
            // ---------------------------------------------------------
            System.out.println("\n--- [Step 4: 도메인 관계 조회 전수 검사] ---");

            // (1) UserService.findMyChannels & findMyMessages
            System.out.println("[유저2 검증]");
            System.out.println("- 참여 채널 수: " + channelService.findMyChannels(u2.getId()).size() + " (기대값: 2)");
            System.out.println("- 작성 메시지 수: " + messageService.findMyMessages(u2.getId()).size() + " (기대값: 3)");

            // (2) ChannelService.findMembers & findMessages
            System.out.println("[채널A 검증]");
            System.out.println("- 멤버 수: " + userService.findMembers(channelA.getId()).size() + " (기대값: 2)");
            System.out.println("- 메시지 수: " + messageService.findMessages(channelA.getId()).size() + " (기대값: 5)");

            // ---------------------------------------------------------
            // 5. [UPDATE & POLICY] 데이터 수정 및 정책 검증
            // ---------------------------------------------------------
            System.out.println("\n--- [Step 5: 수정 및 정책 예외 검증] ---");

            // [U] 유저 닉네임 수정 (공백 예외 포함)
            userService.update(u1.getId(), "유저1_NEW");
            runTest("E-3: 닉네임 정책(공백) 검증", "  ", () -> userService.update(u1.getId(), "  "));

            // [U] 메시지 수정 (내용 초과 예외 포함)
            Message targetMsg = messageService.findMyMessages(u3.getId()).get(0);
            messageService.update(targetMsg.getId(), "수정된 메시지입니다.");
            runTest("E-4: 메시지 정책(길이) 검증", "LongText",
                    () -> messageService.update(targetMsg.getId(), "A".repeat(501)));

            // ---------------------------------------------------------
            // 6. [REMOVE & DELETE] 관계 해제 및 데이터 삭제 검증
            // ---------------------------------------------------------
            System.out.println("\n--- [Step 6: 관계 해제 및 삭제 검증] ---");

            // (1) removeMember: 유저2가 채널A에서 퇴장
            channelService.removeMember(channelA.getId(), u2.getId());
            System.out.println("Step 6-1: 유저2 채널A 퇴장 완료");
            System.out.println("- 채널A 현재 멤버 수: " + userService.findMembers(channelA.getId()).size() + " (기대값: 1)");
            System.out.println("- 유저2 현재 참여 채널 수: " + channelService.findMyChannels(u2.getId()).size() + " (기대값: 1)");

            // (2) delete: 메시지 삭제 및 유저 삭제
            UUID msgId = messageService.findMyMessages(u1.getId()).get(0).getId();
            messageService.delete(msgId);
            System.out.println("Step 6-2: 메시지 1건 삭제 완료 (전체 메시지: " + messageService.findAll().size() + ")");

            userService.delete(u3.getId());
            System.out.println("Step 6-3: 유저3 삭제 완료 (전체 유저: " + userService.findAll().size() + ")");

            // ---------------------------------------------------------
            // 7. [FINAL CHECK] 최종 데이터 정합성 보고
            // ---------------------------------------------------------
            System.out.println("\n====================================================");
            System.out.println(">>> [최종 시스템 상태 리포트]");
            System.out.println("- 생존 유저: " + userService.findAll());
            System.out.println("- 남은 채널: " + channelService.findAll());
            System.out.println("- 시스템 내 총 메시지: " + messageService.findAll().size() + "개");
            System.out.println("====================================================");

            System.out.println("\n>>> 모든 통합 시나리오 통과! 도메인 간의 유기적 협력이 증명되었습니다.");

        } catch (Exception e) {
            System.err.println("!!! 테스트 중 치명적 오류 발생 !!!");
        }
    }

    // 테스트용 헬퍼 메소드
    private static void runTest(String title, String input, Runnable action) {
        try {
            action.run();
            System.out.println("[실패] " + title + " : 예외가 발생해야 합니다.");
        } catch (Exception e) {
            System.out.printf("[예외발생 확인] %-30s | 입력값: [%-10s] | 원인: %s%n", title, input, e.getMessage());
        }
    }
}
