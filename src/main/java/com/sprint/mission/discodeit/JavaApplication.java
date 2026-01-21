package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.consistency.FileConsistencyManager;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class JavaApplication {

    public static void init(Path directory) {
        // 저장할 경로의 파일 초기화
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        Path baseDir = Paths.get(System.getProperty("user.dir"), "data");
        Path userDir = baseDir.resolve("users");
        Path channelDir = baseDir.resolve("channels");
        Path messageDir = baseDir.resolve("messages");

        init(userDir);
        init(channelDir);
        init(messageDir);

//        UserRepository userRepository = new JCFUserRepository();
//        ChannelRepository channelRepository = new JCFChannelRepository();
//        MessageRepository messageRepository = new JCFMessageRepository();

        UserRepository userRepository = new FileUserRepository(userDir);
        ChannelRepository channelRepository = new FileChannelRepository(channelDir);
        MessageRepository messageRepository = new FileMessageRepository(messageDir);

//        ConsistencyManager consistencyManager = new ConsistencyManager(userRepository, channelRepository, messageRepository);
//        UserService userService = new JCFUserService(consistencyManager, userRepository);
//        ChannelService channelService = new JCFChannelService(consistencyManager, channelRepository, userService);
//        MessageService messageService = new JCFMessageService(messageRepository, channelService, userService);

//        FileConsistencyManager fileConsistencyManager = new FileConsistencyManager(userRepository, channelRepository, messageRepository);
//        UserService userService = new FileUserService(fileConsistencyManager, userRepository);
//        ChannelService channelService = new FileChannelService(fileConsistencyManager, channelRepository, userService);
//        MessageService messageService = new FileMessageService(fileConsistencyManager, messageRepository, channelService, userService);

        UserService userService = new BasicUserService(userRepository, channelRepository, messageRepository);
        ChannelService channelService = new BasicChannelService(userRepository, channelRepository, messageRepository);
        MessageService messageService = new BasicMessageService(userRepository, channelRepository, messageRepository);

        final String HR = "============================================================";
        final String SR = "------------------------------------------------------------";
        final String BR = "===============================";

        // =========================================================
        // 회원 테스트
        // =========================================================
        System.out.println("\n" + HR);
        System.out.println("회원 테스트");
        System.out.println(HR);

        System.out.println("\n[회원 등록]");
        User user1 = userService.createUser("홍길동", "hong@test.com");
        User user2 = userService.createUser("김철수", "kim@test.com");
        User user3 = userService.createUser("박민수", "park@test.com");
        System.out.println("  ✔ 등록 완료: 홍길동 / 김철수 / 박민수");
        System.out.println("  • " + user1);
        System.out.println("  • " + user2);
        System.out.println("  • " + user3);

        System.out.println("\n[회원 단건 조회]");
        System.out.println("  • " + userService.findUserById(user1.getId()));
        System.out.println("  • " + userService.findUserById(user2.getId()));
        System.out.println("  • " + userService.findUserById(user3.getId()));

        System.out.println("\n[회원 다건 조회]");
        List<User> usersAll1 = userService.findAll();
        System.out.println("  총 " + usersAll1.size() + "명");
        usersAll1.forEach(u -> System.out.println("  • " + u));

        System.out.println("\n[회원 수정]");
        System.out.println("  - 김철수 → 철수킴");
        userService.updateUserNickname(user2.getId(), "철수킴");

        System.out.println("\n[회원 수정 후 조회]");
        System.out.println("  • " + userService.findUserById(user2.getId()));

        System.out.println("\n[회원 삭제]");
        System.out.println("  - 박민수");
        userService.deleteUser(user3.getId());
        System.out.println("  ✔ 삭제 완료");

        System.out.println("\n[회원 다건 조회 - 삭제 후]");
        List<User> usersAll2 = userService.findAll();
        System.out.println("  총 " + usersAll2.size() + "명");
        usersAll2.forEach(u -> System.out.println("  • " + u));

        // =========================================================
        // 채널 테스트
        // =========================================================
        System.out.println("\n" + BR);
        System.out.println("\n" + HR);
        System.out.println("채널 테스트");
        System.out.println(HR);

        System.out.println("\n[채널 생성]");
        Channel channel1 = channelService.createChannel("1번 채널", user1.getId());
        Channel channel2 = channelService.createChannel("2번 채널", user2.getId());
        Channel channel3 = channelService.createChannel("3번 채널", user1.getId());

        System.out.println("  ✔ 채널 생성 완료 (3개)");
        System.out.println("  • " + channel1);
        System.out.println("  • " + channel2);
        System.out.println("  • " + channel3);

        System.out.println("\n[채널 단건 조회]");
        System.out.println("  • " + channelService.findChannelById(channel1.getId()));
        System.out.println("  • " + channelService.findChannelById(channel2.getId()));
        System.out.println("  • " + channelService.findChannelById(channel3.getId()));

        System.out.println("\n[채널 다건 조회]");
        List<Channel> channelsAll1 = channelService.findAll();
        System.out.println("  총 " + channelsAll1.size() + "개");
        channelsAll1.forEach(ch -> System.out.println("  • " + ch));

        System.out.println("\n[채널 수정]");
        System.out.println("  - 2번 채널 → 22222번 채널");
        channelService.updateChannelName(channel2.getId(), user2.getId(), "22222번 채널");

        System.out.println("\n[채널 수정 후 조회]");
        System.out.println("  • " + channelService.findChannelById(channel2.getId()));

        System.out.println("\n[회원 다건 조회 - 채널 상태 반영 확인]");
        List<User> usersAll3 = userService.findAll();
        System.out.println("  총 " + usersAll3.size() + "명");
        usersAll3.forEach(u -> System.out.println("  • " + u));

        System.out.println("\n[채널 삭제]");
        System.out.println("  - 3번 채널");
        channelService.deleteChannel(channel3.getId(), user1.getId());
        System.out.println("  ✔ 삭제 완료");

        System.out.println("\n[채널 다건 조회 - 삭제 후]");
        List<Channel> channelsAll2 = channelService.findAll();
        System.out.println("  총 " + channelsAll2.size() + "개");
        channelsAll2.forEach(ch -> System.out.println("  • " + ch));

        System.out.println("\n[회원 다건 조회 - 채널 삭제 반영 확인]");
        List<User> usersAll4 = userService.findAll();
        System.out.println("  총 " + usersAll4.size() + "명");
        usersAll4.forEach(u -> System.out.println("  • " + u));

        System.out.println("\n[채널 유저 조회]");
        System.out.println("  채널명: " + channel1.getName());
        channelService.getMembers(channel1.getId()).forEach(u -> System.out.println("  • " + u));

        System.out.println("\n[채널 참가]");
        System.out.println("  - " + channel1.getName() + " ← " + user2.getNickname());
        channelService.joinChannel(channel1.getId(), user2.getId());
        System.out.println("  ✔ 참가 완료");

        System.out.println("\n[채널 유저 조회 - 참가 후]");
        System.out.println("  채널명: " + channel1.getName());
        channelService.getMembers(channel1.getId()).forEach(u -> System.out.println("  • " + u));

        // =========================================================
        // 메시지 테스트 (채널별)
        // =========================================================
        System.out.println("\n" + BR);
        System.out.println("\n" + HR);
        System.out.println("메시지 테스트 (채널별)");
        System.out.println(HR);

        List<Channel> channelList = channelService.findAll();
        User tester = userService.createUser("테스터", "tester@test.com");
        System.out.println("\n[TESTER 생성]");
        System.out.println("  • " + tester);

        for (Channel channel : channelList) {
            System.out.println("\n" + SR);
            System.out.println("[채널] " + channel.getName());
            System.out.println(SR);

            System.out.println("\n  [채널 가입] 테스터 → " + channel.getName());
            channelService.joinChannel(channel.getId(), tester.getId());
            System.out.println("  ✔ 가입 완료");

            System.out.println("\n  [회원 단건 조회] (테스터 상태 확인)");
            System.out.println("  • " + userService.findUserById(tester.getId()));

            System.out.println("\n  [메시지 생성]");
            messageService.createMessage(channel.getId(), tester.getId(), "이곳은 " + channel.getName() + " 입니다.");
            messageService.createMessage(channel.getId(), tester.getId(), "안녕하세요.");
            messageService.createMessage(channel.getId(), tester.getId(), "저는 테스터입니다.");
            Message updateTestMessage = messageService.createMessage(channel.getId(), tester.getId(), "수정 테스트 메시지입니다.");
            Message deleteTestMessage = messageService.createMessage(channel.getId(), tester.getId(), "삭제 테스트 메시지입니다.");
            System.out.println("  ✔ 생성 완료 (5개)");

            System.out.println("\n  [메시지 조회]");
            List<String> readView1 = messageService.readMessagesByChannelId(channel.getId());
            System.out.println("  총 " + readView1.size() + "개");
            readView1.forEach(s -> System.out.println("  • " + s));

            System.out.println("\n  [메시지 상세 조회]");
            List<Message> detailView1 = messageService.findMessagesByChannelId(channel.getId());
            System.out.println("  총 " + detailView1.size() + "개");
            detailView1.forEach(m -> System.out.println("  • " + m));

            System.out.println("\n  [메시지 수정]");
            messageService.updateMessageContent(
                    channel.getId(),
                    tester.getId(),
                    updateTestMessage.getId(),
                    "수정에 성공했습니다."
            );
            System.out.println("  ✔ 수정 완료");

            System.out.println("\n  [메시지 조회 - 수정 후]");
            List<String> readView2 = messageService.readMessagesByChannelId(channel.getId());
            System.out.println("  총 " + readView2.size() + "개");
            readView2.forEach(s -> System.out.println("  • " + s));

            System.out.println("\n  [메시지 상세 조회 - 수정 후]");
            List<Message> detailView2 = messageService.findMessagesByChannelId(channel.getId());
            System.out.println("  총 " + detailView2.size() + "개");
            detailView2.forEach(m -> System.out.println("  • " + m));

            System.out.println("\n  [메시지 삭제]");
            messageService.deleteMessage(
                    channel.getId(),
                    tester.getId(),
                    deleteTestMessage.getId()
            );
            System.out.println("  ✔ 삭제 완료");
            System.out.println("  - 삭제 대상 객체: " + deleteTestMessage);

            System.out.println("\n  [메시지 조회 - 삭제 후]");
            List<String> readView3 = messageService.readMessagesByChannelId(channel.getId());
            System.out.println("  총 " + readView3.size() + "개");
            readView3.forEach(s -> System.out.println("  • " + s));

            System.out.println("\n  [메시지 상세 조회 - 삭제 후]");
            List<Message> detailView3 = messageService.findMessagesByChannelId(channel.getId());
            System.out.println("  총 " + detailView3.size() + "개");
            detailView3.forEach(m -> System.out.println("  • " + m));

            System.out.println("\n  [채널 탈퇴] 테스터 ← " + channel.getName());
            channelService.leaveChannel(channel.getId(), tester.getId());
            System.out.println("  ✔ 탈퇴 완료");

            System.out.println("\n  [회원 단건 조회] (테스터 상태 확인)");
            System.out.println("  • " + userService.findUserById(tester.getId()));
        }

        System.out.println("\n" + HR);
        System.out.println("메시지 테스트 종료 후 - 회원 다건 조회");
        System.out.println(HR);
        List<User> usersAll5 = userService.findAll();
        System.out.println("  총 " + usersAll5.size() + "명");
        usersAll5.forEach(u -> System.out.println("  • " + u));

        // =========================================================
        // 유저별 메시지 생성/조회
        // =========================================================
        System.out.println("\n" + HR);
        System.out.println("유저별 메시지 생성/조회");
        System.out.println(HR);

        System.out.println("\n[유저별 메시지 생성]");
        System.out.println("  - " + channel1.getName() + " / " + user1.getNickname() + " 메시지 생성");
        messageService.createMessage(channel1.getId(), user1.getId(), "안녕하세요.");
        messageService.createMessage(channel1.getId(), user1.getId(), "유저별 메시지 생성 테스트입니다.");
        messageService.createMessage(channel1.getId(), user1.getId(), "저는 홍길동입니다.");

        System.out.println("  - " + channel1.getName() + " / " + channel2.getName() + " / " + user2.getNickname() + " 메시지 생성");
        messageService.createMessage(channel1.getId(), user2.getId(), "안녕하세요!");
        messageService.createMessage(channel1.getId(), user2.getId(), "유저별 메시지 생성 테스트입니다!");
        messageService.createMessage(channel1.getId(), user2.getId(), "저는 철수킴입니다!");
        messageService.createMessage(channel2.getId(), user2.getId(), "안녕하세요.");
        messageService.createMessage(channel2.getId(), user2.getId(), "유저별 메시지 생성 테스트입니다.");
        messageService.createMessage(channel2.getId(), user2.getId(), "저는 철수킴입니다.");

        System.out.println("  - 1번 채널 / 테스터 참가 + 메시지 생성");
        channelService.joinChannel(channel1.getId(), tester.getId());
        messageService.createMessage(channel1.getId(), tester.getId(), "안녕하세요.");
        messageService.createMessage(channel1.getId(), tester.getId(), "유저별 메시지 생성 테스트입니다.");
        messageService.createMessage(channel1.getId(), tester.getId(), "저는 테스터입니다.");

        System.out.print("\n[유저별 작성한 메시지 조회]");
        List<User> userList = List.of(user1, user2, tester);
        for (User user : userList) {
            System.out.println("\n" + SR);
            System.out.println("[작성자] " + user.getNickname());
            System.out.println(SR);

            List<String> byUser = messageService.readMessagesByUserId(user.getId());
            System.out.println("  총 " + byUser.size() + "개");
            byUser.forEach(s -> System.out.println("  • " + s));
        }

        // =========================================================
        // 정합성 테스트 (채널 삭제 / 유저 삭제)
        // =========================================================
        System.out.println("\n" + BR);
        System.out.println("\n" + HR);
        System.out.println("정합성 테스트 (채널 삭제 / 유저 삭제)");
        System.out.println(HR);

        // =========================================================
        // 채널 삭제 정합성
        // =========================================================

        System.out.println("\n[채널 삭제 테스트 전 저장소 상태]");
        System.out.println("  채널=" + channelRepository.findAll().size() + "개 / 유저=" + userRepository.findAll().size() + "명 / 메시지=" + messageRepository.findAll().size() + "개");
        System.out.println("  • " + channel1.getName() + " 멤버수=" + channel1.getUsers().size() + " / 메시지수=" + channel1.getMessages().size());
        System.out.println("  • " + channel2.getName() + " 멤버수=" + channel2.getUsers().size() + " / 메시지수=" + channel2.getMessages().size());

        System.out.println("\n- 삭제용 채널 생성 → 가입/메시지 생성 (채널+1, 메시지+1)");
        Channel deleteChannel = channelService.createChannel("삭제 검증 채널", user1.getId());
        messageService.createMessage(deleteChannel.getId(), user1.getId(), "삭제 검증 채널 메시지 - user1");

        System.out.println("\n[채널 생성/가입 후 유저의 채널 목록 확인]");
        System.out.println(user1);

        System.out.println("\n[저장소 상태]");
        System.out.println("  채널=" + channelRepository.findAll().size() + "개 / 유저=" + userRepository.findAll().size() + "명 / 메시지=" + messageRepository.findAll().size() + "개");
        System.out.println("  • " + channel1.getName() + " 멤버수=" + channel1.getUsers().size() + " / 메시지수=" + channel1.getMessages().size());
        System.out.println("  • " + channel2.getName() + " 멤버수=" + channel2.getUsers().size() + " / 메시지수=" + channel2.getMessages().size());
        System.out.println("  • " + deleteChannel.getName() + " 멤버수=" + deleteChannel.getUsers().size() + " / 메시지수=" + deleteChannel.getMessages().size());

        channelService.deleteChannel(deleteChannel.getId(), user1.getId());

        System.out.println("\n[채널 삭제 후 저장소 상태]");
        System.out.println("  채널=" + channelRepository.findAll().size() + "개 / 유저=" + userRepository.findAll().size() + "명 / 메시지=" + messageRepository.findAll().size() + "개");
        System.out.println("  • " + channel1.getName() + " 멤버수=" + channel1.getUsers().size() + " / 메시지수=" + channel1.getMessages().size());
        System.out.println("  • " + channel2.getName() + " 멤버수=" + channel2.getUsers().size() + " / 메시지수=" + channel2.getMessages().size());

        System.out.println("\n[채널 삭제 후 유저의 채널 목록 확인]");
        System.out.println(user1);

        if (channelRepository.findAll().contains(deleteChannel)) {
            System.out.println("실패: 삭제된 채널이 channelData에 남아있음");
        }

        if (user1.getChannels().contains(deleteChannel)) {
            System.out.println("실패: user1 joinedChannels에 삭제된 채널이 남아있음");
        }

        if (user2.getChannels().contains(deleteChannel)) {
            System.out.println("실패: user2 joinedChannels에 삭제된 채널이 남아있음");
        }

        boolean messageDataHasDeleteChannel = messageRepository.findAll()
                .stream()
                .anyMatch(m -> m.getChannel().equals(deleteChannel));
        if (messageDataHasDeleteChannel) {
            System.out.println("실패: messageData에 삭제된 채널 메시지가 남아있음");
        }

        boolean user1HasDeleteChannelMsg = user1.getMessages()
                .stream()
                .anyMatch(m -> m.getChannel().equals(deleteChannel));
        if (user1HasDeleteChannelMsg) {
            System.out.println("실패: user1 메시지 목록에 삭제된 채널 메시지가 남아있음");
        }

        boolean user2HasDeleteChannelMsg = user2.getMessages()
                .stream()
                .anyMatch(m -> m.getChannel().equals(deleteChannel));
        if (user2HasDeleteChannelMsg) {
            System.out.println("실패: user2 메시지 목록에 삭제된 채널 메시지가 남아있음");
        }

        // =========================================================
        // 유저 삭제 정합성
        // =========================================================
        System.out.println("\n" + SR);
        System.out.println("\n[유저 삭제 테스트 전 저장소 상태]");
        System.out.println("  채널=" + channelRepository.findAll().size() + "개 / 유저=" + userRepository.findAll().size() + "명 / 메시지=" + messageRepository.findAll().size() + "개");
        System.out.println("  • " + channel1.getName() + " 멤버수=" + channel1.getUsers().size() + " / 메시지수=" + channel1.getMessages().size());
        System.out.println("  • " + channel2.getName() + " 멤버수=" + channel2.getUsers().size() + " / 메시지수=" + channel2.getMessages().size());

        System.out.println("\n- 삭제용 유저 생성 → 채널 가입/메시지 작성 (유저+1, 메시지+1)");
        User deleteUser = userService.createUser("삭제 검증 유저", "delete-user@test.com");
        channelService.joinChannel(channel1.getId(), deleteUser.getId());
        Message deleteUserMsg1 = messageService.createMessage(channel1.getId(), deleteUser.getId(), "삭제 검증 유저 메시지 1");

        System.out.println("\n[저장소 상태]");
        System.out.println("  채널=" + channelRepository.findAll().size() + "개 / 유저=" + userRepository.findAll().size() + "명 / 메시지=" + messageRepository.findAll().size() + "개");
        System.out.println("  • " + channel1.getName() + " 멤버수=" + channel1.getUsers().size() + " / 메시지수=" + channel1.getMessages().size());
        System.out.println("  • " + channel2.getName() + " 멤버수=" + channel2.getUsers().size() + " / 메시지수=" + channel2.getMessages().size());

        userService.deleteUser(deleteUser.getId());

        System.out.println("\n[유저 삭제 후 저장소 상태]");
        System.out.println("  채널=" + channelRepository.findAll().size() + "개 / 유저=" + userRepository.findAll().size() + "명 / 메시지=" + messageRepository.findAll().size() + "개");
        System.out.println("  • " + channel1.getName() + " 멤버수=" + channel1.getUsers().size() + " / 메시지수=" + channel1.getMessages().size());
        System.out.println("  • " + channel2.getName() + " 멤버수=" + channel2.getUsers().size() + " / 메시지수=" + channel2.getMessages().size());

        if (userRepository.findAll().contains(deleteUser)) {
            System.out.println("실패: 삭제된 유저가 userData에 남아있음");
        }

        boolean anyChannelHasDeleteUser = channelRepository.findAll()
                .stream()
                .anyMatch(ch -> ch.getUsers().stream().anyMatch(u -> u.getId().equals(deleteUser.getId())));
        if (anyChannelHasDeleteUser) {
            System.out.println("실패: 채널 멤버 목록에 삭제된 유저가 남아있음");
        }

        boolean messageDataHasDeleteUser = messageRepository.findAll()
                .stream()
                .anyMatch(m -> m.getUser().getId().equals(deleteUser.getId()));
        if (messageDataHasDeleteUser) {
            System.out.println("실패: messageData에 삭제된 유저가 작성한 메시지가 남아있음");
        }

        boolean anyChannelHasDeleteUserMsg = channelRepository.findAll()
                .stream()
                .anyMatch(ch -> ch.getMessages().stream().anyMatch(m -> m.getUser().getId().equals(deleteUser.getId())));
        if (anyChannelHasDeleteUserMsg) {
            System.out.println("실패: 채널 메시지 목록에 삭제된 유저 메시지가 남아있음");
        }

        if (messageRepository.findAll().contains(deleteUserMsg1)) {
            System.out.println("실패: deleteUserMsg1이 messageData에 남아있음");
        }

        System.out.println("\n" + HR);
        System.out.println("테스트 종료");
        System.out.println(HR);
    }
}
