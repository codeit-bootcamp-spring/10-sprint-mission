package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MsgService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMsgService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class JavaApplication {
    public static void main(String[] args){
        ChannelService channelService = new JCFChannelService();
        UserService userService = new JCFUserService();
        MsgService msgService = new JCFMsgService();

        // User CRUD 테스트

        User u1 = userService.createUser("Jake", "jake@12334.com", "jake234");
        userService.createUser("Jake", "jake@123.com", "jake234");
        userService.updateUser("jake234", "jakeyo", "jakebodyh12345");
        User u2 = userService.createUser("HelloMy", "he@la321.com", "hellohel");

        userService.readAllUsers();

        //Channel CRUD 테스트
        Channel ch1 = channelService.createChannel("공지채널", "공지채널이에요", Channel.CHANNEL_TYPE.PUBLIC);
        Channel ch2 = channelService.createChannel("개인채널", "개인채널이에요", Channel.CHANNEL_TYPE.PRIVATE);
        channelService.updateChannel("개인채널", "쳐다보지마세요", null);

        channelService.readAllChannels();

        // Message CRUD 테스트
        Message m1 = msgService.createMessage("안녕하세요", ch1, u1 );
        Message m2 = msgService.createMessage("여기는 비밀채팅", ch2, u2);
        System.out.println(msgService.readMessageByAuthor(u1));
        System.out.println(msgService.readMessageByChannel(ch2));
        msgService.updateMessage(m1.getId(), "안녕하세요를 수정하였음.");
        System.out.println(msgService.readMessageByAuthor(u1));
        msgService.deleteMessage(m2.getId());
        System.out.println(msgService.readAllMessage());


    }
}
