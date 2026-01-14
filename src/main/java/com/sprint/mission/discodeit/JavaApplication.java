package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.jcf.*;

import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.jcf.*;

import com.sprint.mission.discodeit.TimeFormatter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class JavaApplication {

    public static void main(String[] args) {
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();
        MessageService messageService = new JCFMessageService();

        User firstUser = userService.registerUser("AnSeungRi", "asr@naver.com", LocalDate.parse("2000-02-06"),
                "010-1234-5678", "1234"
        );

        System.out.println("첫번째 유저의 이름은 " + firstUser.getName() + " 이메일은 " + firstUser.getEmail()
                + " 생일은 " + firstUser.getBirthDate() + " 전화번호는 " + firstUser.getPhoneNumber()
                + " 비밀번호는 " + firstUser.getPassword() + " 입니다.");

        firstUser.setName("AnSeungBin");
        System.out.println("수정된 이름은 " + firstUser.getName() + " 입니다. ");

        firstUser.setEmail("asb@naver.com");
        System.out.println("수정된 이메일은 " + firstUser.getEmail() + " 입니다. ");

        firstUser.setPhoneNumber("010-9876-5432");
        System.out.println("수정된 전화번호는 " + firstUser.getPhoneNumber() + " 입니다. ");

        firstUser.setPassword("5678");
        System.out.println("수정된 비밀번호는 " + firstUser.getPassword() + " 입니다. ");

        firstUser.setInfo("AnSeungHa", "ash@naver.com", "9876", "010-9999-9999");

        System.out.println("전체 수정된 유저의 이름은 " + firstUser.getName() + " 이메일은 " + firstUser.getEmail()
                + " 생일은 " + firstUser.getBirthDate() + " 전화번호는 " + firstUser.getPhoneNumber()
                + " 비밀번호는 " + firstUser.getPassword() + " 입니다.");

        UUID findId = firstUser.getId();
        User foundUser = userService.findUserById(findId);
        System.out.println("찾는 유저의 이름은 " + foundUser.getName() + " 이메일은 " + foundUser.getEmail()
                + " 생일은 " + foundUser.getBirthDate() + " 전화번호는 " + foundUser.getPhoneNumber()
                + " 비밀번호는 " + foundUser.getPassword() + " 입니다.");

        User secondUser = userService.registerUser("Anshori", "shori@naver.com", LocalDate.parse("2026-01-13"),
                "010-1111-1111", "4321"
        );

        List<User> users = userService.findAllUser();

        for (User userName : users) {
            System.out.println(userName.getName() + " 유저가 채널에 있습니다");
        }
        System.out.println("등록된 유저가 " + userService.userCount() + "명 있습니다." );

        /* userService.deleteUser(secondUser.getId()); // 유저를 삭제
        users = userService.findAllUser();// 아이디가 비었는지 확인하는것보다 다시 조회하는 것이 가독성이 좋다

         for(User userName : users){
            System.out.println(userName.getName() + " 유저가 리스트에 있습니다");
        }
        userService.deleteUser(firstUser.getId()); // 유저를 삭제 */

        Channel firstChannel = channelService.createChannel("서울", "강남");
        System.out.println("생성된 채널 이름 : " + firstChannel.getName()
                + "\n채널 설명 : " + firstChannel.getIntro());

        firstChannel.setName("경기도");
        System.out.println("수정된 채널 이름은 " + firstChannel.getName());

        firstChannel.setIntro("평택");
        System.out.println("수정된 채널 설명은 " + firstChannel.getIntro() + " 입니다.");


        firstChannel.enter(firstUser);
        System.out.println(firstUser.getName() + " 유저가 " + firstChannel.getName() + " 채널에 입장했습니다.");
        firstChannel.enter(secondUser);
        System.out.println(secondUser.getName() + " 유저가  " + firstChannel.getName() + " 채널에 입장했습니다.");

        for (User userName : firstChannel.getUserList()) {
            System.out.println(userName.getName() + " 유저가 " + firstChannel.getName() + " 채널에 있습니다.");
        }

        System.out.println("현재 " + firstChannel.getName() + " 채널에 유저가 " + firstChannel.getCurrentUserCount()
                + "명이 있습니다.");

        firstChannel.exit(secondUser);
        System.out.println(secondUser.getName() + " 유저가  " + firstChannel.getName() + " 채널에 퇴장했습니다.");

        userService.deleteUser(secondUser.getId());
        System.out.println("등록된 유저가 " + userService.userCount() + "명 있습니다." );

        for (User userName : firstChannel.getUserList()) {
            System.out.println(userName.getName() + " 유저가 " + firstChannel.getName() + " 채널에 있습니다.");
        }

        System.out.println("현재 " + firstChannel.getName() + " 채널에 유저가 " + firstChannel.getCurrentUserCount()
                + "명이 있습니다.");


        UUID findChannel = firstChannel.getId();
        Channel foundChannel = channelService.findChannelById(findChannel);
        System.out.println("찾은 채널의 이름은 " + foundChannel.getName() + " 입니다.");

        Channel secondChannel = channelService.createChannel("강원도", "양구");
        System.out.println("생성된 채널 이름 : " + secondChannel.getName()
                + "\n채널 설명 : " + secondChannel.getIntro());


        for (Channel channel : channelService.findAllChannels()) {
            System.out.println(channel.getName() + " 채널이 있습니다.");
        }

        Channel forDelete = channelService.findChannelById(secondChannel.getId());
        channelService.deleteChannel(forDelete.getId());
        System.out.println( secondChannel.getName() + " 채널을 삭제했습니다.");

        for (Channel channel : channelService.findAllChannels()) {
            System.out.println(channel.getName() + " 채널이 있습니다.");
        }

        Message firstUserMessage = messageService.createMessage(firstUser.getId(),firstChannel.getId(),"안녕하세요");
        firstChannel.addMessage(firstUserMessage);
        System.out.println(firstUser.getName() + " : " + firstUserMessage.getContent() + " 보낸 시간 : "
                           + TimeFormatter.format(firstUserMessage.getSentAt()));
        System.out.println(firstUser.getName() + " 유저가 메세지를 보냈습니다." );

        firstUserMessage.setContent("반갑습니다");
        System.out.println(firstUser.getName() + " 유저가 메세지를 수정하였습니다." );
        System.out.println(firstUser.getName() + " : " + firstUserMessage.getContent()
                           + " 수정된 시간 : " + TimeFormatter.format(firstUserMessage.getUpdatedAt()));

        Message firstUserMessage2 = messageService.createMessage(firstUser.getId(),firstChannel.getId(),"곤니치와");
        firstChannel.addMessage(firstUserMessage2);
        System.out.println(firstUser.getName() + " : " + firstUserMessage2.getContent() + " 보낸 시간 : "
                + TimeFormatter.format(firstUserMessage2.getSentAt()));
        System.out.println(firstUser.getName() + " 유저가 메세지를 보냈습니다." );


        UUID foundIdByMessage = firstUserMessage.getId();
        Message foundMessageById = messageService.findMessageById(foundIdByMessage);
        System.out.println("메세지 객체 id로 찾은 메세지의 내용 : " + foundMessageById.getContent() );

        UUID foundIdByUserId = firstUser.getId();
        List <Message>foundMessageByUserId = messageService.findMessageByUserId(foundIdByUserId);
        System.out.print("유저 아이디로 찾은 메세지의 내용 : ");
        for (Message message : foundMessageByUserId) {
            System.out.println(message.getContent());
        }

        UUID foundIdByChannelId = firstChannel.getId();
        List <Message> foundMessageByChannelId = messageService.findMessageByChannelId(foundIdByChannelId);
        System.out.print("채널 아이디로 찾은 메세지의 내용 : ");
        for(Message message : foundMessageByChannelId) {
            System.out.println(message.getContent());
        }

        int messageCount = firstChannel.getMessageCount();
        System.out.println("현재 " +firstChannel.getName() + " 채널에 " + messageCount + "개의 메세지가 있습니다.");

        messageService.deleteMessage(firstUserMessage2.getId());    //유저의 메세지 삭제
        firstChannel.removeMessage(firstUserMessage2);              //채널에서 메세지 삭제
        System.out.println("두번째 메세지 삭제");

        foundMessageByUserId = messageService.findMessageByUserId(foundIdByUserId);
        System.out.print("삭제 후 유저 아이디로 찾은 메세지의 내용 : ");
        for (Message message : foundMessageByUserId) {
            System.out.println(message.getContent());
        }

        messageCount = firstChannel.getMessageCount();
        System.out.println("현재 " +firstChannel.getName() + " 채널에 " + messageCount + "개의 메세지가 있습니다.");




    }




}
