package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.file.*;
import com.sprint.mission.discodeit.service.basic.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class JavaApplication4 {

    private static final Scanner scanner = new Scanner(System.in);
    private static BasicUserService userService;
    private static BasicChannelService channelService;
    private static BasicMessageService messageService;

    public static void main(String[] args) {
        // 1. 서비스 및 레포지토리 초기화 (JavaApplication3 방식 적용)
        initServices("app4");

        while (true) {
            printMainMenu(); //
            String mainChoice = scanner.nextLine();

            try {
                switch (mainChoice) {
                    case "1": handleCreationAndJoinMenu(); break;   // 생성 및 가입 (1~4번)
                    case "2": handleUserManagementMenu(); break;    // 유저 관리 (5~6번)
                    case "3": handleChannelManagementMenu(); break; // 채널 관리 (7~8번)
                    case "4": handleMessageManagementMenu(); break; // 메시지 관리 (9~10번)
                    case "5": handleLookupMenu(); break;            // 정보 조회 (11~14번)
                    case "0":
                        System.out.println("프로그램을 종료합니다.");
                        return;
                    default:
                        System.out.println("!! 잘못된 선택입니다. 다시 입력해주세요.");
                }
            } catch (Exception e) {
                System.out.println("!! 오류 발생: " + e.getMessage());
            }
            System.out.println("\n--------------------------------------------------");
        }
    }

    // =================================================================
    // [대분류 1] 생성 및 가입 메뉴
    // =================================================================
    private static void handleCreationAndJoinMenu() {
        System.out.println("\n[1. 생성 및 가입 메뉴]");
        System.out.println("1.유저 생성  2.채널 생성  3.채널 가입  4.메시지 전송  0.이전으로");
        System.out.print(">> 선택: ");
        String choice = scanner.nextLine();

        switch (choice) {
            case "1": createUserMenu(); break;
            case "2": createChannelMenu(); break;
            case "3": joinChannelMenu(); break;
            case "4": writeMessageMenu(); break;
        }
    }

    private static void createUserMenu() {
        System.out.print("Username: "); String un = scanner.nextLine();
        System.out.print("Nickname: "); String nn = scanner.nextLine();
        System.out.print("Email: "); String em = scanner.nextLine();
        System.out.print("Phone: "); String ph = scanner.nextLine();
        User user = userService.createUser(un, nn, em, ph); //
        System.out.println(">> 유저 생성 완료: " + user.getNickname());
    }

    private static void createChannelMenu() {
        System.out.print("채널 이름: "); String name = scanner.nextLine();
        System.out.print("채널 설명: "); String desc = scanner.nextLine();
        System.out.print("공개 여부 (1.PUBLIC / 2.PRIVATE): ");
        Channel.ChannelType vis = scanner.nextLine().equals("2") ? Channel.ChannelType.PRIVATE : Channel.ChannelType.PUBLIC;
        channelService.createChannel(name, desc, vis); //
        System.out.println(">> 채널 생성 완료");
    }

    private static void joinChannelMenu() {
        User u = selectUser(); if (u == null) return;
        Channel c = selectChannel(); if (c == null) return;
        userService.joinChannel(u.getId(), c.getId()); //
        System.out.println(">> 가입 완료");
    }

    private static void writeMessageMenu() {
        User u = selectUser(); if (u == null) return;
        List<Channel> joined = new ArrayList<>(u.getChannels());
        if (joined.isEmpty()) { System.out.println("가입된 채널이 없습니다."); return; }

        for (int i=0; i<joined.size(); i++) System.out.printf("%d. %s\n", i+1, joined.get(i).getChannelName());
        System.out.print("채널 선택: ");
        int idx = Integer.parseInt(scanner.nextLine()) - 1;
        System.out.print("내용: "); String content = scanner.nextLine();
        messageService.sendMessage(u.getId(), joined.get(idx).getId(), content); //
        System.out.println(">> 메시지 전송 완료");
    }

    // =================================================================
    // [대분류 2] 유저 관리 메뉴
    // =================================================================
    private static void handleUserManagementMenu() {
        System.out.println("\n[2. 유저 관리 메뉴]");
        System.out.println("1.유저 정보 수정  2.유저 삭제  0.이전으로");
        System.out.print(">> 선택: ");
        String choice = scanner.nextLine();

        switch (choice) {
            case "1": updateUserMenu(); break;
            case "2": deleteUserMenu(); break;
        }
    }

    private static void updateUserMenu() {
        User u = selectUser(); if (u == null) return;
        System.out.println("1.프로필 수정  2.상태 수정");
        String sub = scanner.nextLine();
        if (sub.equals("1")) {
            System.out.print("새 별명: "); String nn = scanner.nextLine();
            userService.updateUserProfile(u.getId(), null, nn.isEmpty()?null:nn, null, null); //
        } else {
            System.out.print("상태(1.ONLINE 2.AWAY 3.OFFLINE): ");
            String pStr = scanner.nextLine();
            User.UserPresence p = pStr.equals("2")?User.UserPresence.AWAY:(pStr.equals("3")?User.UserPresence.OFFLINE:User.UserPresence.ONLINE);
            userService.updateUserStatus(u.getId(), p, null, null); //
        }
        System.out.println(">> 수정 완료");
    }

    private static void deleteUserMenu() {
        User u = selectUser(); if (u == null) return;
        System.out.print("정말 삭제하시겠습니까? (y/n): ");
        if (scanner.nextLine().equalsIgnoreCase("y")) {
            userService.deleteUser(u.getId()); //
            System.out.println(">> 유저 삭제 완료");
        }
    }

    // =================================================================
    // [대분류 3] 채널 관리 메뉴
    // =================================================================
    private static void handleChannelManagementMenu() {
        System.out.println("\n[3. 채널 관리 메뉴]");
        System.out.println("1.채널 정보 수정  2.채널 삭제  0.이전으로");
        System.out.print(">> 선택: ");
        String choice = scanner.nextLine();

        switch (choice) {
            case "1": updateChannelMenu(); break;
            case "2": deleteChannelMenu(); break;
        }
    }

    private static void updateChannelMenu() {
        Channel c = selectChannel(); if (c == null) return;
        System.out.print("새 이름: "); String name = scanner.nextLine();
        System.out.print("새 설명: "); String desc = scanner.nextLine();
        channelService.updateChannel(c.getId(), name.isEmpty()?null:name, desc.isEmpty()?null:desc, null); //
        System.out.println(">> 채널 수정 완료");
    }

    private static void deleteChannelMenu() {
        Channel c = selectChannel(); if (c == null) return;
        System.out.print("삭제하시겠습니까? (y/n): ");
        if (scanner.nextLine().equalsIgnoreCase("y")) {
            channelService.deleteChannel(c.getId()); //
            System.out.println(">> 채널 삭제 완료");
        }
    }

    // =================================================================
    // [대분류 4] 메시지 관리 메뉴
    // =================================================================
    private static void handleMessageManagementMenu() {
        System.out.println("\n[4. 메시지 관리 메뉴]");
        System.out.println("1.메시지 수정  2.메시지 삭제  0.이전으로");
        System.out.print(">> 선택: ");
        String choice = scanner.nextLine();

        switch (choice) {
            case "1": updateMessageMenu(); break;
            case "2": deleteMessageMenu(); break;
        }
    }

    private static void updateMessageMenu() {
        User u = selectUser(); if (u == null) return;
        List<Message> msgs = messageService.findMessagesByAuthor(u.getId()); //
        if (msgs.isEmpty()) { System.out.println("작성한 메시지가 없습니다."); return; }

        for (int i=0; i<msgs.size(); i++) System.out.printf("%d. %s\n", i+1, msgs.get(i).getContent());
        int idx = Integer.parseInt(scanner.nextLine()) - 1;
        System.out.print("수정 내용: "); String content = scanner.nextLine();
        messageService.updateMessage(msgs.get(idx).getId(), content); //
        System.out.println(">> 메시지 수정 완료");
    }

    private static void deleteMessageMenu() {
        User u = selectUser(); if (u == null) return;
        List<Message> msgs = messageService.findMessagesByAuthor(u.getId()); //
        if (msgs.isEmpty()) return;

        for (int i=0; i<msgs.size(); i++) System.out.printf("%d. %s\n", i+1, msgs.get(i).getContent());
        int idx = Integer.parseInt(scanner.nextLine()) - 1;
        messageService.deleteMessage(msgs.get(idx).getId()); //
        System.out.println(">> 메시지 삭제 완료");
    }

    // =================================================================
    // [대분류 5] 정보 조회 메뉴
    // =================================================================
    private static void handleLookupMenu() {
        System.out.println("\n[5. 정보 조회 메뉴]");
        System.out.println("1.유저 정보  2.채널 정보  3.유저 메시지  4.채널 메시지  0.이전으로");
        System.out.print(">> 선택: ");
        String choice = scanner.nextLine();

        switch (choice) {
            case "1": printUserInfoMenu(); break;
            case "2": printChannelInfoMenu(); break;
            case "3": printUserMessagesMenu(); break;
            case "4": printChannelMessagesMenu(); break;
        }
    }

    private static void printUserInfoMenu() {
        User u = selectUser(); if (u != null) System.out.println(u);
    }

    private static void printChannelInfoMenu() {
        Channel c = selectChannel(); if (c != null) System.out.println(c);
    }

    private static void printUserMessagesMenu() {
        User u = selectUser(); if (u == null) return;
        List<Message> msgs = messageService.findMessagesByAuthor(u.getId()); //
        msgs.forEach(m -> System.out.println("[" + new Date(m.getCreatedAt()) + "] " + m.getContent()));
    }

    private static void printChannelMessagesMenu() {
        Channel c = selectChannel(); if (c == null) return;
        List<Message> msgs = messageService.findMessagesByChannel(c.getId()); //
        msgs.forEach(m -> System.out.println("[" + m.getAuthor().getNickname() + "] " + m.getContent()));
    }

    // =================================================================
    // Helper Methods
    // =================================================================
    private static void initServices(String testName) {
        // A. 레포지토리 생성 (FileIO 사용)
        UserRepository userRepository = new FileUserRepository(Paths.get("data",testName, "users")); //
        ChannelRepository channelRepository = new FileChannelRepository(Paths.get("data",testName, "channels")); //
        MessageRepository messageRepository = new FileMessageRepository(Paths.get("data",testName, "messages")); //

        // B. Basic 서비스 초기화 (레포지토리 주입)
        userService = new BasicUserService(userRepository); //
        channelService = new BasicChannelService(channelRepository); //
        messageService = new BasicMessageService(messageRepository); //

        // C. 순환 참조 해결을 위한 Setter 주입
        userService.setChannelService(channelService);
        userService.setMessageService(messageService);
        channelService.setUserService(userService);
        channelService.setMessageService(messageService);
        messageService.setUserService(userService);
        messageService.setChannelService(channelService);

        // 초기 데이터 확인 및 생성
        if (userService.findAll().isEmpty()) {
            initDummyData();
        }
    }

    private static User selectUser() {
        List<User> users = userService.findAll(); //
        if (users.isEmpty()) { System.out.println("!! 유저가 없습니다."); return null; }
        for (int i=0; i<users.size(); i++) System.out.printf("%d. %s\n", i+1, users.get(i).getNickname());
        System.out.print("유저 선택: ");
        try {
            return users.get(Integer.parseInt(scanner.nextLine()) - 1);
        } catch (Exception e) { return null; }
    }

    private static Channel selectChannel() {
        List<Channel> channels = channelService.findAll(); //
        if (channels.isEmpty()) { System.out.println("!! 채널가 없습니다."); return null; }
        for (int i=0; i<channels.size(); i++) System.out.printf("%d. %s\n", i+1, channels.get(i).getChannelName());
        System.out.print("채널 선택: ");
        try {
            return channels.get(Integer.parseInt(scanner.nextLine()) - 1);
        } catch (Exception e) { return null; }
    }

    private static void printMainMenu() {
        System.out.println("\n========== DISCODE-IT (REPOSITORY SYSTEM) ==========");
        System.out.println("1. 생성 및 가입 (유저/채널/가입/전송)");
        System.out.println("2. 유저 관리   (정보수정/삭제)");
        System.out.println("3. 채널 관리   (정보수정/삭제)");
        System.out.println("4. 메시지 관리 (수정/삭제)");
        System.out.println("5. 정보 조회   (상세정보/목록)");
        System.out.println("0. 종료");
        System.out.println("==============================================");
        System.out.print(">> 대메뉴 선택: ");
    }

    private static void initDummyData() {
        User u = userService.createUser("admin", "관리자", "admin@test.com", "010-0000-0000"); //
        Channel c = channelService.createChannel("공지사항", "시스템 공지 채널", Channel.ChannelType.PUBLIC); //
        userService.joinChannel(u.getId(), c.getId()); //
        messageService.sendMessage(u.getId(), c.getId(), "Repository 기반 시스템이 시작되었습니다."); //
    }
}