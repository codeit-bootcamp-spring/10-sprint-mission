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

        // 예외 케이스 테스트용 id (존재하지 않는 UUID)
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
        // ch2 ~ 4
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

        // 예외 케이스 테스트
        System.out.println();
        System.out.println("===Exception Cases (Not Found)===");

        // User: find / update / delete
        try{
            System.out.println("User find" + userService.find(id));
        } catch (IllegalArgumentException e){
            System.out.println("User find EX " + e.getMessage());
        }

        try{
            userService.update(id, "HAHHA");
            System.out.println("User update Success");
        } catch (IllegalArgumentException e){
            System.out.println("User update EX " + e.getMessage());
        }

        try{
            userService.delete(id);
            System.out.println("User Delete Success");
        } catch (IllegalArgumentException e){
            System.out.println("User delete EX " + e.getMessage());
        }

        // Channel: find / update / delete
        try{
            System.out.println("Channel find" + channelService.find(id));
            System.out.println("Channel find Success");
        } catch (IllegalArgumentException e){
            System.out.println("Channel find EX " + e.getMessage());
        }

        try{
            channelService.update(id, "OMG");
            System.out.println("Channel update Success");
        } catch (IllegalArgumentException e){
            System.out.println("Channel update EX " + e.getMessage());
        }

        try{
            channelService.delete(id);
            System.out.println("Channel Delete Success");
        } catch (IllegalArgumentException e){
            System.out.println("Channel delete EX " + e.getMessage());
        }

        // Message: find / update / delete
        try{
            System.out.println("Message find" + messageService.find(id));
        } catch (IllegalArgumentException e){
            System.out.println("Message find EX " + e.getMessage());
        }

        try{
            messageService.update(id, "HAHHA");
            System.out.println("Message update Success");
        } catch (IllegalArgumentException e){
            System.out.println("Message update EX " + e.getMessage());
        }

        try{
            messageService.delete(id);
            System.out.println("Message Delete Success");
        } catch (IllegalArgumentException e){
            System.out.println("Message delete EX " + e.getMessage());
        }
        System.out.println("=======");

        // Member, Channel
        System.out.println();
        System.out.println("===Channel Member Exception Test===");

        try {
            channelService.addMember(id, ch.getId()); // 없는 userId
        } catch (IllegalArgumentException e) {
            System.out.println("addMember EX - user " + e.getMessage());
        }

        try {
            channelService.addMember(user1.getId(), id); // 없는 channelId
        } catch (IllegalArgumentException e) {
            System.out.println("addMember EX - channel " + e.getMessage());
        }

        try {
            channelService.removeMember(id, ch.getId()); // 없는 userId
        } catch (IllegalArgumentException e) {
            System.out.println("removeMember EX - user " + e.getMessage());
        }

        try {
            channelService.removeMember(user1.getId(), id); // 없는 channelId
        } catch (IllegalArgumentException e) {
            System.out.println("removeMember EX - channel " + e.getMessage());
        }
        System.out.println();

        // read & readAll test
        // User 조회
        System.out.println();
        System.out.println("===read test===");
        System.out.println("===user1 read test===");
        System.out.println(userService.find(user1.getId()));
        System.out.println("===user readall test===");
        System.out.println(userService.readAll());

        // Channel 조회
        System.out.println();
        System.out.println("===channel test===");
        System.out.println(channelService.find(ch.getId()));
        System.out.println("===channel readall test===");
        System.out.println(channelService.readAll());
        System.out.println();

        // Message 조회
        System.out.println();
        System.out.println("===Message test===");
        System.out.println(messageService.find(msg2.getId()));
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
        System.out.println("===Message Delete Test===");
        User testUser = msg3.getSender();
        Channel testChannel = msg3.getChannel();

        System.out.println();
        System.out.println("===Before Delete===");
        System.out.println(messageService.readAll());
        System.out.println("channel messageList(before): "+ testChannel.getMessageList());

        System.out.println("===After Delete===");
        messageService.delete(msg3.getId());
        System.out.println(messageService.readAll());

        System.out.println("===Test User, Channel===");
        System.out.println("user&channel messageList");
        System.out.println("user messageList: " + testUser.getMessageList());
        System.out.println("channel messageList: "+ testChannel.getMessageList());


    }
}