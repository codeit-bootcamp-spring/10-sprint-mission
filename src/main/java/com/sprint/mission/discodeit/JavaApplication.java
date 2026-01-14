package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.RoleGroup;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.RoleGroupService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFRoleGroupService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class JavaApplication {
    public static void main(String[] args) {
        //생성
        UserService userdata = JCFUserService.getInstance();
        ChannelService channeldata = JCFChannelService.getInstance();
        MessageService messagedata = JCFMessageService.getInstance();
        RoleGroupService groupdata = JCFRoleGroupService.getInstance();

        User user1 = userdata.create("초시");
        User user2 = userdata.create("초코");
        User user3 = userdata.create("경단");

        RoleGroup group1 = groupdata.create("허용 그룹");
        group1.addUser(user1);
        group1.addUser(user2);

        Channel ch1 = channeldata.create("코딩", "코딩 관련해서 이야기 하는 채널");
        Channel ch2 = channeldata.create("마인크래프트", "마인크래프트 이야기 하는 채널");

        Message mes1 = messagedata.create(user1.getId(), "이게 뭐지", ch1.getId());
        Message mes2 = messagedata.create(user2.getId(), "그게 뭔데", ch1.getId());

        //테스팅 내용
        channeldata.userJoinChannel(ch1.getId(), group1.getId());
        Message mes3 = messagedata.create(user1.getId(), "몰루?", ch1.getId());
        Message mes4 = messagedata.create(user2.getId(), "몰?루", ch1.getId());
        ch1.getMessages().stream().forEach(System.out::println);
        //Message mes5 = messagedata.create(user3.getId(), "?몰루", ch1.getId());


        //ch1.getMessages().stream().forEach(System.out::println);

    }
}
