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

public class JavaApplication7 {

    private static BasicUserService userService;
    private static BasicChannelService channelService;
    private static BasicMessageService messageService;

    // 검증을 위해 레포지토리 직접 접근 (서비스 거치지 않고 파일 상태 확인용)
    private static FileUserRepository userRepo;
    private static FileChannelRepository channelRepo;
    private static FileMessageRepository messageRepo;

    public static void main(String[] args) {
        // 1. 서비스 초기화 (데이터 폴더: data/app7)
        initServices("app7");

        System.out.println("##################################################");
        System.out.println("###     FILE REPOSITORY UPDATE SYNC TEST       ###");
        System.out.println("##################################################\n");

        // 시나리오 1: 유저 정보 수정 시 -> 채널에 저장된 유저 정보 동기화 확인
        testUserUpdateSync();

        // 시나리오 2: 채널 정보 수정 시 -> 유저에 저장된 채널 정보 동기화 확인
        testChannelUpdateSync();

        // 시나리오 3: 메시지 수정 시 -> 유저/채널에 저장된 메시지 동기화 확인 (보너스)
        testMessageUpdateSync();
    }

    // ======================================================================
    // [TEST 1] 유저 수정 -> 채널 내 유저 정보 확인
    // ======================================================================
    private static void testUserUpdateSync() {
        System.out.println("========== [TEST 1] 유저 수정 및 채널 전파 테스트 ==========");

        // 1. 데이터 셋업
        User user = userService.createUser("updateUser", "BeforeNick", "u@test.com", "010-1111-1111");
        Channel channel = channelService.createChannel("UserSyncTestChannel", "desc", Channel.ChannelType.PUBLIC);
        userService.joinChannel(user.getId(), channel.getId());

        UUID userId = user.getId();
        UUID channelId = channel.getId();

        // 2. 수정 전 상태 확인
        String originalNickInChannel = channelRepo.findById(channelId).get().getUsers().stream()
                .filter(u -> u.getId().equals(userId)).findFirst().get().getNickname();
        System.out.println("[Before] 채널 내 저장된 유저 닉네임: " + originalNickInChannel);

        // 3. 유저 수정 수행
        String newNickname = "AfterNick";
        System.out.println("\n>> 유저 닉네임을 '" + newNickname + "'으로 변경 중...");
        userService.updateUserProfile(userId, null, newNickname, null, null);

        // 4. 검증 (파일에서 다시 로드)
        Channel reloadedChannel = channelRepo.findById(channelId).orElseThrow();
        User userInChannel = reloadedChannel.getUsers().stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("채널에서 유저를 찾을 수 없음"));

        String loadedNickname = userInChannel.getNickname();
        System.out.println("[After] 채널 파일에서 다시 불러온 유저 닉네임: " + loadedNickname);

        if (newNickname.equals(loadedNickname)) {
            System.out.println(">> RESULT: SUCCESS (동기화 완료)");
        } else {
            System.out.println(">> RESULT: FAIL (동기화 실패 - 기대값: " + newNickname + ", 실제값: " + loadedNickname + ")");
        }
        System.out.println("-------------------------------------------------------\n");
    }

    // ======================================================================
    // [TEST 2] 채널 수정 -> 유저 내 채널 정보 확인
    // ======================================================================
    private static void testChannelUpdateSync() {
        System.out.println("========== [TEST 2] 채널 수정 및 유저 전파 테스트 ==========");

        // 1. 데이터 셋업
        User user = userService.createUser("channelWatcher", "Watcher", "cw@test.com", "010-2222-2222");
        Channel channel = channelService.createChannel("BeforeChannelName", "desc", Channel.ChannelType.PUBLIC);
        userService.joinChannel(user.getId(), channel.getId());

        UUID userId = user.getId();
        UUID channelId = channel.getId();

        // 2. 수정 전 상태 확인
        String originalNameInUser = userRepo.findById(userId).get().getChannels().stream()
                .filter(c -> c.getId().equals(channelId)).findFirst().get().getChannelName();
        System.out.println("[Before] 유저 내 저장된 채널 이름: " + originalNameInUser);

        // 3. 채널 수정 수행
        String newChannelName = "AfterChannelName";
        System.out.println("\n>> 채널 이름을 '" + newChannelName + "'으로 변경 중...");
        channelService.updateChannel(channelId, newChannelName, null, null);

        // 4. 검증 (파일에서 다시 로드)
        User reloadedUser = userRepo.findById(userId).orElseThrow();
        Channel channelInUser = reloadedUser.getChannels().stream()
                .filter(c -> c.getId().equals(channelId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("유저에게서 채널을 찾을 수 없음"));

        String loadedChannelName = channelInUser.getChannelName();
        System.out.println("[After] 유저 파일에서 다시 불러온 채널 이름: " + loadedChannelName);

        if (newChannelName.equals(loadedChannelName)) {
            System.out.println(">> RESULT: SUCCESS (동기화 완료)");
        } else {
            System.out.println(">> RESULT: FAIL (동기화 실패 - 기대값: " + newChannelName + ", 실제값: " + loadedChannelName + ")");
        }
        System.out.println("-------------------------------------------------------\n");
    }

    // ======================================================================
    // [TEST 3] 메시지 수정 -> 유저/채널 내 메시지 정보 확인
    // ======================================================================
    private static void testMessageUpdateSync() {
        System.out.println("========== [TEST 3] 메시지 수정 및 전파 테스트 ==========");

        // 1. 데이터 셋업
        User user = userService.createUser("msgWriter", "Writer", "mw@test.com", "010-3333-3333");
        Channel channel = channelService.createChannel("MsgTestChannel", "desc", Channel.ChannelType.PUBLIC);
        userService.joinChannel(user.getId(), channel.getId());
        Message msg = messageService.sendMessage(user.getId(), channel.getId(), "Original Content");

        UUID userId = user.getId();
        UUID channelId = channel.getId();
        UUID msgId = msg.getId();

        System.out.println("[Before] 원본 내용: " + msg.getContent());

        // 2. 메시지 수정 수행
        String newContent = "Updated Content";
        System.out.println("\n>> 메시지 내용을 '" + newContent + "'으로 변경 중...");
        messageService.updateMessage(msgId, newContent);

        // 3. 검증
        User reloadedUser = userRepo.findById(userId).orElseThrow();
        Channel reloadedChannel = channelRepo.findById(channelId).orElseThrow();

        Message msgInUser = reloadedUser.getMessages().stream().filter(m -> m.getId().equals(msgId)).findFirst().orElseThrow();
        Message msgInChannel = reloadedChannel.getMessages().stream().filter(m -> m.getId().equals(msgId)).findFirst().orElseThrow();

        System.out.println("[After] 유저 내 메시지 내용: " + msgInUser.getContent());
        System.out.println("[After] 채널 내 메시지 내용: " + msgInChannel.getContent());

        if (newContent.equals(msgInUser.getContent()) && newContent.equals(msgInChannel.getContent())) {
            System.out.println(">> RESULT: SUCCESS (동기화 완료)");
        } else {
            System.out.println(">> RESULT: FAIL");
        }
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