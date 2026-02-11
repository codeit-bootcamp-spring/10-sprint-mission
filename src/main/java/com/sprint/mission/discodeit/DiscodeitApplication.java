package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicAuthService;
import com.sprint.mission.discodeit.service.basic.BasicReadStatusService;
import com.sprint.mission.discodeit.service.basic.BasicUserStatusService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

        UserService userService = context.getBean(UserService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);
        BasicAuthService authService = context.getBean(BasicAuthService.class);
        BasicReadStatusService readStatusService = context.getBean(BasicReadStatusService.class);
        BasicUserStatusService userStatusService = context.getBean(BasicUserStatusService.class);

        DiscodeitApplication app = context.getBean(DiscodeitApplication.class);

        System.out.println("\n=== 정상 흐름 테스트 ===");
        app.runTest(userService, channelService, messageService, authService, readStatusService, userStatusService);

        System.out.println("\n=== 유효성 검증 실패 테스트 ===");
        app.runValidationTest(userService, channelService, messageService);
    }

    public void runTest(UserService us, ChannelService cs, MessageService ms,
                        BasicAuthService as, BasicReadStatusService rss, BasicUserStatusService uss) {
        // 1. 데이터 등록
        UserResponse u1 = us.createUser(new UserCreateRequest("최현호", "abc123@gmail.com", "password"));
        UserResponse u2 = us.createUser(new UserCreateRequest("김민교", "qqq123@gmail.com", "password"));
        ChannelResponse c1 = cs.createChannel(new ChannelCreateRequest("채팅", "chat"));
        ChannelResponse c2 = cs.createChannel(new ChannelCreateRequest("음성", "voice"));
        ChannelResponse c3 = cs.createChannel(new ChannelCreateRequest("잡담", "chat"));
        // Private 채널 추가
        ChannelResponse c4 = cs.createChannel(new ChannelCreateRequest("비밀방", "비밀 대화방", List.of(u1.getId(), u2.getId())));

        // 2. 채널 입장 및 메시지 발송
        List.of(c1, c2).forEach(c -> { 
            cs.enterChannel(u1.getId(), c.getId()); 
            cs.enterChannel(u2.getId(), c.getId()); 
        });
        cs.enterChannel(u2.getId(), c3.getId());


        MessageResponse m1 = ms.createMessage(new MessageCreateRequest(c1.getId(), u1.getId(), "안녕하세요"));
        MessageResponse m2 = ms.createMessage(new MessageCreateRequest(c2.getId(), u2.getId(), "반갑습니다"));
        ms.createMessage(new MessageCreateRequest(c1.getId(), u2.getId(), "교이루~"));
        ms.createMessage(new MessageCreateRequest(c4.getId(), u1.getId(), "비밀 메시지"));

        // 3. Auth, ReadStatus, UserStatus 테스트
        System.out.println("\n--- Auth & Status 테스트 ---");
        uss.create(new UserStatusCreateRequest(u1.getId()));
        uss.create(new UserStatusCreateRequest(u2.getId()));

        // Login
        UserResponse loggedInUser = as.login(new LoginRequest(u1.getName(), "password"));
        System.out.println("로그인 유저: " + loggedInUser.getName() + ", Online: " + loggedInUser.isOnline());
        
        // ReadStatus 생성 및 업데이트
        ReadStatusResponse rs1 = rss.createStatus(new ReadStatusCreateRequest(u1.getId(), c1.getId()));
        System.out.println("읽기 상태 생성: 채널 " + c1.getName() + " -> " + rs1.getLastReadAt());
        
        // 잠시 대기 후 업데이트
        try { Thread.sleep(100); } catch (InterruptedException e) {}
        
        rss.updateStatus(new ReadStatusUpdateRequest(rs1.getId()));
        System.out.println("읽기 상태 업데이트 완료");

        uss.updateByUserId(u1.getId());
        System.out.println("유저 상태(LastSeen) 업데이트 완료");

        as.logout();
        System.out.println("로그아웃 완료");


        printAllStatus("초기 데이터", us, cs, ms);

        // 4. 데이터 수정
        us.updateUser(new UserUpdateRequest(u1.getId(), "김현호", "def123@gmail.com"));
        cs.updateChannel(new ChannelUpdateRequest(c1.getId(), "소통", "PUBLIC", "voice"));
        ms.updateMessage(new MessageUpdateRequest(m1.getId(), "하이요"));
        printAllStatus("수정 후 데이터", us, cs, ms);

        // 5. 삭제 및 확인
        ms.deleteMessage(m1.getId());
        cs.deleteChannel(c1.getId());
        us.deleteUser(u1.getId());
        cs.leaveChannel(u2.getId(), c2.getId());

        printAllStatus("최종 삭제 확인", us, cs, ms);
    }

    public void printAllStatus(String title, UserService us, ChannelService cs, MessageService ms) {
        System.out.println("\n=== [" + title + "] ===");
        System.out.println("Users: " + us.getAllUsers());
        System.out.println("Channels: " + cs.getAllChannels());
        System.out.println("Messages: " + ms.getAllMessages());
    }

    public void runValidationTest(UserService userService, ChannelService channelService, MessageService messageService) {
        try {
            System.out.print("[테스트 1] 이름 없이 유저 생성: ");
            userService.createUser(new UserCreateRequest("", "test@test.com", "pass"));
        } catch (IllegalArgumentException e) {
            System.out.println("성공 (" + e.getMessage() + ")");
        }

        try {
            System.out.print("[테스트 2] 존재하지 않는 ID로 채널 조회: ");
            channelService.getChannel(UUID.randomUUID());
        } catch (IllegalArgumentException e) {
            System.out.println("성공 (" + e.getMessage() + ")");
        }

        try {
            System.out.print("[테스트 3] 저장되지 않은 유저 ID로 메시지 생성: ");
            ChannelResponse channel = channelService.createChannel(new ChannelCreateRequest("테스트채널", "chat"));
            messageService.createMessage(new MessageCreateRequest(channel.getId(), UUID.randomUUID(), "안녕"));
        } catch (IllegalArgumentException e) {
            System.out.println("성공 (" + e.getMessage() + ")");
        }

        try {
            System.out.print("[테스트 4] 빈 메시지 내용으로 생성: ");
            UserResponse user = userService.createUser(new UserCreateRequest("테스터", "tester@test.com", "pass"));
            ChannelResponse channel = channelService.getAllChannels().get(0);
            messageService.createMessage(new MessageCreateRequest(channel.getId(), user.getId(), "  "));
        } catch (IllegalArgumentException e) {
            System.out.println("성공 (" + e.getMessage() + ")");
        }

        try {
            System.out.print("[테스트 5] 삭제된 유저 수정 시도: ");
            UserResponse user = userService.createUser(new UserCreateRequest("삭제될사람", "delete@test.com", "pass"));
            userService.deleteUser(user.getId());
            userService.updateUser(new UserUpdateRequest(user.getId(), "새이름", null));
        } catch (IllegalArgumentException e) {
            System.out.println("성공 (" + e.getMessage() + ")");
        }
    }
}
