package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.*;

public class JavaApplication {

    // 전역 스캐너 및 서비스
    private static final Scanner scanner = new Scanner(System.in);
    private static final JCFUserService userService = new JCFUserService();
    private static final JCFChannelService channelService = new JCFChannelService();
    private static final JCFMessageService messageService = new JCFMessageService();

    public static void main(String[] args) {
        // 0. 의존성 주입 (Dependency Injection)
        userService.setMessageService(messageService);
        userService.setChannelService(channelService);
        channelService.setMessageService(messageService);
        messageService.setUserService(userService);
        messageService.setChannelService(channelService);

        // 초기 데이터 (테스트 편의용)
        initDummyData();

        while (true) {
            printMenu();
            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1": createUserMenu(); break;
                    case "2": createChannelMenu(); break;
                    case "3": joinChannelMenu(); break;
                    case "4": writeMessageMenu(); break;
                    case "5": updateUserMenu(); break;
                    case "6": deleteUserMenu(); break;
                    case "7": updateChannelMenu(); break;
                    case "8": deleteChannelMenu(); break;
                    case "9": updateMessageMenu(); break;
                    case "10": deleteMessageMenu(); break;
                    // [추가된 기능]
                    case "11": printUserInfoMenu(); break;
                    case "12": printChannelInfoMenu(); break;
                    case "13": printUserMessagesMenu(); break;
                    case "14": printChannelMessagesMenu(); break;

                    case "0":
                        System.out.println("프로그램을 종료합니다.");
                        return;
                    default:
                        System.out.println("!! 잘못된 입력입니다. 다시 선택해주세요.");
                }
            } catch (Exception e) {
                System.out.println("!! 에러 발생: " + e.getMessage());
            }
            System.out.println("\n--------------------------------------------------"); // 구분선
        }
    }

    // =================================================================
    // [메뉴 1] 유저 생성
    // =================================================================
    private static void createUserMenu() {
        System.out.println("\n[1. 유저 생성]");
        System.out.print("Username (ID): ");
        String username = scanner.nextLine();
        System.out.print("이름 (Nickname): ");
        String nickname = scanner.nextLine();
        System.out.print("이메일: ");
        String email = scanner.nextLine();
        System.out.print("전화번호: ");
        String phoneNumber = scanner.nextLine();

        User user = userService.createUser(username, nickname, email, phoneNumber);
        System.out.println(">> 유저 생성 완료: " + user);
    }

    // =================================================================
    // [메뉴 2] 채널 생성
    // =================================================================
    private static void createChannelMenu() {
        System.out.println("\n[2. 채널 생성]");
        System.out.print("채널 이름: ");
        String name = scanner.nextLine();
        System.out.print("공개 여부 (y/n): ");
        boolean isPublic = scanner.nextLine().trim().equalsIgnoreCase("y");

        Channel channel = channelService.createChannel(name, isPublic);
        System.out.println(">> 채널 생성 완료: " + channel);
    }

    // =================================================================
    // [메뉴 3] 채널 가입
    // =================================================================
    private static void joinChannelMenu() {
        System.out.println("\n[3. 채널 가입]");
        User user = selectUser();
        if (user == null) return;

        Channel channel = selectChannel();
        if (channel == null) return;

        user.joinChannel(channel);
        System.out.printf(">> '%s'님이 '%s' 채널에 가입했습니다.\n", user.getNickname(), channel.getChannelName());
    }

    // =================================================================
    // [메뉴 4] 메시지 남기기
    // =================================================================
    private static void writeMessageMenu() {
        System.out.println("\n[4. 메시지 남기기]");
        User user = selectUser();
        if (user == null) return;

        Set<Channel> joinedChannels = user.getChannels();
        if (joinedChannels.isEmpty()) {
            System.out.println("!! 가입한 채널이 없습니다.");
            return;
        }

        System.out.println("--- 가입한 채널 목록 ---");
        List<Channel> channelList = new ArrayList<>(joinedChannels);
        for (int i = 0; i < channelList.size(); i++) {
            System.out.printf("%d. %s\n", (i + 1), channelList.get(i).getChannelName());
        }
        System.out.print(">> 채널 선택 (번호): ");
        int chIdx = Integer.parseInt(scanner.nextLine()) - 1;

        if (chIdx < 0 || chIdx >= channelList.size()) {
            System.out.println("!! 잘못된 번호입니다.");
            return;
        }
        Channel targetChannel = channelList.get(chIdx);

        System.out.print("메시지 내용: ");
        String content = scanner.nextLine();

        Message message = messageService.sendMessage(user.getId(), targetChannel.getId(), content);
        System.out.println(">> 메시지 전송 완료: " + message);
    }

    // =================================================================
    // [메뉴 5] 유저 수정
    // =================================================================
    private static void updateUserMenu() {
        System.out.println("\n[5. 유저 수정]");
        User user = selectUser();
        if (user == null) return;

        System.out.println("1. 이름 수정  2. 이메일 수정  3. 전화번호 수정");
        System.out.print(">> 선택: ");
        String subChoice = scanner.nextLine();

        switch (subChoice) {
            case "1":
                System.out.print("새 이름: ");
                System.out.println(">> 결과: " + userService.updateNickname(user.getId(), scanner.nextLine()));
                break;
            case "2":
                System.out.print("새 이메일: ");
                System.out.println(">> 결과: " + userService.updateEmail(user.getId(), scanner.nextLine()));
                break;
            case "3":
                System.out.print("새 전화번호: ");
                System.out.println(">> 결과: " + userService.updatePhoneNumber(user.getId(), scanner.nextLine()));
                break;
            default: System.out.println("!! 잘못된 입력");
        }
    }

    // =================================================================
    // [메뉴 6] 유저 삭제
    // =================================================================
    private static void deleteUserMenu() {
        System.out.println("\n[6. 유저 삭제]");
        User user = selectUser();
        if (user == null) return;

        System.out.print("정말 삭제하시겠습니까? (y/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            System.out.println(">> 삭제됨: " + userService.deleteUser(user.getId()));
        } else {
            System.out.println(">> 취소되었습니다.");
        }
    }

    // =================================================================
    // [메뉴 7] 채널 수정
    // =================================================================
    private static void updateChannelMenu() {
        System.out.println("\n[7. 채널 수정]");
        Channel channel = selectChannel();
        if (channel == null) return;

        System.out.print("새 채널 이름: ");
        System.out.println(">> 결과: " + channelService.updateChannel(channel.getId(), scanner.nextLine()));
    }

    // =================================================================
    // [메뉴 8] 채널 삭제
    // =================================================================
    private static void deleteChannelMenu() {
        System.out.println("\n[8. 채널 삭제]");
        Channel channel = selectChannel();
        if (channel == null) return;

        System.out.print("정말 삭제하시겠습니까? (y/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            System.out.println(">> 삭제됨: " + channelService.deleteChannel(channel.getId()));
        } else {
            System.out.println(">> 취소되었습니다.");
        }
    }

    // =================================================================
    // [메뉴 9] 메시지 수정
    // =================================================================
    private static void updateMessageMenu() {
        System.out.println("\n[9. 메시지 수정]");
        User user = selectUser();
        if (user == null) return;

        List<Message> myMessages = messageService.findMessagesByAuthor(user.getId());
        if (myMessages.isEmpty()) {
            System.out.println("!! 작성한 메시지가 없습니다.");
            return;
        }

        System.out.println("--- 작성한 메시지 목록 ---");
        for (int i = 0; i < myMessages.size(); i++) {
            System.out.printf("%d. [%s] %s\n", (i + 1), myMessages.get(i).getChannel().getChannelName(), myMessages.get(i).getContent());
        }
        System.out.print(">> 수정할 메시지 선택 (번호): ");
        int msgIdx = Integer.parseInt(scanner.nextLine()) - 1;

        if (msgIdx < 0 || msgIdx >= myMessages.size()) {
            System.out.println("!! 잘못된 번호입니다.");
            return;
        }

        System.out.print("수정할 내용: ");
        String newContent = scanner.nextLine();
        System.out.println(">> 결과: " + messageService.updateMessage(myMessages.get(msgIdx).getId(), newContent));
    }

    // =================================================================
    // [메뉴 10] 메시지 삭제
    // =================================================================
    private static void deleteMessageMenu() {
        System.out.println("\n[10. 메시지 삭제]");
        User user = selectUser();
        if (user == null) return;

        List<Message> myMessages = messageService.findMessagesByAuthor(user.getId());
        if (myMessages.isEmpty()) {
            System.out.println("!! 작성한 메시지가 없습니다.");
            return;
        }

        System.out.println("--- 작성한 메시지 목록 ---");
        for (int i = 0; i < myMessages.size(); i++) {
            System.out.printf("%d. [%s] %s\n", (i + 1), myMessages.get(i).getChannel().getChannelName(), myMessages.get(i).getContent());
        }
        System.out.print(">> 삭제할 메시지 선택 (번호): ");
        int msgIdx = Integer.parseInt(scanner.nextLine()) - 1;

        if (msgIdx < 0 || msgIdx >= myMessages.size()) {
            System.out.println("!! 잘못된 번호입니다.");
            return;
        }

        System.out.println(">> 삭제됨: " + messageService.deleteMessage(myMessages.get(msgIdx).getId()));
    }

    // =================================================================
    // [메뉴 11] 유저 정보 출력
    // =================================================================
    private static void printUserInfoMenu() {
        System.out.println("\n[11. 유저 정보 출력]");
        User user = selectUser();
        if (user == null) return;

        System.out.println(">> 유저 정보:");
        System.out.println(user.toString());
    }

    // =================================================================
    // [메뉴 12] 채널 정보 출력
    // =================================================================
    private static void printChannelInfoMenu() {
        System.out.println("\n[12. 채널 정보 출력]");
        Channel channel = selectChannel();
        if (channel == null) return;

        System.out.println(">> 채널 정보:");
        System.out.println(channel.toString());
    }

    // =================================================================
    // [메뉴 13] 유저 메시지들 출력
    // =================================================================
    private static void printUserMessagesMenu() {
        System.out.println("\n[13. 유저 작성 메시지 목록]");
        User user = selectUser();
        if (user == null) return;

        List<Message> msgs = messageService.findMessagesByAuthor(user.getId());
        System.out.println(">> 작성한 메시지 수: " + msgs.size() + "개");
        if (msgs.isEmpty()) {
            System.out.println("(메시지가 없습니다)");
        } else {
            for (Message m : msgs) {
                // [채널명] 내용 (수정여부)
                String edited = m.isEdited() ? " (수정됨)" : "";
                System.out.printf(" - [%s] %s%s\n", m.getChannel().getChannelName(), m.getContent(), edited);
            }
        }
    }

    // =================================================================
    // [메뉴 14] 채널 메시지들 출력
    // =================================================================
    private static void printChannelMessagesMenu() {
        System.out.println("\n[14. 채널 내 메시지 목록]");
        Channel channel = selectChannel();
        if (channel == null) return;

        List<Message> msgs = messageService.findMessagesByChannel(channel.getId());
        System.out.println(">> 채널 메시지 수: " + msgs.size() + "개");
        if (msgs.isEmpty()) {
            System.out.println("(메시지가 없습니다)");
        } else {
            for (Message m : msgs) {
                // [작성자] 내용 (수정여부)
                String edited = m.isEdited() ? " (수정됨)" : "";
                System.out.printf(" - [%s] %s%s\n", m.getAuthor().getNickname(), m.getContent(), edited);
            }
        }
    }


    // =================================================================
    // [Helper Methods]
    // =================================================================
    private static User selectUser() {
        List<User> users = userService.findAll();
        if (users.isEmpty()) {
            System.out.println("!! 등록된 유저가 없습니다.");
            return null;
        }

        System.out.println("--- 유저 목록 ---");
        for (int i = 0; i < users.size(); i++) {
            System.out.printf("%d. %s (%s)\n", (i + 1), users.get(i).getNickname(), users.get(i).getUsername());
        }
        System.out.print(">> 유저 선택 (번호): ");
        try {
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            if (idx >= 0 && idx < users.size()) {
                return users.get(idx);
            }
        } catch (NumberFormatException e) {}

        System.out.println("!! 잘못된 선택입니다.");
        return null;
    }

    private static Channel selectChannel() {
        List<Channel> channels = channelService.findAll();
        if (channels.isEmpty()) {
            System.out.println("!! 등록된 채널이 없습니다.");
            return null;
        }

        System.out.println("--- 채널 목록 ---");
        for (int i = 0; i < channels.size(); i++) {
            System.out.printf("%d. %s (공개:%s)\n", (i + 1), channels.get(i).getChannelName(), channels.get(i).isPublic());
        }
        System.out.print(">> 채널 선택 (번호): ");
        try {
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            if (idx >= 0 && idx < channels.size()) {
                return channels.get(idx);
            }
        } catch (NumberFormatException e) {}

        System.out.println("!! 잘못된 선택입니다.");
        return null;
    }

    private static void printMenu() {
        System.out.println("\n================ 메뉴 ================");
        System.out.println("1. 유저 생성        2. 채널 생성");
        System.out.println("3. 채널 가입        4. 메시지 남기기");
        System.out.println("5. 유저 수정        6. 유저 삭제");
        System.out.println("7. 채널 수정        8. 채널 삭제");
        System.out.println("9. 메시지 수정      10. 메시지 삭제");
        System.out.println("11. 유저 정보       12. 채널 정보");
        System.out.println("13. 유저 메시지     14. 채널 메시지");
        System.out.println("0. 종료");
        System.out.println("======================================");
        System.out.print(">> 선택: ");
    }

    private static void initDummyData() {
        User u1 = userService.createUser("user1", "철수", "u1@test.com", "010-1111-1111");
        userService.createUser("user2", "영희", "u2@test.com", "010-2222-2222");
        Channel c1 = channelService.createChannel("자유게시판", true);
        channelService.createChannel("공지사항", false);
        u1.joinChannel(c1);
    }
}