package com.sprint.mission;

import com.sprint.mission.entity.User;
import com.sprint.mission.service.ChannelService;
import com.sprint.mission.service.MessageService;
import com.sprint.mission.service.UserService;
import com.sprint.mission.service.jcf.JCFChannelService;
import com.sprint.mission.service.jcf.JCFMessageService;
import com.sprint.mission.service.jcf.JCFUserService;

import java.util.List;
import java.util.stream.Collectors;

public class JavaApplication {
    public static void main(String[] args) {
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();
        MessageService messageService = new JCFMessageService();

        // 유저 서비스 구현 테스트
        // 등록 =========================
        User user1 = userService.createUser("종혁");

        // 조회(단건) =========================
        System.out.println(userService.findById(user1.getId()).getNickName());

        // 수정 및 수정 확인
        userService.updateUser(user1.getId(), "오종혁");
        System.out.println(user1.getNickName());

        // 조회(다건) =========================
        userService.createUser("유리");
        List<User> users = userService.findAll();
        String result = users.stream()
                .map(User::getNickName)
                .collect(Collectors.joining(", ", "[", "]"));
        System.out.println(result);

        // 삭제 및 삭제확인
        userService.deleteById(user1.getId());
        List<User> users2 = userService.findAll();
        String result2 = users2.stream()
                .map(User::getNickName)
                .collect(Collectors.joining(", ", "[", "]"));
        System.out.println(result2);
    }
}