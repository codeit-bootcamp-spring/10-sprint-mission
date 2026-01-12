package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class JavaApplication {
    public static void main(String[] args) {
        JCFUserService userService = new JCFUserService();
        JCFChannelService channelService = new JCFChannelService();
        JCFMessageService messageService = new JCFMessageService(userService, channelService);
        runTest(userService, channelService, messageService);

    }
    private static void runTest(JCFUserService userService, JCFChannelService channelService, JCFMessageService messageService){
        System.out.println("데이터 등록");
        User user1 = userService.createUser("최현호","abc123@gmail.com");
        User user2 = userService.createUser("김민교","qqq123@gmail.com");
        Channel channel1 = channelService.createChannel("채팅","chat");
        Channel channel2 = channelService.createChannel("음성","voice");
        Message message1 = messageService.createMessage(channel1, user1, "안녕하세요");
        Message message2 = messageService.createMessage(channel2, user2, "반갑습니다");

        System.out.println("데이터 조회(단건)");
        System.out.println(userService.getUser(user1.getId()));
        System.out.println(channelService.getChannel(channel1.getId()));
        System.out.println(messageService.getMessage(message2.getId()));
        System.out.println("데이터 조회(다건)");
        System.out.println(userService.getAllUsers());
        System.out.println(channelService.getAllChannels());
        System.out.println(messageService.getAllMessages());

        System.out.println("데이터 수정");
        userService.updateUser(user1.getId(),"김현호","def123@gmail.com");
        channelService.updateChannel(channel1.getId(),"음성2","voice");
        messageService.updateMessage("하이요",message1.getId());

        System.out.println("수정된 데이터 조회");
        System.out.println(userService.getAllUsers());
        System.out.println(channelService.getAllChannels());
        System.out.println(messageService.getAllMessages());

        System.out.println("데이터 삭제");
        userService.deleteUser(user1.getId());
        channelService.deleteChannel(channel1.getId());
        messageService.deleteMessage(message1.getId());

        System.out.println("데이터 삭제 확인");
        System.out.println(userService.getAllUsers());
        System.out.println(channelService.getAllChannels());
        System.out.println(messageService.getAllMessages());

    }
}
