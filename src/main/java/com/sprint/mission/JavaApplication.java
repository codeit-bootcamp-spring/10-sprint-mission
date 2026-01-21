package com.sprint.mission;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ChannelUserRoleRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.*;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.basic.*;

import java.io.File;
import java.util.*;

import java.io.File;
import java.util.*;

public class JavaApplication {
    private static final String DATA_DIR = "discodeit.data";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // 기존 데이터 삭제 여부 확인
        System.out.print("기존 데이터 파일을 삭제하고 시작하시겠습니까? (y/n): ");
        String choice = sc.nextLine().trim();
        if ("y".equalsIgnoreCase(choice)) {
            deleteDataFiles();
        } else {
            System.out.println("기존 데이터를 유지하고 시작합니다.");
        }

        printSection("[discodeit] 서비스 기능 테스트 시작 (Basic Service + File Repository Mode)");
        boolean running = true;

        // Dependency Injection
        UserRepository userRepository = new FileUserRepository();
        ChannelRepository channelRepository = new FileChannelRepository();
        MessageRepository messageRepository = new FileMessageRepository();
        ChannelUserRoleRepository channelUserRoleRepository = new FileChannelUserRoleRepository();

        UserService userService = new BasicUserService(userRepository);
        ChannelService channelService = new BasicChannelService(channelRepository);

        MessageService messageService = new BasicMessageService(
                messageRepository,
                userRepository,
                channelRepository,
                channelUserRoleRepository
        );

        ChannelUserRoleService channelUserRoleService = new BasicChannelUserRoleService(
                channelUserRoleRepository,
                userRepository,
                channelRepository
        );


        // 리스너 등록 (기존과 동일)
        userService.addListener((UUID userId) -> {
            channelService.deleteChannelsByOwnerId(userId);
            messageService.deleteAllMessagesByUserId(userId);
            channelUserRoleService.deleteAllAssociationsByUserId(userId);
        });

        channelService.addListener((UUID channelId) -> {
            messageService.deleteAllMessagesByChannelId(channelId);
            channelUserRoleService.deleteAllAssociationsByChannelId(channelId);
        });

        // 메인 루프 (기존과 동일)
        while (running) {
            System.out.println("\n\t\t\t\t\t----------------------------------");
            System.out.println("\t\t\t\t\t|   📌[discodeit] 테스트 메뉴 선택📌  |");
            System.out.println("\t\t\t\t\t| 1. User 도메인 테스트               |");
            System.out.println("\t\t\t\t\t| 2. Channel 도메인 테스트            |");
            System.out.println("\t\t\t\t\t| 3. Message 도메인 테스트            |");
            System.out.println("\t\t\t\t\t| 4. ChannelUser(참여자) 도메인 테스트  |");
            System.out.println("\t\t\t\t\t| 0. 종료                           |");
            System.out.print("\t\t\t\t\t>> 선택할 번호를 입력하세요: ");

            String input = sc.nextLine();

            try {
                switch (input) {
                    case "1":
                        testUserDomain(userService, channelService, messageService, channelUserRoleService);
                        break;
                    case "2":
                        testChannelDomain(userService, channelService, channelUserRoleService);
                        break;
                    case "3":
                        testMessageDomain(messageService, channelService, userService, channelUserRoleService);
                        break;
                    case "4":
                        testChannelUserRoleDomain(channelUserRoleService, channelService, userService);
                        break;
                    case "0":
                        System.out.println("테스트를 종료합니다.");
                        running = false;
                        break;
                    default:
                        System.out.println("⚠️잘못된 입력입니다. 다시 선택해주세요.⚠️");
                }
            } catch (Exception e) {
                e.printStackTrace(); // 디버깅용
                System.out.println("\n⚠️테스트 실행 중 오류 발생: " + e.getMessage());
            }
        }
        sc.close();
    }

    // 데이터 파일 삭제
    private static void deleteDataFiles() {
        File dir = new File(DATA_DIR);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".ser")) {
                        if (file.delete()) {
                            System.out.println("[삭제됨] " + file.getName());
                        }
                    }
                }
            }
            System.out.println("기존 데이터 파일 삭제 완료.");
        } else {
            System.out.println("삭제할 데이터 폴더가 존재하지 않습니다.");
        }
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

        // [2] 조회 (단건, 다건) / 조회 (Read)
        System.out.println("2) 조회(단건, 다건) / [Read]");
        User foundUser = userService.findUserByUserId(testUser1.getId());
        System.out.println("\t-> [특정 유저 조회] username: " + foundUser.getUsername() + " (id: " + testUser1.getId() + ")");

        System.out.println("\t\t---전체 유저 조회를 위한 유저 생성---");
        User testUser2 = userService.createUser("2번_유저"); printUserCreated(testUser2);
        User testUser3 = userService.createUser("3번_유저"); printUserCreated(testUser3);
        User testUser4 = userService.createUser("4번_유저"); printUserCreated(testUser4);
        List<User> allUsers = userService.findAllUsers();  // TODO: 순서 보장되도록 수정
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
        System.out.println("\t-> [이름 수정 완료] username: " + updatedUser1.getUsername() + " (id: " + updatedUser1.getId() + ")");

        // [5] 삭제
        System.out.println("5) 삭제 / 유저 삭제 / [Delete]");

        System.out.println("\t--- [삭제 검증을 위한 데이터 준비] ---");
        // 1 유저가 소유한 채널 생성 (유저 삭제 시 이 채널도 삭제되어야 함)
        Channel user1Channel = channelService.createChannel("1번_유저_이름바꿈_채널", updatedUser1);
        System.out.println("\t(준비1 - 유저 소유 채널 생성) channelName: " + user1Channel.getChannelName()
                + "\n\t\t(channelId: " + user1Channel.getId() + ")"
                + "\n\t\t(channelName: " + user1Channel.getChannelName() + ")"
        );

        // 2. 유저가 해당 채널에 참여 (메시지를 쓰기 위함)
        ChannelUserRole createdRole =
                channelUserRoleService.addChannelUser(user1Channel.getId(), updatedUser1.getId(), ChannelRole.OWNER);

        System.out.println("\t(준비2 - 채널 참여 완료) " + updatedUser1.getUsername()
                + " -> " + user1Channel.getChannelName() + "\n\t\t(channelId: " + user1Channel.getId() + ")"
                + "\n\t\t(userRole: " + ChannelRole.OWNER + ")");

        // 3. 유저가 메시지 작성 (유저 삭제/채널 삭제 시 이 메시지도 삭제되어야 함)
        Message user1Msg = messageService.createMessage("OWNER 삭제되면 이 메시지도 사라지나요?", updatedUser1.getId(), user1Channel.getId());
        System.out.println("\t(준비3 - 유저가 메시지 작성 완료)");
        System.out.println("\t\tusername: " + updatedUser1.getUsername()
                + "\n\t\tcontent: " + user1Msg.getContent()
                + "\n\t\tmessage-id: " + user1Msg.getId()
                + "\n\t\tmessage-updatedAt: " + user1Msg.getUpdatedAt() );
        System.out.println("\t----------------------------------");

        userService.deleteUser(testUser1.getId()); // 실행흐름 (1)

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
                + "\n\t\t(channelId: " + channel.getId() + ")"
                + "\n\t\t(ownerId: " + channel.getOwner().getId() + ")");
    }
    private static void testChannelDomain(UserService userService,
                                          ChannelService channelService,
                                          ChannelUserRoleService channelUserRoleService) {
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
        // 채널 생성 시 자동 채널 관계 참여로 설정 (추후 구현)
        channelUserRoleService.addChannelUser(testChannel1.getId(), testOwner1.getId(), ChannelRole.OWNER);

        printChannelCreated(testChannel1);

        // [2] 조회
        System.out.println("2) 조회(단건, 다건) / [Read]");
        Channel foundChannel = channelService.findChannelById(testChannel1.getId());
        System.out.println("\t-> [특정 채널 조회] channelName: " + foundChannel.getChannelName() + " (id: " + foundChannel.getId() + ")");

        System.out.println("\t\t---전체 채널 조회를 위한 유저 및 채널 생성---");
        User testOwner2 = userService.createUser("Owner_testUser2"); printUserCreated(testOwner2);
        User testOwner3 = userService.createUser("Owner_testUser3"); printUserCreated(testOwner3);
        Channel testChannel2 = channelService.createChannel("testOwner2의 채널", testOwner2); printChannelCreated(testChannel2);
        Channel testChannel3 = channelService.createChannel("testOwner3의 채널", testOwner3); printChannelCreated(testChannel3);
        List<Channel> allChannels = channelService.findAllChannels();
        System.out.println("\t\t-> [전체 채널 조회]: " + allChannels.size() + "개");
        for(Channel ch : allChannels){
            System.out.println("\tchannelName: " + ch.getChannelName() + " (id: " + ch.getId() + ")");
        }

        // [3] 수정
        System.out.println("3) 수정 / 채널 이름 수정 / [Update]");
        channelService.updateChannel(testChannel1.getId(), "testOwner1의 채널_이름 수정됨");

        // [4] 수정된 데이터 조회
        System.out.println("4) 수정된 데이터 조회");
        Channel updatedChannel = channelService.findChannelById(testChannel1.getId());
        System.out.println("\t-> [채널 이름 수정 완료] channelName: " + updatedChannel.getChannelName()
                + "\n\t(channelId: " + updatedChannel.getId() + ")");

        // [5] 삭제
        System.out.println("5) 삭제 / 채널 삭제 / [Delete]");
        channelService.deleteChannel(testChannel1.getId());
        System.out.println("\t-> 채널 삭제 완료");

        // [6-1] 특정 채널 조회를 통한 삭제 확인
        System.out.println("6) 조회를 통해 삭제되었는지 확인");
        try {
            channelService.findChannelById(testChannel1.getId());
            System.out.println("\t-> [실패] 삭제되지 않음.");
        } catch (IllegalArgumentException e) {
            System.out.println("\t-> [성공] 조회 실패 (예상된 에러: " + e.getMessage() + ")");
        }
        // [6-2] 전체 채널 조회를 통한 삭제 확인
        allChannels = channelService.findAllChannels();
        System.out.println("\t\t-> [전체 채널 조회]: " + allChannels.size() + "개");
        for(Channel ch : allChannels){
            System.out.println("\tchannelName: " + ch.getChannelName() + " (id: " + ch.getId() + ")");
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

    // =================================================================
    // 3. Message 도메인 테스트
    // =================================================================
    private static void printMessageCreated(Message message) {
        System.out.println("\t-> [메시지 생성 완료] messageContent: " + message.getContent()
                + "\n\t\t(messageId: " + message.getId()+ ")"
                + "\n\t\t(userId: " + message.getSender().getId() + ")"
                + "\n\t\t(channelId: " + message.getChannel().getId() + ")");
    }
    private static void testMessageDomain(MessageService messageService, ChannelService channelService, UserService userService, ChannelUserRoleService channelUserRoleService) {
        printSection("3. Message 서비스 테스트");

        // (선행조건) 메시지 전송을 위한 User와 Channel 필요
        System.out.println("0) 사전 작업");
        User testSender1 = userService.createUser("MEMBER_User8"); printUserCreated(testSender1);
        User testOwner1 = userService.createUser("OWNER_User9"); printUserCreated(testOwner1); // 방장
        Channel testChannel1 = channelService.createChannel("Free-Topic", testOwner1); printChannelCreated(testChannel1);
        // 채널 생성 시 자동 채널 관계 참여로 설정 (추후 구현)
        channelUserRoleService.addChannelUser(testChannel1.getId(), testOwner1.getId(), ChannelRole.OWNER);
        channelUserRoleService.addChannelUser(testChannel1.getId(), testSender1.getId(), ChannelRole.MEMBER);

        // [1] 등록 (내용, 작성자ID, 채널ID)
        System.out.println("1) 메시지 등록");
        Message testMsg1 = messageService.createMessage("안녕하세요1!", testSender1.getId(), testChannel1.getId());
        printMessageCreated(testMsg1);

        // [2] 조회
        System.out.println("2) 조회");
        // [2-1] 특정 메시지 단건 조회
        Message foundMsg = messageService.findMessageById(testMsg1.getId());
        System.out.println("\t-> 단건 조회: " + foundMsg.getContent());
        // [2-2] 특정 채널의 전체 메시지 조회
        System.out.println("\t\t---특정 채널의 전체 메시지 조회를 위한 유저 및 채널 생성---");
        Message testMsg2 = messageService.createMessage("안녕하세요2!", testSender1.getId(), testChannel1.getId());
        printMessageCreated(testMsg2);
        Message testMsg3 = messageService.createMessage("안녕하세요3!", testSender1.getId(), testChannel1.getId());
        printMessageCreated(testMsg3);

        List<Message> channelMsgs = messageService.findAllMessagesByChannelId(testChannel1.getId());
        System.out.println("\t\t-> [특정 채널 내 전체 메시지 조회](채널 내 메시지 수): " + channelMsgs.size() + "개");
        for(Message msg : channelMsgs){
            System.out.println("\tmessageContent: " + msg.getContent() + " (id: " + msg.getId() + ")");
        }
        // [2-3] 특정 유저가 작성한 전체 메시지 조회
        // [2-4] 특정 유저가 특정 채널에서 보낸 메시지 조회

        // [3] 수정
        System.out.println("3) 수정");
        messageService.updateMessage(testMsg1.getId(), "안녕하세요1! (수정됨)");

        // [4] 수정 확인
        System.out.println("4) 수정 결과 확인");
        Message updatedMsg = messageService.findMessageById(testMsg1.getId());
        System.out.println("\t-> 변경된 메시지 내용: \"" + updatedMsg.getContent() + "\"");

        // [5] 삭제
        System.out.println("5) 삭제");
        messageService.deleteMessage(testMsg1.getId());
        System.out.println("\t-> 삭제 완료");

        // [6] 삭제 확인
        System.out.println("6) 삭제 확인 (조회 시도)");
        try {
            messageService.findMessageById(testMsg1.getId());
            System.out.println("\t-> [실패] 삭제되지 않음.");
        } catch (IllegalArgumentException e) {
            System.out.println("\t-> [성공] 조회 실패 (예상된 에러: " + e.getMessage() + ")");
        }

        // =================================================================

        // [Unhappy Path]
        printSubSection("Message 서비스 테스트 - Unhappy Path");

        // 1. 없는 메시지 조회
        System.out.print("Test 1) 없는 메시지 ID 조회: ");
        try {
            messageService.findMessageById(UUID.randomUUID());
            System.out.println("실패");
        } catch (IllegalArgumentException e) { // 혹은 IllegalStateException
            System.out.println("성공 (방어: " + e.getMessage() + ")");
        }

        // 2. 없는 메시지 수정
        System.out.print("Test 2) 없는 메시지 ID 수정: ");
        try {
            messageService.updateMessage(UUID.randomUUID(), "New Content");
            System.out.println("실패");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("성공 (방어: " + e.getMessage() + ")");
        }

        // 3. 없는 메시지 삭제
        System.out.print("Test 3) 없는 메시지 ID 삭제 시도: ");
        try {
            messageService.deleteMessage(UUID.randomUUID());
            System.out.println("실패 (예외가 발생하지 않음)");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("성공 (방어: " + e.getMessage() + ")");
        }

        // 4. 메시지 중복 생성 (-> 메시지는 String content를 인자로 받으므로 같은 Id 중복 생성 테스트 불가능)

        printSubSection("Message 서비스 테스트 - 예외 케이스");

        // 1. 없는 유저가 메시지 전송
        System.out.print("Test 1) 존재하지 않는 유저로 전송 시도: ");
        try {
            messageService.createMessage("Ghost Message", UUID.randomUUID(), testChannel1.getId());
            System.out.println("실패 (유령 회원이 글을 씀)");
        } catch (IllegalArgumentException e) {
            System.out.println("성공 (방어: " + e.getMessage() + ")");
        }

        // 2. 없는 채널에 메시지 전송
        System.out.print("Test 2) 존재하지 않는 채널로 전송 시도: ");
        try {
            messageService.createMessage("Void Message", testSender1.getId(), UUID.randomUUID());
            System.out.println("실패 (채널 없이 글을 씀)");
        } catch (IllegalArgumentException e) {
            System.out.println("성공 (방어: " + e.getMessage() + ")");
        }

        // 3. 특정 유저가 동일한 메시지 내용 전송
        System.out.println("Test 3) 동일한 내용 연속 전송 확인: ");

        Message msgA = messageService.createMessage("안녕하세요", testSender1.getId(), testChannel1.getId());
        printMessageCreated(msgA);
        Message msgB = messageService.createMessage("안녕하세요", testSender1.getId(), testChannel1.getId());
        printMessageCreated(msgB);

        // 검증 1: 둘 다 저장이 잘 되었는가?
        // 검증 2: 둘의 ID가 다른가? (별개의 객체인가?)
        if (!msgA.getId().equals(msgB.getId())) {
            System.out.println("성공 (내용은 같지만 서로 다른 메시지(id)로 잘 저장됨)");
        } else {
            System.out.println("실패 (id가 같음)");
        }

        // 4. 다른 유저가 동일한 메시지 내용 전송
        // 예외 케이스
        // [ ] 추가 필요
    }

    // =================================================================
    // 4. ChannelUserRole(채널-유저 관계 + Role) 도메인 테스트
    // =================================================================
    private static void testChannelUserRoleDomain(ChannelUserRoleService channelUserRoleService,
                                                  ChannelService channelService,
                                                  UserService userService) {
        printSection("4. ChannelUserRole 서비스 테스트");

        // [0] 테스트 데이터 준비
        System.out.println("0) 데이터 준비");
        User owner = userService.createUser("방장_유저");
        User user1 = userService.createUser("참여자1");
        User user2 = userService.createUser("참여자2");
        User outsider = userService.createUser("외부인"); // 채널에 참여하지 않을 유저

        printUserCreated(owner);
        printUserCreated(user1);
        printUserCreated(user2);

        Channel channel1 = channelService.createChannel("테스트채널1", owner);
        printChannelCreated(channel1);

        // === [Happy Path] ===
        System.out.println("4.1 Happy Path");

        // [1] 등록 (Create) - 채널 입장
        System.out.println("1) 등록 / 채널 입장 / [Create]");
        // 방장 입장 (Role: OWNER)
        channelUserRoleService.addChannelUser(channel1.getId(), owner.getId(), ChannelRole.OWNER);
        // 유저1 입장 (Role: MEMBER)
        channelUserRoleService.addChannelUser(channel1.getId(), user1.getId(), ChannelRole.MEMBER);
        // 유저2 입장 (Role: MEMBER)
        channelUserRoleService.addChannelUser(channel1.getId(), user2.getId(), ChannelRole.MEMBER);

        System.out.println("\t-> [채널 입장 완료] "
                + "\n\t\t- " + owner.getUsername() + " (OWNER)"
                + "\n\t\t- " + user1.getUsername() + " (MEMBER)"
                + "\n\t\t- " + user2.getUsername() + " (MEMBER)");

        // [2] 조회 (Read)
        System.out.println("2) 조회(단건, 다건) / [Read]");

        // [2-1] 단건 조회 (findChannelUser) - 특정 유저의 권한 확인
        ChannelUserRole foundRole = channelUserRoleService.findChannelUser(channel1.getId(), user1.getId());
        System.out.println("\t-> [단건 조회] 유저: " + foundRole.getUser().getUsername()
                + ", 현재 권한: " + foundRole.getChannelRole());

        // [2-2] 채널 내 참여자 목록 조회 (findUsersByChannelId)
        List<User> participants = channelUserRoleService.findUsersByChannelId(channel1.getId());
        System.out.println("\t-> [채널 참여자 목록 조회] 총 " + participants.size() + "명");
        for (User u : participants) {
            System.out.println("\t\t- " + u.getUsername());
        }

        // [2-3] 유저가 참여 중인 채널 목록 조회 (findChannelsByUserId)
        System.out.println("\t-> [특정 유저의 가입 채널 목록 조회]");
        // 테스트를 위해 채널 하나 더 생성 및 참여
        Channel channel2 = channelService.createChannel("테스트채널2", owner);
        channelUserRoleService.addChannelUser(channel2.getId(), user1.getId(), ChannelRole.MEMBER); // user1이 channel2에도 참여

        List<Channel> myChannels = channelUserRoleService.findChannelsByUserId(user1.getId());
        System.out.println("\t\t[" + user1.getUsername() + "]님은 총 " + myChannels.size() + "개의 채널에 참여 중입니다.");
        for (Channel ch : myChannels) {
            System.out.println("\t\t- " + ch.getChannelName());
        }

        // [3] 수정 (Update) - 권한 변경
        System.out.println("3) 수정 / 권한 변경 / [Update]");
        // user1의 권한을 MEMBER -> ADMIN으로 변경
        channelUserRoleService.updateChannelRole(channel1.getId(), user1.getId(), ChannelRole.ADMIN);

        // 수정 확인
        ChannelUserRole updatedRole = channelUserRoleService.findChannelUser(channel1.getId(), user1.getId());
        System.out.println("\t-> [권한 변경 확인] " + updatedRole.getUser().getUsername()
                + " : " + updatedRole.getChannelRole());

        // [4] 삭제 (Delete) - 채널 탈퇴
        System.out.println("4) 삭제 / 채널 탈퇴 / [Delete]");
        // user2 탈퇴
        channelUserRoleService.deleteChannelUserAssociation(channel1.getId(), user2.getId());
        System.out.println("\t-> [탈퇴 완료] " + user2.getUsername() + "가 " + channel1.getChannelName() + "에서 나갔습니다.");

        // [5] 삭제 확인
        System.out.println("5) 삭제 확인");
        try {
            channelUserRoleService.findChannelUser(channel1.getId(), user2.getId());
            System.out.println("\t-> [실패] 탈퇴한 유저가 여전히 조회됨.");
        } catch (IllegalArgumentException e) {
            System.out.println("\t-> [성공] 조회 실패 (예상된 에러: " + e.getMessage() + ")");
        }

        // 유저의 가입 채널 목록에서도 사라졌는지 확인
        List<Channel> user2Channels = channelUserRoleService.findChannelsByUserId(user2.getId());
        boolean isStillJoined = user2Channels.stream().anyMatch(ch -> ch.getId().equals(channel1.getId()));
        if (!isStillJoined) {
            System.out.println("\t-> [성공] 유저의 가입 채널 목록에서도 정상적으로 제거됨.");
        } else {
            System.out.println("\t-> [실패] 유저의 가입 채널 목록에 데이터가 남아있음.");
        }


        // === [Unhappy Path] ===
        System.out.println("4.2 Unhappy Path");

        // 1. 참여하지 않은 유저 조회
        System.out.print("Test 1) 참여하지 않은 유저 조회: ");
        try {
            channelUserRoleService.findChannelUser(channel1.getId(), outsider.getId());
            System.out.println("실패 (예외가 발생하지 않음)");
        } catch (IllegalArgumentException e) {
            System.out.println("성공 (방어: " + e.getMessage() + ")");
        }

        // 2. 참여하지 않은 유저 권한 수정
        System.out.print("Test 2) 참여하지 않은 유저 권한 수정: ");
        try {
            channelUserRoleService.updateChannelRole(channel1.getId(), outsider.getId(), ChannelRole.ADMIN);
            System.out.println("실패 (예외가 발생하지 않음)");
        } catch (IllegalArgumentException e) {
            System.out.println("성공 (방어: " + e.getMessage() + ")");
        }

        // 3. 참여하지 않은 유저 탈퇴(삭제) 시도
        System.out.print("Test 3) 참여하지 않은 유저 탈퇴 시도: ");
        try {
            channelUserRoleService.deleteChannelUserAssociation(channel1.getId(), outsider.getId());
            System.out.println("실패 (예외가 발생하지 않음)");
        } catch (IllegalArgumentException e) {
            System.out.println("성공 (방어: " + e.getMessage() + ")");
        }

        // 4. 중복 가입 시도
        System.out.print("Test 4) 중복 가입 시도: ");
        try {
            // user1은 이미 channel1에 있음
            channelUserRoleService.addChannelUser(channel1.getId(), user1.getId(), ChannelRole.MEMBER);
            System.out.println("실패 (중복 가입이 허용됨)");
        } catch (IllegalArgumentException e) {
            System.out.println("성공 (방어: " + e.getMessage() + ")");
        }
    }

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