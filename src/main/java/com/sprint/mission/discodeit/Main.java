package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        final JCFUserService userService = new JCFUserService();
        final JCFChannelService channelService = new JCFChannelService();
        final JCFMessageService messageService = new JCFMessageService();

        // 사용자 테스트 (현재 user2, user3 유효)
        User user1 = null;
        User user2 = null;
        User user3 = null;
        User user4 = null;

        // 생성 테스트
        try {   // 정상
            user1 = userService.createUser("dlekthf0906@codeit.com", "1234567890", "LeeDyol");
        } catch (Exception e){
            System.err.println(e.getMessage());
        }

        try {   // 정상
            user2 = userService.createUser("dlekthf@codeit.com", "1234567890", "dyoool");
        } catch (Exception e){
            System.err.println(e.getMessage());
        }

        try {   // 정상
            user3 = userService.createUser("Yushi@codeit.com", "1234567890", "tokuno");
        } catch (Exception e){
            System.err.println(e.getMessage());
        }

        try {   // 이메일 중복
            user3 = userService.createUser("Yushi@codeit.com", "1234567890", "yushi");
        } catch (Exception e){
            System.err.println(e.getMessage());
        }

        // 조회 테스트
        if (user1 != null) {
            try {       // 예상 출력: LeeDyol
                System.out.println("단건 조회 테스트: " + userService.searchUser(user1.getId()).getNickname());
            } catch (Exception e){
                System.err.println(e.getMessage());
            }
        }

        ArrayList<User> users = userService.searchUserAll();

        try {   // 에상 출력: Lee Dyol, dyoool, tokuno
            System.out.println("다건 조회 테스트");
            users.forEach(user -> System.out.println(user.getNickname()));
        }  catch (Exception e){
            System.err.println(e.getMessage());
        }

        // 수정
        if (user3 != null) {
            try {   // 비밀번호 변경 실패
                userService.updateUser(user3.getId(), "", "sakuya");
                System.out.println("수정 테스트: " + userService.searchUser(user3.getId()).getNickname());
            } catch (Exception e){
                System.err.println(e.getMessage());
            }
        }

        if(user3 != null) {
            try {   // 닉네임 변경 실패
                userService.updateUser(user3.getId(), "1234", "");
                System.out.println("수정 테스트: " + userService.searchUser(user3.getId()).getNickname());
            } catch (Exception e){
                System.err.println(e.getMessage());
            }
        }

        if (user3 != null) {
            try {   // 예상 출력: sakuya
                userService.updateUser(user3.getId(), null, "sakuya");
                System.out.println("수정 테스트: " + userService.searchUser(user3.getId()).getNickname());
            } catch (Exception e){
                System.err.println(e.getMessage());
            }
        }

        // 삭제
        if (user1 != null) {
            try {   // 예상 출력: dyoool, sakuya
                userService.deleteUser(user1.getId());
                System.out.println("삭제 테스트");
                users.forEach(user -> System.out.println(user.getNickname()));
            } catch (Exception e){
                System.err.println(e.getMessage());
            }
        }

        System.out.println("=========================");

        // 채널 테스트 (현재 channel2만 유효)
        Channel channel1 = null;
        Channel channel2 = null;
        Channel channel3 = null;

        // 생성 테스트
        if (user3 != null) {
            try {
                channel1 = channelService.createChannel("Codeit", user3, ChannelType.CHAT);
            } catch (Exception e){
                System.err.println(e.getMessage());
            }
        }

        if (user3 != null) {
            try {
                channel2 = channelService.createChannel("Book Club", user3, ChannelType.VOICE);
            } catch (Exception e){
                System.err.println(e.getMessage());
            }
        }

        // if (user1 != null) {     // 존재하지 않는 사용자
            try {
                channel3 = channelService.createChannel("Running Club", user1, ChannelType.CHAT);
            } catch (Exception e){
                System.err.println(e.getMessage());
            }
        // }

        // 조회
        if (channel1 != null) {
            try {   // 예상 출력: Codeit
                System.out.println("단건 조회 테스트: " + channelService.searchChannel(channel1.getId()).getChannelName());
            } catch (Exception e){
                System.err.println(e.getMessage());
            }
        }

        ArrayList<Channel> channels = channelService.searchChannelAll();

        try {   // 에상 출력: Codeit, Book Club
            System.out.println("다건 조회 테스트");
            channels.forEach(channel -> System.out.println(channel.getChannelName()));
        }  catch (Exception e){
            System.err.println(e.getMessage());
        }

        // 수정
        if (channel2 != null) {
            try {   // 채널명 변경 실패
                channelService.updateChannel(channel2.getId(), " ");
                System.out.println("수정 테스트: " + channelService.searchChannel(channel2.getId()).getChannelName());
            }  catch (Exception e){
                System.err.println(e.getMessage());
            }
        }

        if (channel2 != null) {
            try {   // 예상 출력: Study Club
                channelService.updateChannel(channel2.getId(), "Study Club");
                System.out.println("수정 테스트: " + channelService.searchChannel(channel2.getId()).getChannelName());
            }  catch (Exception e){
                System.err.println(e.getMessage());
            }
        }

        // 삭제
        if (channel1 != null) {
            try {   // 예상 출력: Study Club
                channelService.deleteChannel(channel1.getId());
                System.out.println("삭제 테스트");
                channels.forEach(channel -> System.out.println(channel.getChannelName()));
            }   catch (Exception e){
                System.err.println(e.getMessage());
            }
        }

        // 초대
        if (channel2 != null && user2 != null) {
            try {
                channelService.inviteMembers(user2.getId(), channel2.getUsers());
                System.out.println("초대 테스트");
                users.forEach(user -> System.out.println(user.getNickname()));
            }   catch (Exception e){
                System.err.println(e.getMessage());
            }
        }

        if (channel2 != null && user3 != null) {
            try {       // 이미 존재하는 사용자
                channelService.inviteMembers(user3.getId(), channel2.getUsers());
                System.out.println("초대 테스트");
                users.forEach(user -> System.out.println(user.getNickname()));
            }   catch (Exception e){
                System.err.println(e.getMessage());
            }
        }

        System.out.println("=========================");

        // 메시지 테스트 (현재 message2만 유효)
        Message message1 = null;
        Message message2 = null;
        Message message3 = null;

        // 생성
        if (user3 != null && channel2 != null) {
            try {
                message1 = messageService.createMessage("안녕하세요", user3, channel2, MessageType.CHAT);
            } catch (Exception e){
                System.err.println(e.getMessage());
            }
        }

        if (user3 != null && channel2 != null) {
            try {
                message2 = messageService.createMessage("안녕 못하네요.", user3, channel2, MessageType.CHAT);
            } catch (Exception e){
                System.err.println(e.getMessage());
            }
        }

        // if (user3 != null && channel2 != null) {     // 존재하지 않는 사용자
            try {
                message3 = messageService.createMessage("저는 안녕해요", user1, channel2, MessageType.CHAT);
            } catch (Exception e){
                System.err.println(e.getMessage());
            }
        //}

        // 조회
        if (message1 != null){
            try {   // 예상 출력: 안녕하세요
                System.out.println("단건 조회 테스트: " + messageService.searchMessage(message1.getId()).getMessage());
            }   catch (Exception e){
                System.err.println(e.getMessage());
            }
        }

        ArrayList<Message> messages = messageService.searchMessageAll();

        try {   // 예상 출력: 안녕하세요, 안녕 못하네요
            System.out.println("다건 조회 테스트");
            messages.forEach(message -> System.out.println(message.getMessage()));
        } catch (Exception e){
            System.err.println(e.getMessage());
        }

        // 수정
        if (message2 != null){
            try {   // 메시지 변경 실패
                messageService.updateMessage(message2.getId(), "");
                System.out.println("수정 테스트: " + messageService.searchMessage(message2.getId()).getMessage());
            } catch (Exception e){
                System.err.println(e.getMessage());
            }
        }

        if (message2 != null){
            try {   // 예상 출력: 죄송해요 안녕합니다
                messageService.updateMessage(message2.getId(), "죄송해요. 안녕합니다");
                System.out.println("수정 테스트: " + messageService.searchMessage(message2.getId()).getMessage());
            } catch (Exception e){
                System.err.println(e.getMessage());
            }
        }

        // 삭제
        if (message1 != null){
            try {   // 예상 출력: 죄송해요 안녕합니다
                messageService.deleteMessage(message1.getId());
                System.out.println("삭제 테스트");
                messages.forEach(message -> System.out.println(message.getMessage()));
            }  catch (Exception e){
                System.err.println(e.getMessage());
            }
        }
    }
}