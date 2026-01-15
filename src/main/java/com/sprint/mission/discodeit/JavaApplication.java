package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.factory.ServiceFactory;
import com.sprint.mission.discodeit.service.*;

public class JavaApplication {
    public static void main(String[] args) {
        ServiceFactory factory = ServiceFactory.getInstance();
        UserService userService = factory.userService();
        ChannelService channelService = factory.channelService();
        MessageService messageService = factory.messageService();

        User user1 = userService.create("정혁");
        Channel channel1 = channelService.create("헬스 정보 공유방");
        channelService.joinChannel(channel1.getId(), user1.getId());
        System.out.println("채널 참가자: " + channelService.findUsersByChannelId(channel1.getId()));
        System.out.println("유저의 채널 목록: " + userService.findById(user1.getId()).getChannels());
        channelService.leaveChannel(channel1.getId(), user1.getId());
        System.out.println("탈퇴 후 참가자: " + channelService.findUsersByChannelId(channel1.getId()));
    }

}
