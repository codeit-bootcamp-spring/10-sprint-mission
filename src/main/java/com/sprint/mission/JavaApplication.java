package com.sprint.mission;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.UUID;
//
//public class JavaApplication {
//    public static void main(String[] args) {
//
//        JCFUserService jcfUserService = new JCFUserService();
//        JCFMessageService jcfMessageService = new JCFMessageService(jcfUserService);
//        JCFChannelService jcfChannelService = new JCFChannelService(jcfMessageService, jcfUserService);
//        // fileService
//        FileUserService fileUserService = new FileUserService();
//
//        // 🔑 순환 고리 연결
//        jcfMessageService.setChannelService(jcfChannelService);
//        jcfUserService.setChannelService(jcfChannelService);
//        // fileService
//        fileUserService.setChannelService(jcfChannelService);
//
//        System.out.println("------------------- 유저 서비스 테스트 -------------------");
//        System.out.println();
//
//        // UserService 테스트
//        User alice = jcfUserService.createUser("Alice", "alice@gmail.com", "1234");
//        User aliceFile = fileUserService.createUser("Alice", "alice@gmail.com", "1234");
//        System.out.println("Alice 추가 " + jcfUserService.getUserList());
//        jcfUserService.updateUserName(alice.getId(), "Bob");
//        System.out.println("Alice -> Bob 변경 " + jcfUserService.getUserList());
//
//        UUID userId = alice.getId();
//        System.out.println("변경된 Bob의 id: " + userId);
//
//        jcfUserService.deleteUser(userId);
//        System.out.println("Bob 삭제 " + jcfUserService.getUserList());
//
//        System.out.println();
//        System.out.println("------------------- 서비스 통합 테스트 -------------------");
//        System.out.println();
//
//        User charlie = jcfUserService.createUser("Charlie", "charlie@gmail.com", "1234");
//        User david = jcfUserService.createUser("David", "david@gmail.com", "1234");
//        // ChannelService 테스트
//        Channel testChannel = jcfChannelService.createChannel("Test Channel");
//        Channel chatChannel = jcfChannelService.createChannel("Chat Channel");
//        System.out.println("채널 생성 후: " + jcfChannelService.getChannelList());
//        // 채널에 유저 추가
//        jcfChannelService.joinChannel(testChannel.getId(), charlie.getId());
//        jcfChannelService.joinChannel(chatChannel.getId(), charlie.getId());
//        jcfChannelService.joinChannel(testChannel.getId(), david.getId());
//
//        for (var channel: jcfChannelService.getChannelList()) {
//            System.out.println(channel.getChannelName() + " 채널에 유저 추가 후: " + jcfUserService.getUsersByChannel(channel.getId()));
//        }
//
//        System.out.println();
//        // 유저 별로 참여중인 채널 확인
//        for (var user: jcfUserService.getUserList()) {
//            System.out.println(user.getUsername() + "의 참여중인 채널 조회: " + jcfChannelService.getChannelsByUser(user.getId()));
//        }
//        // 채널 이름 변경
//        jcfChannelService.updateChannelName(testChannel.getId(), "NMIXX Channel");
//        System.out.println("채널 이름 변경 후: " + jcfChannelService.getChannelList());
//
//        System.out.println();
//        System.out.println("------------------- 메시지 서비스 테스트 -------------------");
//        System.out.println();
//
//        // MessageService 테스트
//        jcfMessageService.sendMessage(charlie.getId(), testChannel.getId(), "Hello, World!");
//        jcfMessageService.sendMessage(charlie.getId(), testChannel.getId(), "This is test");
//        jcfMessageService.sendMessage(charlie.getId(), testChannel.getId(), "for testing");
//        System.out.println("메시지 전송 후: " + jcfMessageService.getAllMessages());
//
//        for (var user : jcfUserService.getUserList()) {
//            System.out.println(user.getUsername() + "의 메시지 조회: " + jcfMessageService.getMessageListByUser(user.getId()));
//        }
//
//        for (var channel : jcfChannelService.getChannelList()) {
//            System.out.println(channel.getChannelName() + " 채널의 메시지 조회: " + jcfMessageService.getMessageListByChannel(channel.getId()));
//        }
//
//        UUID messageId = jcfMessageService.getMessageListByUser(charlie.getId()).get(0).getId();
//        jcfMessageService.updateMessage(messageId, "NMIXX Change Up!");
//        System.out.println("메시지 수정 후: " + jcfMessageService.getAllMessages());
//        jcfMessageService.deleteMessage(messageId);
//        System.out.println("메시지 삭제 후: " + jcfMessageService.getAllMessages());
//
//        // 채널에서 유저 제거
//        jcfChannelService.leaveChannel(testChannel.getId(), charlie.getId());
//        System.out.println("채널에서 유저 제거 후: " + jcfChannelService.getChannelList());
//
//        // 채널 삭제
//        jcfChannelService.deleteChannel(testChannel.getId());
//        System.out.println("채널 삭제 후: " + jcfChannelService.getChannelList());
//        System.out.println("채널 삭제 후 메시지 존재 여부 확인: " + jcfMessageService.getAllMessages());
//    }
//}

public class JavaApplication {

    public static void main(String[] args) {

        // =========================
        // Repository 초기화 (File)
        // =========================
        UserRepository userRepository = new FileUserRepository();
        ChannelRepository channelRepository = new FileChannelRepository();
        MessageRepository messageRepository = new FileMessageRepository();

        // =========================
        // Service 초기화 (Basic)
        // =========================
        UserService userService =
                new BasicUserService(
                        userRepository,
                        channelRepository,
                        messageRepository
                );

        ChannelService channelService =
                new BasicChannelService(
                        userRepository,
                        channelRepository,
                        messageRepository
                );

        MessageService messageService =
                new BasicMessageService(
                        userRepository,
                        channelRepository,
                        messageRepository
                );

        // =========================
        // 유저 서비스 테스트
        // =========================
        System.out.println("===== 유저 서비스 테스트 (File) =====");

        User alice = userService.createUser(
                "Alice",
                "alice@gmail.com",
                "1234"
        );
        User bob = userService.createUser(
                "Bob",
                "bob@gmail.com",
                "1234"
        );

        System.out.println("유저 생성 후: " + userService.getUserList());

        userService.updateUserName(alice.getId(), "AliceUpdated");
        System.out.println("유저 이름 수정 후: " + userService.getUserList());

        UUID aliceId = alice.getId();
        userService.deleteUser(aliceId);
        System.out.println("유저 삭제 후: " + userService.getUserList());

        System.out.println();

        // =========================
        // 채널 서비스 테스트
        // =========================
        System.out.println("===== 채널 서비스 테스트 (File) =====");

        Channel noticeChannel = channelService.createChannel("공지 채널");
        Channel chatChannel = channelService.createChannel("잡담 채널");

        System.out.println("채널 생성 후: " + channelService.getChannelList());

        // 채널 참여
        channelService.joinChannel(noticeChannel.getId(), bob.getId());
        channelService.joinChannel(chatChannel.getId(), bob.getId());

        System.out.println(
                "Bob이 참여한 채널: " +
                        channelService.getChannelsByUser(bob.getId())
        );

        // 채널 이름 수정
        channelService.updateChannelName(
                noticeChannel.getId(),
                "공지사항 채널"
        );
        System.out.println("채널 이름 수정 후: " + channelService.getChannelList());

        // 채널 나가기
        channelService.leaveChannel(chatChannel.getId(), bob.getId());
        System.out.println(
                "채널 나간 후 Bob의 채널: " +
                        channelService.getChannelsByUser(bob.getId())
        );

        System.out.println();

        // =========================
        // 메시지 서비스 테스트
        // =========================
        System.out.println("===== 메시지 서비스 테스트 (File) =====");

        Message m1 = messageService.sendMessage(
                bob.getId(),
                noticeChannel.getId(),
                "안녕하세요."
        );
        Message m2 = messageService.sendMessage(
                bob.getId(),
                noticeChannel.getId(),
                "공지 확인 부탁드립니다."
        );

        System.out.println("메시지 전송 후 전체 메시지:");
        System.out.println(messageService.getAllMessages());

        System.out.println(
                "Bob의 메시지 목록: " +
                        messageService.getMessageListByUser(bob.getId())
        );

        System.out.println(
                "공지 채널 메시지 목록: " +
                        messageService.getMessageListByChannel(noticeChannel.getId())
        );

        // 메시지 수정
        messageService.updateMessage(
                m1.getId(),
                "안녕하세요! 수정된 메시지입니다."
        );
        System.out.println("메시지 수정 후:");
        System.out.println(messageService.getAllMessages());

        // 메시지 삭제
        messageService.deleteMessage(m2.getId());
        System.out.println("메시지 삭제 후:");
        System.out.println(messageService.getAllMessages());

        System.out.println();

        // =========================
        // 채널 삭제 테스트
        // =========================
        System.out.println("===== 채널 삭제 테스트 (File) =====");

        channelService.deleteChannel(noticeChannel.getId());

        System.out.println("채널 삭제 후 채널 목록:");
        System.out.println(channelService.getChannelList());

        System.out.println(
                "채널 삭제 후 메시지 존재 여부: " +
                        messageService.getAllMessages()
        );
    }
}
