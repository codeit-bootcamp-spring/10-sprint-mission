package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class JavaApplication {
    public static void main(String[] args) {
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();
        MessageService messageService = new JCFMessageService();
        /*
        * [ ] 등록
        [ ] 조회(단건, 다건)
        [ ] 수정
        [ ] 수정된 데이터 조회
        [ ] 삭제
        [ ] 조회를 통해 삭제되었는지 확인
        * */

        System.out.println("-------------user-------------");

        System.out.println("---등록---");
        User user1 = userService.CreateUser("하나", "test1@codeit.com");
        User user2;
        try {
            user2 = userService.CreateUser("둘", "test1@codeit.com");
        } catch (Exception e) {
            System.out.println(e);
        }
        user2 = userService.CreateUser("둘", "test2@codeit.com");
        User user3 = userService.CreateUser("셋", "test3@codeit.com");

        System.out.println("---단건조회---");
        System.out.println(userService.findId(user1.getId()) + " 조회 성공");

        System.out.println("---다건 조회---");
        System.out.println("전체 유저 수: " + userService.findAll().size());

        System.out.println("---수정/조회---");
        System.out.println("이름 수정 전 " + user1.getUserName());
        user1 = userService.updateName(user1.getId(), "일");
        System.out.println("이름 수정 후 " + user1.getUserName());

        System.out.println("이름 수정 전 " + user2.getUserName());
        try {
            user2 = userService.updateName(null,"이");
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("이름 수정 후 " + user2.getUserName());


        System.out.println("이메일 수정 전 " + user2.getEmail());
        try {
            user2 = userService.updateName(user2.getId(), "");
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("이메일 수정 후 " + user2.getUserName());

        System.out.println("이메일 수정 전 " + user3.getEmail());
        try {
            user3 = userService.updateName(user3.getId(), "     ");
        } catch (Exception e) {
            System.out.println(e);
        }

        System.out.println("---삭제---");
        System.out.println("계정 삭제 전 전체 유저 수: " + userService.findAll().size());
        userService.delete(user1.getId());
        System.out.println("계정 삭제 후 전체 유저 수: " + userService.findAll().size());



        System.out.println("-------------Channel-------------");

        System.out.println("---등록---");
        Channel channel1 = channelService.createChannel("채널1");
        Channel channel2 = channelService.createChannel("채널2");
        Channel channel3 = channelService.createChannel("채널3");

        System.out.println("---단건조회---");
        System.out.println(channelService.findId(channel1.getId()) + " 조회 성공");

        System.out.println("---다건 조회---");
        System.out.println("전체 유저 수: " + channelService.findAll().size());

        System.out.println("---수정/조회---");
        System.out.println("채널명 수정 전 " + channel1.getChannelName());
        channelService.update(channel1.getId(), "ch11111");
        System.out.println("채널명 수정 후 " + channel1.getChannelName());

        System.out.println("채널명 수정 전 " + channel2.getChannelName());
        try {
            channel2 = channelService.update(channel2.getId(), "");
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("채널명 수정 후 " + channel2.getChannelName());

        System.out.println("채널명 수정 전 " + channel3.getChannelName());
        try {
            channel3 = channelService.update(channel3.getId(), "       ");
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("채널명 수정 후 " + channel3.getChannelName());


        System.out.println("---삭제---");
        System.out.println("채널 삭제 전 전체 채널 수: " + channelService.findAll().size());
        channelService.delete(channel1.getId());
        System.out.println("채널 삭제 후 전체 채널 수: " + channelService.findAll().size());


        System.out.println("-------------Message-------------");
        System.out.println("---등록---");
        Message msg2 = messageService.createMessage("12345", user2, channel2);
        Message msg3 = messageService.createMessage("가나다라", user3, channel3);

        System.out.println("---단건조회---");
        System.out.println(messageService.findId(msg3) + " 조회 성공");

        System.out.println("---다건 조회---");
        System.out.println("전체 메세지 수: " + messageService.findAll().size());

        System.out.println("---수정/조회---");
        System.out.println("메세지 수정 전 " + msg2.getContent());
        try {
            msg3 = messageService.update(msg3, "1aaa5");
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("메세지 수정 후 " + msg2.getContent());
        System.out.println("메세지 수정 전 " + msg3.getContent());
        try {
            msg3 = messageService.update(msg3, "");
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("메세지 수정 후 " + msg3.getContent());
        System.out.println("메세지 수정 전 " + msg3.getContent());
        try {
            msg3 = messageService.update(msg3, "     ");
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("메세지 수정 후 " + msg3.getContent());

        System.out.println("---삭제---");
        System.out.println("메세지 삭제 전 전체 메세지 수: " + messageService.findAll().size());
        messageService.delete(msg2);
        System.out.println("메세지 삭제 후 전체 메세지 수: " + messageService.findAll().size());
    }
}
