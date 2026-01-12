package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

public class JavaApplication {

    // Scanner를 전역이나 메인에서 사용하여 입력을 받습니다.
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("          Discodeit Integrated Test Runner        ");
        System.out.println("==================================================");
        System.out.println("테스트할 도메인을 선택하세요:");
        System.out.println("1. User (유저)");
        System.out.println("2. Channel (채널) - 미구현");
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
     * 독립적으로 실행 가능
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
        System.out.println(">> [Input] Email 변경 요청: " + newEmail);
        userService.updateEmail(targetId, newEmail);

        // 변경 확인
        System.out.println(">> [Result] 변경된 Email: " + userService.findById(targetId).get().getEmail().orElse("없음"));


        // -------------------------------------------------------------------
        // 5. Update - Status (상태 및 장비)
        // -------------------------------------------------------------------
        System.out.println("\n[TEST 5] Update Status & Devices");

        System.out.println(">> [Input] 상태 변경: AWAY, 마이크: ON, 헤드셋: ON");
        userService.updateUserStatus(targetId, UserStatus.AWAY);
        userService.toggleMicrophone(targetId, true);
        userService.toggleHeadset(targetId, true);

        User statusUser = userService.findById(targetId).get();
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
        //System.out.printf(">> [Input] 역할 부여: %s (ID: %s)\n", adminRole.getName(), adminId);
        userService.addRoleToUser(targetId, adminRole);

        // 서비스의 hasRole 메서드를 통해 확인 (내부적으로 getUserOrThrow 사용)
        boolean hasRoleResult = userService.hasRole(targetId, adminId);
        System.out.println(">> [Result] userService.hasRole() 확인: " + (hasRoleResult ? "성공" : "실패"));

        // 6-2. 엔티티 수준에서 직접 확인
        User userObj = userService.findById(targetId).get();
        System.out.println(">> [Result] userEntity.hasRole() 확인: " + (userObj.hasRole(adminId) ? "성공" : "실패"));

        // 6-3. 역할 회수 및 검증
        //System.out.printf(">> [Input] 역할 회수: %s\n", adminRole.getName());
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
     * 2. 채널 도메인 테스트 (미구현)
     * 요구사항: 채널은 유저 없이는 생성될 수 없음.
     * 추후 구현 시: 테스트용 유저를 먼저 생성하거나, UserService를 주입받아 유저를 확보한 후 채널 테스트 진행 필요.
     */
    private static void testChannelDomain() {
        System.out.println("\n##################################################");
        System.out.println("            Channel Domain Test (미구현)          ");
        System.out.println("##################################################");
        System.out.println(">> 알림: 채널 테스트를 위해서는 선행된 'User'가 필요합니다.");
        System.out.println(">> 현재 구현된 내용이 없습니다.");
    }

    /**
     * 3. 메시지 도메인 테스트 (미구현)
     * 요구사항: 메시지는 유저와 채널이 모두 존재해야 함.
     * 추후 구현 시: 유저 생성 -> 채널 생성 -> 메시지 전송 순서로 시나리오 구성 필요.
     */
    private static void testMessageDomain() {
        System.out.println("\n##################################################");
        System.out.println("            Message Domain Test (미구현)          ");
        System.out.println("##################################################");
        System.out.println(">> 알림: 메시지 테스트를 위해서는 'User'와 'Channel'이 모두 필요합니다.");
        System.out.println(">> 현재 구현된 내용이 없습니다.");
    }
}