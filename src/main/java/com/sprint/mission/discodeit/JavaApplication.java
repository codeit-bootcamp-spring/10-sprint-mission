/*
package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.ChannelService;
import com.sprint.mission.discodeit.service.jcf.MessageService;
import com.sprint.mission.discodeit.service.jcf.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import static com.sprint.mission.discodeit.service.helper.ChannelHelper.*;
import static com.sprint.mission.discodeit.service.helper.MessageHelper.*;
import static com.sprint.mission.discodeit.service.helper.UserHelper.*;

public class JavaApplication {



    public static void main(String[] args){
        ChannelService channelService = new JCFChannelService();
        UserService userService = new JCFUserService(channelService);
        channelService.setUserService(userService);
        MessageService messageService = new JCFMessageService(channelService, userService);




        //Channel CRUD 테스트
        Channel ch1 = safeCreateChannel(channelService, "공지채널", "공지채널이에요", Channel.CHANNEL_TYPE.PUBLIC);
        Channel ch2 = safeCreateChannel(channelService,"q채널", "개인방", Channel.CHANNEL_TYPE.PRIVATE);
        Channel ch3 = safeCreateChannel(channelService, "게임채널", "hi", Channel.CHANNEL_TYPE.PUBLIC);

        User u1 = safeCreateUser(userService, "성경", "bible@naver.com", "bible123");
        User u2 = safeCreateUser(userService, "홀리", "holly@google.com", "holygoyly");

        safeJoinChannel(userService, u1, ch1);
        safeJoinChannel(userService, u2, ch1);

        safeUpdateUser(userService, u1.getUserId(), "호경겅ㄱ니", "WW.Nver.com");
        System.out.println(safeReadUser(userService, u1));
        channelService.readChannelsbyUser(u1.getUserId());







        Message m1 = safeCreateMsg(messageService, "안녕하세요", ch1, u1);
        Message m2 = safeCreateMsg(messageService, "안녕하세요GG", ch1, u1);
        //Message m2 = safeCreateMsg(messageService, "asdgag", ch2, u1);
        //Message m3 = safeCreateMsg(messageService, "a11231g", ch3, u1);

        safeReadMsg(messageService, m1);
        channelService.readChannelsbyUser(u1.getUserId());
        //messageService.readMessagebyChannel(ch1.getId());

    }
}
*/
