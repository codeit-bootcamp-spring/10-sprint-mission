package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {
        //생성
        JCFUserService userdata = JCFUserService.getInstance();
        JCFChannelService channeldata = JCFChannelService.getInstance();
        JCFMessageService messagedata = JCFMessageService.getInstance();

        User user1 = userdata.create("초시");
        User user2 = userdata.create("초코");

        Channel ch1 = channeldata.create("코딩", "코딩 관련해서 이야기 하는 채널");
        Channel ch2 = channeldata.create("마인크래프트", "마인크래프트 이야기 하는 채널");

        Message mes1 = messagedata.create(user1.getId(), "이게 뭐지", ch1.getId());
        Message mes2 = messagedata.create(user2.getId(), "그게 뭔데", ch1.getId());

        //테스팅 내용

        ch1.getMessages().stream().forEach(System.out::println);

    }
}
