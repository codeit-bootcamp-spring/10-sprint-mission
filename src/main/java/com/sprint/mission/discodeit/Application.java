package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.jcf.ChannelService;
import com.sprint.mission.discodeit.service.jcf.MessageService;
import com.sprint.mission.discodeit.service.jcf.UserService;

public class Application {

    static User setupUser(UserService userService){
        return userService.createUser("성경","tjdrud@naver.com", "tjdrud");
    }

    static Channel setupChannel(ChannelService channelService){
        return channelService.createChannel("공지","공지채널입니다.", Channel.CHANNEL_TYPE.PUBLIC);
    }

    static void messageCreateTest(MessageService messageService, Channel channel, User user){
        Message message = messageService.createMessage("안녕하세요", channel.getId(), user.getUserId());
        System.out.println("메시지 생성: " + message.getId());
    }

    public static void main(String[] args) {

        ChannelRepository channelRepository = new FileChannelRepository();
        BasicChannelService channelService = new BasicChannelService(channelRepository);

        channelService.createChannel("공지1","공지채널입니다.", Channel.CHANNEL_TYPE.PUBLIC);
        channelService.createChannel("공지2","공지채널입니다.", Channel.CHANNEL_TYPE.PUBLIC);

        System.out.println(channelService.readAllChannels());




    }
}
