package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class JavaApplication {

    public static void main(String[] args) {
        System.out.println("========== [Discodeit JCF 버전 테스트 시작] ==========\n");

        // ---------------------------------------------------------------
        // 1. 서비스 생성 및 의존성 조립 (Setter Injection)
        // ---------------------------------------------------------------
        // (1) 껍데기 생성 (아직 서로 모름)
        JCFUserService userService = new JCFUserService();
        JCFChannelService channelService = new JCFChannelService();
        JCFMessageService messageService = new JCFMessageService();

        // (2) 관계 맺어주기 (Setter 주입)
        // -> 이 과정 덕분에 순환 참조 문제 없이 서로를 호출할 수 있게 됩니다.
        userService.setMessageService(messageService);
        channelService.setMessageService(messageService);
        messageService.setUserService(userService);
        messageService.setChannelService(channelService);

        System.out.println("✅ 서비스 의존성 주입 완료 (Setter Injection)\n");


        // ---------------------------------------------------------------
        // 2. 데이터 준비 (유저 생성, 채널 생성)
        // ---------------------------------------------------------------
        User userA = userService.createUser("userA", "철수", "a@test.com", "010-1111-1111");
        User userB = userService.createUser("userB", "영희", "b@test.com", "010-2222-2222");
        Channel channelGeneral = channelService.createChannel("일반-채널", true);

        // (편의상 유저들을 채널에 입장시킴 - 엔티티 메서드 직접 호출)
        userA.joinChannel(channelGeneral);
        userB.joinChannel(channelGeneral);

        System.out.println("✅ 데이터 생성 완료");
        System.out.println("   - 유저: 철수, 영희");
        System.out.println("   - 채널: 일반-채널 (참여자: " + channelGeneral.getUsers().size() + "명)\n");


        // ---------------------------------------------------------------
        // 3. 메시지 전송 테스트 (Create)
        // ---------------------------------------------------------------
        System.out.println(">> [Test 1] 메시지 전송");

        // 철수가 메시지 전송
        Message msg1 = messageService.sendMessage(userA.getId(), channelGeneral.getId(), "안녕하세요! 철수입니다.");
        // 영희가 메시지 전송
        Message msg2 = messageService.sendMessage(userB.getId(), channelGeneral.getId(), "반가워요. 영희입니다.");

        printChannelMessages(messageService, channelGeneral.getId());


        // ---------------------------------------------------------------
        // 4. 메시지 수정 및 빈 내용 삭제 테스트 (Update -> Delete)
        // ---------------------------------------------------------------
        System.out.println("\n>> [Test 2] 메시지 수정 (빈 내용 입력 시 삭제 확인)");

        // 정상 수정
        messageService.updateMessage(msg1.getId(), "안녕하세요! (수정됨)");
        System.out.println("   -> 철수 메시지 수정 완료: " + msg1.getContent());

        // 빈 내용으로 수정 시도 -> 삭제되어야 함
        System.out.println("   -> 영희 메시지를 빈 값(\"\")으로 수정 시도...");
        try {
            messageService.updateMessage(msg2.getId(), ""); // 빈 문자열
            System.out.println("   -> (성공) 영희 메시지가 삭제되었습니다.");
        } catch (Exception e) {
            System.out.println("   -> (에러) " + e.getMessage());
        }

        // 확인: 메시지가 1개만 남아야 함 (철수 것만)
        printChannelMessages(messageService, channelGeneral.getId());


        // ---------------------------------------------------------------
        // 5. 유저 탈퇴 시 연쇄 삭제 테스트 (Delete User -> Delete Messages)
        // ---------------------------------------------------------------
        System.out.println("\n>> [Test 3] 유저 '철수' 탈퇴 (작성한 메시지도 함께 삭제되어야 함)");

        // 현재 상태: 채널에는 철수의 메시지 1개가 남아있음
        userService.deleteUser(userA.getId());

        // 검증 1: 유저 조회 실패해야 함
        boolean userExists = userService.findById(userA.getId()).isPresent();
        System.out.println("   -> 철수 유저 존재 여부: " + userExists); // false 예상

        // 검증 2: 채널의 메시지가 0개가 되어야 함 (철수가 쓴 글이 삭제됐으므로)
        List<Message> remainingMessages = messageService.findMessagesByChannel(channelGeneral.getId());
        System.out.println("   -> 채널에 남은 메시지 수: " + remainingMessages.size() + "개"); // 0개 예상

        // 검증 3: 채널 참여자 명단에서 철수가 빠져야 함
        System.out.println("   -> 채널 참여자 수: " + channelGeneral.getUsers().size() + "명"); // 1명(영희) 예상


        // ---------------------------------------------------------------
        // 6. 채널 삭제 테스트 (Delete Channel)
        // ---------------------------------------------------------------
        System.out.println("\n>> [Test 4] 채널 삭제");

        channelService.deleteChannel(channelGeneral.getId());

        boolean channelExists = channelService.findById(channelGeneral.getId()).isPresent();
        System.out.println("   -> 채널 존재 여부: " + channelExists); // false 예상

        // 영희의 채널 목록에서도 삭제되었는지 확인
        System.out.println("   -> 영희가 가입한 채널 수: " + userB.getChannels().size() + "개"); // 0개 예상

        System.out.println("\n========== [모든 테스트 통과 완료] ==========");
    }

    // 메시지 목록 출력 헬퍼 메서드
    private static void printChannelMessages(JCFMessageService service, UUID channelId) {
        List<Message> msgs = service.findMessagesByChannel(channelId);
        System.out.println("   [현재 채널 메시지 목록 (" + msgs.size() + "개)]");
        for (Message m : msgs) {
            System.out.println("    - [" + m.getAuthor().getNickname() + "]: " + m.getContent());
        }
    }
}