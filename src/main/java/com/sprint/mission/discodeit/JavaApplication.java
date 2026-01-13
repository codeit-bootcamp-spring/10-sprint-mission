package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.*;

import static com.sprint.mission.discodeit.entity.PermissionTarget.*;

public class JavaApplication {

    // Scanner를 전역이나 메인에서 사용하여 입력을 받습니다.
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("          Discodeit Integrated Test Runner        ");
        System.out.println("==================================================");
        System.out.println("테스트할 도메인을 선택하세요:");
        System.out.println("1. User (유저)");
        System.out.println("2. Channel (채널)");
        System.out.println("3. Message (메시지) - 미구현");
        System.out.print(">> 선택 (번호 입력): ");

        String input = scanner.nextLine();

        switch (input) {
            case "1":
                testUserDomain();
                break;
            case "2":
                testChannelDomain();
                break;
            case "3":
                testMessageDomain();
                break;
            default:
                System.out.println("!! 잘못된 입력입니다. 프로그램을 종료합니다.");
        }

        scanner.close();
    }

    /**
     * 1. 유저 도메인 테스트
     */
    private static void testUserDomain() {
        System.out.println("\n##################################################");
        System.out.println("              User Domain Test Start              ");
        System.out.println("##################################################\n");

        UserService userService = new JCFUserService();

        // -------------------------------------------------------------------
        // 1. Create (생성)
        // -------------------------------------------------------------------
        System.out.println("[TEST 1] Create User");

        // 입력 값 출력
        String u1Name = "test_user";
        String u1Nick = "테스트유저";
        System.out.printf(">> [Input] 생성 요청: username=%s, nickname=%s\n", u1Name, u1Nick);

        String u2Name = "dummy_user";
        String u2Nick = "더미유저";
        System.out.printf(">> [Input] 생성 요청: username=%s, nickname=%s\n", u2Name, u2Nick);

        User user1 = userService.createUser(u1Name, u1Nick, "test@example.com", "010-1234-5678");
        User user2 = userService.createUser(u2Name, u2Nick, null, "010-9876-5432");

        if (user1 != null && user2 != null) {
            System.out.println(">> [Result] 생성 완료: " + user1.getId() + " / " + user2.getId());
        } else {
            System.out.println(">> [FAIL] 유저 생성 실패");
            return; // 생성 실패 시 테스트 중단
        }

        // -------------------------------------------------------------------
        // 2. Read All (전체 조회)
        // -------------------------------------------------------------------
        System.out.println("\n[TEST 2] Find All Users");
        List<User> allUsers = userService.findAll();
        System.out.println(">> [Result] 현재 등록된 유저 수: " + allUsers.size() + "명");

        for (User u : allUsers) {
            System.out.println("   - " + u.getUsername() + " (" + u.getNickname() + ")");
        }


        // -------------------------------------------------------------------
        // 3. Read Single (단건 조회)
        // -------------------------------------------------------------------
        System.out.println("\n[TEST 3] Find By ID & Username");
        UUID targetId = user1.getId();

        System.out.println(">> [Input] ID로 조회 시도: " + targetId);
        Optional<User> byId = userService.findById(targetId);
        byId.ifPresentOrElse(
                u -> System.out.println(">> [Result] 조회 성공: " + u.getUsername()),
                () -> System.out.println(">> [FAIL] ID 조회 실패")
        );


        // -------------------------------------------------------------------
        // 4. Update - Profile (프로필 정보 수정)
        // -------------------------------------------------------------------
        System.out.println("\n[TEST 4] Update Profile");

        // 4-1. Username 변경
        String newUsername = "real_user";
        System.out.printf(">> [Input] Username 변경 요청 (%s -> %s)\n", user1.getUsername(), newUsername);
        userService.updateUsername(targetId, newUsername);

        // 변경 확인
        User updatedUser = userService.findById(targetId).get();
        System.out.println(">> [Result] 변경된 Username: " + updatedUser.getUsername());


        // 4-2. Nickname 변경
        String newNickname = "개발왕";
        System.out.printf(">> [Input] Nickname 변경 요청 (%s -> %s)\n", updatedUser.getNickname(), newNickname);
        userService.updateNickname(targetId, newNickname);

        // 변경 확인
        System.out.println(">> [Result] 변경된 Nickname: " + userService.findById(targetId).get().getNickname());


        // 4-3. Email 변경
        String newEmail = "new@code.com";
        System.out.printf(">> [Input] Email 변경 요청 (%s -> %s)\n", updatedUser.getEmail().get(), newEmail);
        userService.updateEmail(targetId, newEmail);

        // 변경 확인
        System.out.println(">> [Result] 변경된 Email: " + userService.findById(targetId).get().getEmail().orElse("없음"));


        // -------------------------------------------------------------------
        // 5. Update - Status (상태 및 장비)
        // -------------------------------------------------------------------
        System.out.println("\n[TEST 5] Update Status & Devices");

        // 현재 targetId에는 user1(테스트유저->개발왕)의 UUID가 저장됨
        User statusUser = userService.findById(targetId).get();
        System.out.println(">> 현재 유저 Presence:" +
                "\n유저 이름: " + statusUser.getNickname() +
                "\n활성 상태: " + statusUser.getPresence().getStatus() +
                "\n마이크 정보: " + statusUser.getPresence().isMicrophoneOn() +
                "\n헤드셋 정보: " + statusUser.getPresence().isHeadsetOn());

        System.out.println(">> [Input] 상태 변경: AWAY, 마이크: ON, 헤드셋: ON");
        userService.updateUserStatus(targetId, UserStatus.AWAY);
        userService.toggleMicrophone(targetId, true);
        userService.toggleHeadset(targetId, true);

        System.out.println(">> [Result] 유저 이름: " + statusUser.getNickname());
        System.out.println(">> [Result] 현재 상태: " + statusUser.getPresence().getStatus());
        System.out.println(">> [Result] 마이크 상태: " + (statusUser.getPresence().isMicrophoneOn() ? "ON" : "OFF"));
        System.out.println(">> [Result] 헤드셋 상태: " + (statusUser.getPresence().isHeadsetOn() ? "ON" : "OFF"));


        // -------------------------------------------------------------------
        // 6. Role Management (hasRole & Service Integration)
        // -------------------------------------------------------------------
        System.out.println("\n[TEST 6] Role Management (Service & Entity hasRole)");
        Role adminRole = new Role("Administrator");
        UUID adminId = adminRole.getId();

        // 6-1. 역할 부여 및 서비스 레이어 확인
        System.out.printf(">> [Input] 역할 부여: %s (ID: %s)\n", adminRole.getRoleName(), adminId);
        userService.addRoleToUser(targetId, adminRole);

        // 서비스의 hasRole 메서드를 통해 확인 (내부적으로 getUserOrThrow 사용)
        boolean hasRoleResult = userService.hasRole(targetId, adminId);
        System.out.println(">> [Result] userService.hasRole() 확인: " + (hasRoleResult ? "성공" : "실패"));

        // 6-2. 엔티티 수준에서 직접 확인
        User userObj = userService.findById(targetId).get();
        System.out.println(">> [Result] userEntity.hasRole() 확인: " + (userObj.hasRole(adminId) ? "성공" : "실패"));

        // 6-3. 역할 회수 및 검증
        System.out.printf(">> [Input] 역할 회수: %s\n", adminRole.getRoleName());
        userService.removeRoleFromUser(targetId, adminRole);

        boolean hasRoleAfter = userService.hasRole(targetId, adminId);
        System.out.println(">> [Result] 회수 후 보유 여부: " + (hasRoleAfter ? "실패 (여전히 있음)" : "성공 (없음)"));


        // -------------------------------------------------------------------
        // 7. Delete (삭제)
        // -------------------------------------------------------------------
        System.out.println("\n[TEST 7] Delete User");
        System.out.println(">> [Input] 유저 삭제 요청 ID: " + targetId);

        userService.deleteUser(targetId);

        // 검증
        Optional<User> deletedCheck = userService.findById(targetId);
        List<User> remainingUsers = userService.findAll();

        if (deletedCheck.isEmpty()) {
            System.out.println(">> [Result] 해당 ID 조회 결과: 없음 (삭제 성공)");
        } else {
            System.out.println(">> [FAIL] 삭제되지 않음");
        }

        System.out.println(">> [List] 남은 유저 목록 확인:");
        for (User u : remainingUsers) {
            System.out.println("   - " + u.getUsername() + " (ID: " + u.getId() + ")");
        }

        System.out.println("\n[SUCCESS] User Domain Test Finished");
    }

    /**
     * 2. 채널 도메인 테스트
     */
    private static void testChannelDomain() {
        System.out.println("\n##################################################");
        System.out.println("              Channel Domain Test Start           ");
        System.out.println("##################################################\n");

        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();

        // -------------------------------------------------------------------
        // [Step 0] 기초 데이터 준비
        // -------------------------------------------------------------------
        System.out.println("[Step 0] 유저 및 역할 준비");

        User userAdmin = userService.createUser("admin_user", "관리자", "admin@test.com", "010-1111-1111");
        User userVip = userService.createUser("vip_user", "특별회원", "vip@test.com", "010-2222-2222");
        User userNormal = userService.createUser("normal_user", "일반인", "normal@test.com", "010-3333-3333");

        Role managerRole = new Role("Manager");
        userService.addRoleToUser(userAdmin.getId(), managerRole);
        System.out.println("역할 생성: " +  userAdmin.getUsername() + ", " + userAdmin.getNickname());
        System.out.println(userAdmin.getNickname() +"에게" + '"' + managerRole.getRoleName()+ '"' +"역할 부여 ");

        System.out.println(">> 준비 완료");
        System.out.println(">> 유저 목록: " + userAdmin.getNickname() +
                ", " + userVip.getNickname() +
                ", " + userNormal.getNickname());

        // -------------------------------------------------------------------
        // 1. Create (채널 생성)
        // -------------------------------------------------------------------
        System.out.println("\n[TEST 1] Create Channel");
        Channel publicChannel = channelService.createChannel("자유게시판", true);
        Channel privateChannel = channelService.createChannel("임원회의실", false);
        System.out.println(">> 채널 생성 완료: " + publicChannel.getChannelName() + ", " + privateChannel.getChannelName());


        // -------------------------------------------------------------------
        // 2. 권한 부여 (Grant)
        // -------------------------------------------------------------------
        System.out.println("\n[TEST 2] Grant Permissions");

        // 임원회의실: Manager(Role) + VIP(User) 허용
        channelService.grantPermission(privateChannel.getId(), managerRole.getId(), ROLE);
        channelService.grantPermission(privateChannel.getId(), userVip.getId(), USER);
        System.out.println(">> 임원회의실 권한 부여 완료 (Manager 역할, VIP 유저)");


        // -------------------------------------------------------------------
        // 3. Read (권한 체크)
        // -------------------------------------------------------------------
        System.out.println("\n[TEST 3] Check Accessibility (Before Revoke)");

        // 관리자: 2개 (자유 + 임원)
        System.out.print(">> 관리자(Manager) 접근 가능: " + channelService.findChannelsAccessibleBy(userAdmin).size());
        System.out.println(" (2개 예상)");
        System.out.print(">> 접근 가능 채널: ");
        for (Channel channel: channelService.findChannelsAccessibleBy(userAdmin)) {
            System.out.print(channel.getChannelName() + ", ");
        }
        System.out.println();

        // VIP: 2개 (자유 + 임원)
        System.out.print(">> 특별회원(User) 접근 가능: " + channelService.findChannelsAccessibleBy(userVip).size());
        System.out.println(" (2개 예상)");
        System.out.print(">> 접근 가능 채널: ");
        for (Channel channel: channelService.findChannelsAccessibleBy(userVip)) {
            System.out.print(channel.getChannelName() + ", ");
        }
        System.out.println();


        // -------------------------------------------------------------------
        // 4. [NEW] Revoke & FindAll (권한 회수 및 전체 조회)
        // -------------------------------------------------------------------
        System.out.println("\n[TEST 4] Revoke Permission & Find All");

        // 4-1. findAll (관리자용 전체 조회)
        List<Channel> allChannels = channelService.findAll();
        System.out.println(">> [FindAll] 전체 채널 수: " + allChannels.size() + "개 (권한 상관없이 2개 예상) [SUCCESS]");

        // 4-2. revokePermission (Manager 역할의 권한을 뺏음)
        System.out.println(">> [Revoke] 임원회의실에서 'Manager' 역할 권한 제거");
        channelService.revokePermission(privateChannel.getId(), managerRole.getId());

        // 검증 A: 관리자는 이제 임원회의실을 못 봐야 함
        int adminCount = channelService.findChannelsAccessibleBy(userAdmin).size();
        System.out.print(">> 권한 제거 후 관리자 채널 수: " + adminCount);
        if (adminCount == 1) System.out.println(" [SUCCESS] (자유게시판 1개만 보임)");
        else System.out.println(" [FAIL] (여전히 보임)");

        System.out.print(">> 접근 가능 채널: ");
        for (Channel channel: channelService.findChannelsAccessibleBy(userAdmin)) {
            System.out.print(channel.getChannelName() + ", ");
        }
        System.out.println();

        // 검증 B: 특별회원은 개인 권한(User Permission)이므로 여전히 보여야 함
        int vipCount = channelService.findChannelsAccessibleBy(userVip).size();
        System.out.print(">> 권한 제거 후 특별회원 채널 수: " + vipCount);
        if (vipCount == 2) System.out.println(" [SUCCESS] (영향 없음)");
        else System.out.println(" [FAIL]");

        System.out.print(">> 접근 가능 채널: ");
        for (Channel channel: channelService.findChannelsAccessibleBy(userVip)) {
            System.out.print(channel.getChannelName() + ", ");
        }
        System.out.println();


        // -------------------------------------------------------------------
        // 5. Update (수정 및 비공개 전환)
        // -------------------------------------------------------------------
        System.out.println("\n[TEST 5] Update & Visibility Change");

        // 자유게시판 -> 수다방(비공개) -> Manager에게 권한 주기
        channelService.updateChannel(publicChannel.getId(), "수다방");
        channelService.updateChannelVisibility(publicChannel.getId(), false); // 비공개 전환
        channelService.grantPermission(publicChannel.getId(), managerRole.getId(), ROLE);

        System.out.println(">> 자유게시판 -> '수다방(비공개)' 변경 및 Manager 역할 부여");

        // 관리자 확인: 임원회의실(X-아까 뺏음) + 수다방(O-방금 받음) = 1개
        List<Channel> finalAdminList = channelService.findChannelsAccessibleBy(userAdmin);
        System.out.println(">> [Check] 관리자의 현재 채널 수: " + finalAdminList.size() + "개 (수다방 1개 예상)");

        System.out.print(">> 접근 가능 채널: ");
        for (Channel channel: channelService.findChannelsAccessibleBy(userAdmin)) {
            System.out.print(channel.getChannelName() + ", ");
        }
        System.out.println();


        // -------------------------------------------------------------------
        // 6. Delete (삭제)
        // -------------------------------------------------------------------
        System.out.println("\n[TEST 6] Delete Channel");
        System.out.println(">> 임원회의실 삭제");

        channelService.deleteChannel(privateChannel.getId());

        if (channelService.findById(privateChannel.getId()).isEmpty()) {
            System.out.println(">> [SUCCESS] 삭제 확인 완료");
        } else {
            System.out.println(">> [FAIL] 삭제 실패");
        }

        System.out.println(">> [Check] 특별회원의 현재 채널 수: " + channelService.findChannelsAccessibleBy(userVip).size() + "개 (0개 예상)");

        System.out.print(">> 접근 가능 채널: ");
        for (Channel channel: channelService.findChannelsAccessibleBy(userVip)) {
            System.out.print(channel.getChannelName() + ", ");
        }
        System.out.println();



        System.out.println("\n[SUCCESS] Channel Domain Test Finished");
    }

    /**
     * 3. 메시지 도메인 테스트
     * 시나리오: 메시지 전송 -> 변수 기반 수정(목록 출력) -> 변수 기반 삭제(목록 출력)
     */
    private static void testMessageDomain() {
        System.out.println("\n##################################################");
        System.out.println("              Message Domain Test Start           ");
        System.out.println("##################################################\n");

        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();
        MessageService messageService = new JCFMessageService(userService, channelService);

        // [Step 0] 기초 데이터 준비
        User userA = userService.createUser("user_a", "유저A", "a@test.com", "010-1111-1111");
        User userB = userService.createUser("user_b", "유저B", "b@test.com", "010-2222-2222");
        Channel publicCh = channelService.createChannel("자유게시판", true);

        // [Test 1] 메시지 전송 및 객체 보관
        System.out.println("[TEST 1] Send & Store Messages");
        Message msg1 = messageService.sendMessage(userA.getId(), publicCh.getId(), "첫 번째 메시지입니다.");
        Message msg2 = messageService.sendMessage(userB.getId(), publicCh.getId(), "두 번째 메시지입니다.");
        Message msg3 = messageService.sendMessage(userA.getId(), publicCh.getId(), "세 번째 메시지입니다.");
        System.out.println("------------------------------------------\n");
        System.out.println(">> 초기 메시지 전송 완료");
        printAllMessagesInChannel(messageService, publicCh);


        // -------------------------------------------------------------------
        // [Test 2] 수정 (전/후 출력)
        // -------------------------------------------------------------------
        System.out.println("\n[TEST 2] Update Message (Using Variable)");

        System.out.println(">> [Before Update] 수정 전 목록:");
        printAllMessagesInChannel(messageService, publicCh);

        System.out.println(">> [Action] 'msg2' (두 번째 메시지) 수정 요청");
        messageService.updateMessage(msg2.getId(), "내용이 수정된 두 번째 메시지! (Edited)");

        System.out.println(">> [After Update] 수정 후 목록:");
        printAllMessagesInChannel(messageService, publicCh);


        // -------------------------------------------------------------------
        // [Test 3] 단일 삭제 (전/후 출력)
        // -------------------------------------------------------------------
        System.out.println("\n[TEST 3] Delete Single Message (Using Variable)");

        System.out.println(">> [Before Delete] 삭제 전 목록:");
        printAllMessagesInChannel(messageService, publicCh);

        // 인덱스가 아닌 변수 msg1의 ID를 사용하여 정확히 첫 번째 메시지 타겟팅
        System.out.println(">> [Action] 'msg1' (첫 번째 메시지) 삭제 요청");
        messageService.deleteMessage(msg1.getId());

        System.out.println(">> [After Delete] 삭제 후 목록:");
        printAllMessagesInChannel(messageService, publicCh);


        // -------------------------------------------------------------------
        // [Test 4] 연쇄 삭제 확인
        // -------------------------------------------------------------------
        System.out.println("\n[TEST 4] Cascading Delete Check");
        messageService.deleteMessagesByChannelId(publicCh.getId());
        channelService.deleteChannel(publicCh.getId());

        List<Message> result = messageService.findAllByChannelId(publicCh.getId());
        if (result.isEmpty()) {
            System.out.println(">> [SUCCESS] 채널 삭제와 함께 모든 메시지가 파기되었습니다.");
        }

        System.out.println("\n[SUCCESS] Message Domain Test Finished");
    }

    private static void printAllMessagesInChannel(MessageService ms, Channel ch) {
        // List<Message> msgs = ms.findAllByChannelId(ch.getId()); // 맵에서 정렬
        List<Message> msgs = ms.findAll(ch.getId()); // 채널에 리스트로 메시지 들고있음
        System.out.println("--------------------------------------------------");
        System.out.printf(" [%s] 현재 메시지 상태 (%d개)\n", ch.getChannelName(), msgs.size());
        if (msgs.isEmpty()) {
            System.out.println(" (메시지가 없습니다)");
        } else {
            for (Message m : msgs) {
                String editedTag = m.isEdited() ? "(수정됨)" : "";
                System.out.printf(" - [%s] %s %s\n", m.getAuthor().getNickname(), m.getContent(), editedTag);
            }
        }
        System.out.println("--------------------------------------------------");
    }
}