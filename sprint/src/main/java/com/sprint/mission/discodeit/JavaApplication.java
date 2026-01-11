package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.UUID;

public class JavaApplication {

    public static void main(String[] args) {
        // 서비스 초기화
        UserService userService = JCFUserService.getInstance();
        ChannelService channelService = JCFChannelService.getInstance();
        MessageService messageService = JCFMessageService.getInstance(userService, channelService);

        // 유저 등록
        userService.createUser("Raphael", "contact@example.com");
        UUID b = userService.createUser("Luce", "contact2@example.com");
        userService.createUser("Gabriel", "contact3@example.com");

        // 유저 조회
        System.out.println("=== 유저 === ");
        User qUser = userService.getUserByUsername("Raphael");
        System.out.println("Username: " + qUser.getUsername()); // Raphael
        System.out.println("Email: " + qUser.getEmail());

        User qUser2 = userService.getUser(b);
        System.out.println("Username: " + qUser2.getUsername()); // Raphael
        System.out.println("Email: " + qUser2.getEmail());

        // 모든 유저 조회
        Iterable<User> users = userService.getAllUsers();
        System.out.println("\n== 모든 유저 ==");
        users.forEach((user) -> {
            System.out.println("Username: " + user.getUsername());
            System.out.println("Email: " + user.getEmail() + '\n');
        });

        // 유저 정보 업데이트
        userService.updateUser(qUser.getId(), "Raphael", "raphael@example.com");
        System.out.println("== 업데이트 후 ==");
        System.out.println("Username: " + qUser.getUsername()); // Raphael
        System.out.println("Email: " + qUser.getEmail());

        // 유저 삭제
        User userTarget = userService.getUserByUsername("Luce");
        UUID targetId = userTarget.getId();
        try {
            userService.deleteUser(targetId);
            userService.deleteUser(targetId); // Must be exception occurred
        } catch (NotFoundException e) {
            System.err.println(e.getMessage());
        }
        if (userService.getUser(targetId) == null) {
            System.out.println("사용자 " + targetId + "를 찾을 수 없었습니다.");
        }

        // 채널 개설
        UUID geographyChannelId = channelService.createChannel("Victoria", "세계지리, 세계사와 관련 게임을 좋아하는 사람들의 채널");
        UUID computerChannelId = channelService.createChannel("LinuxNFreeBSD", "리눅스 모임 채널");

        // 채널 조회
        Channel ch = channelService.getChannel(geographyChannelId);
        System.out.println("\nChannel: " + ch.getTitle()); // Raphael
        System.out.println("Description: " + ch.getDescription());

        // 모든 채널 조회
        Iterable<Channel> chs = channelService.getAllChannels();
        System.out.println("\n== 모든 채널 ==");
        chs.forEach((channel) -> {
            System.out.println("Channel: " + channel.getTitle());
            System.out.println("Description: " + channel.getDescription() + '\n');
        });

        // 채널 정보 업데이트
        channelService.updateChannel(computerChannelId, "LinuxNFreeBSD", "리눅스와 FreeBSD 모임 채널");
        ch =  channelService.getChannel(computerChannelId);
        System.out.println("== 업데이트 후 ==");
        System.out.println("Channel: " + ch.getTitle()); // Raphael
        System.out.println("Description: " + ch.getDescription() + '\n');


        // 채널 삭제
        try {
            channelService.deleteChannel(computerChannelId);
            channelService.deleteChannel(computerChannelId);
        } catch (NotFoundException e) {
            System.err.println(e.getMessage());
        }
        if (channelService.getChannel(computerChannelId) == null) {
            System.out.println("채널 " + computerChannelId + "를 찾을 수 없었습니다.");
        }


        // 메시지
        ch =  channelService.getChannel(geographyChannelId);
        User user3 = userService.getUserByUsername("Gabriel");
        try {
            messageService.addMessage(qUser, ch, "혹시 패러독스 빅토리아 3 게임 해 보신 분 있나요?");
            Thread.sleep(10);
            messageService.addMessage(user3, ch, "해보진 않았어. 대신 유로파를 좋아해.");
            Thread.sleep(10);
            messageService.addMessage(qUser, ch, "나도 좋아하는데 유로파, 근데 너무 어려움 ㅇㅇ");
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }


        // 메시지 조회
        Iterable<Message> messages = messageService.getUserMessages(qUser);
        System.out.println("\n=== 메시지 ===");
        messages.forEach((msg) -> {
            System.out.println(msg.getUser().getUsername() + ": " + msg.getText() + " [" + msg.getCreatedAt() + "]");
        });

        messages = messageService.getAllMessages();
        System.out.println("\n== 모든 메시지 ==");
        messages.forEach((msg) -> {
            System.out.println(msg.getUser().getUsername() + ": " + msg.getText() + " [" + msg.getCreatedAt() + "]");
        });

        // 메시지 수정
        UUID targetMessageId = messageService.addMessage(qUser, ch, "DLS 뭐 사야 하지?");
        System.out.println("\n== 메시지 수정 ==");
        System.out.println("변경 전: " + messageService.getMessage(targetMessageId).getText());
        messageService.updateMessage(targetMessageId, "빅토리아 3 DLC 뭐 사면 되지?");
        System.out.println("변경 후: " + messageService.getMessage(targetMessageId).getText());

        // 메시지 삭제
        try {
            messageService.deleteMessage(targetMessageId);
            messageService.deleteMessage(targetMessageId);
        } catch (NotFoundException e) {
            System.err.println(e.getMessage());
        }
        if (messageService.getMessage(targetMessageId) == null) {
            System.out.println("메시지 " + targetMessageId + "를 찾을 수 없었습니다.");
        }
    }
}
