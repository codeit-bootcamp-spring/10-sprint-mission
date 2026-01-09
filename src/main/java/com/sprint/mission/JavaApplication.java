package com.sprint.mission;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {

        JCFUserService jcfUserService = new JCFUserService();

        jcfUserService.createUser(new User("Alice"));
        System.out.println("Alice 추가 " + jcfUserService.getUserList());
        jcfUserService.updateUserName(jcfUserService.getUserIdByName("Alice"), "Bob");
        System.out.println("Alice -> Bob 변경 " + jcfUserService.getUserList());

        UUID userId = jcfUserService.getUserIdByName("Bob");
        System.out.println("변경된 Bob의 id: " + userId);

        jcfUserService.deleteUser(userId);
        System.out.println("Bob 삭제 " + jcfUserService.getUserList());
    }
}