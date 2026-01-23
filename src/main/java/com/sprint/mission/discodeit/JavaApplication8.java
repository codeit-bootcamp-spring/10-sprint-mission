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

public class JavaApplication8 {

    private static BasicUserService userService;
    private static BasicChannelService channelService;
    private static BasicMessageService messageService;

    // 검증용 레포지토리
    private static FileUserRepository userRepo;
    private static FileChannelRepository channelRepo;
    private static FileMessageRepository messageRepo;

    public static void main(String[] args) {
        initServices("app8"); // app8 폴더 사용

        System.out.println("##################################################");
        System.out.println("###   DEEP DELETE SYNC TEST (MESSAGE LIST CHECK) ###");
        System.out.println("##################################################\n");

        testUserDeletionDeepSync();
        testChannelDeletionDeepSync();
    }

    // [TEST 1] 유저 삭제 -> 채널의 '유저 목록'과 '메시지 목록'에서 모두 제거되었는지 확인
    private static void testUserDeletionDeepSync() {
        System.out.println("========== [TEST 1] 유저 삭제 시 채널 내 흔적(유저+메시지) 제거 확인 ==========");

        // 1. Setup
        User user = userService.createUser("leaver", "탈퇴자", "bye@test.com", "010-0000-0000");
        Channel channel = channelService.createChannel("GhostCheckRoom", "desc", Channel.ChannelType.PUBLIC);
        userService.joinChannel(user.getId(), channel.getId());
        Message msg = messageService.sendMessage(user.getId(), channel.getId(), "유저가 떠나면 이 글도 채널에서 사라져야 해");

        UUID userId = user.getId();
        UUID channelId = channel.getId();
        UUID msgId = msg.getId();

        System.out.println("[Before] 채널 내 유저 수: " + channelRepo.findById(channelId).get().getUsers().size());
        System.out.println("[Before] 채널 내 메시지 수: " + channelRepo.findById(channelId).get().getMessages().size());

        // 2. Delete User
        System.out.println("\n>> 유저 삭제 실행...");
        userService.deleteUser(userId);

        // 3. Verify
        Channel updatedChannel = channelRepo.findById(channelId).orElseThrow();

        // 검증 1: 채널 유저 목록에 없는가?
        boolean userInChannel = updatedChannel.getUsers().stream().anyMatch(u -> u.getId().equals(userId));
        // 검증 2: 채널 메시지 목록에 없는가? (추가된 부분)
        boolean msgInChannel = updatedChannel.getMessages().stream().anyMatch(m -> m.getId().equals(msgId));

        System.out.println("[After] 채널 내 유저 존재 여부 (False 기대): " + userInChannel);
        System.out.println("[After] 채널 내 메시지 존재 여부 (False 기대): " + msgInChannel);

        if (!userInChannel && !msgInChannel) {
            System.out.println(">> RESULT: SUCCESS (완벽하게 제거됨)");
        } else {
            System.out.println(">> RESULT: FAIL (찌꺼기 데이터 남음)");
        }
        System.out.println("-------------------------------------------------------\n");
    }

    // [TEST 2] 채널 삭제 -> 유저의 '채널 목록'과 '메시지 목록'에서 모두 제거되었는지 확인
    private static void testChannelDeletionDeepSync() {
        System.out.println("========== [TEST 2] 채널 삭제 시 유저 내 흔적(채널+메시지) 제거 확인 ==========");

        // 1. Setup
        User user = userService.createUser("survivor", "생존자", "live@test.com", "010-1111-1111");
        Channel channel = channelService.createChannel("BoomRoom", "폭파예정", Channel.ChannelType.PUBLIC);
        userService.joinChannel(user.getId(), channel.getId());
        Message msg = messageService.sendMessage(user.getId(), channel.getId(), "채널이 터지면 내 기록에서도 사라져야 해");

        UUID userId = user.getId();
        UUID channelId = channel.getId();
        UUID msgId = msg.getId();

        System.out.println("[Before] 유저 가입 채널 수: " + userRepo.findById(userId).get().getChannels().size());
        System.out.println("[Before] 유저 작성 메시지 수: " + userRepo.findById(userId).get().getMessages().size());

        // 2. Delete Channel
        System.out.println("\n>> 채널 삭제 실행...");
        channelService.deleteChannel(channelId);

        // 3. Verify
        User updatedUser = userRepo.findById(userId).orElseThrow();

        // 검증 1: 유저 채널 목록에 없는가?
        boolean channelInUser = updatedUser.getChannels().stream().anyMatch(c -> c.getId().equals(channelId));
        // 검증 2: 유저 메시지 목록에 없는가? (추가된 부분)
        boolean msgInUser = updatedUser.getMessages().stream().anyMatch(m -> m.getId().equals(msgId));

        System.out.println("[After] 유저 내 채널 존재 여부 (False 기대): " + channelInUser);
        System.out.println("[After] 유저 내 메시지 존재 여부 (False 기대): " + msgInUser);

        if (!channelInUser && !msgInUser) {
            System.out.println(">> RESULT: SUCCESS (완벽하게 제거됨)");
        } else {
            System.out.println(">> RESULT: FAIL (찌꺼기 데이터 남음)");
        }
        System.out.println("-------------------------------------------------------\n");
    }

    private static void initServices(String testName) {
        Path rootPath = Paths.get("data", testName);

        userRepo = new FileUserRepository(rootPath.resolve("users"));
        channelRepo = new FileChannelRepository(rootPath.resolve("channels"));
        messageRepo = new FileMessageRepository(rootPath.resolve("messages"));

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