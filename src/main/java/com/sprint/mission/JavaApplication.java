package com.sprint.mission;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.List;
import java.util.NoSuchElementException;

public class JavaApplication {
    public static void main(String[] args) {
        printN("유저 테스트 시작");
        userServiceTest();
        printN("채널 테스트 시작");
        channelServiceTest();
        printN("메세지 테스트 시작");
        messageServiceTest();

    }

    private static void userServiceTest() {
        UserService userService = new JCFUserService();
        User user1 = userService.createUser("user1", "1234", "");
        User user2 = userService.createUser("user2", "4567", "");

        // 단건 조회
        if(user1.equals(userService.getUser(user1.getId())))
            System.out.println("단건 조회 테스트 성공");
        else
            System.out.println("단건 조회 테스트 실패");

        // 다건 조회
        if(userService.getAllUsers().size() == 2)
            System.out.println("다건 조회 테스트 성공");
        else
            System.out.println("다건 조회 테스트 실패");

        // 수정
        User user2Updated = userService.updateUser(user2.getId(), "user2_수정", "45678", "user2email");
        if (user2Updated.getUserName().equals(user2.getUserName())
        && user2Updated.getEmail().equals(user2.getEmail()))
            System.out.println("수정 테스트 성공");
        else
            System.out.println("수정 테스트 실패");

        // 수정 된 데이터 조회
        System.out.printf(
                "수정된 user2 이름 : %s, 비밀번호 : %s, 이메일 : %s%n", user2Updated.getUserName(),
                user2Updated.getPassword(), user2Updated.getEmail());

        // 삭제
        userService.deleteUser(user1.getId());
        if(userService.getAllUsers().size() == 1)
            System.out.println("삭제 테스트 성공");
        else
            System.out.println("삭제 테스트 실패");

        // 조회를 통해 삭제되었는지 확인
        System.out.println("남은 유저들");
        userService.getAllUsers()
                .stream()
                .forEach(
                        u -> System.out.println("id : " + u.getId() + ", name : " + u.getUserName())
                );

        // 중복 생성
        try {
            userService.createUser("user3", "4567", "");
            userService.createUser("user3", "4567", "");
            System.out.println("중복 처리 테스트 실패");
        } catch (IllegalStateException e) {
            System.out.println("중복 처리 테스트 성공");
        }

        // 없는 사용자 조회
        try {
            userService.getUser(java.util.UUID.randomUUID());
            System.out.println("없는 사용자 조회 테스트 실패");
        } catch (NoSuchElementException e) {
            System.out.println("없는 사용자 조회 테스트 성공");
        }
    }

    private static void channelServiceTest() {
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService(userService);
        User user1 = userService.createUser("user1", "1234", "");
        User user2 = userService.createUser("user2", "4567", "");
        Channel channel1 = channelService.createChannel("channel1", ChannelType.PUBLIC, "channel1 description");
        Channel channel2 = channelService.createChannel("channel2", ChannelType.PRIVATE, "channel2 description");
        channelService.joinChannel(channel1.getId(), user1.getId());
        channelService.joinChannel(channel1.getId(), user2.getId());
        // 단건 조회
        Channel result = channelService.getChannel(channel1.getId());
        if(result.getId().equals(channel1.getId())) System.out.println("단건 조회 테스트 성공");
        else System.out.println("단건 조회 테스트 실패");

        // 다건 조회
        List<Channel> channels = channelService.getAllChannels();
        if(channels.size() == 2) System.out.println("다건 조회 테스트 성공");
        else System.out.println("다건 조회 테스트 실패");

        // 수정
        Channel updatedChannel = channelService.updateChannel(channel1.getId(), "channel1_수정됨", ChannelType.PRIVATE, "수정된 description");
        if(updatedChannel.getId().equals(channel1.getId())
        && updatedChannel.getChannelName().equals(channel1.getChannelName()))
            System.out.println("수정 테스트 성공");
        else
            System.out.println("수정 테스트 실패");

        // 수정 된 데이터 조회
        System.out.printf("수정된 channel1 이름 : %s, 채널타입 : %s, 설명 : %s%n"
                , channel1.getChannelName(), channel1.getChannelType(), channel1.getDescription());

        // 삭제
        channelService.deleteChannel(channel2.getId());
        if(channelService.getAllChannels().size() == 1)
            System.out.println("삭제 테스트 성공");
        else
            System.out.println("삭제 테스트 실패");

        // 조회를 통해 삭제되었는지 확인
        System.out.println("남은 채널들");
        channelService.getAllChannels()
                .stream()
                .forEach(
                        c -> System.out.println("id : " + c.getId() + ", name : " + c.getChannelName())
                );

        // 채널 중복 테스트
        try {
            channelService.createChannel("channel2", ChannelType.PUBLIC, "channel2 description 중복");
            channelService.createChannel("channel2", ChannelType.PUBLIC, "channel2 description 중복");
            System.out.println("중복 처리 테스트 실패");
        } catch (IllegalStateException e) {
            System.out.println("중복 처리 테스트 성공");
        }

        // 없는 채널 조회
        try {
            Channel notFound = channelService.getChannel(java.util.UUID.randomUUID());
            System.out.println("없는 채널 조회 테스트 실패");
        } catch (NoSuchElementException e) {
            System.out.println("없는 채널 조회 테스트 성공");
        }
    }

    private static void messageServiceTest() {
        MessageService messageService = new JCFMessageService();
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService(userService);

        User user1 = userService.createUser("user1", "1234", "");
        Channel channel1 = channelService.createChannel("channel1", ChannelType.PUBLIC, "channel1 description");
        Message message1 = messageService.createMessage("message1 content", user1, channel1);
        Message message2 = messageService.createMessage("message2 content", user1, channel1);
        // 단건 조회
        Message result = messageService.getMessage(message1.getId());
        if(result.getId().equals(message1.getId())) System.out.println("단건 조회 테스트 성공");
        else System.out.println("단건 조회 테스트 실패");

        // 다건 조회
        List<Message> messages = messageService.getAllMessages();
        if(messages.size() == 2) System.out.println("다건 조회 테스트 성공");
        else System.out.println("다건 조회 테스트 실패");

        // 수정
        Message updatedMessage = messageService.updateMessage(message1.getId(), "message1 content updated");
        if(updatedMessage.getId().equals(message1.getId())
                && updatedMessage.getContent().equals(message1.getContent()))
            System.out.println("수정 테스트 성공");
        else
            System.out.println("수정 테스트 실패");

        // 수정 된 데이터 조회
        System.out.printf("수정된 message1 내용 : %s\n", updatedMessage.getContent());

        // 삭제
        messageService.deleteMessage(message2.getId());
        if(messageService.getAllMessages().size() == 1)
            System.out.println("삭제 테스트 성공");
        else
            System.out.println("삭제 테스트 실패");

        // 조회를 통해 삭제되었는지 확인
        System.out.println("남은 메세지들");
        messageService.getAllMessages()
                .stream()
                .forEach(
                        m -> System.out.println("content : " + m.getContent()
                                + ", channel : " + m.getChannel().getChannelName() + ", sender name : " + m.getSender().getUserName())
                );

        // 없는 메세지 조회
        try {
            Message notFound = messageService.getMessage(java.util.UUID.randomUUID());
            System.out.println("없는 메세지 조회 테스트 실패");
        } catch (NoSuchElementException e) {
            System.out.println("없는 메세지 조회 테스트 성공");
        }
    }

    private static void printN(String message) {
        System.out.println();
        System.out.println("-".repeat(5) + message + "-".repeat(5));
    }
}