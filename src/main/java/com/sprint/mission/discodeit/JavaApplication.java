package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.jcf.*;

import com.sprint.mission.discodeit.utils.TimeFormatter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class JavaApplication {

    public static void main(String[] args) {
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();
        MessageService messageService = new JCFMessageService(userService, channelService);


        User firstUser = userService.registerUser("AnSeungRi", "asr@naver.com", LocalDate.parse("2000-02-06"),
                "010-1234-5678", "1234"
        );

        System.out.println("첫번째 유저의 이름은 " + firstUser.getName() + " 이메일은 " + firstUser.getEmail()
                + " 생일은 " + firstUser.getBirthDate() + " 전화번호는 " + firstUser.getPhoneNumber()
                + " 비밀번호는 " + firstUser.getPassword() + " 입니다.");

        try {
            userService.registerUser("tem", "asr@naver.com",LocalDate.parse("2026-01-14"),
                    "010-8888-8888", "8888" );
        } catch (IllegalArgumentException e){
            System.out.println(e.getMessage());
        }

        try{
            userService.updateUser(firstUser.getId(), "AnSeungBin",null,null,null);
            System.out.println("수정된 이름은 " + firstUser.getName() + " 입니다. ");
        }
        catch (IllegalArgumentException e) {
            System.out.println("이름 변경 실패 : " + e.getMessage());
        }

        try {
            userService.updateUser(firstUser.getId(),null ,"asb@naver.com",null,null);
            System.out.println("수정된 이메일은 " + firstUser.getEmail() + " 입니다. ");
        } catch (IllegalArgumentException e) {
            System.out.println("이메일 변경 실패 : " + e.getMessage());
        }


        try {
            userService.updateUser(firstUser.getId(),null,null, "010-9876-5432",null);
            System.out.println("수정된 전화번호는 " + firstUser.getPhoneNumber() + " 입니다. ");
        } catch (IllegalArgumentException e){
            System.out.println("전화번호 변경 실패 : " + e.getMessage());
        }
        try {
            userService.updateUser(firstUser.getId(), null, null,null,"9876");
            System.out.println("수정된 비밀번호는 " + firstUser.getPassword() + " 입니다. ");
        } catch (IllegalArgumentException e) {
            System.out.println("비밀번호 변경 실패 : " + e.getMessage());
        }


        try{
            UUID findId = firstUser.getId();
            User foundUser = userService.findUserById(findId);
            System.out.println("찾는 유저의 이름은 " + foundUser.getName() + " 이메일은 " + foundUser.getEmail()
                    + " 생일은 " + foundUser.getBirthDate() + " 전화번호는 " + foundUser.getPhoneNumber()
                    + " 비밀번호는 " + foundUser.getPassword() + " 입니다.");
        }
        catch (IllegalArgumentException e) {
            System.out.println("유저 정보 변경 실패 : " + e.getMessage());
        }

        User secondUser = userService.registerUser("Anshori", "shori@naver.com", LocalDate.parse("2026-01-13"),
                "010-1111-1111", "4321"
        );

        List<User> users = userService.findAllUser();

        for (User userName : users) {
            System.out.println(userName.getName() + " 유저가 채널에 있습니다");
        }
        System.out.println("등록된 유저가 " + userService.userCount() + "명 있습니다." );



        Channel firstChannel = channelService.createChannel("서울", "강남");
        System.out.println("생성된 채널 이름 : " + firstChannel.getName()
                + "\n채널 설명 : " + firstChannel.getIntro());

        try{
            Channel temChannel = channelService.createChannel("서울","실패테스트");
        } catch (IllegalArgumentException e) {
            System.out.println("채널 생성 실패 : " + e.getMessage());
        }

        try {channelService.updateChannel(firstChannel.getId(),"경기도",null);
        System.out.println("수정된 채널 이름은 " + firstChannel.getName());}
        catch (IllegalArgumentException e) {
            System.out.println("채널 이름 변경 실패 : " + e.getMessage());
        }

        try {channelService.updateChannel(firstChannel.getId(),"경기도",null);
            System.out.println("수정된 채널 이름은 " + firstChannel.getName());}
        catch (IllegalArgumentException e) {
            System.out.println("수정 실패 테스트 : " + e.getMessage());
        }

        try { channelService.updateChannel(firstChannel.getId(),null,"평택");
        System.out.println("수정된 채널 설명은 " + firstChannel.getIntro() + " 입니다."); }
        catch (IllegalArgumentException e) {
            System.out.println("채널 설명 변경 실패 : " + e.getMessage());
        }


        channelService.enter(firstChannel, firstUser);
        System.out.println(firstUser.getName() + " 유저가 " + firstChannel.getName() + " 채널에 입장했습니다.");
        channelService.enter(firstChannel, secondUser);
        System.out.println(secondUser.getName() + " 유저가  " + firstChannel.getName() + " 채널에 입장했습니다.");

        for (User userName : firstChannel.getUserList()) {
            System.out.println(userName.getName() + " 유저가 " + firstChannel.getName() + " 채널에 있습니다.");
        }

        System.out.println("현재 " + firstChannel.getName() + " 채널에 유저가 "
                + channelService.getCurrentUserCount(firstChannel) + "명이 있습니다.");


        channelService.exit(firstChannel, secondUser);
        System.out.println(secondUser.getName() + " 유저가  " + firstChannel.getName() + " 채널에 퇴장했습니다.");

        try {
            String deleteUserName = secondUser.getName();
            userService.deleteUser(secondUser.getId());
            System.out.println(deleteUserName + " 유저가 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
             System.out.println("유저 삭제 실패 : " + e.getMessage());
        }
        System.out.println("등록된 유저가 " + userService.userCount() + "명 있습니다." );

        for (User userName : firstChannel.getUserList()) {
            System.out.println(userName.getName() + " 유저가 " + firstChannel.getName() + " 채널에 있습니다.");
        }

        System.out.println("현재 " + firstChannel.getName() + " 채널에 유저가 "
                + channelService.getCurrentUserCount(firstChannel) + "명이 있습니다.");


        try {
            Channel foundChannel = channelService.findChannelById(firstChannel.getId());
            System.out.println("찾은 채널의 이름은 " + foundChannel.getName() + " 입니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("채널 찾기 실패 : " + e.getMessage());
        }


        Channel secondChannel = channelService.createChannel("강원도", "양구");
        System.out.println("생성된 채널 이름 : " + secondChannel.getName()
                + "\n채널 설명 : " + secondChannel.getIntro());

        System.out.println("채널 리스트에 ");
        for (Channel channel : channelService.findAllChannels()) {
            System.out.println(channel.getName());
        }
        System.out.println("채널이 있습니다");


        try {UUID uuid = UUID.randomUUID();
            Channel forDelete = channelService.findChannelById(secondChannel.getId());
            channelService.deleteChannel(forDelete.getId());
            System.out.println(secondChannel.getName() + " 채널을 삭제했습니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("채널 삭제 실패 : " + e.getMessage());
        }

        System.out.println("채널 리스트에 ");
        for (Channel channel : channelService.findAllChannels()) {
            System.out.println(channel.getName());
        }
        System.out.println("채널이 있습니다");

        Message firstUserMessage = messageService.createMessage(firstUser.getId(),firstChannel.getId(),"안녕하세요");
        channelService.addMessage(firstChannel,firstUserMessage);
        System.out.println(firstUser.getName() + " : " + firstUserMessage.getContent() + " 보낸 시간 : "
                           + TimeFormatter.format(firstUserMessage.getSentAt()));
        System.out.println(firstUser.getName() + " 유저가 메세지를 보냈습니다." );

        try {
            UUID uuid = UUID.randomUUID();
            Message temMessage = messageService.createMessage(uuid, firstChannel.getId(),"삭제 체크");
            channelService.addMessage(firstChannel,firstUserMessage);
            System.out.println(firstUser.getName() + " : " + firstUserMessage.getContent() + " 보낸 시간 : "
                    + TimeFormatter.format(firstUserMessage.getSentAt()));
            System.out.println(firstUser.getName() + " 유저가 메세지를 보냈습니다." );
        } catch (IllegalArgumentException e){
            System.out.println("메세지 생성 실패 : " + e.getMessage());
        }

        try {
            UUID uuid = UUID.randomUUID();
            Message temMessage = messageService.createMessage(firstUser.getId(), uuid,"삭제 체크");
            channelService.addMessage(firstChannel,firstUserMessage);
            System.out.println(firstUser.getName() + " : " + firstUserMessage.getContent() + " 보낸 시간 : "
                    + TimeFormatter.format(firstUserMessage.getSentAt()));
            System.out.println(firstUser.getName() + " 유저가 메세지를 보냈습니다." );
        } catch (IllegalArgumentException e){
            System.out.println("메세지 생성 실패 : " + e.getMessage());
        }

        try {
            messageService.updateMessage(firstUserMessage.getId(),"반갑습니다");
            System.out.println(firstUser.getName() + " 유저가 메세지를 수정하였습니다.");
            System.out.println(firstUser.getName() + " : " + firstUserMessage.getContent()
                    + " 수정된 시간 : " + TimeFormatter.format(firstUserMessage.getUpdatedAt()));
        } catch (IllegalArgumentException e){
            System.out.println("메세지 수정 실패 : " + e.getMessage());
        }

        try {UUID uuid = UUID.randomUUID();
            messageService.updateMessage(uuid,"업데이트 테스트");
        } catch (IllegalArgumentException e){
            System.out.println("메세지 수정 실패 : " + e.getMessage());
        }

        Message firstUserMessage2 = messageService.createMessage(firstUser.getId(),firstChannel.getId(),"곤니치와");
        channelService.addMessage(firstChannel,firstUserMessage2);
        System.out.println(firstUser.getName() + " : " + firstUserMessage2.getContent() + " 보낸 시간 : "
                + TimeFormatter.format(firstUserMessage2.getSentAt()));
        System.out.println(firstUser.getName() + " 유저가 메세지를 보냈습니다." );


        try {
            UUID foundIdByMessage = firstUserMessage.getId();
            Message foundMessageById = messageService.findMessageById(foundIdByMessage);
            System.out.println("메세지 객체 id로 찾은 메세지의 내용 : " + foundMessageById.getContent());
        } catch (IllegalArgumentException e){
            System.out.println("메세지 찾기 실패 : " + e.getMessage());
        }

        UUID foundIdByUserId = firstUser.getId();
        List <Message>foundMessageByUserId = messageService.findMessageByUserId(foundIdByUserId);
        System.out.print("유저 아이디로 찾은 메세지의 내용 : ");
        for (Message message : foundMessageByUserId) {
            System.out.println(message.getContent());
        }

        try{
            UUID uuid = UUID.randomUUID();
            List <Message> test = messageService.findMessageByUserId(uuid);
        } catch (IllegalArgumentException e){
            System.out.println("메시지 찾기 실패 : 유저 테스트 , " + e.getMessage());
        }

        try{
            UUID uuid = UUID.randomUUID();
            List <Message> test = messageService.findMessageByChannelId(uuid);
        } catch (IllegalArgumentException e){
            System.out.println("메시지 찾기 실패 : 채널 테스트 , " + e.getMessage());
        }


        UUID foundIdByChannelId = firstChannel.getId();
        List <Message> foundMessageByChannelId = messageService.findMessageByChannelId(foundIdByChannelId);
        System.out.print("채널 아이디로 찾은 메세지의 내용 : ");
        for(Message message : foundMessageByChannelId) {
            System.out.println(message.getContent());
        }

        int messageCount = channelService.getMessageCount(firstChannel);
        System.out.println("현재 " +firstChannel.getName() + " 채널에 " + messageCount + "개의 메세지가 있습니다.");

        messageService.deleteMessage(firstUserMessage2.getId());    //유저의 메세지 삭제
        channelService.removeMessage(firstChannel,firstUserMessage2);              //채널에서 메세지 삭제
        System.out.println("두번째 메세지 삭제");

        try{UUID uuid = UUID.randomUUID();
            messageService.deleteMessage(uuid);
        } catch (IllegalArgumentException e){
            System.out.println("메세지 찾기 실패 : 삭제 테스트, " + e.getMessage());
        }

        foundMessageByUserId = messageService.findMessageByUserId(foundIdByUserId);
        System.out.print("삭제 후 유저 아이디로 찾은 메세지의 내용 : ");
        for (Message message : foundMessageByUserId) {
            System.out.println(message.getContent());
        }

        messageCount = channelService.getMessageCount(firstChannel);
        System.out.println("현재 " +firstChannel.getName() + " 채널에 " + messageCount + "개의 메세지가 있습니다.");




    }




}
