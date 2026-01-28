//package com.sprint.mission.discodeit;
//
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.repository.ChannelRepository;
//import com.sprint.mission.discodeit.repository.MessageRepository;
//import com.sprint.mission.discodeit.repository.UserRepository;
//import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
//import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
//import com.sprint.mission.discodeit.repository.file.FileUserRepository;
//import com.sprint.mission.discodeit.service.ChannelService;
//import com.sprint.mission.discodeit.service.MessageService;
//import com.sprint.mission.discodeit.service.UserService;
//import com.sprint.mission.discodeit.service.basic.BasicChannelService;
//import com.sprint.mission.discodeit.service.basic.BasicMessageService;
//import com.sprint.mission.discodeit.service.basic.BasicUserService;
//
//import java.util.UUID;
//
//public class JavaApplication {
//
//    public static void main(String[] args) {
//
//        // =========================
//        // Repository 초기화 (File)
//        // =========================
//        UserRepository userRepository = new FileUserRepository();
//        ChannelRepository channelRepository = new FileChannelRepository();
//        MessageRepository messageRepository = new FileMessageRepository();
//
//        // =========================
//        // Service 초기화 (Basic)
//        // =========================
//        UserService userService =
//                new BasicUserService(
//                        userRepository,
//                        channelRepository,
//                        messageRepository
//                );
//
//        ChannelService channelService =
//                new BasicChannelService(
//                        userRepository,
//                        channelRepository,
//                        messageRepository
//                );
//
//        MessageService messageService =
//                new BasicMessageService(
//                        userRepository,
//                        channelRepository,
//                        messageRepository
//                );
//
//        // =========================
//        // 유저 서비스 테스트
//        // =========================
//        System.out.println("===== 유저 서비스 테스트 (File) =====");
//
//        User alice = userService.createUser(
//                "Alice",
//                "alice@gmail.com",
//                "1234"
//        );
//        User bob = userService.createUser(
//                "Bob",
//                "bob@gmail.com",
//                "1234"
//        );
//
//        System.out.println("유저 생성 후: " + userService.getUserList());
//
//        userService.updateUserName(alice.getId(), "AliceUpdated");
//        System.out.println("유저 이름 수정 후: " + userService.getUserList());
//
//        UUID aliceId = alice.getId();
//        userService.deleteUser(aliceId);
//        System.out.println("유저 삭제 후: " + userService.getUserList());
//
//        System.out.println();
//
//        // =========================
//        // 채널 서비스 테스트
//        // =========================
//        System.out.println("===== 채널 서비스 테스트 (File) =====");
//
//        Channel noticeChannel = channelService.createChannel("공지 채널");
//        Channel chatChannel = channelService.createChannel("잡담 채널");
//
//        System.out.println("채널 생성 후: " + channelService.getChannelList());
//
//        // 채널 참여
//        channelService.joinChannel(noticeChannel.getId(), bob.getId());
//        channelService.joinChannel(chatChannel.getId(), bob.getId());
//
//        System.out.println(
//                "Bob이 참여한 채널: " +
//                        channelService.getChannelsByUser(bob.getId())
//        );
//
//        // 채널 이름 수정
//        channelService.updateChannelName(
//                noticeChannel.getId(),
//                "공지사항 채널"
//        );
//        System.out.println("채널 이름 수정 후: " + channelService.getChannelList());
//
//        // 채널 나가기
//        channelService.leaveChannel(chatChannel.getId(), bob.getId());
//        System.out.println(
//                "채널 나간 후 Bob의 채널: " +
//                        channelService.getChannelsByUser(bob.getId())
//        );
//
//        System.out.println();
//
//        // =========================
//        // 메시지 서비스 테스트
//        // =========================
//        System.out.println("===== 메시지 서비스 테스트 (File) =====");
//
//        Message m1 = messageService.sendMessage(
//                bob.getId(),
//                noticeChannel.getId(),
//                "안녕하세요."
//        );
//        Message m2 = messageService.sendMessage(
//                bob.getId(),
//                noticeChannel.getId(),
//                "공지 확인 부탁드립니다."
//        );
//
//        System.out.println("메시지 전송 후 전체 메시지:");
//        System.out.println(messageService.getAllMessages());
//
//        System.out.println(
//                "Bob의 메시지 목록: " +
//                        messageService.getMessageListByUser(bob.getId())
//        );
//
//        System.out.println(
//                "공지 채널 메시지 목록: " +
//                        messageService.getMessageListByChannel(noticeChannel.getId())
//        );
//
//        // 메시지 수정
//        messageService.updateMessage(
//                m1.getId(),
//                "안녕하세요! 수정된 메시지입니다."
//        );
//        System.out.println("메시지 수정 후:");
//        System.out.println(messageService.getAllMessages());
//
//        // 메시지 삭제
//        messageService.deleteMessage(m2.getId());
//        System.out.println("메시지 삭제 후:");
//        System.out.println(messageService.getAllMessages());
//
//        System.out.println();
//
//        // =========================
//        // 채널 삭제 테스트
//        // =========================
//        System.out.println("===== 채널 삭제 테스트 (File) =====");
//
//        channelService.deleteChannel(noticeChannel.getId());
//
//        System.out.println("채널 삭제 후 채널 목록:");
//        System.out.println(channelService.getChannelList());
//
//        System.out.println(
//                "채널 삭제 후 메시지 존재 여부: " +
//                        messageService.getAllMessages()
//        );
//    }
//}
