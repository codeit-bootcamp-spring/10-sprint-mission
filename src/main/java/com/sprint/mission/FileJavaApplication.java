package com.sprint.mission;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.FIleChannelService;
import com.sprint.mission.discodeit.service.file.FIleMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.List;
import java.util.UUID;

public class FileJavaApplication {

    public static void main(String[] args) {
        String path = System.getProperty("user.dir") + "/data";
        UserService userService = new FileUserService(path);
        ChannelService channelService = new FIleChannelService(userService, path);
        MessageService messageService = new FIleMessageService(channelService, userService, path);

        System.out.println("========[채널 테스트]========");
        //채널 등록 테스트
        Channel channel1 = channelService.create(ChannelType.PUBLIC, "공부 채널", "자유 채널입니다.");
        Channel channel2 = channelService.create(ChannelType.PUBLIC, "코딩 채널", "코딩 채널입니다.");
        System.out.println("채널 생성: " + channel1 + ", " + channel2);
        System.out.println("채널 생성 시간: " + channel1.getCreatedAt());
        System.out.println("채널 수정 시간: " + channel1.getUpdateAt());

        //중복 채널명 등록 테스트 (예외발생)
        //channelService.createChannel("공부 채널");

        //이름을 통해 채널 검색
        Channel nameSearchChannel = channelService.findChannelByName("공부 채널");
        System.out.println("이름으로 찾은 채널 = " + nameSearchChannel);

        //Id를 통해 채널 검색
        Channel idSearchChannel = channelService.findChannelById(channel1.getId());
        System.out.println("ID로 찾은 채널 = " + idSearchChannel);

        //채널 수정 테스트
        Channel updatedChannel = channelService.update(channel1.getId(), "게임 채널");
        System.out.println("수정된 채널 = " + updatedChannel);
        System.out.println("채널 수정 시간: " + channel1.getUpdateAt());

        //중복 채널명 수정 테스트 (예외발생)
        //channelService.updateChannel(channel1.getId(), "코딩 채널");

        //채널 전체 검색
        System.out.println("전체 채널 목록: " + channelService.findAllChannel());

        //채널 삭제 테스트
        channelService.delete(channel1.getId());
        System.out.println("채널 삭제 후 전체 채널 목록: " + channelService.findAllChannel());

        System.out.println();
        System.out.println("========[유저 테스트]========");

        //유저 등록 테스트
        User user1 = userService.create("이규빈", "hello@hello.com", "1234");
        User user2 = userService.create("홍길동", "hi@codeit.com", "hgd0000");
        System.out.println("유저 생성:" + user1);

        //중복 이메일 등록 테스트 (예외발생)
        //userService.createUser("이순신", "hello@hello.com");

        //이메일을 통해 유저 검색
        User emailSearchUser = userService.findUserByEmail("hello@hello.com");
        System.out.println("이메일로 찾은 유저 = " + emailSearchUser);

        //유저 Id를 통해 유저 검색
        User idSearchUser = userService.findUserById(user1.getId());
        System.out.println("ID로 찾은 유저 = " + idSearchUser);

        //중복 이메일로 변경 테스트 (예외발생)
        //userService.updateEmail(user1.getId(), "hello@hello.com");

        //유저 이메일 변경 테스트
        User updatedEmailUser = userService.update(user1.getId(), null, "bye@bye.com", "1234");
        System.out.println("유저 이메일 변경 = " + updatedEmailUser);

        //유저 이름 변경 테스트
        User updatedNameUser = userService.update(user1.getId(), "김코딩", null, "1234");
        System.out.println("유저 이름 변경 = " + updatedNameUser);

        //유저 이메일, 이름 모두 변경 테스트
        User updatedUser = userService.update(user1.getId(), "이공부", "good@good.com", "1234");
        System.out.println("유저 이름, 이메일 변경 = " + updatedUser);

        //비밀번호 다르게 입력 (예외발생)
        //userService.updateUser(user1.getId(), "실패할 테스트", "dd@dd.dd", "1111");

        //유저 전체 검색
        System.out.println("전체 유저 목록: " + userService.findAllUser());

        //유저 삭제 테스트
        userService.delete(user1.getId(), user1.getPassword());
        System.out.println("유저 삭제 후 전체 유저 목록: " + userService.findAllUser());

        System.out.println();
        System.out.println("========[메세지 테스트]========");

        //메세지 등록 테스트 (테스트채널, 관리자채널, 관리자 A, 관리자 B 등록)
        channelService.create(ChannelType.PUBLIC, "테스트 채널", "테스트 채널입니다.");
        Channel testChannel = channelService.findChannelByName("테스트 채널");

        channelService.create(ChannelType.PRIVATE, "관리자 채널", "관리자 채널입니다.");
        Channel adminChannel = channelService.findChannelByName("관리자 채널");

        userService.create("관리자 A", "adminA@discodeit.com", "adminapassword");
        User adminA = userService.findUserByEmail("adminA@discodeit.com");

        userService.create("관리자 B", "adminB@discodeit.com", "adminbpassword");
        User adminB = userService.findUserByEmail("adminB@discodeit.com");

        //관리자 A가 테스트 채널에 메세지 2번, 관리자 채널에 메세지 1번 전송
        Message message1 = messageService.create("첫번째 메세지 전송 테스트입니다.", testChannel.getId(), adminA.getId());

        Message message2 = messageService.create("두번째 메세지 전송 테스트입니다.", testChannel.getId(), adminA.getId());

        Message message3 = messageService.create("여기는 관리자 채널입니다.", adminChannel.getId(), adminA.getId());

        //메세지 Id로 메세지 조회
        Message foundMessage = messageService.findMessage(message3.getId());
        System.out.println("메세지 ID로 찾은 메세지 = " + foundMessage);

        //특정 채널의 특정 유저의 메세지 조회 - 테스트 채널, 관리자 A
        System.out.println("테스트 채널의 관리자 A의 모든 채팅");
        List<Message> testChannelAdminAMessages = messageService.findMessagesByUserAndChannel(testChannel.getId(), adminA.getId());
        for (Message testChannelAdminAMessage : testChannelAdminAMessages) {
            System.out.println(testChannelAdminAMessage);
        }
        System.out.println();

        //특정 채널의 모든 메세지 조회 (관리자 B가 테스트 채널에 메세지 추가)
        System.out.println("테스트 채널의 모든 채팅");
        messageService.create("관리자 B가 테스트 합니다.", testChannel.getId(), adminB.getId());
        List<Message> testChannelMessages = messageService.findMessagesByChannel(testChannel.getId());
        for (Message testChannelMessage : testChannelMessages) {
            System.out.println(testChannelMessage);
        }
        System.out.println();

        //특정 유저가 발행한 모든 메세지 조회 (관리자 A)
        System.out.println("관리자 A의 모든 채팅");
        List<Message> userMessages = messageService.findMessagesByUser(adminA.getId());
        for (Message userMessage : userMessages) {
            System.out.println(userMessage);
        }
        System.out.println();

        //특정 채널에 참여 중인 유저 조회
        System.out.println("현재 테스트 채널에 참여 중인 유저");
        List<User> usersInChannel = userService.findUsersByChannel(testChannel.getId());
        for (User user : usersInChannel) {
            System.out.println(user);
        }
        System.out.println();

        //특정 유저가 참여 중인 채널 조회 (관리자 A의 채널)
        List<Channel> adminAs = channelService.findChannelsByUser(adminA.getId());
        System.out.println("관리자 A가 참여중인 채널");
        for (Channel channel : adminAs) {
            System.out.println(channel);
        }
        System.out.println();

        //특정 유저가 참여 중인 채널 조회 (관리자 B의 채널)
        List<Channel> adminBs = channelService.findChannelsByUser(adminB.getId());
        System.out.println("관리자 B가 참여중인 채널");
        for (Channel channel : adminBs) {
            System.out.println(channel);
        }
        System.out.println();

        //모든 채널의 모든 메세지 조회
        List<Message> allMessages = messageService.findAllMessages();
        System.out.println("모든 채널의 모든 메세지");
        for (Message allMessage : allMessages) {
            System.out.println(allMessage);
        }
        System.out.println();

        //메세지 수정 테스트 (테스트 채널의 관리자 A 메세지 리스트 중 처음 메세지 수정)
        System.out.println("테스트 채널의 관리자 A 채팅");
        Message firstMessage = testChannelAdminAMessages.get(0);
        UUID messageId = firstMessage.getId();
        Message updated = messageService.update(messageId, "첫번째 메세지를 수정해봤습니다.");
        for (Message testChannelAdminAMessage : testChannelAdminAMessages) {
            System.out.println(testChannelAdminAMessage);
        }
        System.out.println();

        //메세지 삭제 테스트 (관리자 채널의 관리자 A의 메세지 지우기)
        List<Message> removeTestMessages = messageService.findMessagesByChannel(adminChannel.getId());
        System.out.println("관리자 채널 메세지 삭제 전: " + removeTestMessages);
        Message removeTarget = removeTestMessages.get(0);
        UUID targetId = removeTarget.getId();
        messageService.delete(targetId);

        System.out.println("관리자 채널 메세지 삭제 후: " + messageService.findMessagesByChannel(adminChannel.getId()));

        System.out.println();
        System.out.println("========[채널 참가, 탈퇴 테스트]========");

        //메세지 없이 채널에 유저 참가 (테스트 채널)
        User joinUser = userService.create("관리자 C", "adminC@discodeit.com", "admincpassword");
        channelService.join(testChannel.getId(), joinUser.getId());

        //특정 유저가 참여중인 채널 리스트 조회 (관리자C)
        List<Channel> channelsByUserId = channelService.findChannelsByUser(joinUser.getId());
        System.out.println("관리자 C가 참여중인 채널: " + channelsByUserId);

        //메세지 없이 유저 탈퇴
        System.out.println("삭제 전 테스트 채널에 참여 중인 유저");
        for (User user : userService.findUsersByChannel(testChannel.getId())) {
            System.out.println(user);
        }
        System.out.println();

        channelService.leave(testChannel.getId(), joinUser.getId());

        System.out.println("삭제 후 테스트 채널에 참여 중인 유저");
        for (User user : userService.findUsersByChannel(testChannel.getId())) {
            System.out.println(user);
        }

        System.out.println();
        System.out.println("========[유저 삭제 테스트]========");
        System.out.println("관리자 A 삭제 전");
        System.out.println(adminA.getMessages());
        System.out.println(testChannel.getMessages());
        userService.delete(adminA.getId(), adminA.getPassword());
        System.out.println("관리자 A 삭제 후");
        System.out.println(adminA.getChannels());
        System.out.println(adminA.getMessages());
        System.out.println(testChannel.getUsers());
        System.out.println(testChannel.getMessages());
        System.out.println(adminChannel.getUsers());
        System.out.println(adminChannel.getMessages());

        System.out.println();
        System.out.println("========[채널 삭제 테스트]========");
        System.out.println("테스트 채널 삭제 전");
        System.out.println(testChannel.getMessages());
        System.out.println(testChannel.getUsers());
        channelService.delete(testChannel.getId());
        System.out.println("테스트 채널 삭제 후");
        System.out.println(testChannel.getMessages());
        System.out.println(testChannel.getUsers());
        System.out.println(adminB.getMessages());
        System.out.println(adminB.getChannels());
        System.out.println(channelService.findAllChannel());
    }
}
