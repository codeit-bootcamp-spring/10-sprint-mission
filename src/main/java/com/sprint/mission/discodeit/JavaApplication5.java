package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.*;
import com.sprint.mission.discodeit.service.basic.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class JavaApplication5 {

    private static BasicUserService userService;
    private static BasicChannelService channelService;
    private static BasicMessageService messageService;

    public static void main(String[] args) {
        // 1. 초기화 (app5 전용 폴더 사용)
        initServices("app5");

        System.out.println("========== [STEP 1] 초기 데이터 생성 ==========");
        User user = userService.createUser("tester", "초기닉네임", "test@test.com", "010-1111-1111");
        Channel channel = channelService.createChannel("자유게시판", "초기설명", Channel.ChannelType.PUBLIC);

        // 가입 및 메시지 전송
        userService.joinChannel(user.getId(), channel.getId());
        Message message = messageService.sendMessage(user.getId(), channel.getId(), "테스트 메시지");

        System.out.println("초기 데이터 생성 완료.");
        System.out.println("----------------------------------------------\n");

        // 2. 유저 수정 테스트
        testUserUpdate(user.getId(), message.getId(), channel.getId());

        // 3. 채널 수정 테스트
        testChannelUpdate(channel.getId(), user.getId(), message.getId());

        // 4. 메시지 수정 테스트
        testMessageUpdate(message.getId(), user.getId(), channel.getId());
    }

    private static void testUserUpdate(UUID userId, UUID messageId, UUID channelId) {
        System.out.println("========== [STEP 2] 유저 수정 및 전파 테스트 ==========");

        // Before 출력
        Message beforeMsg = messageService.findById(messageId);
        Channel beforeCh = channelService.findById(channelId);
        System.out.println("[Before] 메시지 작성자 닉네임: " + beforeMsg.getAuthor().getNickname());
        System.out.println("[Before] 채널 내 유저 정보: " + beforeCh.getUsers().iterator().next().getNickname());

        // 수정 수행
        System.out.println("\n>> 유저 닉네임을 '수정된닉네임'으로 변경 중...");
        userService.updateUserProfile(userId, null, "수정된닉네임", null, null);

        // After 출력 (레포지토리에서 새로 불러와 확인)
        Message afterMsg = messageService.findById(messageId);
        Channel afterCh = channelService.findById(channelId);
        System.out.println("[After] 메시지 작성자 닉네임: " + afterMsg.getAuthor().getNickname());
        System.out.println("[After] 채널 내 유저 정보: " + afterCh.getUsers().iterator().next().getNickname());
        System.out.println("----------------------------------------------\n");
    }

    private static void testChannelUpdate(UUID channelId, UUID userId, UUID messageId) {
        System.out.println("========== [STEP 3] 채널 수정 및 전파 테스트 ==========");

        // Before 출력
        User beforeUser = userService.findById(userId);
        Message beforeMsg = messageService.findById(messageId);
        System.out.println("[Before] 유저 가입 채널 이름: " + beforeUser.getChannels().iterator().next().getChannelName());
        System.out.println("[Before] 메시지 소속 채널 이름: " + beforeMsg.getChannel().getChannelName());

        // 수정 수행
        System.out.println("\n>> 채널 이름을 '공지사항'으로 변경 중...");
        channelService.updateChannel(channelId, "공지사항", null, null);

        // After 출력
        User afterUser = userService.findById(userId);
        Message afterMsg = messageService.findById(messageId);
        System.out.println("[After] 유저 가입 채널 이름: " + afterUser.getChannels().iterator().next().getChannelName());
        System.out.println("[After] 메시지 소속 채널 이름: " + afterMsg.getChannel().getChannelName());
        System.out.println("----------------------------------------------\n");
    }

    private static void testMessageUpdate(UUID messageId, UUID userId, UUID channelId) {
        System.out.println("========== [STEP 4] 메시지 수정 및 전파 테스트 ==========");

        // Before 출력
        User beforeUser = userService.findById(userId);
        Channel beforeCh = channelService.findById(channelId);
        System.out.println("[Before] 유저 보관 메시지 내용: " + beforeUser.getMessages().get(0).getContent());
        System.out.println("[Before] 채널 보관 메시지 내용: " + beforeCh.getMessages().get(0).getContent());

        // 수정 수행
        System.out.println("\n>> 메시지 내용을 '내용이수정됨'으로 변경 중...");
        messageService.updateMessage(messageId, "내용이수정됨");

        // After 출력
        User afterUser = userService.findById(userId);
        Channel afterCh = channelService.findById(channelId);
        System.out.println("[After] 유저 보관 메시지 내용: " + afterUser.getMessages().get(0).getContent());
        System.out.println("[After] 채널 보관 메시지 내용: " + afterCh.getMessages().get(0).getContent());
        System.out.println("----------------------------------------------\n");
    }

    private static void initServices(String testName) {
        Path rootPath = Paths.get("data", testName);

        FileUserRepository userRepo = new FileUserRepository(rootPath.resolve("users"));
        FileChannelRepository channelRepo = new FileChannelRepository(rootPath.resolve("channels"));
        FileMessageRepository messageRepo = new FileMessageRepository(rootPath.resolve("messages"));

        userService = new BasicUserService(userRepo);
        channelService = new BasicChannelService(channelRepo);
        messageService = new BasicMessageService(messageRepo);

        userService.setChannelService(channelService);
        userService.setMessageService(messageService);
        channelService.setUserService(userService);
        channelService.setMessageService(messageService);
        messageService.setUserService(userService);
        messageService.setChannelService(channelService);
    }
}