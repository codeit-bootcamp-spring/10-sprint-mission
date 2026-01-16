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

        Role role1 = roledata.create(ADMIN, user1.getId(), ch1.getId());
        Role role2 = roledata.create(USER, user2.getId(), ch1.getId());
        Role role3 = roledata.create(ADMIN, user3.getId(), ch2.getId());

        Message mes1 = channeldata.addMessage(ch1.getId(), user1.getId(), "이게 뭐지?");
        Message mes2 = channeldata.addMessage(ch1.getId(), user2.getId(), "그게 뭔데?");
        Message mes3 = channeldata.addMessage(ch2.getId(), user3.getId(), "몰?루");

        //조회
        System.out.println("------ 이하 단건조회 ------");
        channeldata.find(ch1.getId()).printChannel();
        System.out.println("------ 이하 다건조회 ------");
        channeldata.findAll().forEach(Channel::printChannel);


        //수정
        System.out.println("------ 이하 수정 ------");
        channeldata.update(ch1.getId(), "코딩하는 채널", "말그대로 코딩 하는 채널");//채널정보 수정
        messagedata.update(mes1.getId(), "이게 뭐어어어어어지?");//메시지 수정
        userdata.update(user3.getId(), "경-단");//유저이름 수정
        channeldata.updateUserRole(ch1.getId(), user2.getId(), ADMIN, user1.getId());//권한등급 수정
        channeldata.findAll().forEach(Channel::printChannel);

        //삭제
        System.out.println("------ 이하 삭제 ------");
        messagedata.delete(mes1.getId(), user1.getId());//전송된 메시지 삭제
        roledata.delete(role2.getId());//권한 삭제
        channeldata.delete(ch2.getId(), user3.getId());//채널 삭제
        channeldata.findAll().forEach(Channel::printChannel);
    }
}
