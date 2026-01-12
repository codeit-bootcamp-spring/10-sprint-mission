package com.sprint.mission;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {

        JCFUserService jcfUserService = new JCFUserService();
        JCFMessageService jcfMessageService = new JCFMessageService();
        JCFChannelService jcfChannelService = new JCFChannelService(jcfMessageService);

        System.out.println("------------------- 유저 서비스 테스트 -------------------");
        System.out.println();

        // UserService 테스트
        User alice = new User("Alice");
        jcfUserService.createUser(alice);
        System.out.println("Alice 추가 " + jcfUserService.getUserList());
        jcfUserService.updateUserName(alice.getUserId(), "Bob");
        System.out.println("Alice -> Bob 변경 " + jcfUserService.getUserList());

        UUID userId = alice.getUserId();
        System.out.println("변경된 Bob의 id: " + userId);

        jcfUserService.deleteUser(userId);
        System.out.println("Bob 삭제 " + jcfUserService.getUserList());

        System.out.println();
        System.out.println("------------------- 서비스 통합 테스트 -------------------");
        System.out.println();

        User charlie = jcfUserService.createUser(new User("Charlie"));
        User david = jcfUserService.createUser(new User("David"));
        // ChannelService 테스트
        Channel testChannel = jcfChannelService.createChannel("Test Channel");
        System.out.println("채널 생성 후: " + jcfChannelService.getChannelList());
        // 채널에 유저 추가
        jcfChannelService.joinChannel(testChannel.getChannelId(), charlie);
        jcfChannelService.joinChannel(testChannel.getChannelId(), david);
        System.out.println("채널에 유저 추가 후: " + jcfChannelService.getChannelList());
        // 채널 이름 변경
        jcfChannelService.updateChannelName(testChannel.getChannelId(), "NMIXX Channel");
        System.out.println("채널 이름 변경 후: " + jcfChannelService.getChannelList());

        System.out.println();
        System.out.println("------------------- 메시지 서비스 테스트 -------------------");
        System.out.println();

        // MessageService 테스트
        jcfMessageService.sendMessage(charlie, testChannel, "Hello, World!");
        jcfMessageService.sendMessage(charlie, testChannel, "This is test");
        jcfMessageService.sendMessage(charlie, testChannel, "for testing");
        System.out.println("메시지 전송 후: " + jcfMessageService.getAllMessages());

        for (var user : jcfUserService.getUserList()) {
            System.out.println(user.getUsername() + "의 메시지 조회: " + jcfMessageService.getMessageListByUser(user.getUserId()));
        }
        System.out.println("채널별 메시지 조회: " + jcfMessageService.getMessageListByChannel(testChannel.getChannelId()));

        UUID messageId = jcfMessageService.getMessageListByUser(charlie.getUserId()).get(0).getId();
        jcfMessageService.editMessage(messageId, "NMIXX Change Up!");
        System.out.println("메시지 수정 후: " + jcfMessageService.getAllMessages());
        jcfMessageService.deleteMessage(messageId);
        System.out.println("메시지 삭제 후: " + jcfMessageService.getAllMessages());

        // 채널에서 유저 제거
        jcfChannelService.leaveChannel(testChannel.getChannelId(), charlie);
        System.out.println("채널에서 유저 제거 후: " + jcfChannelService.getChannelList());

        // 채널 삭제
        jcfChannelService.deleteChannel(testChannel.getChannelId());
        System.out.println("채널 삭제 후: " + jcfChannelService.getChannelList());
        System.out.println("채널 삭제 후 메시지 존재 여부 확인: " + jcfMessageService.getAllMessages());
    }
}