package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.basic.*;

public class JavaApplication {

    // [템플릿 메서드들]
    static User setupUser(UserService userService) {
        return userService.createUser("woody@codeit.com", "woody1234", "woody", UserStatusType.ONLINE);
    }

    static Channel setupChannel(ChannelService channelService, User owner) {
        return channelService.createChannel("공지", owner.getId(), ChannelType.CHAT);
    }

    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        Message message = messageService.createMessage("안녕하세요.", author.getId(), channel.getId(), MessageType.CHAT);
        System.out.println("메시지 생성 성공! ID: " + message.getId());
    }

    public static void main(String[] args) {
        // 1. 레포지토리
        UserRepository userRepo = new JCFUserRepository();
        ChannelRepository channelRepo = new JCFChannelRepository();
        MessageRepository messageRepo = new JCFMessageRepository();

        /* [ ] File*Repository 활용 테스트 시 아래 3줄로 교체
        UserRepository userRepo = new FileUserRepository();
        ChannelRepository channelRepo = new FileChannelRepository();
        MessageRepository messageRepo = new FileMessageRepository();
        */

        // 2. 서비스 초기화
        BasicUserService userService = new BasicUserService(userRepo);
        BasicChannelService channelService = new BasicChannelService(channelRepo, userService);
        BasicMessageService messageService = new BasicMessageService(messageRepo, userService, channelService);

        // 유저 서비스에 필요한 나머지 서비스들 주입
        userService.setJCFChannelService(channelService);
        userService.setJCFMessageService(messageService);

        // 3. 셋업 및 테스트 실행
        try {
            User user = setupUser(userService);
            System.out.println("사용자 셋업 완료: " + user.getNickname());

            Channel channel = setupChannel(channelService, user);
            System.out.println("채널 셋업 완료: " + channel.getChannelName());

            messageCreateTest(messageService, channel, user);

            System.out.println("\n=== 전체 데이터 확인 ===");
            System.out.println("전체 유저 수: " + userService.searchUserAll().size());
            System.out.println("전체 채널 수: " + channelService.searchChannelAll().size());
        } catch (Exception e) {
            System.err.println("테스트 중 오류 발생: " + e.getMessage());
        }
    }
}