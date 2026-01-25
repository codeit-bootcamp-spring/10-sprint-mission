package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.RoleRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFRoleRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.RoleService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicRoleService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

import java.io.IOException;

import static com.sprint.mission.discodeit.entity.PermissionLevel.ADMIN;
import static com.sprint.mission.discodeit.entity.PermissionLevel.USER;

public class JavaApplication {
    public static void main(String[] args) throws IOException {
        //레포지토리 생성
        RoleRepository roleRepository = JCFRoleRepository.getInstance();
        UserRepository userRepository = JCFUserRepository.getInstance();
        ChannelRepository channelRepository = JCFChannelRepository.getInstance();
        MessageRepository messageRepository = JCFMessageRepository.getInstance();

        UserService userdata = new BasicUserService(userRepository);
        ChannelService channeldata = new BasicChannelService(channelRepository, userRepository, roleRepository, messageRepository);
        MessageService messagedata = new BasicMessageService(messageRepository, userRepository, channelRepository, roleRepository);
        RoleService roledata = new BasicRoleService(roleRepository, userdata, channeldata);

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

        //레포에 잘 들어갔는지 확인
        System.out.println("====레포에 잘 들어갔는지 체크!====");
        System.out.println(roledata.findAll());
        System.out.println(channeldata.findAll());
        System.out.println(messagedata.findAll());
        System.out.println(userdata.findAll());

        //동기화 체크
        System.out.println("====동기화 체크!====");
        channeldata.find(ch1.getId())
                .getMessagesID()
                .stream()
                .forEach(id -> System.out.println(messagedata.find(id)));

        channeldata.find(ch1.getId())
                .getRolesID()
                .stream()
                .forEach(id -> System.out.println(roledata.find(id)));

        //수정
        System.out.println("====수정 체크!====");

        channeldata.updateUserRole(ch1.getId(), user2.getId(), USER, user1.getId());
        System.out.println(roledata.findAll());
        messagedata.update(mes1.getId(), "이게 뭐ㅓ어ㅓㅓㅓ어ㅓㅓ어어러어");
        System.out.println(messagedata.findAll());
        channeldata.update(ch1.getId(), "코딩하는채널", "어쩌고 저쩌고");
        System.out.println(channeldata.findAll());
        userdata.update(user3.getId(), "경-단");
        System.out.println(userdata.findAll());

        //삭제
        System.out.println("====삭제 체크!====");
        roledata.delete(role1.getId());
        System.out.println(roledata.findAll());
        userdata.delete(user2.getId());
        System.out.println(userdata.findAll());
        channeldata.delete(ch2.getId(), user3.getId());
        System.out.println(channeldata.findAll());
        messagedata.delete(mes1.getId(), user1.getId());

        //삭제 후 동기화 체크
        System.out.println("====삭제 후 동기화 체크!====");
        channeldata.find(ch1.getId())
                .getMessagesID()
                .stream()
                .forEach(id -> System.out.println(messagedata.find(id)));

        channeldata.find(ch1.getId())
                .getRolesID()
                .stream()
                .forEach(id -> System.out.println(roledata.find(id)));
    }
}
