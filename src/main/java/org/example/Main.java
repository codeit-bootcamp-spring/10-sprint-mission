package org.example;

import org.example.entity.*;
import org.example.service.jcf.*;

import java.util.UUID;

public class Main {

    public static void main(String[] args) {

// 서비스 초기화
        JCFUserService userService = new JCFUserService();
        JCFChannelService channelService = new JCFChannelService(userService);
        JCFMessageService messageService = new JCFMessageService(userService, channelService);

        channelService.setMessageService(messageService);
        userService.setMessageService(messageService);

// ==================== 1. 유저 3명 생성 ====================
        System.out.println("\n[ 1. 유저 3명 생성: a, b, c ]");
        System.out.println("----------------------------------------");

        User a = userService.create("a", "a@test.com", "pw", "유저A");
        User b = userService.create("b", "b@test.com", "pw", "유저B");
        User c = userService.create("c", "c@test.com", "pw", "유저C");

        UUID aId = a.getId();
        UUID bId = b.getId();
        UUID cId = c.getId();

        System.out.println(">> 전체 유저: " + userService.findAll().stream()
                .map(User::getUsername)
                .toList());

// ==================== 2. 채널 2개 생성 ====================
        System.out.println("\n[ 2. 채널 2개 생성: a의 채널, b의 채널 ]");
        System.out.println("----------------------------------------");

        Channel channelA = channelService.create("A의채널", "a가 만든 채널", ChannelType.PUBLIC, aId);
        Channel channelB = channelService.create("B의채널", "b가 만든 채널", ChannelType.PUBLIC, bId);

        UUID channelAId = channelA.getId();
        UUID channelBId = channelB.getId();

        System.out.println(">> 전체 채널: " + channelService.findAll().stream()
                .map(Channel::getName)
                .toList());
        System.out.println(">> A의채널 오너: " + channelA.getOwner().getUsername());
        System.out.println(">> B의채널 오너: " + channelB.getOwner().getUsername());

// ==================== 3. 각 채널에 유저 3명씩 추가 ====================
        System.out.println("\n[ 3. 각 채널에 유저 3명씩 추가 ]");
        System.out.println("----------------------------------------");

        channelService.addMember(channelAId, bId);
        channelService.addMember(channelAId, cId);
        channelService.addMember(channelBId, aId);
        channelService.addMember(channelBId, cId);

        System.out.println(">> A의채널 멤버: " + channelService.findMembersByChannel(channelAId).stream()
                .map(User::getUsername)
                .toList());
        System.out.println(">> B의채널 멤버: " + channelService.findMembersByChannel(channelBId).stream()
                .map(User::getUsername)
                .toList());
        System.out.println(">> a의 채널목록: " + userService.findChannelByUser(aId).stream()
                .map(Channel::getName)
                .toList());
        System.out.println(">> b의 채널목록: " + userService.findChannelByUser(bId).stream()
                .map(Channel::getName)
                .toList());
        System.out.println(">> c의 채널목록: " + userService.findChannelByUser(cId).stream()
                .map(Channel::getName)
                .toList());

// ==================== 4. 각자 메시지 2개씩 생성 ====================
        System.out.println("\n[ 4. 각자 메시지 2개씩 생성 (총 12개) ]");
        System.out.println("----------------------------------------");

        Message msgA1 = messageService.create("a의 A채널 메시지1", aId, channelAId);
        Message msgA2 = messageService.create("a의 A채널 메시지2", aId, channelAId);
        Message msgB1 = messageService.create("b의 A채널 메시지1", bId, channelAId);
        Message msgB2 = messageService.create("b의 A채널 메시지2", bId, channelAId);
        Message msgC1 = messageService.create("c의 A채널 메시지1", cId, channelAId);
        Message msgC2 = messageService.create("c의 A채널 메시지2", cId, channelAId);

        Message msgA3 = messageService.create("a의 B채널 메시지1", aId, channelBId);
        Message msgA4 = messageService.create("a의 B채널 메시지2", aId, channelBId);
        Message msgB3 = messageService.create("b의 B채널 메시지1", bId, channelBId);
        Message msgB4 = messageService.create("b의 B채널 메시지2", bId, channelBId);
        Message msgC3 = messageService.create("c의 B채널 메시지1", cId, channelBId);
        Message msgC4 = messageService.create("c의 B채널 메시지2", cId, channelBId);

        UUID msgB1Id = msgB1.getId();

        System.out.println(">> A의채널 메시지: " + messageService.findByChannel(channelAId).stream()
                .map(Message::getContent)
                .toList());
        System.out.println(">> B의채널 메시지: " + messageService.findByChannel(channelBId).stream()
                .map(Message::getContent)
                .toList());
        System.out.println(">> 전체 메시지 수: " + messageService.findAll().size());
        System.out.println(">> a의 메시지: " + messageService.findBySender(aId).stream()
                .map(Message::getContent)
                .toList());
        System.out.println(">> b의 메시지: " + messageService.findBySender(bId).stream()
                .map(Message::getContent)
                .toList());
        System.out.println(">> c의 메시지: " + messageService.findBySender(cId).stream()
                .map(Message::getContent)
                .toList());

// ==================== 5. B의채널에서 유저 a 내보내기 ====================
        System.out.println("\n[ 5. B의채널에서 유저 a 내보내기 ]");
        System.out.println("----------------------------------------");

        channelService.removeMember(channelBId, aId);

        System.out.println(">> B의채널 멤버: " + channelService.findMembersByChannel(channelBId).stream()
                .map(User::getUsername)
                .toList());
        System.out.println(">> a의 채널목록: " + userService.findChannelByUser(aId).stream()
                .map(Channel::getName)
                .toList());
        System.out.println(">> a의 메시지: " + messageService.findBySender(aId).stream()
                .map(Message::getContent)
                .toList());

// ==================== 6. B의채널 오너를 c로 변경 ====================
        System.out.println("\n[ 6. B의채널 오너를 c로 변경 ]");
        System.out.println("----------------------------------------");

        System.out.println(">> 변경 전 B의채널 오너: " + channelB.getOwner().getUsername());
        channelService.transferOwnership(channelBId, cId);
        System.out.println(">> 변경 후 B의채널 오너: " + channelB.getOwner().getUsername());

// ==================== 7. 유저 b의 메시지 1개 삭제 ====================
        System.out.println("\n[ 7. 유저 b의 A채널 메시지1 삭제 ]");
        System.out.println("----------------------------------------");

        System.out.println(">> 삭제 전 b의 메시지: " + messageService.findBySender(bId).stream()
                .map(Message::getContent)
                .toList());
        System.out.println(">> 삭제 전 A의채널 메시지: " + messageService.findByChannel(channelAId).stream()
                .map(Message::getContent)
                .toList());

        messageService.hardDelete(msgB1Id);

        System.out.println(">> 삭제 후 b의 메시지: " + messageService.findBySender(bId).stream()
                .map(Message::getContent)
                .toList());
        System.out.println(">> 삭제 후 A의채널 메시지: " + messageService.findByChannel(channelAId).stream()
                .map(Message::getContent)
                .toList());
        System.out.println(">> 전체 메시지 수: " + messageService.findAll().size());

// ==================== 8. 유저 b 완전 삭제 ====================
        System.out.println("\n[ 8. 유저 b 완전 삭제 ]");
        System.out.println("----------------------------------------");

        System.out.println(">> 삭제 전 상태");
        System.out.println("   전체 유저: " + userService.findAll().stream()
                .map(User::getUsername)
                .toList());
        System.out.println("   A의채널 멤버: " + channelService.findMembersByChannel(channelAId).stream()
                .map(User::getUsername)
                .toList());
        System.out.println("   B의채널 멤버: " + channelService.findMembersByChannel(channelBId).stream()
                .map(User::getUsername)
                .toList());
        System.out.println("   b의 메시지: " + messageService.findBySender(bId).stream()
                .map(Message::getContent)
                .toList());
        System.out.println("   전체 메시지 수: " + messageService.findAll().size());

        userService.hardDelete(bId);

        System.out.println("\n>> 삭제 후 상태");
        System.out.println("   전체 유저: " + userService.findAll().stream()
                .map(User::getUsername)
                .toList());
        System.out.println("   A의채널 멤버: " + channelService.findMembersByChannel(channelAId).stream()
                .map(User::getUsername)
                .toList());
        System.out.println("   B의채널 멤버: " + channelService.findMembersByChannel(channelBId).stream()
                .map(User::getUsername)
                .toList());
        System.out.println("   A의채널 메시지: " + messageService.findByChannel(channelAId).stream()
                .map(Message::getContent)
                .toList());
        System.out.println("   B의채널 메시지: " + messageService.findByChannel(channelBId).stream()
                .map(Message::getContent)
                .toList());
        System.out.println("   전체 메시지 수: " + messageService.findAll().size());

// ==================== 9. B의채널 완전 삭제 ====================
        System.out.println("\n[ 9. B의채널 완전 삭제 ]");
        System.out.println("----------------------------------------");

        System.out.println(">> 삭제 전 상태");
        System.out.println("   전체 채널: " + channelService.findAll().stream()
                .map(Channel::getName)
                .toList());
        System.out.println("   a의 채널목록: " + userService.findChannelByUser(aId).stream()
                .map(Channel::getName)
                .toList());
        System.out.println("   c의 채널목록: " + userService.findChannelByUser(cId).stream()
                .map(Channel::getName)
                .toList());
        System.out.println("   전체 메시지 수: " + messageService.findAll().size());

        channelService.delete(channelBId);

        System.out.println("\n>> 삭제 후 상태");
        System.out.println("   전체 채널: " + channelService.findAll().stream()
                .map(Channel::getName)
                .toList());
        System.out.println("   a의 채널목록: " + userService.findChannelByUser(aId).stream()
                .map(Channel::getName)
                .toList());
        System.out.println("   c의 채널목록: " + userService.findChannelByUser(cId).stream()
                .map(Channel::getName)
                .toList());
        System.out.println("   a의 메시지: " + messageService.findBySender(aId).stream()
                .map(Message::getContent)
                .toList());
        System.out.println("   c의 메시지: " + messageService.findBySender(cId).stream()
                .map(Message::getContent)
                .toList());
        System.out.println("   전체 메시지 수: " + messageService.findAll().size());

// ==================== 최종 상태 ====================
        System.out.println("\n[ 최종 상태 ]");
        System.out.println("========================================");
        System.out.println(">> 전체 유저: " + userService.findAll().stream()
                .map(User::getUsername)
                .toList());
        System.out.println(">> 전체 채널: " + channelService.findAll().stream()
                .map(Channel::getName)
                .toList());
        System.out.println(">> 전체 메시지: " + messageService.findAll().stream()
                .map(Message::getContent)
                .toList());
        System.out.println("\n>> A의채널 멤버: " + channelService.findMembersByChannel(channelAId).stream()
                .map(User::getUsername)
                .toList());
        System.out.println(">> A의채널 메시지: " + messageService.findByChannel(channelAId).stream()
                .map(Message::getContent)
                .toList());

        System.out.println("\n========================================");
        System.out.println("[ 시나리오 테스트 완료 ]");

    }
}
