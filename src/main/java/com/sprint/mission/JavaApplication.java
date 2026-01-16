package com.sprint.mission;

import com.sprint.mission.entity.Channel;
import com.sprint.mission.entity.Message;
import com.sprint.mission.entity.User;
import com.sprint.mission.repository.ChannelRepository;
import com.sprint.mission.repository.MessageRepository;
import com.sprint.mission.repository.UserRepository;
import com.sprint.mission.repository.file.FileChannelRepository;
import com.sprint.mission.repository.file.FileMessageRepository;
import com.sprint.mission.repository.file.FileUserRepository;
import com.sprint.mission.repository.jcf.JCFChannelRepository;
import com.sprint.mission.repository.jcf.JCFMessageRepository;
import com.sprint.mission.repository.jcf.JCFUserRepository;
import com.sprint.mission.service.ChannelService;
import com.sprint.mission.service.MessageService;
import com.sprint.mission.service.UserService;
import com.sprint.mission.service.basic.BasicChannelService;
import com.sprint.mission.service.basic.BasicMessageService;
import com.sprint.mission.service.basic.BasicUserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JavaApplication {
    public static void main(String[] args) {
        // ===================== File I/O 구현 ===================== //
        Path userDirectory = Paths.get(System.getProperty("user.dir"), "users");
        Path channelDirectory = Paths.get(System.getProperty("user.dir"), "channels");
        Path messageDirectory = Paths.get(System.getProperty("user.dir"), "messages");

        init(userDirectory);
        init(channelDirectory);
        init(messageDirectory);

        UserRepository userRepository = new FileUserRepository(userDirectory);
        ChannelRepository channelRepository = new FileChannelRepository(channelDirectory);
        MessageRepository messageRepository = new FileMessageRepository(messageDirectory);

        UserService userService = new BasicUserService(userRepository);
        ChannelService channelService = new BasicChannelService(channelRepository, userRepository, userService);
        MessageService messageService = new BasicMessageService(messageRepository, userService, channelService);

        // 유저 테스트 =================================================

        // 파일로 저장하기 때문에 처음 실행을 제외한 실행 시 이미 있는 유저 예외처리를 막기 위해 주석처리
        // 유저 생성
        // User user1 = userService.create("1번유저", "user1@test.com"); b6a50b11-3d20-4508-a234-334e2d4aefc2
        // User user2 = userService.create("2번유저", "user2@test.com"); 4c5cc8c5-911f-4d25-a83f-c507fe9d6d0c
        // User user3 = userService.create("3번유저", "user3@test.com"); 127faac9-4b1b-4d3e-a667-a0bc21d7302c
        // User user4 = userService.create("4번유저", "user4@test.com"); 498bb20e-c621-4f05-9f9a-2ef09d1320ff
        // User user5 = userService.create("5번유저", "user5@test.com"); 251dc221-35cc-46e6-87ee-98a10883eef7

        // 유저 단건 조회
        User user1 = userService.getUserOrThrow(UUID.fromString("b6a50b11-3d20-4508-a234-334e2d4aefc2"));
        System.out.printf("1번 유저의 이름 조회: %s%n", user1.getName());

        // 유저 다건 조회
        System.out.println(userService.getAllUser().stream()
                .map(User::getName)
                .collect(Collectors.joining(", ", "유저 다건 조회: [", "]")));

        // 유저 수정 및 확인
        userService.update(user1.getId(), "1번유저 수정");
        System.out.println(user1.getName());
        System.out.printf("1번 유저의 수정된 이름 조회: %s%n",
                userService.getUserOrThrow(user1.getId()).getName());

        // 유저 삭제 및 확인 -> 테스트 용이성을 위해 주석
        // userService.deleteById(UUID.fromString("4c5cc8c5-911f-4d25-a83f-c507fe9d6d0c"));
        // System.out.println(userService.findAll().stream()
        //         .map(User::getName)
        //         .collect(Collectors.joining(", ", "2번 유저 삭제 확인: [", "]")));

        System.out.println("=".repeat(50));

        // 채널 테스트 =================================================

        // 사전작업: 테스트를 용이하게 하기 위한 유저 찾아오기
        User user3 = userService.getUserOrThrow(UUID.fromString("127faac9-4b1b-4d3e-a667-a0bc21d7302c"));
        User user4 = userService.getUserOrThrow(UUID.fromString("498bb20e-c621-4f05-9f9a-2ef09d1320ff"));
        User user5 = userService.getUserOrThrow(UUID.fromString("251dc221-35cc-46e6-87ee-98a10883eef7"));

        // 채널 생성 -> 유저 생성에서의 이유와 마찬가지로 주석처리
        // Channel channel1 = channelService.create(user1.getId(), "채널 1"); 2054292b-d28a-4271-af0e-2f625c8874ae
        // Channel channel2 = channelService.create(user3.getId(), "채널 2"); 5f770e3d-4619-455f-b759-e6216b3b4d23
        // Channel channel3 = channelService.create(user1.getId(), "채널 3"); 34af6e64-b1af-4710-a9d5-e8a0e6f7f0fb

        // 채널 단건 조회
        Channel channel1 = channelService.getChannelOrThrow(UUID.fromString("2054292b-d28a-4271-af0e-2f625c8874ae"));
        Channel channel2 = channelService.getChannelOrThrow(UUID.fromString("5f770e3d-4619-455f-b759-e6216b3b4d23"));
        Channel channel3 = channelService.getChannelOrThrow(UUID.fromString("34af6e64-b1af-4710-a9d5-e8a0e6f7f0fb"));

        System.out.printf("방장: [%s], 채널명: [%s]%n", channel1.getOwner().getName(), channel1.getName());
        System.out.printf("방장: [%s], 채널명: [%s]%n", channel2.getOwner().getName(), channel2.getName());

        // 채널 다건 조회
        System.out.println(channelService.getAllChannel().stream()
                .map(channel ->
                        channel.getName() + " 방장: " + channel.getOwner().getName())
                .collect(Collectors.joining(" / ", "전체 채널정보 조회: [", "]"))
        );

        // 채널 이름 수정
        channelService.update(channel3.getId(), "채널 3 수정");
        System.out.printf("수정된 채널 3 이름: [%s]%n", channelService.getChannelOrThrow(channel3.getId()).getName());

        // 채널 삭제 및 확인 -> 테스트 용이성을 위해 주석
        // channelService.deleteById(channel3.getId());
        // System.out.println(channelService.findAll().stream()
        //      .map(channel ->
        //           channel.getName() + " 방장: " + channel.getOwner().getName())
        //      .collect(Collectors.joining(" / ", "전체 채널정보 조회: [", "]"))
        // );

        // 유저가 채널에 참여 및 채널에 있는 유저 확인
        channelService.joinChannel(user4.getId(), channel1.getId());
        channelService.joinChannel(user5.getId(), channel1.getId());

        System.out.println(userService.getChannelUsers(channel1.getId()).stream()
                .map(User::getName)
                .collect(Collectors.joining(", ", "채널에 참가한 유저 목록: [", "]")));

        // 유저가 채널을 떠남 및 떠난 채널의 유저 확인
        channelService.leaveChannel(user5.getId(), channel1.getId());
        System.out.println(userService.getChannelUsers(channel1.getId()).stream()
                .map(User::getName)
                .collect(Collectors.joining(", ", "채널에 참가한 유저 목록: [", "]")));

        System.out.println("=".repeat(50));

        // 메시지 테스트 =================================================
        // 현재 상황
        // 채널1: user1, user4
        // 채널2: user3

        // 메시지 생성
//         Message message1 = messageService.create(user1.getId(), channel1.getId(), "메시지1입니다."); b8ebdfb6-aa0e-407c-9575-58473258d0a8
//         Message message2 = messageService.create(user1.getId(), channel1.getId(), "메시지2입니다."); fb682e15-a502-4a26-a76e-d8fcd0559dfc
//         Message message3 = messageService.create(user4.getId(), channel1.getId(), "메시지3입니다."); c81b6995-6a42-49a6-ba50-04771e29573b
//         Message message4 = messageService.create(user4.getId(), channel1.getId(), "메시지4입니다."); b2f59762-3da4-40db-b79b-6f2c6c639522
////         Message message5 = messageService.create(user4.getId(), channel2.getId(), "메시지5입니다."); // 예외 발생 속해있지 않은 채널
//         Message message6 = messageService.create(user1.getId(), channel3.getId(), "메시지6입니다."); 5d71ac87-859d-44b4-9d6f-7658664d8a4b

        // 사전 작업: 테스트 용이하게 하기 위한
        Message message1 = messageService.getMessageOrThrow(UUID.fromString("b8ebdfb6-aa0e-407c-9575-58473258d0a8"));
        Message message2 = messageService.getMessageOrThrow(UUID.fromString("fb682e15-a502-4a26-a76e-d8fcd0559dfc"));
        Message message3 = messageService.getMessageOrThrow(UUID.fromString("c81b6995-6a42-49a6-ba50-04771e29573b"));
        Message message4 = messageService.getMessageOrThrow(UUID.fromString("b2f59762-3da4-40db-b79b-6f2c6c639522"));
        Message message6 = messageService.getMessageOrThrow(UUID.fromString("5d71ac87-859d-44b4-9d6f-7658664d8a4b"));

         // 메시지 단건 조회 -> 이미 수정을 한 번 해서 "메시지1 수정입니다."라고 출력
        Message findMessage1 = messageService.getMessageOrThrow(message1.getId());
        System.out.printf("찾은 메시지1의 내용: %s%n", findMessage1.getContent());

        // 메시지 다건 조회
        List<Message> messages = messageService.getAllMessage();
        System.out.println(messages.stream()
                .map(Message::getContent)
                .collect(Collectors.joining(" / ", "모든 메시지 출력: [", "]")));

        // 메시지 수정
        Message updatedMessage = messageService.update(user1.getId(), message1.getId(), "메시지1 수정입니다.");
        System.out.println("수정된 메시지 내용: " + messageService.getMessageOrThrow(updatedMessage.getId()).getContent());

        // 메시지 삭제 -> 테스트 용이성을 위해 주석
        // messageService.deleteById(user1.getId(), message1.getId()); // 유저 1의 "메시지1 수정입니다" 삭제
        // messageService.findById(message1.getId()); // 예외 발생

        // 채널에 있는 모든 메시지 확인 -> "채널명: 채널 1 메시지 내용: 메시지1 수정입니다." 는 삭제 처리 된 것
        List<Message> channelInMessage = messageService.getMessagesInChannel(channel1.getId());
        System.out.println(channelInMessage.stream()
                .map(message ->
                        "채널명: " + message.getChannel().getName() +
                                " 메시지 내용: " + message.getContent())
                .collect(Collectors.joining(" / ", "\n채널 1 메시지 전체 내용:\n[", "]")));

        // 채널에서 해당 유저가 보낸 모든 메시지 확인 -> "채널명: 채널 1 메시지 내용: 메시지1 수정입니다."는 삭제 처리 된 것
        List<Message> channelInUserMessage = messageService.getMessagesByUserInChannel(user1.getId(), channel1.getId());
        System.out.println(channelInUserMessage.stream()
                .map(message ->
                        "채널명: " + message.getChannel().getName() +
                                " 메시지 내용: " + message.getContent())
                .collect(Collectors.joining(" / ", "\n채널 1에 속한 유저1의 메시지 내용\n[", "]")));

        System.out.println("=".repeat(50));

        // ===================== JCF 구현 ===================== //
/*
        UserRepository userRepository = new JCFUserRepository();
        ChannelRepository channelRepository = new JCFChannelRepository();
        MessageRepository messageRepository = new JCFMessageRepository();

        UserService userService = new BasicUserService(userRepository);
        ChannelService channelService = new BasicChannelService(channelRepository, userRepository, userService);
        MessageService messageService = new BasicMessageService(messageRepository, userService, channelService);

        // 유저 서비스 구현 테스트
        // 등록
        User user1 = userService.create("종혁", "user1@test.com");
        User user2 = userService.create("유리", "user2@test.com");
        User user3 = userService.create("이씨", "user3@test.com");
        User user4 = userService.create("오씨", "user4@test.com");
        User user5 = userService.create("서씨", "user5@test.com");
        User user6 = userService.create("황씨", "user6@test.com");
        User user7 = userService.create("홍씨", "user7@test.com");

        // 조회(단건)
        System.out.println(userService.getUserOrThrow(user1.getId()).getName());

        // 수정 및 수정 확인
        userService.update(user1.getId(), "오종혁");
        System.out.println(user1.getName());

        // 조회(다건)
        List<User> users = userService.getAllUser();
        String result = users.stream()
                .map(User::getName)
                .collect(Collectors.joining(", ", "[", "]"));
        System.out.println(result);

        // 삭제 및 삭제확인
        userService.deleteUser(user1.getId());
        List<User> users2 = userService.getAllUser();
        String result2 = users2.stream()
                .map(User::getName)
                .collect(Collectors.joining(", ", "[", "]"));
        System.out.println(result2);
        // ==================================================

        System.out.println("=".repeat(50));

        // ==================================================
        // 현재 살아있는 유저(유리(user2), 이씨(user3), 오씨(user4), 서씨(user5), 황씨(user6), 홍씨(user7))
        // 채널 서비스 구현 테스트
        // 등록
        Channel channel1 = channelService.create(user2.getId(), "일반1");
        Channel channel2 = channelService.create(user3.getId(), "일반2");
        Channel channel3 = channelService.create(user6.getId(), "일반3");

        // 조회(단건)
        System.out.println(channelService.getChannelOrThrow(channel1.getId()).getName());
        System.out.println("사용자 이름: " + user2.getName());
        System.out.println("방장 이름: " + channel1.getOwner().getName());

        // 수정 및 수정 확인
        System.out.println("채널명 수정 전: " + channel1.getName());
        channel1.updateName("일반11");
        System.out.println("채널명 수정 후: " + channel1.getName());

        // 조회(다건)
        List<Channel> channels = channelService.getAllChannel();
        String channelResult = channels.stream()
                .map(Channel::getName)
                .collect(Collectors.joining(", ", "모든 채널 출력: [", "]"));
        System.out.println(channelResult);

        // 삭제 및 삭제확인
        List<Channel> beforeDeleteChannels1 = channelService.getAllChannel();
        String deleteChannelsResult1 = beforeDeleteChannels1.stream()
                .map(Channel::getName)
                .collect(Collectors.joining(", ", "채널 삭제 전 : [", "]"));
        System.out.println(deleteChannelsResult1);

        channelService.deleteChannel(channel1.getId()); // 채널1 삭제

        List<Channel> afterDeleteChannels2 = channelService.getAllChannel();
        String deleteChannelsResult2 = afterDeleteChannels2.stream()
                .map(Channel::getName)
                .collect(Collectors.joining(", ", "채널 삭제 후 : [", "]"));
        System.out.println(deleteChannelsResult2);

        // 채널에 유저 추가 및 확인
        Channel user4JoinChannel2 = channelService.joinChannel(user4.getId(), channel2.getId());
        System.out.printf("%s가 참여한 채널 이름: %s%n", user4.getName(), user4JoinChannel2.getName());
        boolean participated1 = user4.getChannels().stream()
                .anyMatch(channelId -> channelId.equals(user4JoinChannel2.getId()));
        if (participated1) {
            System.out.printf("%s는 %s에 참가하고 있습니다.%n", user4.getName(), user4JoinChannel2.getName());
        }

        Channel user5JoinChannel2 = channelService.joinChannel(user5.getId(), channel2.getId());
        System.out.printf("%s가 참여한 채널 이름: %s%n", user5.getName(), user5JoinChannel2.getName());
        boolean participated2 = user5.getChannels().stream()
                .anyMatch(channelId -> channelId.equals(user5JoinChannel2.getId()));
        if (participated2) {
            System.out.printf("%s는 %s에 참가하고 있습니다.%n", user5.getName(), user5JoinChannel2.getName());

        }

        // 예시용 데이터 추가
        channelService.joinChannel(user7.getId(), channel3.getId());

        // 유저5의 채널2 나가기 및 확인 (양방향 체크)
        Channel user5LeaveChannel2 = channelService.leaveChannel(user5.getId(), user4JoinChannel2.getId());

        // 채널2 안에 유저5가 없는지
        boolean isUser5StillIn = user5LeaveChannel2.getUsers().stream()
                .anyMatch(user -> user.equals(user5));
        if (!isUser5StillIn) {
            System.out.printf("채널:[%s] 에 유저:[%s] 가 존재하지 않습니다.%n",
                    user5LeaveChannel2.getName(), user5.getName());
        }

        // 유저5가 속했던 채널2 중 방금 나갔던 채널2가 없는지
        boolean channelRemovedFromUser = user5.getChannels()
                .stream()
                .noneMatch(channel -> channel.getId().equals(user5LeaveChannel2.getId()));
        if (channelRemovedFromUser) {
            System.out.printf("유저:[%s] 의 채널 목록에는 채널:[%s] 가 존재하지 않습니다.%n",
                    user5.getName(), user5LeaveChannel2.getName());
        }

        // ==================================================

        System.out.println("=".repeat(50));

        // ==================================================

        // 메시지 서비스 구현 테스트
        // 현재 살아있는 유저(유리(user2), 이씨(user3), 오씨(user4), 서씨(user5), 황씨(user6), 홍씨(user7))
        // 현재 살아있는 채널(일반2: 이씨(방장), 일반3: 황씨(방장), 홍씨)
        // 등록
        Message message = messageService.create(user3.getId(), channel2.getId(), "안녕하세요");
        Message message2 = messageService.create(user3.getId(), channel2.getId(), "이씨입니다.");
        Message message3 = messageService.create(user6.getId(), channel3.getId(), "반갑습니다");
        Message message4 = messageService.create(user6.getId(), channel3.getId(), "황씨입니다.");

        // 조회(단건)
        System.out.println("메시지 id로 조회: " + messageService.getMessageOrThrow(message.getId()).getContent());

        // 수정 및 수정 확인
        System.out.println("메시지 수정 전: " + message.getContent());
        messageService.update(message.getUser().getId(), message.getId(), "안녕하세요 !!");
        System.out.println("메시지 수정 후: " + message.getContent());

        // 조회(다건)
        List<Message> messages = messageService.getAllMessage();
        messages.forEach(m -> System.out.println("모든 채널의 메시지 출력: " + m.getContent()));

        // 조회(다건) 해당 채널 안의 메시지 표시
        List<Message> channelInMessage = messageService.getMessagesInChannel(channel2.getId());
        channelInMessage.forEach(m -> System.out.println("채널 안 메시지: " + m.getContent()));

        // 조회(다건) 해당 채널 안의 유저가 남긴 메시지 표시
        List<Message> channelInUserMessage = messageService.getMessagesByUserInChannel(user3.getId(), channel2.getId());
        channelInMessage.forEach(m -> System.out.println("채널 안 유저 메시지: " + m.getContent()));

        // 삭제 및 삭제확인
        List<Message> originalMessages = messageService.getAllMessage();
        String originalMessagesResult = originalMessages.stream()
                .map(Message::getContent)
                .collect(Collectors.joining(", ", "메시지 삭제 전: [", "]"));
        System.out.println(originalMessagesResult);

        messageService.deleteMessage(user3.getId(), message.getId()); // 안녕하세요 삭제

        List<Message> deleteMessages = messageService.getAllMessage();
        String deleteMessagesResult = deleteMessages.stream()
                .map(Message::getContent)
                .collect(Collectors.joining(", ", "메시지 삭제 후: [", "]"));
        System.out.println(deleteMessagesResult);

        // ==================================================

        System.out.println("=".repeat(50));

        // ==================================================

        // 특정 유저의 참가한 채널 리스트 조회
        User user10 = userService.create("10번유저", "user10@test.com");
        Channel user10JoinChannel2 = channelService.joinChannel(user10.getId(), channel2.getId());
        Channel user10JoinChannel3 = channelService.joinChannel(user10.getId(), channel3.getId());
        List<Channel> user10Channels = channelService.getUserChannels(user10.getId());
        String user10ChannelsResult = user10Channels.stream()
                .map(Channel::getName)
                .collect(Collectors.joining(", ", "[", "]"));
        System.out.printf("[%s]의 채널 리스트 %s%n", user10.getName(), user10ChannelsResult);

        // 특정 채널의 참가 중인 유저 리스트 조회
        List<User> channel2InUsers = userService.getChannelUsers(channel2.getId());
        String channel2InUsersResult = channel2InUsers.stream()
                .map(User::getName)
                .collect(Collectors.joining(", ", "[", "]"));
        System.out.printf("[%s]채널 안의 유저리스트 %s%n", channel2.getName(), channel2InUsersResult);

        System.out.println("=".repeat(50));

 */
    }

    private static void init(Path directory) {
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}