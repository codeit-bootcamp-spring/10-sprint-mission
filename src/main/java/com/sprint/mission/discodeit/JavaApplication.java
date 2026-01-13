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
import static com.sprint.mission.discodeit.service.UserHelper.*;

public class JavaApplication {



    public static void main(String[] args){
        ChannelService channelService = new JCFChannelService();
        UserService userService = new JCFUserService();
        MsgService msgService = new JCFMsgService();




        //Channel CRUD 테스트
        Channel ch1 = safeCreateChannel(channelService, "공지채널", "공지채널이에요", Channel.CHANNEL_TYPE.PUBLIC);
        Channel ch2 = safeCreateChannel(channelService,"개인채널", null, Channel.CHANNEL_TYPE.PRIVATE);
        Channel ch3 = safeCreateChannel(channelService, "게임채널", "hi", Channel.CHANNEL_TYPE.PUBLIC);

        // SafeDeleteChannel을 통해 내부적으로 try - catch 를 통한 예외 처리.
        safeDeleteChannel(channelService, "ㄹㄹ체날"); // Exception 발생
        safeDeleteChannel(channelService, "공지채널"); // 성공적으로 삭제

        safeReadChannel(channelService, ch1); // Exception 발생
        safeReadChannel(channelService, ch2); // Exception 발생
        safeReadChannel(channelService, ch3); // 조회 성공

        safeUpdateChannel(channelService, ch3, "hogsg",null,null); // 성공적으로 update
        channelService.readAllChannels();

        // User CRUD 테스트

        User u1 = safeCreateUser(userService, "성경", "tjdrud@naver.com", "bible");
        User u2 = safeCreateUser(userService, "성경", "tjdrud@naver.com", "bible");
        User u3 = safeCreateUser(userService, "헬루", "hello@naver.com", "hello");

        safeDeleteUser(userService, u2);

        safeUpdateUser(userService, u3, "bible", "경성", null);
        safeReadUser(userService, u2);
        safeReadUser(userService, u3);
        safeReadUser(userService, u2);


        userService.readAllUsers();







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
}
