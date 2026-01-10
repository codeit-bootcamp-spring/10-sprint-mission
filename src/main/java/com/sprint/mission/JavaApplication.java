package com.sprint.mission;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {

        JCFUserService jcfUserService = new JCFUserService();
        JCFMessageService jcfMessageService = new JCFMessageService();

        jcfUserService.createUser(new User("Alice"));
        System.out.println("Alice 추가 " + jcfUserService.getUserList());
        jcfUserService.updateUserName(jcfUserService.getUserIdByName("Alice"), "Bob");
        System.out.println("Alice -> Bob 변경 " + jcfUserService.getUserList());

        UUID userId = jcfUserService.getUserIdByName("Bob");
        System.out.println("변경된 Bob의 id: " + userId);

        jcfUserService.deleteUser(userId);
        System.out.println("Bob 삭제 " + jcfUserService.getUserList());

        jcfUserService.createUser(new User("Charlie"));
        jcfMessageService.sendMessage(jcfUserService.getUserIdByName("Charlie"), "Hello, World!");
        jcfMessageService.sendMessage(jcfUserService.getUserIdByName("Charlie"), "This is test");
        jcfMessageService.sendMessage(jcfUserService.getUserIdByName("Charlie"), "for testing");
        System.out.println("메시지 전송 후: " + jcfMessageService.getAllMessages());

        UUID messageId = jcfMessageService.getMessageListByUser(jcfUserService.getUserIdByName("Charlie")).get(0).getId();
        jcfMessageService.editMessage(messageId, "NMIXX Change Up!");
        System.out.println("메시지 수정 후: " + jcfMessageService.getAllMessages());
        jcfMessageService.deleteMessage(messageId);
        System.out.println("메시지 삭제 후: " + jcfMessageService.getAllMessages());
    }
}