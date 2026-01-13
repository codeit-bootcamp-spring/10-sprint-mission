package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;

import java.util.List;
import java.util.UUID;

public class JavaApplication {

    public static void main(String[] args) {

        // 1. 서비스 구현체 생성 (List 기반)
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();
        MessageService messageService = new JCFMessageService();

        // 2. User 등록
        System.out.println("=== User 등록 ===");
        User user1 = new User("user1", "user1@example.com");
        User user2 = new User("user2", "user2@example.com");

        userService.create(user1);
        userService.create(user2);

        // 단건 조회
        System.out.println("단건 조회 (user1): " + userService.findById(user1.getId()));

        // 전체 조회
        System.out.println("전체 User 조회:");
        List<User> users = userService.findAll();
        for (User u : users) {
            System.out.println(" - " + u.getId() + ", " + u.getUsername() + ", " + u.getEmail());
        }

        //3. Channel 등록
        System.out.println("\n=== Channel 등록 ===");
        Channel channel1 = new Channel("general", "일반 채팅 채널");
        Channel channel2 = new Channel("voice-room", "음성 채널 설명");

        channelService.create(channel1);
        channelService.create(channel2);

        // 전체 Channel 조회
        System.out.println("전체 Channel 조회:");
        for (Channel c : channelService.findAll()) {
            System.out.println(" - " + c.getId() + ", " + c.getName() + ", " + c.getDescription());
        }

        //4. Message 등록
        System.out.println("\n=== Message 등록 ===");
        Message message1 = new Message(user1, channel1, "안녕하세요! 두번째 메시지입니다.");
        Message message2 = new Message(user2, channel1, "반가워요!");

        messageService.create(message1);
        messageService.create(message2);

        // 전체 Message 조회
        System.out.println("전체 Message 조회:");
        for (Message m : messageService.findAll()) {
            System.out.println(" - " + m.getId()
                    + ", user=" + m.getUser().getUsername()
                    + ", channel=" + m.getChannel().getName()
                    + ", content=" + m.getContent());
        }

        // 5. 수정 테스트 (User / Channel / Message)
        System.out.println("\n=== 수정 테스트 ===");

        // User 수정
        userService.update(user1.getId(), "user1-renamed", "user1-new@example.com");
        System.out.println("수정된 User 조회: " + userService.findById(user1.getId()).getUsername());

        // Channel 수정
        channelService.update(channel1.getId(), "general-renamed", "수정된 채널 설명");
        Channel updatedChannel = channelService.findById(channel1.getId());
        System.out.println("수정된 Channel 조회: " + updatedChannel.getName() + ", " + updatedChannel.getDescription());

        // Message 수정
        messageService.update(message1.getId(),
                "수정된 두번째 메시지",
                user1,
                channel1);
        Message updatedMessage = messageService.findById(message1.getId());
        System.out.println("수정된 Message 조회: " + updatedMessage.getContent());

        //6. 삭제 테스트
        System.out.println("\n=== 삭제 테스트 ===");

        // User 삭제
        userService.delete(user2.getId());
        System.out.println("User 전체 조회 (user2 삭제 후):");
        for (User u : userService.findAll()) {
            System.out.println(" - " + u.getId() + ", " + u.getUsername());
        }

        // Channel 삭제
        channelService.delete(channel2.getId());
        System.out.println("Channel 전체 조회 (channel2 삭제 후):");
        for (Channel c : channelService.findAll()) {
            System.out.println(" - " + c.getId() + ", " + c.getName());
        }

        // Message 삭제
        messageService.delete(message2.getId());
        System.out.println("Message 전체 조회 (message2 삭제 후):");
        for (Message m : messageService.findAll()) {
            System.out.println(" - " + m.getId() + ", content=" + m.getContent());
        }

        System.out.println("=== 메인 테스트 종료 ===");
    }
}
