package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MsgService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.helper.MessageHelper;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMsgService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import static com.sprint.mission.discodeit.service.helper.ChannelHelper.*;
import static com.sprint.mission.discodeit.service.helper.MessageHelper.*;
import static com.sprint.mission.discodeit.service.helper.UserHelper.*;

public class JavaApplication {



    public static void main(String[] args){
        ChannelService channelService = new JCFChannelService();
        UserService userService = new JCFUserService(channelService);
        MsgService msgService = new JCFMsgService(channelService, userService);




        //Channel CRUD 테스트
        Channel ch1 = safeCreateChannel(channelService, "공지채널", "공지채널이에요", Channel.CHANNEL_TYPE.PUBLIC);
        Channel ch2 = safeCreateChannel(channelService,"개인채널", "개인방", Channel.CHANNEL_TYPE.PRIVATE);
        Channel ch3 = safeCreateChannel(channelService, "게임채널", "hi", Channel.CHANNEL_TYPE.PUBLIC);

        User u1 = safeCreateUser(userService, "성경", "bible@naver.com", "bible123");




    }
}
