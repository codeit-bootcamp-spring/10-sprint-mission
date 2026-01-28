package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.file.*; // 파일 저장소를 쓰려면 import
import com.sprint.mission.discodeit.repository.jcf.*;  // 메모리 저장소를 쓰려면 import
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.basic.*;

public class JavaApplication {
    static User setupUser(UserService userService) {
        User user = userService.create("정혁");
        return user;
    }

    static Channel setupChannel(ChannelService channelService) {
        Channel channel = channelService.create("공지 채널");
        return channel;
    }

    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        Message message = messageService.create(author.getId(), channel.getId(), "안녕하세요.");
        System.out.println("메시지 생성 성공! ID: " + message.getId());
        System.out.println("내용: " + message.getContent());
    }

    public static void main(String[] args) {
        UserRepository userRepository = new FileUserRepository();
        ChannelRepository channelRepository = new FileChannelRepository();
        MessageRepository messageRepository = new FileMessageRepository();

        UserService userService = new BasicUserService(userRepository);
        ChannelService channelService = new BasicChannelService(channelRepository);
        MessageService messageService = new BasicMessageService(messageRepository, userRepository, channelRepository);

        System.out.println("=== 테스트 시작 ===");
        User user = setupUser(userService);
        System.out.println("유저 생성: " + user.getUsername());

        Channel channel = setupChannel(channelService);
        System.out.println("채널 생성: " + channel.getName());

        messageCreateTest(messageService, channel, user);
        System.out.println("=== 테스트 종료 ===");
    }
}