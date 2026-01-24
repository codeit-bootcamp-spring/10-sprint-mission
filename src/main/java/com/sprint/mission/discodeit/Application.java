package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.helper.ChannelHelper;
import com.sprint.mission.discodeit.service.helper.UserHelper;
import com.sprint.mission.discodeit.service.jcf.ChannelService;
import com.sprint.mission.discodeit.service.jcf.MessageService;
import com.sprint.mission.discodeit.service.jcf.UserService;

import java.util.Optional;

public class Application {

    static User setupUser(UserService userService) {
        User user = userService.createUser("woody", "woody@codeit.com", "woody1234");
        return user;
    }

    static Channel setupChannel(ChannelService channelService) {
        Channel channel = channelService.createChannel("공지", "공지 채널입니다.", Channel.CHANNEL_TYPE.PUBLIC);
        return channel;
    }

    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        Message message = messageService.createMessage("안녕하세요.", channel.getId(), author.getUserId());
        System.out.println("메시지 생성: " + message.getId());
    }

    public static void main(String[] args) {
        UserService userService;
        ChannelService channelService;
        MessageService messageService;

        userService = new BasicUserService(new FileUserRepository());
        channelService = new BasicChannelService(new FileChannelRepository());
        messageService = new BasicMessageService(new FileMessageRepository(), userService, channelService);

        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);

        messageCreateTest(messageService, channel, user);
    }




}
