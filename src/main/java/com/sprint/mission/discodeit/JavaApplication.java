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

import static com.sprint.mission.discodeit.service.ChannelHelper.*;

public class JavaApplication {



    public static void main(String[] args){
        ChannelService channelService = new JCFChannelService();
        UserService userService = JCFUserService();
        MsgService msgService = new JCFMsgService();


        userService.readAllUsers(); // 다건 조회

        userService.updateUser("hajiel234","Adrian","adrian@naver.com"); // 특정 유저의 이름과 이메일 변경
        userService.readAllUsers(); // 다건 조회

        //userService.deleteUser("hajiel234");
        //userService.deleteUser(u1.getId());
        //userService.readAllUsers(); // 다건 조회


        //Channel CRUD 테스트
        Channel ch1 = safeCreateChannel(channelService, "공지채널", "공지채널이에요", Channel.CHANNEL_TYPE.PUBLIC);
        Channel ch2 = safeCreateChannel(channelService,"개인채널", null, Channel.CHANNEL_TYPE.PRIVATE);
        Channel ch3 = safeCreateChannel(channelService, "게임채널", "hi", Channel.CHANNEL_TYPE.PUBLIC);

        safeDeleteChannel(channelService, "ㄹㄹ체날");
        safeDeleteChannel(channelService, "공지채널");


        safeReadChannel(channelService, ch1); // 단건 조회
        safeReadChannel(channelService, ch2);

        safeUpdateChannel(channelService, ch3, "hogsg",null,null);
//        channelService.readChannel(ch2.getId()); // UUID 를 이용한 채널 단건 조회
//        channelService.readAllChannels(); // 다건 조회
//
//
//        channelService.updateChannel("게임채널", "프라이빗게임공간", Channel.CHANNEL_TYPE.PRIVATE);
        //channelService.readChannel("게임채널");
//

        channelService.readAllChannels();
//        channelService.readAllChannels();

//        u1.joinChannel(ch1);
//        u2.joinChannel(ch1);
//        u1.joinChannel(ch2);
//        u1.joinChannel(ch3);
//        u3.joinChannel(ch3);
//        u2.exitChannel(ch1);
//        u2.exitChannel(ch1);
//        ch1.getUsers();
//        u1.getChannelList();


        // Message CRUD 테스트
//        Message m1 = msgService.createMessage("안녕하세요", ch1, u1 );
//        Message m2 = msgService.createMessage("여기는 비밀채팅", ch2, u2);
//        Message m3 = msgService.createMessage("여기는 비밀채팅22", ch2, u1);
//
//        msgService.deleteMessage(m1.getId());
//        m1.getUser();
//        u1.getMsgList();

//        msgService.readMessageByAuthor(u1);
//        msgService.readMessageByChannel(ch2);
//        msgService.updateMessage(m1.getId(), "안녕하세요를 수정하였음.");
//        System.out.println(msgService.readMessageByAuthor(u1));
//        msgService.deleteMessage(m2.getId());
//        System.out.println(msgService.readAllMessage());


    }

    private static UserService getUserService() {
        UserService userService = new JCFUserService();
        MsgService msgService = new JCFMsgService();


        // User CRUD 테스트

        User u1 = userService.createUser("Jake", "jake@12334.com", "jake234");
        User u2 = userService.createUser("HelloMy", "he@la321.com", "hellohel");
        User u3 = userService.createUser("Hailey", "jheily@g2123.com", "hajiel234");

        userService.readUser(u1.getId()); // u1 유저의 UUID를 통하여 data에서 조회
        userService.readUser("hellohel"); // u2 유저의 아이디를 통하여 data에서 조회
        return userService;
    }
}
