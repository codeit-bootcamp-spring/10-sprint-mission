package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                SpringApplication.run(DiscodeitApplication.class, args);

        UserService userService = context.getBean(UserService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);
        BinaryContentService binaryContentService = context.getBean(BinaryContentService.class);
        ReadStatusService readStatusService = context.getBean(ReadStatusService.class);
        UserStatusService userStatusService = context.getBean(UserStatusService.class);
        AuthService authService = context.getBean(AuthService.class);

        UserRepository userRepository = context.getBean(UserRepository.class);
        ChannelRepository channelRepository = context.getBean(ChannelRepository.class);
        MessageRepository messageRepository = context.getBean(MessageRepository.class);
        BinaryContentRepository binaryContentRepository = context.getBean(BinaryContentRepository.class);
        ReadStatusRepository readStatusRepository = context.getBean(ReadStatusRepository.class);
        UserStatusRepository userStatusRepository = context.getBean(UserStatusRepository.class);

        System.out.println("[UserRepository] " + userRepository.getClass().getName());
        System.out.println("[ChannelRepository] " + channelRepository.getClass().getName());
        System.out.println("[MessageRepository] " + messageRepository.getClass().getName());
        System.out.println("[BinaryContentRepository] " + binaryContentRepository.getClass().getName());
        System.out.println("[ReadStatusRepository] " + readStatusRepository.getClass().getName());
        System.out.println("[UserStatusRepository] " + userStatusRepository.getClass().getName());
        System.out.println("=============================================================");

        runTest(
                userService,
                channelService,
                messageService,
                binaryContentService,
                readStatusService,
                userStatusService,
                authService,
                userRepository,
                channelRepository,
                messageRepository,
                binaryContentRepository,
                readStatusRepository,
                userStatusRepository
        );
    }

    private static void runTest(
            UserService userService,
            ChannelService channelService,
            MessageService messageService,
            BinaryContentService binaryContentService,
            ReadStatusService readStatusService,
            UserStatusService userStatusService,
            AuthService authService,
            UserRepository userRepository,
            ChannelRepository channelRepository,
            MessageRepository messageRepository,
            BinaryContentRepository binaryContentRepository,
            ReadStatusRepository readStatusRepository,
            UserStatusRepository userStatusRepository
    ) {
        UserResponse user1 = createUser(userService, "tester1", "tester1" + "@test.com", true);
        UserResponse user2 = createUser(userService, "tester2", "tester2" + "@test.com", false);

        User user1Entity = findUserEntity(userRepository, user1.id());
        User user2Entity = findUserEntity(userRepository, user2.id());

        System.out.println("[유저 생성] " + user1Entity);
        System.out.println("[유저 생성] " + user2Entity);

        login(authService, user1.email(), "pass1234");
        System.out.println("[로그인] " + user1Entity);

        Channel publicChannelEntity = createPublicChannel(channelService, channelRepository, "public");
        Channel privateChannelEntity = createPrivateChannel(channelService, channelRepository, List.of(user1.id(), user2.id()));

        System.out.println("[채널 생성] " + publicChannelEntity);
        System.out.println("[채널 생성] " + privateChannelEntity);

        List<Channel> user1Channels = channelService.findAllByUserId(user1.id()).stream()
                .map(resp -> channelRepository.findById(resp.id()).orElseThrow())
                .toList();

        System.out.println("[유저1 채널 목록]");
        for (Channel ch : user1Channels) {
            System.out.println("  • " + ch);
        }

        ReadStatusResponse rs1 = createReadStatus(readStatusService, user1.id(), publicChannelEntity.getId());
        ReadStatusResponse rs2 = createReadStatus(readStatusService, user2.id(), publicChannelEntity.getId());
        System.out.println("[읽음 상태 생성] id=" + rs1.id() + ", lastReadAt=" + rs1.lastReadAt());
        System.out.println("[읽음 상태 생성] id=" + rs2.id() + ", lastReadAt=" + rs2.lastReadAt());

        ReadStatusResponse rs1Updated = updateReadStatus(readStatusService, rs1.id(), Instant.now());
        System.out.println("[읽음 상태 수정] id=" + rs1Updated.id() + ", lastReadAt=" + rs1Updated.lastReadAt());

        Message message1Entity = createMessageWithAttachment(messageService, messageRepository, publicChannelEntity.getId(), user1.id(), "hello message 1");
        Message message2Entity = createMessage(messageService, messageRepository, publicChannelEntity.getId(), user1.id(), "hello message 2");

        System.out.println("[메시지 생성] " + message1Entity);
        System.out.println("[메시지 생성] " + message2Entity);

        printMessages(messageRepository, publicChannelEntity.getId(), "[공개 채널 메시지 목록]");

        Message updatedMessage1Entity = updateMessage(messageService, messageRepository, message1Entity.getId(), "hello message 1 (updated)");
        System.out.println("[메시지 수정] " + updatedMessage1Entity);

        printMessages(messageRepository, publicChannelEntity.getId(), "[공개 채널 메시지 목록 - 수정 후]");

        if (!updatedMessage1Entity.getAttachmentIds().isEmpty()) {
            int count = binaryContentService.findAllByIdIn(updatedMessage1Entity.getAttachmentIds()).size();
            System.out.println("[첨부파일 조회] count=" + count);

            int repoCount = binaryContentRepository.findAllByIdIn(updatedMessage1Entity.getAttachmentIds()).size();
            System.out.println("[첨부파일 조회 - repository] count=" + repoCount);
        }

        Channel updatedPublicChannelEntity = updatePublicChannel(channelService, channelRepository, publicChannelEntity.getId(), "public" + "-updated");
        System.out.println("[공개 채널 수정] " + updatedPublicChannelEntity);

        UserResponse user2Updated = updateUserNickname(userService, user2.id(), "tester2" + "-updated");
        User user2UpdatedEntity = findUserEntity(userRepository, user2Updated.id());
        System.out.println("[유저 수정] " + user2UpdatedEntity);

        var touched = userStatusService.updateByUserId(user1.id());
        UserStatus userStatusEntity = userStatusRepository.findById(touched.id()).orElseThrow();
        System.out.println("[유저 상태 갱신] id=" + userStatusEntity.getId() + ", status=" + userStatusEntity.getStatus().getUserStatus());

        userStatusService.update(new UserStatusUpdateRequest(touched.id(), UserStatus.Status.ONLINE));
        UserStatus userStatusEntity2 = userStatusRepository.findById(touched.id()).orElseThrow();
        System.out.println("[유저 상태 변경] id=" + userStatusEntity2.getId() + ", status=" + userStatusEntity2.getStatus().getUserStatus());

        System.out.println("[완료] public=" + publicChannelEntity.getId() + " private=" + privateChannelEntity.getId());
    }

    private static UserResponse createUser(UserService userService, String nickname, String email, boolean withProfile) {
        BinaryContentCreateRequest profile = null;
        if (withProfile) {
            profile = new BinaryContentCreateRequest(
                    "profile.txt",
                    "text/plain",
                    ("profile-" + nickname).getBytes(StandardCharsets.UTF_8)
            );
        }

        UserCreateRequest request = new UserCreateRequest(
                email,
                "pass1234",
                nickname,
                profile
        );

        return userService.create(request);
    }

    private static void login(AuthService authService, String email, String password) {
        LoginRequest request = new LoginRequest(email, password);
        authService.login(request);
    }

    private static Channel createPublicChannel(ChannelService channelService, ChannelRepository channelRepository, String name) {
        PublicChannelCreateRequest request = new PublicChannelCreateRequest(
                name,
                "test channel"
        );

        ChannelResponse response = channelService.create(request);
        return channelRepository.findById(response.id()).orElseThrow();
    }

    private static Channel createPrivateChannel(ChannelService channelService, ChannelRepository channelRepository, List<UUID> userIds) {
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(userIds);
        ChannelResponse response = channelService.create(request);
        return channelRepository.findById(response.id()).orElseThrow();
    }

    private static ReadStatusResponse createReadStatus(ReadStatusService readStatusService, UUID userId, UUID channelId) {
        ReadStatusCreateRequest request = new ReadStatusCreateRequest(userId, channelId);
        return readStatusService.create(request);
    }

    private static ReadStatusResponse updateReadStatus(ReadStatusService readStatusService, UUID readStatusId, Instant lastReadAt) {
        ReadStatusUpdateRequest request = new ReadStatusUpdateRequest(readStatusId, lastReadAt);
        return readStatusService.update(request);
    }

    private static Message createMessage(MessageService messageService, MessageRepository messageRepository, UUID channelId, UUID authorId, String content) {
        MessageCreateRequest request = new MessageCreateRequest(
                channelId,
                authorId,
                content,
                null
        );

        MessageResponse response = messageService.create(request);
        return messageRepository.findById(response.id()).orElseThrow();
    }

    private static Message createMessageWithAttachment(MessageService messageService, MessageRepository messageRepository, UUID channelId, UUID authorId, String content) {
        BinaryContentCreateRequest attachment = new BinaryContentCreateRequest(
                "hello.txt",
                "text/plain",
                "hello discodeit".getBytes(StandardCharsets.UTF_8)
        );

        MessageCreateRequest request = new MessageCreateRequest(
                channelId,
                authorId,
                content,
                List.of(attachment)
        );

        MessageResponse response = messageService.create(request);
        return messageRepository.findById(response.id()).orElseThrow();
    }

    private static Message updateMessage(MessageService messageService, MessageRepository messageRepository, UUID messageId, String newContent) {
        MessageUpdateRequest request = new MessageUpdateRequest(
                messageId,
                newContent,
                null
        );

        MessageResponse response = messageService.update(request);
        return messageRepository.findById(response.id()).orElseThrow();
    }

    private static Channel updatePublicChannel(ChannelService channelService, ChannelRepository channelRepository, UUID channelId, String newName) {
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest(
                channelId,
                newName,
                null
        );

        ChannelResponse response = channelService.update(request);
        return channelRepository.findById(response.id()).orElseThrow();
    }

    private static UserResponse updateUserNickname(UserService userService, UUID userId, String newNickname) {
        UserUpdateRequest request = new UserUpdateRequest(
                userId,
                null,
                newNickname,
                null
        );

        return userService.update(request);
    }

    private static void printMessages(MessageRepository messageRepository, UUID channelId, String title) {
        System.out.println(title);
        List<Message> messages = messageRepository.findAllByChannelId(channelId);
        for (Message message : messages) {
            System.out.println("  • " + message);
        }
    }

    private static User findUserEntity(UserRepository userRepository, UUID userId) {
        return userRepository.findById(userId).orElseThrow();
    }
}
