package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.file.*; // File Repository 사용 시
import com.sprint.mission.discodeit.repository.jcf.*;  // JCF Repository 사용 시
import com.sprint.mission.discodeit.service.basic.*;
import com.sprint.mission.discodeit.service.*;

import java.nio.file.Paths;

public class JavaApplication3 {

    // 1. 유저 셋업 (필드 순서: username, nickname, email, phoneNumber)
    static User setupUser(UserService userService) {
        return userService.createUser("woody", "우디", "woody@codeit.com", "010-1234-5678");
    }

    // 2. 채널 셋업 (필드 순서: name, description, visibility)
    static Channel setupChannel(ChannelService channelService) {
        return channelService.createChannel("공지사항", "공지 채널입니다.", Channel.ChannelType.PUBLIC);
    }

    // 3. 메시지 테스트 (필드 순서: authorId, channelId, content)
    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        Message message = messageService.sendMessage(author.getId(), channel.getId(), "안녕하세요. 반갑습니다!");
        System.out.println("메시지 생성 성공! ID: " + message.getId());
        System.out.println("내용: " + message.getContent());
    }

    public static void main(String[] args) {
        // --- [서비스 및 레포지토리 초기화 단계] ---

        // A. 레포지토리 생성 (FileIO를 사용할 경우)
        UserRepository userRepository = new FileUserRepository(Paths.get("data/users"));
        ChannelRepository channelRepository = new FileChannelRepository(Paths.get("data/channels"));
        MessageRepository messageRepository = new FileMessageRepository(Paths.get("data/messages"));

//        // 만약 JCF(메모리)로 테스트하고 싶다면 위를 주석처리하고 아래를 사용하세요.
//        UserRepository userRepository = new JCFUserRepository();
//        ChannelRepository channelRepository = new JCFChannelRepository();
//        MessageRepository messageRepository = new JCFMessageRepository();

        // B. Basic 서비스 초기화 (생성자를 통한 레포지토리 주입)
        BasicUserService userService = new BasicUserService(userRepository);
        BasicChannelService channelService = new BasicChannelService(channelRepository);
        BasicMessageService messageService = new BasicMessageService(messageRepository);

        // C. 순환 참조 해결을 위한 Setter 주입
        userService.setChannelService(channelService);
        userService.setMessageService(messageService);
        channelService.setUserService(userService);
        channelService.setMessageService(messageService);
        messageService.setUserService(userService);
        messageService.setChannelService(channelService);

        // 1. 셋업
        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);

        // 2. 테스트
        messageCreateTest(messageService, channel, user);
    }
}