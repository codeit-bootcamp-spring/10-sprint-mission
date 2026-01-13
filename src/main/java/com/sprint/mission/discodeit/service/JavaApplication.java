package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.jcf.*;

import java.util.List;
import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {
        // 1. 서비스 초기화 (의존성 주입)
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();
        MessageService messageService = new JCFMessageService(channelService);
        MembershipService membershipService = new JCFMembershipService(userService, channelService, messageService);

        System.out.println("======= 1. 유저 및 채널 생성 확인 =======");
        userService.create("User1(Alice)", "alice@test.com", "alice_pfp");
        userService.create("User2(Bob)", "bob@test.com", "bob_pfp");
        channelService.create("테스트 채널", "테스트용 설명");

        User alice = userService.findAll().get(0);
        User bob = userService.findAll().get(1);
        Channel testChannel = channelService.findAll().get(0);

        System.out.println("유저 생성 확인: " + alice.getName() + " (ID: " + alice.getId() + ")");
        System.out.println("유저 생성 확인: " + bob.getName() + " (ID: " + bob.getId() + ")");
        System.out.println("채널 생성 확인: " + testChannel.getName() + " (ID: " + testChannel.getId() + ")");


        System.out.println("\n======= 2. 권한별 메시지 생성 테스트 =======");
        // User1(Alice)만 가입
        membershipService.join(alice.getId(), testChannel.getId());
        System.out.println("[진행] User1이 '" + testChannel.getName() + "'에 가입했습니다.");

        // Case A: 가입한 User1의 메시지 생성 시도
        System.out.print("[User1 전송 시도] ");
        messageService.create("안녕하세요, 가입 유저입니다.", alice, testChannel);

        // Case B: 미가입 유저 User2의 메시지 생성 시도
        System.out.print("[User2 전송 시도] ");
        messageService.create("나도 메시지를 보낼 수 있을까?", bob, testChannel);

        // 결과 확인
        List<Message> messages = messageService.findAllByChannelId(testChannel.getId());
        System.out.println("결과: 채널 내 메시지 개수 = " + messages.size());


        System.out.println("\n======= 3. 채널 삭제 및 연쇄 정리 확인 =======");
        // 삭제 전 메시지 ID 보관
        UUID targetMsgId = messages.get(0).getId();
        System.out.println("삭제 전: User1의 채널 목록 개수 = " + alice.getChannelIds().size());
        System.out.println("삭제 전: 메시지 존재 여부 = " + (messageService.findById(targetMsgId) != null));

        // 채널 삭제 실행
        System.out.println("\n[실행] 채널 삭제 시퀀스 가동...");
        membershipService.deleteChannel(testChannel.getId());

        // 최종 결과 검증
        System.out.println("\n[검증 결과]");
        boolean channelExists = channelService.findById(testChannel.getId()) != null;
        boolean userHasChannel = alice.getChannelIds().contains(testChannel.getId());
        boolean messageExists = messageService.findById(targetMsgId) != null;

        System.out.println("1. 채널 서비스에서 삭제되었는가? : " + (!channelExists ? "성공" : "실패"));
        System.out.println("2. 유저의 가입 목록에서 삭제되었는가? : " + (!userHasChannel ? "성공" : "실패"));
        System.out.println("3. 전역 메시지 저장소에서 삭제되었는가? : " + (!messageExists ? "성공" : "실패"));
    }
}