package com.sprint.mission;

import com.sprint.mission.entity.Channel;
import com.sprint.mission.entity.Message;
import com.sprint.mission.entity.User;
import com.sprint.mission.service.ChannelService;
import com.sprint.mission.service.MessageService;
import com.sprint.mission.service.UserService;
import com.sprint.mission.service.file.FileChannelService;
import com.sprint.mission.service.file.FileMessageService;
import com.sprint.mission.service.file.FileUserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JavaApplication {
    public static void main(String[] args) {
        Path userDirectory = Paths.get(System.getProperty("user.dir"), "users");
        Path channelDirectory = Paths.get(System.getProperty("user.dir"), "channels");
        Path messageDirectory = Paths.get(System.getProperty("user.dir"), "messages");

        init(userDirectory);
        init(channelDirectory);
        init(messageDirectory);

        UserService userService = new FileUserService(userDirectory);
        ChannelService channelService = new FileChannelService(channelDirectory, userService);
        MessageService messageService = new FileMessageService(messageDirectory, userService, channelService);

        // 유저 테스트 =================================================

        // 파일로 저장하기 때문에 처음 실행을 제외한 실행 시 이미 있는 유저 예외처리를 막기 위해 주석처리
        // 유저 생성
//        User user1 = userService.create("1번유저", "user1@test.com"); b6a50b11-3d20-4508-a234-334e2d4aefc2
//        User user2 = userService.create("2번유저", "user2@test.com"); 4c5cc8c5-911f-4d25-a83f-c507fe9d6d0c
//        User user3 = userService.create("3번유저", "user3@test.com"); 127faac9-4b1b-4d3e-a667-a0bc21d7302c
//        User user4 = userService.create("4번유저", "user4@test.com"); 498bb20e-c621-4f05-9f9a-2ef09d1320ff
//        User user5 = userService.create("5번유저", "user5@test.com"); 251dc221-35cc-46e6-87ee-98a10883eef7

        // 유저 단건 조회
        User user1 = userService.findById(UUID.fromString("b6a50b11-3d20-4508-a234-334e2d4aefc2"));
        System.out.printf("1번 유저의 이름 조회: %s%n", user1.getName());
//
//        // 유저 다건 조회
//        System.out.println(userService.findAll().stream()
//                .map(User::getName)
//                .collect(Collectors.joining(", ", "유저 다건 조회: [", "]")));
//
        // 유저 수정 및 확인
        userService.update(user1.getId(), "1번유저 수정");
        System.out.println(user1.getName());
        System.out.printf("1번 유저의 수정된 이름 조회: %s%n",
                userService.findById(user1.getId()).getName());

           // 유저 삭제 및 확인 -> 테스트 용이성을 위해 주석
//        userService.deleteById(UUID.fromString("4c5cc8c5-911f-4d25-a83f-c507fe9d6d0c"));
//        System.out.println(userService.findAll().stream()
//                .map(User::getName)
//                .collect(Collectors.joining(", ", "2번 유저 삭제 확인: [", "]")));

        System.out.println("=".repeat(50));

        // 채널 테스트 =================================================

        // 사전작업: 테스트를 용이하게 하기 위한 유저 찾아오기
        User user3 = userService.findById(UUID.fromString("127faac9-4b1b-4d3e-a667-a0bc21d7302c"));
        User user4 = userService.findById(UUID.fromString("498bb20e-c621-4f05-9f9a-2ef09d1320ff"));
        User user5 = userService.findById(UUID.fromString("251dc221-35cc-46e6-87ee-98a10883eef7"));

        // 채널 생성 -> 유저 생성에서의 이유와 마찬가지로 주석처리
//        Channel channel1 = channelService.create(user1.getId(), "채널 1"); e3c31914-35ea-4444-b6e5-216437c29563
//        Channel channel2 = channelService.create(user3.getId(), "채널 2"); 68fbf1ac-298d-4752-8524-8c31034343aa
//        Channel channel3 = channelService.create(user1.getId(), "채널 3"); e59cbb04-eab6-4286-86ea-6a2ced5bf635

        // 채널 단건 조회
        Channel channel1 = channelService.findById(UUID.fromString("e3c31914-35ea-4444-b6e5-216437c29563"));
        Channel channel2 = channelService.findById(UUID.fromString("68fbf1ac-298d-4752-8524-8c31034343aa"));
        Channel channel3 = channelService.findById(UUID.fromString("e59cbb04-eab6-4286-86ea-6a2ced5bf635"));

        System.out.printf("방장: [%s], 채널명: [%s]%n", channel1.getOwner().getName(), channel1.getName());
        System.out.printf("방장: [%s], 채널명: [%s]%n", channel2.getOwner().getName(), channel2.getName());

        // 채널 다건 조회
        System.out.println(channelService.findAll().stream()
                .map(channel ->
                        channel.getName() + " 방장: " + channel.getOwner().getName())
                .collect(Collectors.joining(" / ", "전체 채널정보 조회: [", "]"))
        );

        // 채널 이름 수정
        channelService.update(channel3.getId(), "채널 3 수정");
        System.out.printf("수정된 채널 3 이름: [%s]%n", channelService.findById(channel3.getId()).getName());

       // 채널 삭제 및 확인 -> 테스트 용이성을 위해 주석
//        channelService.deleteById(channel3.getId());
//        System.out.println(channelService.findAll().stream()
//                .map(channel ->
//                        channel.getName() + " 방장: " + channel.getOwner().getName())
//                .collect(Collectors.joining(" / ", "전체 채널정보 조회: [", "]"))
//        );

        // 유저가 채널에 참여 및 채널에 있는 유저 확인
        channelService.joinChannel(user4.getId(), channel1.getId());
        channelService.joinChannel(user5.getId(), channel1.getId());

        System.out.println(channelService.findByChannelId(channel1.getId()).stream()
                .map(User::getName)
                .collect(Collectors.joining(", ", "채널에 참가한 유저 목록: [", "]")));

        // 유저가 채널을 떠남 및 떠난 채널의 유저 확인
        channelService.leaveChannel(user5.getId(), channel1.getId());
        System.out.println(channelService.findByChannelId(channel1.getId()).stream()
                .map(User::getName)
                .collect(Collectors.joining(", ", "채널에 참가한 유저 목록: [", "]")));

        System.out.println("=".repeat(50));

        // 메시지 테스트 =================================================
        // 현재 상황
        // 채널1: user1, user4
        // 채널2: user3

        // 메시지 생성
//        Message message1 = messageService.create(user1.getId(), channel1.getId(), "메시지1입니다."); 99431e8a-a4bb-4fe0-9108-7b9ff7ae2288
//        Message message2 = messageService.create(user1.getId(), channel1.getId(), "메시지2입니다."); e3ecb42f-dbbe-4ffa-90d3-5e73a8d5c3f1
//        Message message3 = messageService.create(user4.getId(), channel1.getId(), "메시지3입니다."); b4a580a0-fbac-4f2d-a160-3dbe51546a36
//        Message message4 = messageService.create(user4.getId(), channel1.getId(), "메시지4입니다."); 52ef8a10-f65a-4375-9b38-4a5adec26115
//        Message message5 = messageService.create(user4.getId(), channel2.getId(), "메시지5입니다."); // 예외 발생 속해있지 않은 채널
//        Message message6 = messageService.create(user1.getId(), channel3.getId(), "메시지5입니다."); f3778166-a1c2-4af3-99c1-31177a4fea18

        // 사전 작업: 테스트 용이하게 하기 위한
        Message message1 = messageService.findById(UUID.fromString("99431e8a-a4bb-4fe0-9108-7b9ff7ae2288"));
        Message message2 = messageService.findById(UUID.fromString("e3ecb42f-dbbe-4ffa-90d3-5e73a8d5c3f1"));
        Message message3 = messageService.findById(UUID.fromString("b4a580a0-fbac-4f2d-a160-3dbe51546a36"));
        Message message4 = messageService.findById(UUID.fromString("52ef8a10-f65a-4375-9b38-4a5adec26115"));
        Message message6 = messageService.findById(UUID.fromString("f3778166-a1c2-4af3-99c1-31177a4fea18"));

        // 메시지 단건 조회
        Message findMessage1 = messageService.findById(message1.getId());
        System.out.printf("찾은 메시지1의 내용: %s%n", findMessage1.getContent());

        // 메시지 다건 조회
        List<Message> messages = messageService.findAll();
        System.out.println(messages.stream()
                .map(Message::getContent)
                .collect(Collectors.joining(" / ", "모든 메시지 출력: [", "]")));

        // 메시지 수정
        Message updatedMessage = messageService.update(user1.getId(), message1.getId(), "메시지1 수정입니다.");
        System.out.println("수정된 메시지 내용: " + messageService.findById(updatedMessage.getId()).getContent());

        // 메시지 삭제 -> 테스트 용이성을 위해 주석
//        messageService.deleteById(user1.getId(), message1.getId()); // 유저 1의 "메시지1 수정입니다" 삭제
//        messageService.findById(message1.getId()); // 예외 발생

        // 채널에 있는 모든 메시지 확인 -> "채널명: 채널 1 메시지 내용: 메시지1 수정입니다." 는 삭제 처리 된 것
        List<Message> channelInMessage = messageService.findByChannelId(channel1.getId());
        System.out.println(channelInMessage.stream()
                .map(message ->
                        "채널명: " + message.getChannel().getName() +
                                " 메시지 내용: " + message.getContent())
                .collect(Collectors.joining(" / ", "\n채널 1 메시지 전체 내용:\n[", "]")));

        // 채널에서 해당 유저가 보낸 모든 메시지 확인 -> "채널명: 채널 1 메시지 내용: 메시지1 수정입니다." 는 삭제 처리 된 것
        List<Message> channelInUserMessage = messageService.findByUserIdAndChannelId(user1.getId(), channel1.getId());
        System.out.println(channelInUserMessage.stream()
                .map(message ->
                        "채널명: " + message.getChannel().getName() +
                                " 메시지 내용: " + message.getContent())
                .collect(Collectors.joining(" / ", "\n채널 1에 속한 유저1의 메시지 내용\n[", "]")));

        System.out.println("=".repeat(50));

//        UserService userService = new JCFUserService();
//        ChannelService channelService = new JCFChannelService(userService);
//        MessageService messageService = new JCFMessageService(userService, channelService);
//
//        // 유저 서비스 구현 테스트
//        // 등록
//        User user1 = userService.createUser("종혁", "user1@test.com");
//        User user2 = userService.createUser("유리", "user2@test.com");
//        User user3 = userService.createUser("이씨", "user3@test.com");
//        User user4 = userService.createUser("오씨", "user4@test.com");
//        User user5 = userService.createUser("서씨", "user5@test.com");
//        User user6 = userService.createUser("황씨", "user6@test.com");
//        User user7 = userService.createUser("홍씨", "user7@test.com");
//
//        // 조회(단건)
//        System.out.println(userService.findById(user1.getId()).getName());
//
//        // 수정 및 수정 확인
//        userService.updateUser(user1.getId(), "오종혁");
//        System.out.println(user1.getName());
//
//        // 조회(다건)
//        List<User> users = userService.findAll();
//        String result = users.stream()
//                .map(User::getName)
//                .collect(Collectors.joining(", ", "[", "]"));
//        System.out.println(result);
//
//        // 삭제 및 삭제확인
//        userService.deleteById(user1.getId());
//        List<User> users2 = userService.findAll();
//        String result2 = users2.stream()
//                .map(User::getName)
//                .collect(Collectors.joining(", ", "[", "]"));
//        System.out.println(result2);
//        // ==================================================
//
//        System.out.println("=".repeat(50));
//
//        // ==================================================
//        // 현재 살아있는 유저(유리(user2), 이씨(user3), 오씨(user4), 서씨(user5), 황씨(user6), 홍씨(user7))
//        // 채널 서비스 구현 테스트
//        // 등록
//        Channel channel1 = channelService.create(user2.getId(), "일반1");
//        Channel channel2 = channelService.create(user3.getId(), "일반2");
//        Channel channel3 = channelService.create(user6.getId(), "일반3");
//
//        // 조회(단건)
//        System.out.println(channelService.findById(channel1.getId()).getName());
//        System.out.println("사용자 이름: " + user2.getName());
//        System.out.println("방장 이름: " + channel1.getOwner().getName());
//
//        // 수정 및 수정 확인
//        System.out.println("채널명 수정 전: " + channel1.getName());
//        channel1.updateName("일반11");
//        System.out.println("채널명 수정 후: " + channel1.getName());
//
//        // 조회(다건)
//        List<Channel> channels = channelService.findAll();
//        String channelResult = channels.stream()
//                .map(Channel::getName)
//                .collect(Collectors.joining(", ", "모든 채널 출력: [", "]"));
//        System.out.println(channelResult);
//
//        // 삭제 및 삭제확인
//        List<Channel> beforeDeleteChannels1 = channelService.findAll();
//        String deleteChannelsResult1 = beforeDeleteChannels1.stream()
//                .map(Channel::getName)
//                .collect(Collectors.joining(", ", "채널 삭제 전 : [", "]"));
//        System.out.println(deleteChannelsResult1);
//
//        channelService.deleteById(channel1.getId()); // 채널1 삭제
//
//        List<Channel> afterDeleteChannels2 = channelService.findAll();
//        String deleteChannelsResult2 = afterDeleteChannels2.stream()
//                .map(Channel::getName)
//                .collect(Collectors.joining(", ", "채널 삭제 후 : [", "]"));
//        System.out.println(deleteChannelsResult2);
//
//        // 채널에 유저 추가 및 확인
//        Channel user4JoinChannel2 = channelService.joinChannel(user4.getId(), channel2.getId());
//        System.out.printf("%s가 참여한 채널 이름: %s%n", user4.getName(), user4JoinChannel2.getName());
//        boolean participated1 = user4.getChannels().stream()
//                .anyMatch(channelId -> channelId.equals(user4JoinChannel2.getId()));
//        if (participated1) {
//            System.out.printf("%s는 %s에 참가하고 있습니다.%n", user4.getName(), user4JoinChannel2.getName());
//        }
//
//        Channel user5JoinChannel2 = channelService.joinChannel(user5.getId(), channel2.getId());
//        System.out.printf("%s가 참여한 채널 이름: %s%n", user5.getName(), user5JoinChannel2.getName());
//        boolean participated2 = user5.getChannels().stream()
//                .anyMatch(channelId -> channelId.equals(user5JoinChannel2.getId()));
//        if (participated2) {
//            System.out.printf("%s는 %s에 참가하고 있습니다.%n", user5.getName(), user5JoinChannel2.getName());
//
//        }
//
//        // 예시용 데이터 추가
//        channelService.joinChannel(user7.getId(), channel3.getId());
//
//        // 유저5의 채널2 나가기 및 확인 (양방향 체크)
//        Channel user5LeaveChannel2 = channelService.leaveChannel(user5.getId(), user4JoinChannel2.getId());
//
//        // 채널2 안에 유저5가 없는지
//        boolean isUser5StillIn = user5LeaveChannel2.getUsers().stream()
//                .anyMatch(userId -> userId.equals(user5.getId()));
//        if (!isUser5StillIn) {
//            System.out.printf("채널:[%s] 에 유저:[%s] 가 존재하지 않습니다.%n",
//                    user5LeaveChannel2.getName(), user5.getName());
//        }
//
//        // 유저5가 속했던 채널2 중 방금 나갔던 채널2가 없는지
//        boolean channelRemovedFromUser = user5.getChannels()
//                .stream()
//                .noneMatch(channelId -> channelId.equals(user5LeaveChannel2.getId()));
//        if (channelRemovedFromUser) {
//            System.out.printf("유저:[%s] 의 채널 목록에는 채널:[%s] 가 존재하지 않습니다.%n",
//                    user5.getName(), user5LeaveChannel2.getName());
//        }
//
//        // ==================================================
//
//        System.out.println("=".repeat(50));
//
//        // ==================================================
//
//        // 메시지 서비스 구현 테스트
//        // 현재 살아있는 유저(유리(user2), 이씨(user3), 오씨(user4), 서씨(user5), 황씨(user6), 홍씨(user7))
//        // 현재 살아있는 채널(일반2: 이씨(방장), 일반3: 황씨(방장), 홍씨)
//        // 등록
//        Message message = messageService.createMessage(user3.getId(), channel2.getId(), "안녕하세요");
//        Message message2 = messageService.createMessage(user3.getId(), channel2.getId(), "이씨입니다.");
//        Message message3 = messageService.createMessage(user6.getId(), channel3.getId(), "반갑습니다");
//        Message message4 = messageService.createMessage(user6.getId(), channel3.getId(), "황씨입니다.");
//
//        // 조회(단건)
//        System.out.println("메시지 id로 조회: " + messageService.findById(message.getId()).getContent());
//
//        // 수정 및 수정 확인
//        System.out.println("메시지 수정 전: " + message.getContent());
//        messageService.updateMessage(message.getUser().getId(), message.getId(), "안녕하세요 !!");
//        System.out.println("메시지 수정 후: " + message.getContent());
//
//        // 조회(다건)
//        List<Message> messages = messageService.findAll();
//        messages.forEach(m -> System.out.println("모든 채널의 메시지 출력: " + m.getContent()));
//
//        // 조회(다건) 해당 채널 안의 메시지 표시
//        List<Message> channelInMessage = messageService.findByChannelId(channel2.getId());
//        channelInMessage.forEach(m -> System.out.println("채널 안 메시지: " + m.getContent()));
//
//        // 조회(다건) 해당 채널 안의 유저가 남긴 메시지 표시
//        List<Message> channelInUserMessage = messageService.findByUserIdAndChannelId(user3.getId(), channel2.getId());
//        channelInMessage.forEach(m -> System.out.println("채널 안 유저 메시지: " + m.getContent()));
//
//        // 삭제 및 삭제확인
//        List<Message> originalMessages = messageService.findAll();
//        String originalMessagesResult = originalMessages.stream()
//                .map(Message::getContent)
//                .collect(Collectors.joining(", ", "메시지 삭제 전: [", "]"));
//        System.out.println(originalMessagesResult);
//
//        messageService.deleteById(user3.getId(), message.getId()); // 안녕하세요 삭제
//
//        List<Message> deleteMessages = messageService.findAll();
//        String deleteMessagesResult = deleteMessages.stream()
//                .map(Message::getContent)
//                .collect(Collectors.joining(", ", "메시지 삭제 후: [", "]"));
//        System.out.println(deleteMessagesResult);
//
//        // ==================================================
//
//        System.out.println("=".repeat(50));
//
//        // ==================================================
//
//        // 특정 유저의 참가한 채널 리스트 조회
//        User user10 = userService.createUser("10번유저", "user10@test.com");
//        Channel user10JoinChannel2 = channelService.joinChannel(user10.getId(), channel2.getId());
//        Channel user10JoinChannel3 = channelService.joinChannel(user10.getId(), channel3.getId());
//        List<Channel> user10Channels = userService.findByUserId(user10.getId());
//        String user10ChannelsResult = user10Channels.stream()
//                .map(Channel::getName)
//                .collect(Collectors.joining(", ", "[", "]"));
//        System.out.printf("[%s]의 채널 리스트 %s%n", user10.getName(), user10ChannelsResult);
//
//        // 특정 채널의 참가 중인 유저 리스트 조회
//        List<User> channel2InUsers = channelService.findByChannelId(channel2.getId());
//        String channel2InUsersResult = channel2InUsers.stream()
//                .map(User::getName)
//                .collect(Collectors.joining(", ", "[", "]"));
//        System.out.printf("[%s]채널 안의 유저리스트 %s%n", channel2.getName(), channel2InUsersResult);
//
//        System.out.println("=".repeat(50));
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