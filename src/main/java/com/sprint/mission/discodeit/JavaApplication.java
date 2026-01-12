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

import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {
        // 유저
        UserService userService = new JCFUserService();

        User user1 = new User("장동규");
        UUID user1Id = user1.getId();
        userService.createUser(user1);

        User user2 = new User("이정민");
        UUID user2Id = user2.getId();
        userService.createUser(user2);

        User user3 = new User("곽인성");
        UUID user3Id = user3.getId();
        userService.createUser(user3);

        User user4 = new User("김혜성");
        UUID user4Id = user4.getId();
        userService.createUser(user4);
        // 유저 조회
        System.out.println("==전체 유저 조회==");
        userService.printAllUsers();
        System.out.println("\n==user1(장동규) 조회==");
        System.out.println(userService.findById(user1Id)+"\n");
        // 유저 수정
        System.out.println("==user4 이름 변경(김혜성 -> 박상호)==");
        userService.updateById(user4Id,"박상호"); // 김혜성 -> 박상호
        System.out.println("==이름 변경 후 user4==");
        System.out.println(userService.findById(user4Id)+"\n");
        // 유저 삭제
        System.out.println("==user4(박상호) 삭제 후 전체 출력==");
        userService.deleteById(user4Id);
        userService.printAllUsers();


        // 채널
        ChannelService channelService = new JCFChannelService();

        Channel channel1 = new Channel("스터디 채널");
        UUID channel1Id = channel1.getId();
        channelService.createChannel(channel1);

        Channel channel2 = new Channel("게임 채널");
        UUID channel2Id = channel2.getId();
        channelService.createChannel(channel2);

        // 채널 입장
        user1.joinChannel(channel1);// 장동규 -> 스터디 채널
        user2.joinChannel(channel1);// 이정민 -> 스터디 채널
        user3.joinChannel(channel2);// 곽인성 -> 게임 채널

        // 채널 조회
        System.out.println("\n==모든 채널==");
        channelService.printAllChannels();
        System.out.println("\n==channel1(스터디 채널) 조회==");
        System.out.println(channelService.findById(channel1Id)+"\n");

        // 채널 수정
        System.out.println("==channel2 이름 변경 (게임 채널 -> game 채널) ==");
        channelService.updateById(channel2Id,"game 채널");
        System.out.println(channelService.findById(channel2Id)+"\n");

        // 채널 삭제
        System.out.println("==channel2(game) 삭제==");
        channelService.deleteById(channel2Id);
        channelService.printAllChannels();


        // 메시지
        MessageService messageService = new JCFMessageService();

        Message message1 = new Message(user1,"토익 스터디 합시다");
        user1.addMessage(message1, channel1);
        UUID message1Id = message1.getId();
        messageService.createMessage(message1);

        Message message2 = new Message(user2,"스프링 스터디 합시다");
        user2.addMessage(message2, channel2);
        UUID message2Id = message2.getId();
        messageService.createMessage(message2);

        // 메시지 조회
        System.out.println("\n==message1 조회==");
        System.out.println(messageService.findById(message1Id)+"\n");;
        System.out.println("\n==전체 메시지 조회==");
        messageService.printAllMessages();

        // 메시지 수정
        System.out.println("\n==message1 수정 (토익 스터디 합시다 -> 영어 스터디 합시다)");
        messageService.updateById(message1Id,"영어 스터디 합시다");
        System.out.println(messageService.findById(message1Id)+"\n");

        // 메시지 삭제
        System.out.println("\n==message1 삭제 후 전체 조회 ('영어 스터디 합시다'삭제)==");
        messageService.deleteById(message1Id);
        messageService.printAllMessages();

    }
}
