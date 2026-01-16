package org.example;

import org.example.entity.*;
import org.example.service.jcf.*;

import java.util.UUID;

public class JavaApplication {

    public static void main(String[] args) {

        // 서비스 초기화
        JCFUserService userService = new JCFUserService();
        JCFChannelService channelService = new JCFChannelService(userService);
        JCFMessageService messageService = new JCFMessageService(userService, channelService);

        channelService.setMessageService(messageService);
        userService.setMessageService(messageService);

        // ==================== 1. 등록 테스트 ====================
        System.out.println("\n[ 1. 등록 테스트 ]");
        System.out.println("----------------------------------------");

        // 유저 등록
        User a = userService.create("a", "a@test.com", "pw", "유저A");
        User b = userService.create("b", "b@test.com", "pw", "유저B");
        UUID aId = a.getId();
        UUID bId = b.getId();
        System.out.println(">> 유저 등록 완료: " + userService.findAll().stream()
                .map(User::getUsername).toList());

        // 채널 등록
        Channel channel = channelService.create("테스트채널", "설명", ChannelType.PUBLIC, aId);
        UUID channelId = channel.getId();
        channelService.addMember(channelId, bId);
        System.out.println(">> 채널 등록 완료: " + channelService.findAll().stream()
                .map(Channel::getName).toList());

        // 메시지 등록
        Message msg1 = messageService.create("첫번째 메시지", aId, channelId);
        Message msg2 = messageService.create("두번째 메시지", bId, channelId);
        UUID msg1Id = msg1.getId();
        System.out.println(">> 메시지 등록 완료: " + messageService.findAll().stream()
                .map(Message::getContent).toList());

        // ==================== 2. 조회 테스트 (단건, 다건) ====================
        System.out.println("\n[ 2. 조회 테스트 (단건, 다건) ]");
        System.out.println("----------------------------------------");

        // 단건 조회
        System.out.println(">> 유저 단건 조회: " + userService.findById(aId).getUsername());
        System.out.println(">> 채널 단건 조회: " + channelService.findById(channelId).getName());
        System.out.println(">> 메시지 단건 조회: " + messageService.findById(msg1Id).getContent());

        // 다건 조회
        System.out.println(">> 유저 다건 조회: " + userService.findAll().size() + "명");
        System.out.println(">> 채널 다건 조회: " + channelService.findAll().size() + "개");
        System.out.println(">> 메시지 다건 조회: " + messageService.findAll().size() + "개");
        System.out.println(">> 채널별 메시지 조회: " + messageService.findByChannel(channelId).stream()
                .map(Message::getContent).toList());
        System.out.println(">> 유저별 메시지 조회: " + messageService.findBySender(aId).stream()
                .map(Message::getContent).toList());

        // ==================== 3. 수정 테스트 ====================
        System.out.println("\n[ 3. 수정 테스트 ]");
        System.out.println("----------------------------------------");

        // 유저 수정
        System.out.println(">> 수정 전 유저 닉네임: " + userService.findById(aId).getNickname());
        userService.update(aId, null, null, "수정된닉네임", null, null);
        System.out.println(">> 수정 후 유저 닉네임: " + userService.findById(aId).getNickname());

        // 채널 수정
        System.out.println(">> 수정 전 채널 설명: " + channelService.findById(channelId).getDescription());
        channelService.update(channelId, null, "수정된 설명", null);
        System.out.println(">> 수정 후 채널 설명: " + channelService.findById(channelId).getDescription());

        // 메시지 수정
        System.out.println(">> 수정 전 메시지: " + messageService.findById(msg1Id).getContent());
        messageService.update(msg1Id, "수정된 메시지");
        System.out.println(">> 수정 후 메시지: " + messageService.findById(msg1Id).getContent());

        // ==================== 4. 수정된 데이터 조회 ====================
        System.out.println("\n[ 4. 수정된 데이터 조회 ]");
        System.out.println("----------------------------------------");
        System.out.println(">> 유저 닉네임: " + userService.findById(aId).getNickname());
        System.out.println(">> 채널 설명: " + channelService.findById(channelId).getDescription());
        System.out.println(">> 메시지 내용: " + messageService.findById(msg1Id).getContent());
        System.out.println(">> 메시지 수정 여부: " + messageService.findById(msg1Id).isEditedAt());

        // ==================== 5. 삭제 테스트 ====================
        System.out.println("\n[ 5. 삭제 테스트 ]");
        System.out.println("----------------------------------------");
        System.out.println(">> 삭제 전 메시지 수: " + messageService.findAll().size());
        messageService.hardDelete(msg2.getId());
        System.out.println(">> 삭제 후 메시지 수: " + messageService.findAll().size());

        // ==================== 6. 삭제 후 조회 확인 ====================
        System.out.println("\n[ 6. 삭제 후 조회 확인 ]");
        System.out.println("----------------------------------------");
        System.out.println(">> 남은 메시지: " + messageService.findAll().stream()
                .map(Message::getContent).toList());
        try {
            messageService.findById(msg2.getId());
            System.out.println(">> [실패] 삭제된 메시지가 조회됨");
        } catch (Exception e) {
            System.out.println(">> [성공] 삭제된 메시지 조회 불가: " + e.getMessage());
        }

        // ==================== 7. 서비스 간 의존성 검증 ====================
        System.out.println("\n[ 7. 서비스 간 의존성 검증 ]");
        System.out.println("----------------------------------------");

        // 존재하지 않는 유저로 메시지 생성 시도
        try {
            messageService.create("테스트", UUID.randomUUID(), channelId);
            System.out.println(">> [실패] 예외가 발생하지 않음");
        } catch (Exception e) {
            System.out.println(">> [성공] 존재하지 않는 유저: " + e.getMessage());
        }

        // 존재하지 않는 채널로 메시지 생성 시도
        try {
            messageService.create("테스트", aId, UUID.randomUUID());
            System.out.println(">> [실패] 예외가 발생하지 않음");
        } catch (Exception e) {
            System.out.println(">> [성공] 존재하지 않는 채널: " + e.getMessage());
        }

        // 빈 content로 메시지 생성 시도
        try {
            messageService.create("", aId, channelId);
            System.out.println(">> [실패] 예외가 발생하지 않음");
        } catch (Exception e) {
            System.out.println(">> [성공] 빈 메시지: " + e.getMessage());
        }

        // ==================== 테스트 완료 ====================
        System.out.println("\n========================================");
        System.out.println("[ 모든 테스트 완료 ]");
        System.out.println("========================================");


    }
}
