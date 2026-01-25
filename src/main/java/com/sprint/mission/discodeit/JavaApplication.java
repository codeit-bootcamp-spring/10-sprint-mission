package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;

public class JavaApplication {
    public static void main(String[] args) {
//        UserService userService = new JCFUserService();
//        ChannelService channelService = new JCFChannelService();
//        MessageService messageService = new JCFMessageService(userService, channelService);

        UserService userService = new FileUserService();
        ChannelService channelService = new FileChannelService();
        MessageService messageService = new FileMessageService(userService, channelService);

        testFile(userService, channelService, messageService);
    }

    public static void testFile(UserService userService, ChannelService channelService, MessageService messageService){
        userService.deleteAll();
        channelService.deleteAll();
        messageService.deleteAll();

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
        try {
            System.out.println(userService.findById(user1.getId()) + " 조회 성공");

        } catch (Exception e) {
            System.out.println(e);
        }

        System.out.println("---다건 조회---");
        System.out.println("전체 유저 수: " + userService.findAll().size());

        System.out.println("---수정/조회---");
        System.out.println("이름 수정 전 " + user1.getUserName());
        user1 = userService.update(user1.getId(), "일", null);
        System.out.println("이름 수정 후 " + user1.getUserName());

        System.out.println("이름 수정 전 " + user2.getUserName());
        try {
            user2 = userService.update(null,"이", null);
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("이름 수정 후 " + user2.getUserName());


        System.out.println("이메일 수정 전 " + user2.getEmail());
        try {
            user2 = userService.update(user2.getId(), null,"");
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("이메일 수정 후 " + user2.getUserName());

        System.out.println("이메일 수정 전 " + user3.getEmail());
        try {
            user3 = userService.update(user3.getId(), null, "     ");
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
        Message msg2 = messageService.createMessage("12345", user2.getId(), channel2.getId());
        Message msg3 = messageService.createMessage("가나다라", user3.getId(), channel3.getId());

        System.out.println("---단건조회---");
        System.out.println(messageService.findId(msg3.getId()).getId() + " 조회 성공");

        System.out.println("---다건 조회---");
        System.out.println("전체 메세지 수: " + messageService.findAll().size());

        System.out.println("---수정/조회---");
        System.out.println("메세지 수정 전 " + messageService.findId(msg2.getId()).getContent());
        try {
            msg2 = messageService.update(messageService.findId(msg2.getId()).getId(), "1aaa5");
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("메세지 수정 후 " + msg2.getContent());
        System.out.println("메세지 수정 전 " + msg3.getContent());
        try {
            msg3 = messageService.update(msg3.getId(), "");
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("메세지 수정 후 " + msg3.getContent());
        System.out.println("메세지 수정 전 " + msg3.getContent());
        try {
            msg3 = messageService.update(msg3.getId(), "     ");
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("메세지 수정 후 " + msg3.getContent());

        System.out.println("---삭제---");
        System.out.println("메세지 삭제 전 전체 메세지 수: " + messageService.findAll().size());
        messageService.delete(msg2.getId());
        System.out.println("메세지 삭제 후 전체 메세지 수: " + messageService.findAll().size());
    }
}
