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

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {
        System.out.println("========== 테스트 시작 ==========");
        Map<UUID, User> userRepo = new HashMap<>();
        Map<UUID, Channel> channelRepo = new HashMap<>();
        Map<UUID, Message> messageRepo = new HashMap<>();

        MessageService messageService = new JCFMessageService(messageRepo);
        ChannelService channelService = new JCFChannelService(channelRepo, messageService);
        UserService userService = new JCFUserService(userRepo, channelService, messageService);

        System.out.println("========== 1. 등록 및 조회 ==========");
        System.out.println("========== 1-1. user1, user2 생성 ==========");
        User user1 = null;
        User user2 = null;
        try {
            user1 = userService.createUser("user1@email,com", "user1Nick", "[user1]Name", "1111", "20001111");
            user2 = userService.createUser("user2@email,com", "user2Nick", "[user2]Name", "2222", "20002222");
            System.out.println("[user1] = " + user1.getId() + ", [user2] = " + user2.getId());
        } catch (IllegalArgumentException | IllegalStateException | NoSuchElementException | NullPointerException e) {
            System.out.println(e);
        }

        System.out.println("\n========== 1-2. channel1, channel2 생성 ==========");
        Channel channel1 = null;
        Channel channel2 = null;
        try {
            channel1 = channelService.createChannel(user1, false, "[channel1]", "channel1");
            channel2 = channelService.createChannel(user2, true, "[channel2]", "channel2");
            System.out.println("[channel1] = " + channel1.getId() + ", [channel2] = " + channel2.getId());
            System.out.println("[channel1Owner] = " + channel1.getOwner().getId() + ", [channel2Owner] = " + channel2.getOwner().getId());
        } catch (IllegalArgumentException | IllegalStateException | NoSuchElementException | NullPointerException e) {
            System.out.println(e.getMessage());
        }


        System.out.println("\n========== 1-3. user1이 channel2에 참여 ==========");
        try {
            System.out.println("(전)channel2 참여자들 = " + channelService.readAllUsersByChannelId(channel2.getId()).stream().map(user -> user.getId()).toList());
            userService.joinChannel(user1.getId(), user1.getId(), channel2);
            System.out.println("(후)channel2 참여자들 = " + channelService.readAllUsersByChannelId(channel2.getId()).stream().map(user -> user.getId()).toList());
        } catch (IllegalArgumentException | IllegalStateException | NoSuchElementException | NullPointerException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("\n========== 1-4. message1, message2, message3 생성 ==========");
        Message message1 = null;
        Message message2 = null;
        Message message3 = null;
        try {
            System.out.println("(전)user1의 message list(message1,2) = " + userService.readUserMessagesByUserId(user1.getId()).stream().map(message -> message.getId()).toList());
            System.out.println("(전)channel1의 모든 message = " + channelService.readChannelMessageByChannelId(channel1.getId()).stream().map(message -> message.getId()).toList());
            message1 = messageService.createMessage(channel1, user1, "channel1user1[message1]");
            message2 = messageService.createMessage(channel1, user1, "channel1user1[message2]");
            message3 = messageService.createMessage(channel1, user2, "channel2user1[message3]");
            System.out.println("[message1] = " + message1.getId() + ", [message2] = " + message2.getId() + ", [message3] = " + message3.getId());
            System.out.println("(후)user1의 message list(message1,2) = " + userService.readUserMessagesByUserId(user1.getId()).stream().map(message -> message.getId()).toList());
            System.out.println("(후)channel1의 모든 message = " + channelService.readChannelMessageByChannelId(channel1.getId()).stream().map(message -> message.getId()).toList());
        } catch (IllegalArgumentException | IllegalStateException | NoSuchElementException | NullPointerException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("\n========== 2. 수정 및 수정된 데이터 조회 ==========");
        System.out.println("========== 2-1. user1의 userName 수정 후 조회 ==========");
        try {
            System.out.println("(전)user1 userName = " + userService.readUserById(user1.getId()).get().getUserName()); // [user1]Name
            userService.updateUserName(user1.getId(), user1.getId(), "[update]user1Name");
            System.out.println("(후)user1 userName = " + userService.readUserById(user1.getId()).get().getUserName()); // [update]user1Name
        } catch (IllegalArgumentException | IllegalStateException | NoSuchElementException | NullPointerException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("\n========== 2-2. channel1의 channelName 수정 후 조회 ==========");
        try {

            System.out.println("(전)channel1 channelName = " + channelService.readChannelByChannelId(channel1.getId()).get().getChannelName());
            channelService.updateChannelName(user1.getId(), channel1.getId(), "[update]channel1Name");
            System.out.println("(후)channel1 channelName = " + channelService.readChannelByChannelId(channel1.getId()).get().getChannelName());
            System.out.println("(후)user1이 참여한 channelName 리스트 = " + userService.readUserJoinChannelsByUserId(user1.getId()).stream().map(channel -> channel.getChannelName()).toList());
        } catch (IllegalArgumentException | IllegalStateException | NoSuchElementException | NullPointerException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("\n========== 2-3. channel2의 owner(user2)를 user1으로 변경 후 조회 ==========");
        try {
            System.out.println("[channel2 id] = " + channelService.readChannelByChannelId(channel2.getId()).get().getId());
            System.out.println("[user1 id] = " + userService.readUserById(user1.getId()).get().getId() + ", [user2 id] = " + userService.readUserById(user2.getId()).get().getId() + ", [channel2 owner] = " + channelService.readChannelByChannelId(channel2.getId()).get().getOwner().getId());
            System.out.println("[user1 ownerlist 업데이트 여부] = " + userService.readUserById(user1.getId()).get().getOwnerChannelList().stream().map(channel -> channel.getId()).toList());
            channelService.updateChannelOwner(user2.getId(), channel2.getId(), user1);
            System.out.println("[user1 id] = " + userService.readUserById(user1.getId()).get().getId() + ", [user2 id] = " + userService.readUserById(user2.getId()).get().getId() + ", [channel2 owner] = " + channelService.readChannelByChannelId(channel2.getId()).get().getOwner().getId());
            System.out.println("[user1 ownerlist 업데이트 여부] = " + userService.readUserById(user1.getId()).get().getOwnerChannelList().stream().map(channel -> channel.getId()).toList());
//            System.out.println("-----원상 복귀-----");
//            channelService.updateChannelOwner(user1.getId(), channel2.getId(), user2);
//            System.out.println("[user1 ownerlist 업데이트 여부] = " + userService.readUserById(user1.getId()).get().getOwnerChannelList().stream().map(channel -> channel.getId()).toList() + ", [user2 ownerlist 업데이트 여부] = " + userService.readUserById(user2.getId()).get().getOwnerChannelList().stream().map(channel -> channel.getId()).toList());
        } catch (IllegalArgumentException | IllegalStateException | NoSuchElementException | NullPointerException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("\n========== 2-4. message2의 content 수정 후 조회 ==========");
        try {
            System.out.println("(전)[message2 content] = " + message2.getContent());
            messageService.updateMessageContent(user1.getId(), message2.getId(), "[update]channel1user1[message2]");
            System.out.println("(후)[message2 content] = " + message2.getContent());
        } catch (IllegalArgumentException | IllegalStateException | NoSuchElementException | NullPointerException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("\n========== 3. 삭제 및 삭제 후 데이터 조회 ==========");
//        System.out.println("========== 3-1. message1 삭제 및 삭제 후 데이터 조회 ==========");
//        try {
//            System.out.println("(전)[message1 id] = " + message1.getId());
//            System.out.println("(전)[channel1의 message list] = " + channel1.getChannelMessagesList().stream().map(message -> message.getId()).toList());
//            System.out.println("(전)[user1의 message list] = " + user1.getWriteMessageList().stream().map(message -> message.getId()).toList());
//            messageService.deleteMessage(user1.getId(), message1.getId());
//            System.out.println("(후)[channel1의 message list] = " + channel1.getChannelMessagesList().stream().map(message -> message.getId()).toList());
//            System.out.println("(후)[user1의 message list] = " + user1.getWriteMessageList().stream().map(message -> message.getId()).toList());
//        } catch (IllegalArgumentException | IllegalStateException | NoSuchElementException | NullPointerException e) {
//            System.out.println(e.getMessage());
//        }

//        System.out.println("========== 3-2. channel1 삭제 및 삭제 후 데이터 조회 ==========");
//        try {
//            System.out.println("(전)[channel1 id] = " + channel1.getId());
//            System.out.println("(전)[user1의 join channel list] = " + user1.getJoinChannelList().stream().map(channel -> channel.getId()).toList());
//            System.out.println("(전)[user1의 owner channel list] = " + user1.getOwnerChannelList().stream().map(channel -> channel.getId()).toList());
//            System.out.println("(후)[channel1의 존재 여부(channel1 id)] = " + channelService.readChannelByChannelId(channel1.getId()).get().getId());
//            channelService.deleteChannel(user1.getId(), channel1.getId());
//            System.out.println("(후)[user1의 join channel list] = " + user1.getJoinChannelList().stream().map(channel -> channel.getId()).toList());
//            System.out.println("(후)[user1의 owner channel list] = " + user1.getOwnerChannelList().stream().map(channel -> channel.getId()).toList());
//            System.out.println("(후)[channel1 존재 여부] = " + channelService.readChannelByChannelId(channel1.getId()));
//
//        } catch (IllegalArgumentException | IllegalStateException | NoSuchElementException | NullPointerException e) {
//            System.out.println(e);
//        }

//        System.out.println("========== 3-3. user1 삭제 및 삭제 후 데이터 조회 ==========");
//        try {
//            System.out.println("(전)[user1 id] = " + user1.getId());
//            System.out.println("(전)[channel1 id] = " + channel1.getId());
//            userService.deleteUser(user1.getId(), user1.getId());
//            System.out.println("(후)[channel1(user1이 owner) 존재 여부] = " + channelService.readChannelByChannelId(channel1.getId()));
////            System.out.println("(후)[channel2(user1이 owner) 존재 여부] = " + channelService.readChannelByChannelId(channel2.getId()));
//            System.out.println("(후)[message1 존재 여부] = " + messageService.readMessageById(message1.getId()));
//            System.out.println("(후)[user1 존재 여부] = " + userService.readUserById(user1.getId()));
//        } catch (IllegalArgumentException | IllegalStateException | NoSuchElementException | NullPointerException e) {
//            System.out.println(e);
//        }


        System.out.println("==========  ==========");
        System.out.println("\n==========  ==========");
    }
}
