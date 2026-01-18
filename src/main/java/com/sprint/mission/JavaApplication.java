package com.sprint.mission;


import com.sprint.mission.discodeit.entity.*;

import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.jcf.*;
import com.sprint.mission.discodeit.service.listener.*;

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
        printSection("[discodeit] 서비스 기능 테스트 시작");

        JCFUserService userService = new JCFUserService();
        JCFChannelService channelService = new JCFChannelService();
        // 의존성 주입 (다른 서비스를 필요로 하는 서비스)
        MessageService messageService = new JCFMessageService(userService, channelService);
        ChannelUserRoleService channelUserRoleService = new JCFChannelUserRoleService(userService, channelService);

        // 리스너 등록 (데이터 무결성 및 연쇄 삭제 처리)
        // 유저 삭제 시 -> 관련 데이터 모두 정리
        UserLifecycleListener userCleaner = userId -> {
            // 내가 Owner인 채널들 삭제 (채널 삭제 시 내부 리스너가 동작하여 -> 그 채널의 메시지/참여정보도 삭제됨)
            channelService.deleteChannelsByOwnerId(userId); // (3)
            // 내가 작성한 모든 메시지 삭제
            messageService.deleteAllMessagesByUserId(userId); // (4)
            // 나의 채널 참여 관계 삭제
            channelUserRoleService.deleteAllAssociationsByUserId(userId); // (5)
        };
        userService.addListener(userCleaner);

        // 채널 삭제 시 -> 채널 내 데이터 정리
        // 채널 내 메시지 삭제 + 채널-유저 참여 관계 삭제
        channelService.addListener(channelId -> { // 익명 함수
            messageService.deleteAllMessagesByChannelId(channelId); // 채널 내 메시지 삭제 // (3-2)
            channelUserRoleService.deleteAllAssociationsByChannelId(channelId); // 채널 참여자 (Channel-User)관계 목록 삭제 // (3-3)
        });

        // 도메인별 테스트 실행
        try {
            testUserDomain(userService, channelService, messageService, channelUserRoleService);
            // testChannelDomain(channelService, userService);
            // testMessageDomain(messageService, channelService, userService, channelUserRoleService);
            // testChannelUserDomain(channelUserRoleService, channelService, userService);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("\n!!! 테스트 실행 중 오류 발생 !!!");
        }

        printSection("[discodeit] 모든 테스트 종료");
    }

    // =================================================================
    // 1. User 도메인 테스트
    // =================================================================
    private static void printUserCreated(User user) {
        System.out.println("\t-> [유저 생성 완료] username: " + user.getUsername() + " (id: " + user.getId() + ")");
    }
    private static void testUserDomain(UserService userService,
                                       ChannelService channelService,
                                       MessageService messageService,
                                       ChannelUserRoleService channelUserRoleService) {
        printSection("1. UserService 테스트");

        // === [Happy Path] ===
        System.out.println("1.1 Happy Path");

        // [1] 등록 / 생성(Create)
        System.out.println("1) 등록 / 유저 생성 / [Create]");
        User testUser1 = userService.createUser("1번_유저");
        printUserCreated(testUser1);

        // [2] 조회 (단건, 다건) / 조회
        System.out.println("2) 조회(단건, 다건) / [Read]");
        User foundUser = userService.findUserByUserId(testUser1.getId());
        System.out.println("\t-> [특정 유저 조회] username: " + foundUser.getUsername() + " (id: " + testUser1.getId() + ")");

            System.out.println("\t\t---전체 유저 조회를 위한 유저 생성---");
            User testUser2 = userService.createUser("2번_유저"); printUserCreated(testUser2);
            User testUser3 = userService.createUser("3번_유저"); printUserCreated(testUser3);
            User testUser4 = userService.createUser("4번_유저"); printUserCreated(testUser4);
            List<User> allUsers = userService.findAllUsers();  // 순서 보장 X
            System.out.println("\t\t-> [전체 유저 조회]: " + allUsers.size() + "명");
            for(User u : allUsers){
                System.out.println("\tusername: "  + u.getUsername()  + " (id: " + u.getId() + ")");
            }

        // [3] 수정
        System.out.println("3) 수정 / 유저 이름 수정 / [Update]");
        userService.updateUser(testUser1.getId(), "1번_유저_이름바꿈");

        // [4] 수정된 데이터 조회
        System.out.println("4) 수정된 데이터 조회");
        User updatedUser1 = userService.findUserByUserId(testUser1.getId());
        System.out.println("   -> [이름 수정 완료] username: " + updatedUser1.getUsername() + " (id: " + updatedUser1.getId() + ")");

        // [5] 삭제
        System.out.println("5) 삭제 / 유저 삭제 / [Delete]");

            System.out.println("\t--- [삭제 검증을 위한 데이터 준비] ---");
            // 1 유저가 소유한 채널 생성 (유저 삭제 시 이 채널도 삭제되어야 함)
            Channel user1Channel = channelService.createChannel("1번_유저_이름바꿈 님의 채널", updatedUser1);
            System.out.println("\t(준비1 - 유저 소유 채널 생성) channelName: " + user1Channel.getChannelName()
                    + "\n\t\t(channel-id: " + user1Channel.getId() + ")"
            );

            // 2. 유저가 해당 채널에 참여 (메시지를 쓰기 위함)
            // 채널 생성시 자동으로 Owner가 되도록 수정 (추후 구현)
            ChannelUserRole createdRole =
                channelUserRoleService.addChannelUser(user1Channel.getId(), updatedUser1.getId(), ChannelRole.OWNER);

            System.out.println("\t(준비2 - 채널 참여 완료) username: " + updatedUser1.getUsername()
                + " -> " + user1Channel.getChannelName() + "\n\t\t(channel-id: " + user1Channel.getId() + ")"
                + "\n\t\t(user-Role: " + ChannelRole.OWNER + ")");

            // 3. 유저가 메시지 작성 (유저 삭제/채널 삭제 시 이 메시지도 삭제되어야 함)
            Message user1Msg = messageService.createMessage("나 삭제되면 이 메시지도 사라지나요?", updatedUser1.getId(), user1Channel.getId());
            System.out.println("\t(준비3 - 유저가 메시지 작성 완료)");
            System.out.println("\t\tusername: " + updatedUser1.getUsername()
                    + "\n\t\tcontent: " + user1Msg.getContent()
                    + "\n\t\tmessage-id: " + user1Msg.getId()
                    + "\n\t\tmessage-updatedAt: " + user1Msg.getUpdatedAt() );
            System.out.println("\t----------------------------------");

        userService.deleteUser(testUser1.getId()); // (1)

        // [6-1] User 단건 조회를 통해 삭제되었는지 확인 (예외 발생 시 성공)
        System.out.println("6) 조회를 통해 삭제되었는지 확인");
        try {
            userService.findUserByUserId(testUser1.getId());
            System.out.println("\t-> [실패] 삭제되지 않음! 유저가 여전히 존재함.");
        } catch (IllegalArgumentException e) {
            System.out.println("\t-> [성공] 조회 실패 / 예상된 에러: " + e.getMessage());
        }
        // [6-2] User 전체 조회를 통해 삭제되었는지 확인
        allUsers = userService.findAllUsers();
        System.out.println("\t\t-> [전체 유저 조회]: " + allUsers.size() + "명");
        for(User u : allUsers) {
            System.out.println("\tusername: " + u.getUsername() + " (id: " + u.getId() + ")");
        }

        // === [Unhappy Path] ===
        System.out.println("1.2 Unhappy Path");

        // 1. 없는 걸 조회 / 없는 유저를 조회하면?
        System.out.print("Test 1) 존재하지 않는 ID 조회: ");
        try {
            userService.findUserByUserId(UUID.randomUUID()); // 랜덤 id
            System.out.println("실패 (예외가 발생하지 않음)");
        } catch (IllegalArgumentException e) {
            System.out.println("성공 (방어: " + e.getMessage() + ")");
        }

        // 2. 없는 걸 수정 / 없는 유저를 수정하면?
        System.out.print("Test 2) 존재하지 않는 ID 수정: ");
        try {
            userService.updateUser(UUID.randomUUID(), "Ghost");
            System.out.println("실패 (예외가 발생하지 않음)");
        } catch (IllegalArgumentException e) {
            System.out.println("성공 (방어: " + e.getMessage() + ")");
        }

        // 3. 없는 걸 삭제 / 없는 유저를 삭제하면?
        System.out.print("Test 3) 이미 삭제된 ID 삭제 시도: ");
        try {
            userService.deleteUser(testUser1.getId()); // testUser1 -> 위에서 이미 삭제함
            System.out.println("실패 (예외가 발생하지 않음)");
        } catch (IllegalArgumentException e) {
            System.out.println("성공 (방어: " + e.getMessage() + ")");
        }

        // 4. 중복 생성 / 중복된 이름의 유저를 생성하면?
        System.out.print("Test 4) 중복된 이름으로 생성 시도: ");
        try {
            userService.createUser("중복된_유저_이름");
            userService.createUser("중복된_유저_이름");
            System.out.println("실패 (중복이 허용됨)");
        } catch (IllegalArgumentException e) {
            System.out.println("성공 (방어: " + e.getMessage() + ")");
        }
    }

    // =================================================================
    // 2. Channel 도메인 테스트
    // =================================================================
    private static void printChannelCreated(Channel channel) {
        System.out.println("\t-> [채널 생성 완료] channelName: " + channel.getChannelName()
                + "\n\t\t(id: " + channel.getId() + ")"
                + "\n\t\t(ownerId: " + channel.getOwner().getId() + ")");
    }
    private static void testChannelDomain(ChannelService channelService, UserService userService) {
        printSection("2. ChannelService 테스트");
        // === [Happy Path] ===
        System.out.println("2.1 Happy Path");

        // [0] (선행조건) 채널 생성을 위한 방장(User) 필요
        System.out.println("0) 채널 생성을 위한 Owner(User) 생성");
        User testOwner1= userService.createUser("Owner_testUser1");
        printUserCreated(testOwner1);

        // [1] 등록
        System.out.println("1) 등록 / 채널 생성 / [Create]");
        Channel testChannel1 = channelService.createChannel("testOwner1의 채널", testOwner1);
        printChannelCreated(testChannel1);

        // [2] 조회
        System.out.println("2) 조회(단건, 다건) / [Read]");
        Channel foundChannel = channelService.findChannelById(testChannel1.getId());
        System.out.println("\t-> [특정 채널 조회] channelName: " + foundChannel.getChannelName() + " (id: " + foundChannel.getId() + ")");

        System.out.println("\t(전체 채널 조회를 위한 유저 및 채널 생성)");
        User testOwner2 = userService.createUser("Owner_testUser2"); printUserCreated(testOwner2);
        User testOwner3 = userService.createUser("Owner_testUser3"); printUserCreated(testOwner3);
        Channel testChannel2 = channelService.createChannel("testOwner2의 채널", testOwner2); printChannelCreated(testChannel2);
        Channel testChannel3 = channelService.createChannel("testOwner3의 채널", testOwner3); printChannelCreated(testChannel3);
        List<Channel> allChannels = channelService.findAllChannels();
        System.out.println("\t-> [전체 채널 조회]: " + allChannels.size() + "개");
        for(Channel ch : allChannels){
            System.out.println("\tchannelName: " + ch.getChannelName() + " (id: " + ch.getId() + ")");
        }

        // [3] 수정
        System.out.println("3) 수정 / 채널 이름 수정 / [Update]");
        channelService.updateChannel(testChannel1.getId(), "testOwner1의 채널_이름 수정");

        // [4] 수정된 데이터 조회
        System.out.println("4) 수정된 데이터 조회");
        Channel updatedChannel = channelService.findChannelById(testChannel1.getId());
        System.out.println("\t-> [채널 이름 수정 완료] channelName: " + updatedChannel.getChannelName() + " (id: " + updatedChannel.getId() + ")");

        // [5] 삭제
        System.out.println("5) 삭제 / 채널 삭제 / [Delete]");
        channelService.deleteChannel(testChannel1.getId());
        System.out.println("\t-> 삭제 요청 완료");

        // [6] 삭제 확인
        System.out.println("6) 조회를 통해 삭제되었는지 확인");
        try {
            channelService.findChannelById(testChannel1.getId());
            System.out.println("   -> [실패] 삭제되지 않음.");
        } catch (IllegalArgumentException e) {
            System.out.println("   -> [성공] 조회 실패 (예상된 에러: " + e.getMessage() + ")");
        }

        // === [Unhappy Path] ===
        System.out.println("2.2 Unhappy Path");

        // 1. 없는 걸 조회
        System.out.print("Test 1) 존재하지 않는 채널 조회: ");
        try {
            channelService.findChannelById(UUID.randomUUID());
            System.out.println("실패");
        } catch (IllegalArgumentException e) {
            System.out.println("성공 (방어: " + e.getMessage() + ")");
        }

        // 2. 없는 걸 수정
        System.out.print("Test 2) 존재하지 않는 채널 수정: ");
        try {
            channelService.updateChannel(UUID.randomUUID(), "Hacking");
            System.out.println("실패");
        } catch (IllegalArgumentException e) {
            System.out.println("성공 (방어: " + e.getMessage() + ")");
        }

        // 3. 없는 걸 삭제
        System.out.print("Test 3) 존재하지 않는 채널 삭제: ");
        try {
            channelService.deleteChannel(UUID.randomUUID());
            System.out.println("실패");
        } catch (IllegalArgumentException e) {
            System.out.println("성공 (방어: " + e.getMessage() + ")");
        }

        // 4. 중복 생성
        System.out.print("Test 4) 중복된 채널 이름 생성: ");
        try {
            channelService.createChannel("UniqueChannel", testOwner2);
            channelService.createChannel("UniqueChannel", testOwner3); // 이름 중복
            System.out.println("실패 (중복 허용됨)");
        } catch (IllegalArgumentException e) {
            System.out.println("성공 (방어: " + e.getMessage() + ")");
        }
    }

//    // =================================================================
//    // 3. Message 도메인 테스트
//    // =================================================================
//    private static void testMessageDomain(MessageService messageService, ChannelService channelService, UserService userService, ChannelUserRoleService channelUserRoleService) {
//        printSection("3. Message 서비스 테스트");
//
//        // (선행조건) 메시지 전송을 위한 User와 Channel 필요
//        User testSender8 = userService.createUser("nomalUser8");
//        User testOwner9 = userService.createUser("adminUser9"); // 방장
//        Channel testChannel4_8 = channelService.createChannel("Free-Topic", testOwner9);
//
//        // [1] 등록 (내용, 작성자ID, 채널ID)
//        System.out.println("1) 메시지 등록");
//        Message testMsg1 = messageService.createMessage("안녕하세요!", testSender8.getId(), testChannel4_8.getId());
//        System.out.println("   -> 전송 완료: \"" + testMsg1.getContent() + "\"");
//
//        // [2] 조회
//        System.out.println("2) 조회");
//        Message foundMsg = messageService.findMessageById(testMsg1.getId());
//        System.out.println("   -> 단건 조회: " + foundMsg.getContent());
//
//        Message testMsg2 = messageService.createMessage("안녕하세요2!", testSender8.getId(), testChannel4_8.getId());
//        Message testMsg3 = messageService.createMessage("안녕하세요3!", testSender8.getId(), testChannel4_8.getId());
//        List<Message> channelMsgs = messageService.findAllMessagesByChannelId(testChannel4_8.getId());
//        System.out.println("   -> 다건 조회(채널 내 메시지 수): " + channelMsgs.size() + "개");
//
//        // [3] 수정
//        System.out.println("3) 수정");
//        messageService.updateMessage(testMsg1.getId(), "반갑습니다! (수정됨)");
//
//        // [4] 수정 확인
//        System.out.println("4) 수정 결과 확인");
//        Message updatedMsg = messageService.findMessageById(testMsg1.getId());
//        System.out.println("   -> 변경된 내용: \"" + updatedMsg.getContent() + "\"");
//
//        // [5] 삭제
//        System.out.println("5) 삭제");
//        messageService.deleteMessage(testMsg1.getId());
//        System.out.println("   -> 삭제 요청 완료");
//
//        // [6] 삭제 확인
//        System.out.println("6) 삭제 확인 (조회 시도)");
//        try {
//            messageService.findMessageById(testMsg1.getId());
//            System.out.println("   -> [실패] 삭제되지 않음.");
//        } catch (IllegalArgumentException e) {
//            System.out.println("   -> [성공] 조회 실패 (예상된 에러: " + e.getMessage() + ")");
//        }
//
//        // =================================================================
//
//        // [Unhappy Path]
//        printSubSection("Message 서비스 테스트 - Unhappy Path");
//
//        // 1. 없는 메시지 조회
//        System.out.print("Test 1) 없는 메시지 ID 조회: ");
//        try {
//            messageService.findMessageById(UUID.randomUUID());
//            System.out.println("❌ 실패");
//        } catch (IllegalArgumentException e) { // 혹은 IllegalStateException
//            System.out.println("✅ 성공 (방어: " + e.getMessage() + ")");
//        }
//
//        // 2. 없는 메시지 수정
//        System.out.print("Test 2) 없는 메시지 ID 수정: ");
//        try {
//            messageService.updateMessage(UUID.randomUUID(), "New Content");
//            System.out.println("❌ 실패");
//        } catch (IllegalArgumentException | IllegalStateException e) {
//            System.out.println("✅ 성공 (방어: " + e.getMessage() + ")");
//        }
//
//        // 3. 없는 메시지 삭제
//        System.out.print("Test 3) 없는 메시지 ID 삭제 시도: ");
//        try {
//            messageService.deleteMessage(UUID.randomUUID());
//            System.out.println("❌ 실패 (예외가 발생하지 않음)");
//        } catch (IllegalArgumentException | IllegalStateException e) {
//            System.out.println("✅ 성공 (방어: " + e.getMessage() + ")");
//        }
//
//        // 4. 메시지 중복 생성 (-> 메시지는 String content를 인자로 받으므로 같은 Id 중복 생성 테스트 불가능)
//
//        // =================================================================
//        // 예외 케이스
//        // [ ] 추가 필요
//
//        printSubSection("Message 서비스 테스트 - 예외 케이스");
//
//        // 1. 없는 유저가 메시지 전송
//        System.out.print("Test 1) 존재하지 않는 유저로 전송 시도: ");
//        try {
//            messageService.createMessage("Ghost Message", UUID.randomUUID(), testChannel4_8.getId());
//            System.out.println("❌ 실패 (유령 회원이 글을 씀)");
//        } catch (IllegalArgumentException e) {
//            System.out.println("✅ 성공 (방어: " + e.getMessage() + ")");
//        }
//
//        // 2. 없는 채널에 메시지 전송
//        System.out.print("Test 1) 존재하지 않는 채널로 전송 시도: ");
//        try {
//            messageService.createMessage("Void Message", testSender8.getId(), UUID.randomUUID());
//            System.out.println("❌ 실패 (채널 없이 글을 씀)");
//        } catch (IllegalArgumentException e) {
//            System.out.println("✅ 성공 (방어: " + e.getMessage() + ")");
//        }
//
//        // 3. 동일한 메시지 내용 전송
//        System.out.print("Test 3) 동일한 내용 연속 전송 확인: ");
//
//        Message msgA = messageService.createMessage("안녕하세요", testSender8.getId(), testChannel4_8.getId());
//        Message msgB = messageService.createMessage("안녕하세요", testSender8.getId(), testChannel4_8.getId());
//
//        // 검증 1: 둘 다 저장이 잘 되었는가?
//        // 검증 2: 둘의 ID가 다른가? (별개의 객체인가?)
//        if (!msgA.getId().equals(msgB.getId())) {
//            System.out.println("✅ 성공 (내용은 같지만 서로 다른 메시지(ID)로 잘 저장됨)");
//            System.out.println("   -> ID1: " + msgA.getId());
//            System.out.println("   -> ID2: " + msgB.getId());
//        } else {
//            System.out.println("❌ 실패 (ID가 같음 - 이건 물리적으로 불가능하지만 혹시 몰라 체크)");
//        }
//    }
//
//    // =================================================================
//    // 4. ChannelUser(참여자) 도메인 테스트
//    // =================================================================
//    private static void testChannelUserDomain(ChannelUserRoleService channelUserService,
//                                              ChannelService channelService,
//                                              UserService userService) {
//        printSection("4. ChannelUser 서비스 테스트");
//
//        // [0] 테스트 데이터 준비
//        System.out.println("0) 데이터 준비");
//        User owner = userService.createUser("방장");
//        User member = userService.createUser("일반멤버");
//        User outsider = userService.createUser("외부인"); // 채널에 없는 사람
//        Channel channel = channelService.createChannel("테스트채널", owner);
//
//        // ==========================================
//        // [Happy Path] 생성 -> 조회 -> 수정 -> 삭제 -> 확인
//        // ==========================================
//        printSubSection("Happy Path");
//
//        // [1] 생성 (Create) - 채널 입장
//        System.out.println("1) 생성 (채널 입장)");
//        channelUserService.addChannelUser(channel.getId(), member.getId(), ChannelRole.MEMBER);
//        // 검증 1: 유저 객체의 리스트에 잘 들어갔는지?
//        boolean inUserList = member.getChannelUserRoles().stream()
//                .anyMatch(r -> r.getChannel().getId().equals(channel.getId()));
//        // 검증 2: 서비스의 Map에 잘 들어갔는지?
//        // (Map은 private이므로, 조회 메서드인 findChannelUser를 호출해서 에러가 안 나면 있는 것으로 판단)
//        boolean inServiceMap;
//        try {
//            channelUserService.findChannelUser(channel.getId(), member.getId());
//            inServiceMap = true; // 에러 안 나고 조회되면 성공
//        } catch (IllegalArgumentException e) {
//            inServiceMap = false; // "해당 채널에 참여하지 않은 사용자" 에러가 나면 실패
//        }
//        System.out.println("   -> 입장 결과 (User List): " + (inUserList ? "✅ 성공" : "❌ 실패"));
//        System.out.println("   -> 입장 결과 (Service Map): " + (inServiceMap ? "✅ 성공" : "❌ 실패"));
//
//        // [2] 조회 (Read)
//        System.out.println("2) 조회");
//        ChannelUserRole roleInfo = channelUserService.findChannelUser(channel.getId(), member.getId());
//        System.out.println("   -> 조회된 권한: " + roleInfo.getRole());
//
//        // [3] 수정 (Update) - 권한 변경 (MEMBER -> ADMINISTRATOR 위임 등)
//        System.out.println("3) 수정 (권한 변경)");
//        channelUserService.updateChannelRole(channel.getId(), member.getId(), ChannelRole.ADMIN); // 예: 방장 위임
//
//        // 수정 확인
//        ChannelUserRole updatedRole = channelUserService.findChannelUser(channel.getId(), member.getId());
//        System.out.println("   -> 변경된 권한: " + updatedRole.getRole());
//
//        // [4] 삭제 (Delete) - 채널 탈퇴
//        System.out.println("4) 삭제 (채널 탈퇴)");
//        channelUserService.deleteChannelUser(channel.getId(), member.getId());
//        System.out.println("   -> 탈퇴 요청 완료");
//
//        // [5] 삭제 확인 (Read Fail)
//        System.out.println("5) 삭제 확인 (조회 시도)");
//        try {
//            channelUserService.findChannelUser(channel.getId(), member.getId());
//            System.out.println("   -> [실패] 삭제되지 않음 (여전히 조회됨)");
//        } catch (IllegalArgumentException e) {
//            System.out.println("   -> [성공] 조회 실패 (예상된 에러: " + e.getMessage() + ")");
//        }
//        boolean isStillJoined = member.getChannelUserRoles().stream()
//                .anyMatch(r -> r.getChannel().getId().equals(channel.getId()));
//        if (!isStillJoined) {
//            System.out.println("   -> [성공] 유저 개인 리스트에서도 삭제됨");
//        } else {
//            System.out.println("   -> [실패] 유저 개인 리스트에 데이터가 남아있음");
//        }
//
//        // ==========================================
//        // [Unhappy Path] 예외 상황 테스트
//        // ==========================================
//        printSubSection("Unhappy Path");
//
//        // 1. 없는 걸 조회 (가입 안 한 사람 조회)
//        System.out.print("Test 1) 가입하지 않은 유저 조회: ");
//        try {
//            channelUserService.findChannelUser(channel.getId(), outsider.getId());
//            System.out.println("❌ 실패 (예외 안 터짐)");
//        } catch (IllegalArgumentException e) {
//            System.out.println("✅ 성공 (방어: " + e.getMessage() + ")");
//        }
//
//        // 2. 없는 걸 수정 (가입 안 한 사람 권한 변경)
//        System.out.print("Test 2) 가입하지 않은 유저 권한 수정: ");
//        try {
//            channelUserService.updateChannelRole(channel.getId(), outsider.getId(), ChannelRole.OWNER);
//            System.out.println("❌ 실패 (예외 안 터짐)");
//        } catch (IllegalArgumentException e) {
//            System.out.println("✅ 성공 (방어: " + e.getMessage() + ")");
//        }
//
//        // 3. 없는 걸 삭제 (가입 안 한 사람 강퇴)
//        System.out.print("Test 3) 가입하지 않은 유저 삭제 시도: ");
//        try {
//            channelUserService.deleteChannelUser(channel.getId(), outsider.getId());
//            System.out.println("❌ 실패 (예외 안 터짐)");
//        } catch (IllegalArgumentException e) {
//            System.out.println("✅ 성공 (방어: " + e.getMessage() + ")");
//        }
//
//        // 4. 중복 생성 (이미 가입했는데 또 가입)
//        System.out.print("Test 4) 중복 가입 시도: ");
//        try {
//            // 다시 가입 시키고 (Happy)
//            channelUserService.addChannelUser(channel.getId(), member.getId(), ChannelRole.MEMBER);
//            // 또 가입 시도 (Unhappy)
//            channelUserService.addChannelUser(channel.getId(), member.getId(), ChannelRole.MEMBER);
//            System.out.println("❌ 실패 (중복 가입됨)");
//        } catch (IllegalArgumentException e) {
//            System.out.println("✅ 성공 (방어: " + e.getMessage() + ")");
//        }
//    }

    // 콘솔 구분선 출력 메서드
    private static void printSection(String title) {
        System.out.println("\n--------------------------------------------------");
        System.out.println(title);
        System.out.println("--------------------------------------------------");
    }
    private static void printSubSection(String title) {
        System.out.println("\n-----------------------");
        System.out.println(title);
        System.out.println("-----------------------");
    }
}