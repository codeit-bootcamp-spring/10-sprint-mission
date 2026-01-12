package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        // 사용자 테스트
        JCFUserService userService = new JCFUserService();

        User user1 = userService.createUser("dlekthf0906@codeit.com",
                                           "1234567890",
                                           "LeeDyol");

        System.out.println("단건 조회 테스트: "
                + userService.searchUser(user1.getId()).getNickname());    // 예상 출력: LeeDyol

        User user2 = userService.createUser("Yushi0405@codeit.com",
                                        "1234567890",
                                        "tokuno");

        ArrayList<User> users = userService.searchUserAll();

        System.out.println("다건 조회 테스트");                                // 에상 출력: Lee Dyol, tokuno
        for (User user : users) {
            System.out.println(user.getNickname());
        }

        userService.updateUser(user2.getId(), "", "sakuya");

        System.out.println("수정 테스트: "
                + userService.searchUser(user2.getId()).getNickname());     // 예상 출력: sakuya

        userService.deleteUser(user1.getId());

        System.out.println("삭제 테스트");                                    // 예상 출력: sakuya
        for (User user : users) {
            System.out.println(user.getNickname());
        }

        System.out.println("=========================");

        // 채널 테스트
        JCFChannelService channelService = new JCFChannelService();

        Channel channel1 = channelService.createChannel("Codeit",
                                                        user2,
                                                        ChannelType.CHAT);

        System.out.println("단건 조회 테스트: "
                + channelService.searchChannel(channel1.getId()).getChannelName());    // 예상 출력: Codeit

        Channel channel2 = channelService.createChannel("Book Club",
                                                        user2,
                                                        ChannelType.VOICE);

        ArrayList<Channel> channels = channelService.searchChannelAll();

        System.out.println("다건 조회 테스트");                                           // 에상 출력: Codeit, Book Club
        for (Channel channel : channels) {
            System.out.println(channel.getChannelName());
        }

        channelService.updateChannel(channel2.getId(), "Study Club");

        System.out.println("수정 테스트: "
                + channelService.searchChannel(channel2.getId()).getChannelName());   // 예상 출력: Study Club

        channelService.deleteChannel(channel1.getId());

        System.out.println("삭제 테스트");                                               // 예상 출력: Study Club
         for (Channel channel : channels) {
            System.out.println(channel.getChannelName());
        }

        System.out.println("=========================");

        // 메시지 테스트
        JCFMessageService messageService = new JCFMessageService();

        Message message1 = messageService.createMessage("안녕하세요",
                                                        user2,
                                                        channel2,
                                                        MessageType.CHAT);

        System.out.println("단건 조회 테스트: "
                + messageService.searchMessage(message1.getId()).getMessage());       // 예상 출력: 안녕하세요

        Message message2 = messageService.createMessage("안녕 못하네요",
                                                        user2,
                                                        channel2,
                                                        MessageType.CHAT);

        ArrayList<Message> messages = messageService.searchMessageAll();

        System.out.println("다건 조회 테스트");                                          // 예상 출력: 안녕하세요, 안녕 못하네요
        for (Message message : messages) {
            System.out.println(message.getMessage());
        }

        messageService.updateMessage(message2.getId(), "죄송해요. 안녕합니다");

        System.out.println("수정 테스트: "
                + messageService.searchMessage(message2.getId()).getMessage());      // 예상 출력: 죄송해요 안녕합니다

        messageService.deleteMessage(message1.getId());

        System.out.println("삭제 테스트");                                              // 예상 출력: 죄송해요 안녕합니다
        for (Message message : messages) {
            System.out.println(message.getMessage());
        }
    }
}