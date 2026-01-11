package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;


public class JavaApplication {
    public static void main(String[] args) {
        UserService userService = new JCFUserService();
        User user = userService.createUser("JEON", "ckdgus13@naver.com", "qweasd123");
        System.out.println(user.getUserStatus());

        ChannelService channelService = new JCFChannelService();
        Channel channel = channelService.createChannel(ChannelType.PUBLIC, "공지", "공지 채널입니다.");
        System.out.println(channel.getChannelStatus());

        MessageService messageService = new JCFMessageService();
        Message message = messageService.createMessage("안녕하세요.", channel.getId(), user.getId());
        System.out.println(message.getMessageStatus());
    }
}
