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
        JCFChannelService channelService = new JCFChannelService(userService);
        JCFMessageService messageService = new JCFMessageService(userService, channelService);

        System.out.println("=== 정상 흐름 테스트 ===");
        runTest(userService, channelService, messageService);

        System.out.println("\n=== 유효성 검증 실패 테스트 ===");
        runValidationTest(userService, channelService, messageService);
    }
    private static void runTest(JCFUserService userService, JCFChannelService channelService, JCFMessageService messageService){
        System.out.println("데이터 등록");
        User user1 = userService.createUser("최현호","abc123@gmail.com");
        User user2 = userService.createUser("김민교","qqq123@gmail.com");
        Channel channel1 = channelService.createChannel("채팅","chat");
        Channel channel2 = channelService.createChannel("음성","voice");
        Channel channel3 = channelService.createChannel("잡담", "chat");
        Message message1 = messageService.createMessage(channel1.getId(), user1.getId(), "안녕하세요");
        Message message2 = messageService.createMessage(channel2.getId(), user2.getId(), "반갑습니다");
        messageService.createMessage(channel1.getId(), user2.getId(), "교이루~");
        messageService.createMessage(channel2.getId(), user1.getId(), "Hello~");
        messageService.createMessage(channel3.getId(), user2.getId(), "반갑소");

        System.out.println("데이터 조회(단건)");
        System.out.println(userService.getUser(user1.getId()));
        System.out.println(channelService.getChannel(channel1.getId()));
        System.out.println(messageService.getMessage(message2.getId()));
        System.out.println("데이터 조회(다건)");
        System.out.println(userService.getAllUsers());
        System.out.println(channelService.getAllChannels());
        System.out.println(messageService.getAllMessages());
        System.out.println(messageService.getMessagesByChannel(channel1.getId()));
        System.out.println("추가기능 테스트");
        System.out.println(messageService.getMessagesByChannel(channel1.getId()));
        System.out.println(channelService.getChannelsByUserId(user2.getId()));


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
        channelService.leaveChannel(user2.getId(),channel2.getId());


        System.out.println("데이터 삭제 확인");
        System.out.println(userService.getAllUsers());
        System.out.println(channelService.getAllChannels());
        System.out.println(messageService.getAllMessages());
        System.out.println("김민교가 속해있는 채널");
        System.out.println(channelService.getChannelsByUserId(user2.getId()));

    }

    private static void runValidationTest(JCFUserService userService, JCFChannelService channelService, JCFMessageService messageService) {
        try {
            System.out.print("[테스트 1] 이름 없이 유저 생성: ");
            userService.createUser("", "test@test.com");
        } catch (IllegalArgumentException e) {
            System.out.println("성공 (" + e.getMessage() + ")");
        }

        try {
            System.out.print("[테스트 2] 존재하지 않는 ID로 채널 조회: ");
            channelService.getChannel(java.util.UUID.randomUUID());
        } catch (IllegalArgumentException e) {
            System.out.println("성공 (" + e.getMessage() + ")");
        }

        try {
            System.out.print("[테스트 3] 저장되지 않은 유저 객체로 메시지 생성: ");
            Channel channel = channelService.createChannel("테스트채널", "chat");
            User ghostUser = new User("유령", "ghost@test.com");
            messageService.createMessage(channel.getId(), ghostUser.getId(), "안녕");
        } catch (IllegalArgumentException e) {
            System.out.println("성공 (" + e.getMessage() + ")");
        }

        try {
            System.out.print("[테스트 4] 빈 메시지 내용으로 생성: ");
            User user = userService.createUser("테스터", "tester@test.com");
            Channel channel = channelService.getAllChannels().get(0);
            messageService.createMessage(channel.getId(), user.getId(), "  ");
        } catch (IllegalArgumentException e) {
            System.out.println("성공 (" + e.getMessage() + ")");
        }

        try {
            System.out.print("[테스트 5] 삭제된 유저 수정 시도: ");
            User user = userService.createUser("삭제될사람", "delete@test.com");
            userService.deleteUser(user.getId());
            userService.updateUser(user.getId(), "새이름", null);
        } catch (IllegalArgumentException e) {
            System.out.println("성공 (" + e.getMessage() + ")");
        }
    }
}
