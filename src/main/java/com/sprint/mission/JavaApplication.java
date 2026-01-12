package com.sprint.mission;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.List;
import java.util.UUID;

/*
# 상태 전이 테스트
: 데이터의 생명 주기를 따라가며 테스트하는 방법. CRUD 로직을 짤 때 중요하다.
Happy Path: 생성 > 조회 > 수정 > 삭제 > 조회(없음)
Unhappy Path:
  1. 없는 걸 조회: 생성 안 하고 조회하면?
  2. 없는 걸 수정: 삭제한 뒤에 또 수정하려고 하면?
  3. 없는 걸 삭재: 이미 삭제했는데 또 삭제하려고 하면?
  4. 중복 생성: 아이디가 "ABC"인 유저가 있는데 또 "ABC"를 만들면?
*/

public class JavaApplication {

    public static void main(String[] args) {
        System.out.println("========== [discodeit] 서비스 기능 테스트 시작 ==========");

        // 의존성 주입 및 서비스 초기화
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService(userService);
        // MessageService는 User와 Channel 서비스가 필요함 (생성자 주입)
        MessageService messageService = new JCFMessageService(userService, channelService);

        // 도메인별 테스트 실행
        // 각 테스트는 독립적으로 수행되도록 설계
        try {
            testUserDomain(userService);
            testChannelDomain(channelService, userService);
            testMessageDomain(messageService, channelService, userService);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("!!! 테스트 중 오류 발생 !!!");
        }

        System.out.println("\n========== [discodeit] 모든 테스트 종료 ==========");
    }

    // =================================================================
    // 1. User 도메인 테스트
    // =================================================================
    private static void testUserDomain(UserService userService) {
        printSection("1. User 서비스 테스트");

        // [1] 등록 / 생성(Create)
        System.out.println("1) 사용자 등록");
        User testUser1 = userService.createUser("testUser1의_이름");
        System.out.println("   -> 생성 완료: " + testUser1.getUsername() + " (ID: " + testUser1.getId() + ")");

        // [2] 조회 (단건, 다건) / 조회
        System.out.println("2) 조회");
        User foundUser = userService.findUserByUserId(testUser1.getId());
        System.out.println("   -> 단건 조회: " + foundUser.getUsername());

        User testUser2 = userService.createUser("testUser2의_이름");
        User testUser3 = userService.createUser("testUser3의_이름");
        User testUser4 = userService.createUser("testUser4의_이름");
        List<User> allUsers = userService.findAllUsers();
        System.out.println("   -> 다건 조회(총 인원): " + allUsers.size() + "명");

        // [3] 수정
        System.out.println("3) 수정");
        userService.updateUser(testUser1.getId(), "수정된_testUser1의_이름");

        // [4] 수정된 데이터 조회
        System.out.println("4) 수정 결과 확인");
        User updatedUser1 = userService.findUserByUserId(testUser1.getId());
        System.out.println("   -> 변경된 이름: " + updatedUser1.getUsername() + " (ID: " + updatedUser1.getId() + ")");

        // [5] 삭제
        System.out.println("5) 삭제");
        userService.deleteUser(testUser1.getId());
        System.out.println("   -> 삭제 요청 완료");

        // [6] 조회를 통해 삭제되었는지 확인 (예외 발생 시 성공)
        System.out.println("6) 삭제 확인 (조회 시도)");
        try {
            userService.findUserByUserId(testUser1.getId());
            System.out.println("   -> [실패] 삭제되지 않음! 유저가 여전히 존재함.");
        } catch (IllegalArgumentException e) {
            System.out.println("   -> [성공] 조회 실패 (예상된 에러: " + e.getMessage() + ")");
        }
    }

    // =================================================================
    // 2. Channel 도메인 테스트
    // =================================================================
    private static void testChannelDomain(ChannelService channelService, UserService userService) {
        printSection("2. Channel 서비스 테스트");

        // (선행조건) 채널 생성을 위한 방장(User) 필요
        User testOwner5 = userService.createUser("채널주인_testUser5");

        // [1] 등록 (User ID 참조 사용)
        System.out.println("1) 채널 등록");
        Channel testChannel1_5 = channelService.createChannel("Java 스터디", testOwner5.getId());
        System.out.println("   -> 생성 완료: " + testChannel1_5.getChannelName() + " (OwnerID: " + testChannel1_5.getOwner().getId() + ")");

        // [2] 조회
        System.out.println("2) 조회");
        Channel foundChannel5 = channelService.findChannelById(testChannel1_5.getId());
        System.out.println("   -> 단건 조회: " + foundChannel5.getChannelName());

        User testOwner6 = userService.createUser("채널주인_testUser6");
        User testOwner7 = userService.createUser("채널주인_testUser7");
        Channel testChannel2_6 = channelService.createChannel("testUser6의 채널", testOwner6.getId());
        Channel testChannel3_7 = channelService.createChannel("testUser7의 채널", testOwner7.getId());
        List<Channel> allChannels = channelService.findAllChannels();
        System.out.println("   -> 다건 조회(총 개수): " + allChannels.size() + "개");

        // [3] 수정
        System.out.println("3) 수정");
        channelService.updateChannel(testChannel1_5.getId(), "Spring 스터디");

        // [4] 수정된 데이터 조회
        System.out.println("4) 수정 결과 확인");
        Channel updatedChannel = channelService.findChannelById(testChannel1_5.getId());
        System.out.println("   -> 변경된 채널명: " + updatedChannel.getChannelName());

        // [5] 삭제
        System.out.println("5) 삭제");
        channelService.deleteChannel(testChannel1_5.getId());
        System.out.println("   -> 삭제 요청 완료");

        // [6] 삭제 확인
        System.out.println("6) 삭제 확인 (조회 시도)");
        try {
            channelService.findChannelById(testChannel1_5.getId());
            System.out.println("   -> [실패] 삭제되지 않음.");
        } catch (IllegalArgumentException e) {
            System.out.println("   -> [성공] 조회 실패 (예상된 에러: " + e.getMessage() + ")");
        }
    }

    // =================================================================
    // 3. Message 도메인 테스트
    // =================================================================
    private static void testMessageDomain(MessageService messageService, ChannelService channelService, UserService userService) {
        printSection("3. Message 서비스 테스트");

        // (선행조건) 메시지 전송을 위한 User와 Channel 필요
        User testSender8 = userService.createUser("nomalUser8");
        User testOwner9 = userService.createUser("adminUser9"); // 방장
        Channel testChannel4_8 = channelService.createChannel("Free-Topic", testOwner9.getId());

        // [1] 등록 (내용, 작성자ID, 채널ID)
        System.out.println("1) 메시지 등록");
        Message testMsg1 = messageService.createMessage("안녕하세요!", testSender8.getId(), testChannel4_8.getId());
        System.out.println("   -> 전송 완료: \"" + testMsg1.getContent() + "\"");

        // [2] 조회
        System.out.println("2) 조회");
        Message foundMsg = messageService.findMessageById(testMsg1.getId());
        System.out.println("   -> 단건 조회: " + foundMsg.getContent());

        Message testMsg2 = messageService.createMessage("안녕하세요2!", testSender8.getId(), testChannel4_8.getId());
        Message testMsg3 = messageService.createMessage("안녕하세요3!", testSender8.getId(), testChannel4_8.getId());
        List<Message> channelMsgs = messageService.findAllMessagesByChannelId(testChannel4_8.getId());
        System.out.println("   -> 다건 조회(채널 내 메시지 수): " + channelMsgs.size() + "개");

        // [3] 수정
        System.out.println("3) 수정");
        messageService.updateMessage(testMsg1.getId(), "반갑습니다! (수정됨)");

        // [4] 수정 확인
        System.out.println("4) 수정 결과 확인");
        Message updatedMsg = messageService.findMessageById(testMsg1.getId());
        System.out.println("   -> 변경된 내용: \"" + updatedMsg.getContent() + "\"");

        // [5] 삭제
        System.out.println("5) 삭제");
        messageService.deleteMessage(testMsg1.getId());
        System.out.println("   -> 삭제 요청 완료");

        // [6] 삭제 확인
        System.out.println("6) 삭제 확인 (조회 시도)");
        try {
            messageService.findMessageById(testMsg1.getId());
            System.out.println("   -> [실패] 삭제되지 않음.");
        } catch (IllegalArgumentException e) {
            System.out.println("   -> [성공] 조회 실패 (예상된 에러: " + e.getMessage() + ")");
        }
    }

    // 콘솔 구분선 출력 메서드
    private static void printSection(String title) {
        System.out.println("\n--------------------------------------------------");
        System.out.println(title);
        System.out.println("--------------------------------------------------");
    }
}