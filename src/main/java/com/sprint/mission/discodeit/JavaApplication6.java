package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class JavaApplication6 {

    private static BasicUserService userService;
    private static BasicChannelService channelService;
    private static BasicMessageService messageService;

    // 검증을 위해 레포지토리 직접 접근 (서비스 거치지 않고 파일 상태 확인용)
    private static FileUserRepository userRepo;
    private static FileChannelRepository channelRepo;
    private static FileMessageRepository messageRepo;

    public static void main(String[] args) {
        // 1. 서비스 초기화 (데이터 폴더: data/app6)
        initServices("app6");

        System.out.println("##################################################");
        System.out.println("###     FILE REPOSITORY SYNC INTEGRATION TEST  ###");
        System.out.println("##################################################\n");

        // 시나리오 1: 유저 삭제 시 연관 데이터 동기화 확인
        testUserDeletionSync();

        // 시나리오 2: 채널 삭제 시 연관 데이터 동기화 확인
        testChannelDeletionSync();

        // 시나리오 3: 메시지 삭제 시 연관 데이터 동기화 확인
        testMessageDeletionSync();
    }

    // ======================================================================
    // [TEST 1] 유저 삭제 테스트
    // 기대 결과: 유저가 삭제되면 -> 그 유저가 쓴 메시지도 삭제되고 -> 채널 참여자 목록에서도 빠져야 함
    // ======================================================================
    private static void testUserDeletionSync() {
        System.out.println("========== [TEST 1] 유저 삭제 및 전파 테스트 ==========");

        // 1. 데이터 셋업
        User user = userService.createUser("deleteMe", "삭제될유저", "del@test.com", "010-0000-0000");
        Channel channel = channelService.createChannel("유저삭제테스트방", "설명", Channel.ChannelType.PUBLIC);
        userService.joinChannel(user.getId(), channel.getId());
        Message msg = messageService.sendMessage(user.getId(), channel.getId(), "유저가 삭제되면 이 메시지도 사라져야 함");

        UUID userId = user.getId();
        UUID channelId = channel.getId();
        UUID msgId = msg.getId();

        System.out.println("[Before] 유저 존재 여부: " + userRepo.existsById(userId));
        System.out.println("[Before] 메시지 존재 여부: " + messageRepo.existsById(msgId));
        System.out.println("[Before] 채널 내 유저 수: " + channelRepo.findById(channelId).get().getUsers().size());

        // 2. 삭제 수행
        System.out.println("\n>> 유저 삭제 실행...");
        userService.deleteUser(userId);

        // 3. 검증 (파일에서 직접 다시 로드)
        boolean userExists = userRepo.existsById(userId);
        boolean msgExists = messageRepo.existsById(msgId);
        Channel updatedChannel = channelRepo.findById(channelId).orElseThrow();
        boolean userInChannel = updatedChannel.getUsers().stream().anyMatch(u -> u.getId().equals(userId));

        System.out.println("[After] 유저 존재 여부 (False 기대): " + userExists);
        System.out.println("[After] 메시지 존재 여부 (False 기대): " + msgExists);
        System.out.println("[After] 채널 내 유저 존재 (False 기대): " + userInChannel);
        System.out.println("-------------------------------------------------------\n");
    }

    // ======================================================================
    // [TEST 2] 채널 삭제 테스트
    // 기대 결과: 채널이 삭제되면 -> 채널 내 메시지 삭제 -> 유저의 가입 채널 목록에서 삭제
    // ======================================================================
    private static void testChannelDeletionSync() {
        System.out.println("========== [TEST 2] 채널 삭제 및 전파 테스트 ==========");

        // 1. 데이터 셋업
        User user = userService.createUser("survivor", "생존자", "live@test.com", "010-1111-1111");
        Channel channel = channelService.createChannel("폭파될채널", "곧 삭제됨", Channel.ChannelType.PUBLIC);
        userService.joinChannel(user.getId(), channel.getId());
        Message msg = messageService.sendMessage(user.getId(), channel.getId(), "채널이 삭제되면 이 메시지도 사라져야 함");

        UUID userId = user.getId();
        UUID channelId = channel.getId();
        UUID msgId = msg.getId();

        System.out.println("[Before] 채널 존재 여부: " + channelRepo.existsById(channelId));
        System.out.println("[Before] 메시지 존재 여부: " + messageRepo.existsById(msgId));
        System.out.println("[Before] 유저의 가입 채널 수: " + userRepo.findById(userId).get().getChannels().size());

        // 2. 삭제 수행
        System.out.println("\n>> 채널 삭제 실행...");
        channelService.deleteChannel(channelId);

        // 3. 검증
        boolean channelExists = channelRepo.existsById(channelId);
        boolean msgExists = messageRepo.existsById(msgId);
        User updatedUser = userRepo.findById(userId).orElseThrow();
        boolean channelInUser = updatedUser.getChannels().stream().anyMatch(c -> c.getId().equals(channelId));

        System.out.println("[After] 채널 존재 여부 (False 기대): " + channelExists);
        System.out.println("[After] 메시지 존재 여부 (False 기대): " + msgExists);
        System.out.println("[After] 유저의 가입 채널 존재 (False 기대): " + channelInUser);
        System.out.println("-------------------------------------------------------\n");
    }

    // ======================================================================
    // [TEST 3] 메시지 삭제 테스트
    // 기대 결과: 메시지 삭제 시 -> 유저의 메시지 리스트에서 삭제 -> 채널의 메시지 리스트에서 삭제
    // ======================================================================
    private static void testMessageDeletionSync() {
        System.out.println("========== [TEST 3] 메시지 삭제 및 전파 테스트 ==========");

        // 1. 데이터 셋업
        User user = userService.createUser("writer", "글쓴이", "w@test.com", "010-2222-2222");
        Channel channel = channelService.createChannel("수다방", "메시지삭제테스트", Channel.ChannelType.PUBLIC);
        userService.joinChannel(user.getId(), channel.getId());
        Message msg = messageService.sendMessage(user.getId(), channel.getId(), "삭제될 메시지입니다.");

        UUID userId = user.getId();
        UUID channelId = channel.getId();
        UUID msgId = msg.getId();

        System.out.println("[Before] 메시지 파일 존재: " + messageRepo.existsById(msgId));
        System.out.println("[Before] 유저가 가진 메시지 수: " + userRepo.findById(userId).get().getMessages().size());
        System.out.println("[Before] 채널이 가진 메시지 수: " + channelRepo.findById(channelId).get().getMessages().size());

        // 2. 삭제 수행
        System.out.println("\n>> 메시지 삭제 실행...");
        messageService.deleteMessage(msgId);

        // 3. 검증
        boolean msgFileExists = messageRepo.existsById(msgId);
        User updatedUser = userRepo.findById(userId).get();
        Channel updatedChannel = channelRepo.findById(channelId).get();

        boolean msgInUser = updatedUser.getMessages().stream().anyMatch(m -> m.getId().equals(msgId));
        boolean msgInChannel = updatedChannel.getMessages().stream().anyMatch(m -> m.getId().equals(msgId));

        System.out.println("[After] 메시지 파일 존재 (False 기대): " + msgFileExists);
        System.out.println("[After] 유저 목록 내 메시지 존재 (False 기대): " + msgInUser);
        System.out.println("[After] 채널 목록 내 메시지 존재 (False 기대): " + msgInChannel);
        System.out.println("-------------------------------------------------------\n");
    }

    // 초기화 메서드
    private static void initServices(String testName) {
        Path rootPath = Paths.get("data", testName);

        // 레포지토리 초기화
        userRepo = new FileUserRepository(rootPath.resolve("users"));
        channelRepo = new FileChannelRepository(rootPath.resolve("channels"));
        messageRepo = new FileMessageRepository(rootPath.resolve("messages"));

        // 서비스 초기화
        userService = new BasicUserService(userRepo);
        channelService = new BasicChannelService(channelRepo);
        messageService = new BasicMessageService(messageRepo);

        // 의존성 주입 (Setter)
        userService.setChannelService(channelService);
        userService.setMessageService(messageService);
        channelService.setUserService(userService);
        channelService.setMessageService(messageService);
        messageService.setUserService(userService);
        messageService.setChannelService(channelService);
    }
}