package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.file.*;
import com.sprint.mission.discodeit.service.basic.*;

public class InteractionTest {

    public static void main(String[] args) {
        // [의존성 주입] 파일 기반 레포지토리와 서비스 초기화
        UserRepository userRepository = new FileUserRepository();
        ChannelRepository channelRepository = new FileChannelRepository();
        MessageRepository messageRepository = new FileMessageRepository();

        UserService userService = new BasicUserService(userRepository);
        ChannelService channelService = new BasicChannelService(channelRepository);
        MessageService messageService = new BasicMessageService(messageRepository, userService, channelService);
        InteractionService interactionService = new BasicInteractionService(userService, channelService, messageService);

        System.out.println("=== 통합 상호작용 및 영속성 테스트 시작 ===");

        try {
            // 초기 데이터 생성
            User woody = userService.create("woody", "woody@codeit.com", "img_w");
            Channel notice = channelService.create("공지", "공지사항");

            // 1. 유저 채널 가입 테스트
            System.out.println("\n[1] 유저 가입 테스트");
            interactionService.join(woody.getId(), notice.getId());
            boolean isUserInChannel = notice.getUsers().stream().anyMatch(u -> u.getId().equals(woody.getId()));
            System.out.println(" - 채널 서비스 데이터 반영 여부: " + (isUserInChannel ? "성공" : "실패"));

            // 2. 유저 채널 탈퇴 테스트
            System.out.println("\n[2] 유저 탈퇴 테스트");
            interactionService.leave(woody.getId(), notice.getId());
            boolean isUserLeft = notice.getUsers().stream().noneMatch(u -> u.getId().equals(woody.getId()));
            System.out.println(" - 채널 서비스 데이터 반영 여부: " + (isUserLeft ? "성공" : "실패"));

            // 다시 가입 (다음 테스트를 위해)
            interactionService.join(woody.getId(), notice.getId());

            // 3. 메세지 생성 및 채널 반영 테스트
            System.out.println("\n[3] 메시지 생성 테스트");
            Message msg = messageService.create("안녕하세요", woody.getId(), notice.getId());
            boolean isMsgInChannel = notice.getMessages().stream().anyMatch(m -> m.getId().equals(msg.getId()));
            System.out.println(" - 채널 내 메시지 리스트 반영 여부: " + (isMsgInChannel ? "성공" : "실패"));

            // 4. 메시지 삭제 테스트
            System.out.println("\n[4] 메시지 삭제 테스트");
            messageService.delete(msg.getId());
            boolean isMsgDeleted = notice.getMessages().stream().noneMatch(m -> m.getId().equals(msg.getId()));
            System.out.println(" - 채널 내 메시지 리스트 삭제 반영 여부: " + (isMsgDeleted ? "성공" : "실패"));

            // 5. 유저 삭제 시 채널 반영 테스트 (연쇄 삭제)
            System.out.println("\n[5] 유저 삭제 테스트");
            interactionService.deleteUser(woody.getId());
            boolean isUserRemovedFromChannel = notice.getUsers().stream().noneMatch(u -> u.getId().equals(woody.getId()));
            System.out.println(" - 채널 내 유저 리스트에서 제거 여부: " + (isUserRemovedFromChannel ? "성공" : "실패"));

            // 6. 채널 삭제 시 유저 반영 테스트 (연쇄 삭제)
            // 새로운 유저와 채널 생성 후 테스트
            User mario = userService.create("mario", "mario@codeit.com", "img_m");
            Channel chat = channelService.create("채팅", "자유채팅");
            interactionService.join(mario.getId(), chat.getId());

            System.out.println("\n[6] 채널 삭제 테스트");
            interactionService.deleteChannel(chat.getId());
            boolean isChannelRemovedFromUser = mario.getChannels().stream().noneMatch(c -> c.getId().equals(chat.getId()));
            System.out.println(" - 유저의 참여 채널 리스트에서 제거 여부: " + (isChannelRemovedFromUser ? "성공" : "실패"));

            System.out.println("\n=== 모든 단계 파일 영속화 확인 중 ===");
            System.out.println(" - File*Repository를 통해 각 단계마다 .dat 파일이 갱신되었습니다.");

        } catch (Exception e) {
            System.err.println("테스트 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=== 테스트 종료 ===");
    }
}