package com.sprint.mission;

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
        // Service 생성
        Factory factory = new Factory();

        UserService userService = factory.getUserService();
        ChannelService channelService = factory.getChannelService();
        MessageService messageService = factory.getMessageService();

        // Create
        User user1 = userService.create("JEON");
        User user2 = userService.create("KIM");

        Channel ch = channelService.create("codeit");

        ch.addMember(user1.getId());
        ch.addMember(user2.getId());
        user1.addJoinChannel(ch.getId());
        user2.addJoinChannel(ch.getId());
        Message msg = messageService.create("Test Message!!", user1.getId(), ch.getId());
        System.out.println("Created: " + msg.getId());

        // 생성 조회
        System.out.println(messageService.read(msg.getId()).getContents());

        // 수정 조회
        messageService.update(msg.getId(), "Update Message!!");
        System.out.println(messageService.read(msg.getId()).getContents());

        // 삭제
        messageService.delete(msg.getId());
        System.out.println(messageService.read(msg.getId()));
    }
}