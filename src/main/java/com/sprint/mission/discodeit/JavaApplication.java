package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.List;

public class JavaApplication {

    public static void main(String[] args) {
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService(userService);
        MessageService messageService = new JCFMessageService(channelService, userService);

        // User 테스트
        System.out.println("=== 회원 테스트 ===");
        System.out.println("=== 회원 등록 ===");
        User user1 = userService.createUser("홍길동", "hong@test.com");
        User user2 = userService.createUser("김철수", "kim@test.com");
        User user3 = userService.createUser("박민수", "park@test.com");
        System.out.println("홍길동, 김철수, 박민수 등록 완료");

        System.out.println("\n=== 회원 단건 조회 ===");
        System.out.println(userService.findUserById(user1.getId()));
        System.out.println(userService.findUserById(user2.getId()));
        System.out.println(userService.findUserById(user3.getId()));

        System.out.println("\n=== 회원 다건 조회 ===");
        for (User user : userService.findAll()) {
            System.out.println(user);
        }

        System.out.println("\n=== 회원 수정 ===");
        userService.updateUserNickname(user2.getId(), "철수킴");
        System.out.println("김철수 → 철수킴");

        System.out.println("\n=== 회원 수정 후 조회 ===");
        System.out.println(userService.findUserById(user2.getId()));

        System.out.println("\n=== 회원 삭제 ===");
        userService.deleteUser(user3.getId());
        System.out.println("박민수 삭제");

        System.out.println("\n=== 회원 다건 조회 ===");
        for (User user : userService.findAll()) {
            System.out.println(user);
        }

        // Channel 테스트
        System.out.println("\n===============================");
        System.out.println("\n=== 채널 테스트 ===");
        System.out.println("=== 채널 생성 ===");

        Channel channel1 = channelService.createChannel("1번 채널", user1.getId());
        Channel channel2 = channelService.createChannel("2번 채널", user2.getId());
        Channel channel3 = channelService.createChannel("3번 채널", user1.getId());

        System.out.println(channel1);
        System.out.println(channel2);
        System.out.println(channel3);

        System.out.println("\n=== 채널 단건 조회 ===");
        System.out.println(channelService.findChannelById(channel1.getId()));
        System.out.println(channelService.findChannelById(channel2.getId()));
        System.out.println(channelService.findChannelById(channel3.getId()));

        System.out.println("\n=== 채널 다건 조회 ===");
        for (Channel channel : channelService.findAll()) {
            System.out.println(channel);
        }

        System.out.println("\n=== 채널 수정 ===");
        channelService.updateChannelName(channel2.getId(), "22222번 채널");
        System.out.println("2번 채널 → 22222번 채널");

        System.out.println("\n=== 채널 수정 후 조회 ===");
        System.out.println(channelService.findChannelById(channel2.getId()));

        System.out.println("\n=== 회원 다건 조회 ===");
        for (User user : userService.findAll()) {
            System.out.println(user);
        }

        System.out.println("\n=== 채널 삭제 ===");
        channelService.deleteChannel(channel3.getId());
        System.out.println("3번 채널 삭제");

        System.out.println("\n=== 채널 다건 조회 ===");
        for (Channel channel : channelService.findAll()) {
            System.out.println(channel);
        }

        System.out.println("\n=== 회원 다건 조회 ===");
        for (User user : userService.findAll()) {
            System.out.println(user);
        }

        // Message 테스트
        System.out.println("\n===============================");
        System.out.println("\n=== 메시지 테스트 (채널별) ===");

        List<Channel> channelList = channelService.findAll();
        for (Channel channel : channelList) {
            System.out.println("\n[채널: " + channel.getName() + "]");
            System.out.println("홍길동(user1) 채널 참가");

            channelService.joinChannel(channel.getId(), user1.getId());

            Message message1 = messageService.createMessage(channel.getId(), user1.getId(), "이곳은 " + channel.getName() + " 입니다.");
            Message message2 = messageService.createMessage(channel.getId(), user1.getId(), "안녕하세요");
            Message message3 = messageService.createMessage(channel.getId(), user1.getId(), "저는 홍길동입니다.");

            System.out.println("메시지 생성 완료");

            System.out.println("\n- 메시지 조회");
            messageService.readMessages(channel.getId())
                    .forEach(System.out::println);

            System.out.println("\n- 메시지 수정");
            messageService.updateMessageContent(
                    channel.getId(),
                    user1.getId(),
                    message1.getId(),
                    "이곳은 " + channel.getName() + " 입니다. 수정된 메시지입니다."
            );

            messageService.readMessages(channel.getId())
                    .forEach(System.out::println);

            System.out.println("\n- 메시지 삭제");
            messageService.deleteMessage(
                    channel.getId(),
                    user1.getId(),
                    message3.getId()
            );
            System.out.println(message3);

            System.out.println("\n- 메시지 조회");
            messageService.readMessages(channel.getId())
                    .forEach(System.out::println);
        }
    }
}
