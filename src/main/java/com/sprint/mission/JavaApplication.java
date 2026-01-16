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
        JCFMessageService jcfMessageService = new JCFMessageService(jcfUserService);
        JCFChannelService jcfChannelService = new JCFChannelService(jcfMessageService, jcfUserService);

        // 🔑 순환 고리 연결
        jcfMessageService.setChannelService(jcfChannelService);
        jcfUserService.setChannelService(jcfChannelService);

        System.out.println("------------------- 유저 서비스 테스트 -------------------");
        System.out.println();

        // UserService 테스트
        User alice = jcfUserService.createUser("Alice");
        System.out.println("Alice 추가 " + jcfUserService.getUserList());
        jcfUserService.updateUserName(alice.getId(), "Bob");
        System.out.println("Alice -> Bob 변경 " + jcfUserService.getUserList());

        UUID userId = alice.getId();
        System.out.println("변경된 Bob의 id: " + userId);

        jcfUserService.deleteUser(userId);
        System.out.println("Bob 삭제 " + jcfUserService.getUserList());

        System.out.println();
        System.out.println("------------------- 서비스 통합 테스트 -------------------");
        System.out.println();

        User charlie = jcfUserService.createUser("Charlie");
        User david = jcfUserService.createUser("David");
        // ChannelService 테스트
        Channel testChannel = jcfChannelService.createChannel("Test Channel");
        Channel chatChannel = jcfChannelService.createChannel("Chat Channel");
        System.out.println("채널 생성 후: " + jcfChannelService.getChannelList());
        // 채널에 유저 추가
        jcfChannelService.joinChannel(testChannel.getId(), charlie.getId());
        jcfChannelService.joinChannel(chatChannel.getId(), charlie.getId());
        jcfChannelService.joinChannel(testChannel.getId(), david.getId());

        for (var channel: jcfChannelService.getChannelList()) {
            System.out.println(channel.getChannelName() + " 채널에 유저 추가 후: " + jcfUserService.getUsersByChannel(channel.getId()));
        }

        System.out.println();
        // 유저 별로 참여중인 채널 확인
        for (var user: jcfUserService.getUserList()) {
            System.out.println(user.getUsername() + "의 참여중인 채널 조회: " + jcfChannelService.getChannelsByUser(user.getId()));
        }
        // 채널 이름 변경
        jcfChannelService.updateChannelName(testChannel.getId(), "NMIXX Channel");
        System.out.println("채널 이름 변경 후: " + jcfChannelService.getChannelList());

        System.out.println();
        System.out.println("------------------- 메시지 서비스 테스트 -------------------");
        System.out.println();

        // MessageService 테스트
        jcfMessageService.sendMessage(charlie.getId(), testChannel.getId(), "Hello, World!");
        jcfMessageService.sendMessage(charlie.getId(), testChannel.getId(), "This is test");
        jcfMessageService.sendMessage(charlie.getId(), testChannel.getId(), "for testing");
        System.out.println("메시지 전송 후: " + jcfMessageService.getAllMessages());

        for (var user : jcfUserService.getUserList()) {
            System.out.println(user.getUsername() + "의 메시지 조회: " + jcfMessageService.getMessageListByUser(user.getId()));
        }

        for (var channel : jcfChannelService.getChannelList()) {
            System.out.println(channel.getChannelName() + " 채널의 메시지 조회: " + jcfMessageService.getMessageListByChannel(channel.getId()));
        }

        UUID messageId = jcfMessageService.getMessageListByUser(charlie.getId()).get(0).getId();
        jcfMessageService.updateMessage(messageId, "NMIXX Change Up!");
        System.out.println("메시지 수정 후: " + jcfMessageService.getAllMessages());
        jcfMessageService.deleteMessage(messageId);
        System.out.println("메시지 삭제 후: " + jcfMessageService.getAllMessages());

        // 채널에서 유저 제거
        jcfChannelService.leaveChannel(testChannel.getId(), charlie.getId());
        System.out.println("채널에서 유저 제거 후: " + jcfChannelService.getChannelList());

        // 채널 삭제
        jcfChannelService.deleteChannel(testChannel.getId());
        System.out.println("채널 삭제 후: " + jcfChannelService.getChannelList());
        System.out.println("채널 삭제 후 메시지 존재 여부 확인: " + jcfMessageService.getAllMessages());
    }
}