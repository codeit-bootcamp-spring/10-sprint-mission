package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

import java.util.List;
import java.util.UUID;

public class JavaApplication {

    // 유저 생성
    static User setupUser(UserService userService, String name, String email) {
        return userService.createUser(name, "password123", email);
    }

    // 채널 생성
    static Channel setupChannel(ChannelService channelService, String name) {
        return channelService.createChannel(name);
    }

    // 채널 입장
    static void joinChannelTest(ChannelService channelService, User user, Channel channel) {
        System.out.println("\n--- 채널 입장 테스트 ---");
        try {
            channelService.joinChannel(user.getId(), channel.getId());
            // 참여자 목록 확인
            List<Channel> myChannels = channelService.findChannelByUserId(user.getId());
            System.out.println("현재 참여 중인 채널 수: " + myChannels.size());
        } catch (Exception e) {
            System.out.println("입장 실패: " + e.getMessage());
        }
    }

    // 메시지 작성
    static Message messageCreateTest(MessageService messageService, Channel channel, User author, String content) {
        System.out.println("\n--- 메시지 작성 테스트 ---");
        Message message = messageService.createMessage(content, channel.getId(), author.getId());
        System.out.println("메시지 생성 완료: " + message.getContent());
        return message;
    }

    // 수정 기능 (유저, 채널, 메시지)
    static void updateTest(UserService us, ChannelService cs, MessageService ms, User user, Channel channel, Message message) {
        System.out.println("\n--- 데이터 수정 테스트 ---");

        // 유저 정보 수정
        us.updateUserInfo(user.getId(), "SuperWoody", "new_woody@codeit.com");

        // 채널 이름 수정
        Channel updatedChannel = cs.updateChannel(channel.getId(), "공지사항(수정됨)");
        System.out.println("채널 이름 변경됨: " + updatedChannel.getChannelName());

        // 메시지 수정
        Message updatedMessage = ms.updateMessage(message.getId(), "반갑습니다.(수정됨)");
        System.out.println("메시지 내용 변경됨: " + updatedMessage.getContent());
    }

    // 조회 기능
    static void readTest(UserService us, MessageService ms, Channel channel) {
        System.out.println("\n--- 조회 테스트 ---");

        // 채널 참여자 조회
        List<User> participants = us.findParticipants(channel.getId());
        System.out.println("현재 채널 참여자 수: " + participants.size() + "명");

        // 채널 메시지 목록 조회
        List<Message> messages = ms.findMessagesByChannelId(channel.getId());
        System.out.println("현재 채널 메시지 수: " + messages.size() + "개");
        messages.forEach(m -> System.out.println(" - " + m.getUser().getUsername() + ": " + m.getContent()));
    }

    // 삭제 및 퇴장 기능
    static void deleteAndLeaveTest(UserService us, ChannelService cs, MessageService ms, User user, Channel channel, Message message) {
        System.out.println("\n--- 삭제 및 퇴장 테스트 ---");

        // 메시지 삭제
        ms.deleteMessage(message.getId());

        // 채널 퇴장
        cs.leaveChannel(user.getId(), channel.getId());
        System.out.println("채널 퇴장 완료");

        // 퇴장 후 조회 확인
        List<User> participants = us.findParticipants(channel.getId());
        System.out.println("퇴장 후 참여자 수: " + participants.size());
    }

    // 통합 테스트
    static void runAllTests(UserService userService, ChannelService channelService, MessageService messageService) {
        // 1. 셋업
        User woody = setupUser(userService, "woody", "woody@codeit.com");
        User buzz = setupUser(userService, "buzz", "buzz@codeit.com");
        Channel channel = setupChannel(channelService, "자유게시판");

        // 2. 입장
        joinChannelTest(channelService, woody, channel);
        joinChannelTest(channelService, buzz, channel);

        // 3. 메시지 작성
        Message msg1 = messageCreateTest(messageService, channel, woody, "안녕하세요! 우디입니다.");
        Message msg2 = messageCreateTest(messageService, channel, buzz, "안녕 우디! 버즈야.");

        // 4. 조회
        readTest(userService, messageService, channel);

        // 5. 수정
        updateTest(userService, channelService, messageService, woody, channel, msg1);

        // 6. 삭제 및 퇴장
        deleteAndLeaveTest(userService, channelService, messageService, woody, channel, msg1);
    }

    public static void main(String[] args) {
        // TEST 1: JCF Repository
        System.out.println("\n######## TEST 1: JCF  #########\n");

        // 의존성 조립 (JCF)
        UserRepository jcfUserRepo = new JCFUserRepository();
        ChannelRepository jcfChannelRepo = new JCFChannelRepository();
        MessageRepository jcfMessageRepo = new JCFMessageRepository();

        UserService jcfUserService = new BasicUserService(jcfUserRepo, jcfChannelRepo, jcfMessageRepo);
        ChannelService jcfChannelService = new BasicChannelService(jcfUserRepo,jcfChannelRepo, jcfMessageRepo);
        MessageService jcfMessageService = new BasicMessageService(jcfMessageRepo, jcfChannelRepo, jcfUserRepo);

        // 테스트 실행
        runAllTests(jcfUserService, jcfChannelService, jcfMessageService);

        // TEST 2: File Repository (파일 영속화)
        System.out.println("\n######## TEST 2: File ########\n");

        // 의존성 조립 (File)
        UserRepository fileUserRepo = new FileUserRepository();
        ChannelRepository fileChannelRepo = new FileChannelRepository();
        MessageRepository fileMessageRepo = new FileMessageRepository();

        UserService fileUserService = new BasicUserService(fileUserRepo, fileChannelRepo, fileMessageRepo);
        ChannelService fileChannelService = new BasicChannelService(fileUserRepo, fileChannelRepo, fileMessageRepo);
        MessageService fileMessageService = new BasicMessageService(fileMessageRepo, fileChannelRepo, fileUserRepo);

        // 테스트 실행
        runAllTests(fileUserService, fileChannelService, fileMessageService);

        System.out.println("\n[SYSTEM] 모든 테스트가 성공적으로 완료되었습니다.");
    }
}