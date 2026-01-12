package com.sprint.mission;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {
        // Service 생성
        Factory factory = new Factory();

        UserService userService = factory.getUserService();
        ChannelService channelService = factory.getChannelService();
        MessageService messageService = factory.getMessageService();

        // 등록 테스트
        User user1 = userService.create("JEON");
        User user2 = userService.create("KIM");
        User user3 = userService.create("PARK");
        User user4 = userService.create("Lee");
        UUID id = UUID.randomUUID();

        // 채널 생성
        Channel ch = channelService.create("codeit");
        Channel ch2 = channelService.create("codeTree");
        Channel ch3 = channelService.create("Baekjoon");
        Channel ch4 = channelService.create("LOL");
        // user 채널 가입 및 채널 user등록
        System.out.println("===Channel Join===");
        channelService.addMember(user1.getId(), ch.getId());
        channelService.addMember(user2.getId(), ch.getId());
        channelService.addMember(user3.getId(), ch.getId());
        channelService.addMember(user4.getId(), ch.getId());

        channelService.addMember(user1.getId(), ch2.getId());
        channelService.addMember(user4.getId(), ch3.getId());
        channelService.addMember(user4.getId(), ch4.getId());
        System.out.println("user4 channel list");
        System.out.println(user4.getChannels());
        System.out.println(ch.getMembersSet());
        System.out.println("===user4 leaveChannel Test===");
        channelService.removeMember(user4.getId(), ch.getId());
        System.out.println(user4.getChannels());
        System.out.println(ch.getMembersSet());
        // 메시지 생성
        Message msg1 = messageService.create("Hello everyone", user1, ch);
        Message msg2 = messageService.create("Have a nice Day", user2, ch);
        Message msg3 = messageService.create("GOOD", user3, ch);

        // read & readAll test
        // User 조회
        System.out.println();
        System.out.println("===read test===");
        System.out.println("===user1 read test===");
        System.out.println(userService.read(user1.getId()));
        System.out.println(userService.read(id));
        System.out.println("===user readall test===");
        System.out.println(userService.readAll());

        // Channel 조회
        System.out.println();
        System.out.println("===channel test===");
        System.out.println(channelService.read(ch.getId()));
        System.out.println("===channel readall test===");
        System.out.println(channelService.readAll());
        System.out.println();

        // Message 조회
        System.out.println();
        System.out.println("===Message test===");
        System.out.println(messageService.read(msg2.getId()));
        System.out.println("===Message readall test===");
        System.out.println(messageService.readAll());
        System.out.println();


        // update Test
        // User update
        System.out.println("===User Update Test===");
        userService.update(user2.getId(), "David");
        System.out.println(user2.getName());
        System.out.println(userService.readAll());
        System.out.println();

        // Channel update
        System.out.println("===Channel Update Test===");
        channelService.update(ch.getId(), "CodeIt_10");
        System.out.println(ch.getName());
        System.out.println(channelService.readAll());
        System.out.println();

        // Message update
        System.out.println("===Message Update Test===");
        messageService.update(msg2.getId(), "My name is David");
        System.out.println(msg2.getContents());
        System.out.println(messageService.readAll());
        System.out.println();

        // Delete Test
        // User Delete Test
        System.out.println();
        System.out.println("===Before Delete===");
        System.out.println(userService.readAll());
        System.out.println("===After Delete===");
        userService.delete(user3.getId());
        System.out.println(userService.readAll());

        // channel Delete Test
        System.out.println();
        System.out.println("===Before Delete===");
        System.out.println(channelService.readAll());
        System.out.println("===After Delete===");
        channelService.delete(ch2.getId());
        System.out.println(channelService.readAll());

        // Message Delete Test
        User testUser = msg3.getSender();
        Channel testChannel = msg3.getChannel();

        System.out.println();
        System.out.println("===Before Delete===");
        System.out.println(messageService.readAll());
        System.out.println(testChannel.getMessageList());
        System.out.println("===After Delete===");
        messageService.delete(msg3.getId());
        System.out.println(messageService.readAll());
        System.out.println("===Test User, Channel===");

        System.out.println("user&channel messageList");
        System.out.println(testUser.getMessageList());
        System.out.println(testChannel.getMessageList());


    }
}