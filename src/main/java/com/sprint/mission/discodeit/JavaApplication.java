package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.RoleService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFRoleService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import com.sprint.mission.discodeit.entity.PermissionLevel;

import static com.sprint.mission.discodeit.entity.PermissionLevel.ADMIN;
import static com.sprint.mission.discodeit.entity.PermissionLevel.USER;

public class JavaApplication {
    public static void main(String[] args) {
        //생성
        UserService userdata = JCFUserService.getInstance();
        ChannelService channeldata = JCFChannelService.getInstance();
        MessageService messagedata = JCFMessageService.getInstance();
        RoleService roledata = JCFRoleService.getInstance();

        User user1 = userdata.create("초시");
        User user2 = userdata.create("초코");
        User user3 = userdata.create("경단");

        Channel ch1 = channeldata.create("코딩", "코딩 관련해서 이야기 하는 채널");
        Channel ch2 = channeldata.create("마인크래프트", "마인크래프트 이야기 하는 채널");

        Role role1 = roledata.create(USER, user1.getId(), ch1.getId());
        Role role2 = roledata.create(USER, user2.getId(), ch1.getId());

        Message mes1 = messagedata.create(user1.getId(), "이게 뭐지", ch1.getId());
        Message mes2 = messagedata.create(user2.getId(), "그게 뭔데", ch1.getId());

        //테스팅 내용
        channeldata.printChannel(ch1.getId());

        roledata.update(role1.getId(), ADMIN);

        messagedata.delete(mes1.getId(), user2.getId());

        channeldata.printChannel(ch1.getId());

    }
}
