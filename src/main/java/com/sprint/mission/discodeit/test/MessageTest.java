//package com.sprint.mission.discodeit.test;
//
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.service.ChannelService;
//import com.sprint.mission.discodeit.service.MessageService;
//import com.sprint.mission.discodeit.service.UserService;
//import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
//import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
//import com.sprint.mission.discodeit.service.jcf.JCFUserService;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
//public class MessageTest {
//    public static void main(String[] args) {
//        Map<UUID, User> userRepo = new HashMap<>();
//        Map<UUID, Channel> channelRepo = new HashMap<>();
//        Map<UUID, Message> messageRepo = new HashMap<>();
//
//        MessageService messageService = new JCFMessageService(messageRepo);
//        ChannelService channelService = new JCFChannelService(channelRepo, messageService);
//        UserService userService = new JCFUserService(userRepo, channelService, messageService);
//
//
//        System.out.println("===== user1, user2, user3 생성 =====");
//        User user1 = userService.createUser("park@gmail.com", "user1Nick", "user1Name", "1234", "20000000");
//        User user2 = userService.createUser("jung@gmail.com", "user2Nick", "user2Name", "1234", "20101010");
//        User user3 = userService.createUser("hyun@gmail.com", "user3Nick", "user3Name", "1234", "20101020");
//        System.out.println(userService);
//
//
//        System.out.println("===== channel1, channel2 생성 =====");
//        Channel channel1 = channelService.createChannel(user1, false, "channel1General", "General Channel");
//        Channel channel2 = channelService.createChannel(user2, true, "channel2Private", "private Channel");
//        System.out.println(channelService);
//
//        System.out.println("===== user1 channel2에 참여 =====");
//        userService.joinChannel(user1.getId(), user1.getId(), channel2);
//        System.out.println(user1.getJoinChannelList());
////        userService.joinChannel(user1.getId(), user1.getId(), channel2); // 참여 여부 테스트
//        System.out.println("===== user3 channel1에 참여 =====");
//        userService.joinChannel(user3.getId(), user3.getId(), channel1);
//        System.out.println(user3.getJoinChannelList());
//
//
//        System.out.println("===== Message1, 2, 3 생성 =====");
//        Message message1 = messageService.createMessage(channel1, user1, "channel1user1message1");
//        Message message2 = messageService.createMessage(channel2, user1, "channel2user1message2");
//        Message message3 = messageService.createMessage(channel1, user3, "channel1user3message3");
//        System.out.println(message1.getId());
//        System.out.println(message2.getId());
//        System.out.println(message3.getId());
////        Message message4 = messageService.createMessage(channel2, user3, "channel1user1message1"); // 해당 채널에 참여하지 않은 author가 작성 시도
//
////        System.out.println("===== 유저1가 가진 메시지 목록 =====");
////        System.out.println(user1.getWriteMessageList());
////        System.out.println("===== 채널1이 가진 메시지 목록 =====");
////        System.out.println(channel1.getChannelMessagesList());
//
//        System.out.println("===== 특정 메시지 정보 읽기 =====");
//        System.out.println(messageService.readMessageById(message1.getId()));
//
//        System.out.println("===== 모든 메시지 정보 읽기 =====");
//        System.out.println(messageService.readAllMessage());
//
//        System.out.println("===== 메시지 수정 =====");
//        System.out.println(messageService.readMessageById(message1.getId()));
////        System.out.println(messageService.readMessageById(message3.getId()));
//        System.out.println(messageService.updateMessageContent(user1.getId(), message2.getId(), "update"));
//        System.out.println(messageService.readMessageById(message1.getId()));
//
//        System.out.println("===== 메세지 삭제 =====");
//        System.out.println(messageService.readMessageById(message2.getId()));
//        System.out.println(user1.getWriteMessageList());
//        System.out.println(channel2.getChannelMessagesList());
//        messageService.deleteMessage(user1.getId(), message2.getId());
////        System.out.println(messageService.readMessageById(message2.getId()));
//        System.out.println(user1.getWriteMessageList());
//        System.out.println(channel2.getChannelMessagesList());
//
//
//        System.out.println("===== channel1 삭제 =====");
//        System.out.println(channelService.readChannelByChannelId(channel1.getId()));
//        System.out.println(user1.getOwnerChannelList());
//        System.out.println(user1.getJoinChannelList());
//        channelService.deleteChannel(user1.getId(), channel1.getId());
//        System.out.println(channelService.readChannelByChannelId(channel1.getId()));
//        System.out.println(user1.getOwnerChannelList());
//        System.out.println(user1.getJoinChannelList());
//
//        System.out.println("===== user1 삭제 =====");
//        System.out.println(userService.readUserById(user1.getId()));
//        System.out.println(channel2.getChannelUsersList());
//        userService.deleteUser(user1.getId(), user1.getId());
//        System.out.println(userService.readUserById(user1.getId()));
//        System.out.println(channel2.getChannelUsersList());
//
//
//
//
//    }
//}
