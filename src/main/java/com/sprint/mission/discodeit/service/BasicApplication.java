package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.file.*;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.basic.*;

import java.util.UUID;

public class BasicApplication {

    public static void main(String[] args) {
        // 1. 레포지토리(저장소 부품) 준비 - 파일 기반
        UserRepository userRepository = new FileUserRepository();
        ChannelRepository channelRepository = new FileChannelRepository();
        MessageRepository messageRepository = new FileMessageRepository();

        // 2. 서비스(비즈니스 로직) 준비 - 레포지토리 주입
        UserService userService = new BasicUserService(userRepository);
        ChannelService channelService = new BasicChannelService(channelRepository);
        MessageService messageService = new BasicMessageService(messageRepository, userService, channelService);

        // 3. 상호작용 서비스 준비
        InteractionService interactionService = new BasicInteractionService(userService, channelService, messageService);

        System.out.println("=== 디스코드 시뮬레이션 시작 (파일 저장 모드) ===");

        // 4. 테스트 시나리오 실행
        try {
            // [기존 유저/채널 생성 로직]
            User woody = userService.create("woody","woody@email.com","woody's profile");
            System.out.println("유저 "+woody.getName()+" 가 생성되었습니다.");
            User mario = userService.create("mario","mario@email.com","mario's profile");
            System.out.println("유저 "+mario.getName()+" 가 생성되었습니다.");

            Channel notice = channelService.create("공지 채널","공지 채널입니다.");
            System.out.println("채널 "+notice.getName()+" 가 생성되었습니다.");
            Channel chat = channelService.create("채팅 채널","채팅 채널입니다.");
            System.out.println("채널 "+chat.getName()+" 가 생성되었습니다.");

            // [기존 상호작용 로직]
            interactionService.join(woody.getId(), notice.getId());
            System.out.println(woody.getName()+"가 "+notice.getName()+"에 참여하였습니다.");

            interactionService.join(mario.getId(), notice.getId());
            System.out.println(mario.getName()+"가 "+notice.getName()+"에 참여하였습니다.");

            messageService.create("나의 첫 메세지", woody.getId(), notice.getId());
            messageService.create("나의 두번째 메세지", mario.getId(), notice.getId());

            // ---------------------------------------------------------
            // [추가된 테스트 로직: 채널 삭제 및 연쇄 삭제 확인]
            // ---------------------------------------------------------
            System.out.println("\n--- 채널 삭제 전 상태 확인 ---");
            System.out.println("woody의 참여 채널 수: " + woody.getChannels().size());
            System.out.println("mario의 참여 채널 수: " + mario.getChannels().size());
            System.out.println("공지 채널의 메시지 수: " + messageService.findAllByChannelId(notice.getId()).size());

            System.out.println("\n>> [" + notice.getName() + "] 채널을 삭제합니다...");
            interactionService.deleteChannel(notice.getId());

            System.out.println("\n--- 채널 삭제 후 연쇄 삭제 검증 ---");

            // 1. 유저의 참여 목록에서 채널이 제거되었는지 확인
            System.out.println("woody의 남은 참여 채널 수: " + woody.getChannels().size() + " (0이면 성공)");
            System.out.println("mario의 남은 참여 채널 수: " + mario.getChannels().size() + " (0이면 성공)");

            // 2. 해당 채널의 메시지들이 삭제되었는지 확인
            // 삭제된 채널 ID로 조회 시 결과가 0이거나 예외가 발생하는지 확인합니다.
            try {
                int msgCount = messageService.findAllByChannelId(notice.getId()).size();
                System.out.println("삭제된 채널의 남은 메시지 수: " + msgCount);
            } catch (Exception e) {
                System.out.println("조회 불가: 해당 채널이 존재하지 않아 메시지를 찾을 수 없습니다. (성공)");
            }

            // 3. 전체 채널 목록에서 삭제되었는지 확인
            boolean isExist = channelService.findAll().stream().anyMatch(c -> c.getId().equals(notice.getId()));
            System.out.println("채널 저장소에 존재 여부: " + (isExist ? "남아있음 (실패)" : "삭제됨 (성공)"));
            // ---------------------------------------------------------

        } catch (Exception e) {
            System.err.println("테스트 중 오류 발생!");
            e.printStackTrace();
        }

        System.out.println("\n=== 프로그램 종료 (데이터는 .dat 파일에 저장되었습니다) ===");
    }
}