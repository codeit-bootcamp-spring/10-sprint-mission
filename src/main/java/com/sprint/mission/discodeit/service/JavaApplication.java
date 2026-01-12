package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;

import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {
        // 1. 서비스 초기화 (의존성 주입)
        ChannelService channelService = new JCFChannelService();
        MessageService messageService = new JCFMessageService(channelService);

        // 2. 가상의 유저 ID 생성 (실제 유저 서비스가 있다면 거기서 가져오겠죠?)
        UUID jasonId = UUID.randomUUID();

        // 3. 채널 생성
        Channel generalChannel = new Channel("일반 채널", "이 채팅은 테스트용입니다.");
        channelService.create(generalChannel);
        System.out.println("=== 채널 생성 완료: " + generalChannel.getName() + " ===");

        // 4. 메시지 작성
        Message msg1 = new Message("안녕하세요! 반갑습니다.", jasonId, generalChannel.getId());
        Message msg2 = new Message("자바 컬렉션으로 만드는 디스코드 프로젝트입니다.", jasonId, generalChannel.getId());

        messageService.create(msg1);
        messageService.create(msg2);
        System.out.println("=== 메시지 2건 작성 완료 ===");

        // 5. 채널 내 메시지 조회
        System.out.println("\n[채널 내 전체 메시지 목록]");
        messageService.findAllByChannelId(generalChannel.getId()).forEach(m -> {
            System.out.println("- [" + m.getCreatedAt() + "] " + m.getContent());
        });

        // 6. 메시지 수정
        System.out.println("\n=== 첫 번째 메시지 수정 시도 ===");
        messageService.update(msg1.getId(), "내용을 수정했습니다! (Edited)");

        Message updatedMsg = messageService.findById(msg1.getId());
        System.out.println("수정된 내용: " + updatedMsg.getContent());
        System.out.println("수정 시각(UpdatedAt): " + updatedMsg.getUpdatedAt());

        // 7. 메시지 삭제
        System.out.println("\n=== 두 번째 메시지 삭제 시도 ===");
        messageService.delete(msg2.getId());

        // 8. 최종 결과 확인
        System.out.println("\n[삭제 후 채널 내 남은 메시지]");
        messageService.findAllByChannelId(generalChannel.getId()).forEach(m -> {
            System.out.println("- " + m.getContent());
        });

        System.out.println("\n[전역 저장소(Map) 확인]");
        Message deletedCheck = messageService.findById(msg2.getId());
        System.out.println("삭제된 메시지 ID로 조회 시 결과: " + (deletedCheck == null ? "없음 (정상)" : "남아있음 (오류)"));
    }
}