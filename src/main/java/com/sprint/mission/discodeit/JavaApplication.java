//package com.sprint.mission.discodeit;
//
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.factory.ServiceFactory;
//import com.sprint.mission.discodeit.service.ChannelService;
//import com.sprint.mission.discodeit.service.MessageService;
//import com.sprint.mission.discodeit.service.UserService;
//
//public class JavaApplication {
//    static User setupUser(UserService userService) {
//        User user = userService.createPublicChannel("호야", "hoya@codeit.com");
//        System.out.println("유저 생성 완료: " + user.getUserNickname());
//        return user;
//    }
//
//    static Channel setupChannel(ChannelService channelService) {
//        Channel channel = channelService.createPublicChannel("호야의 채널");
//        System.out.println("채널 생성 완료: " + channel.getChannelName());
//        return channel;
//    }
//
//    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
//        Message message = messageService.createPublicChannel(author.getId(), channel.getId(), "난 호야입니다~");
//        System.out.println("메시지 생성 성공! ID: " + message.getId());
//        System.out.println("내용: " + message.getContent());
//    }
//
//    public static void main(String[] args) {
//        try {
//            UserService userService = ServiceFactory.getUserService();
//            ChannelService channelService = ServiceFactory.getChannelService();
//            MessageService messageService = ServiceFactory.getMessageService();
//
//            User user = setupUser(userService);
//            Channel channel = setupChannel(channelService);
//
//            channelService.addChannelByUserId(channel.getId(), user.getId());
//            System.out.println("채널 가입 완료");
//
//            messageCreateTest(messageService, channel, user);
//
//            System.out.println("현재 등록된 전체 유저 수: " + userService.findAll().size());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
