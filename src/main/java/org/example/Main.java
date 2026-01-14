package org.example;

import org.example.entity.*;
import org.example.service.jcf.*;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        JCFUserService userService = new JCFUserService();
        JCFChannelService channelService = new JCFChannelService(userService);
        JCFMessageService messageService = new JCFMessageService(userService, channelService);

        // ==================== USER CRUD ====================
        System.out.println("[ USER CRUD ]");
        System.out.println("----------------------------------------");

        // Create
        System.out.println(">> CREATE");
        User user1 = userService.create("alice", "alice@email.com", "pass123", "앨리스");
        User user2 = userService.create("bob", "bob@email.com", "pass456", "밥");
        User user3 = userService.create("charlie", "charlie@email.com", "pass789", "찰리");
        System.out.println("   생성: " + user1.getUsername() + ", " + user2.getUsername() + ", " + user3.getUsername());

        // Read (단건)
        System.out.println("\n>> READ (단건)");
        User foundUser = userService.findById(user1.getId());
        System.out.println("   조회: " + foundUser.getUsername() + " / " + foundUser.getEmail());

        // Read (다건)
        System.out.println("\n>> READ (다건)");
        List<User> allUsers = userService.findAll();
        System.out.println("   ACTIVE 유저 수: " + allUsers.size());
        allUsers.forEach(u -> System.out.println("   - " + u.getUsername()));

        // Update
        System.out.println("\n>> UPDATE");
        userService.update(user1.getId(), null, null, "앨리스수정", null, null);
        User updatedUser = userService.findById(user1.getId());
        System.out.println("   수정 후 닉네임: " + updatedUser.getNickname());

        // Soft Delete
        System.out.println("\n>> SOFT DELETE (user2 탈퇴)");
        userService.softDelete(user2.getId());
        System.out.println("   user2 상태: " + userService.findById(user2.getId()).getStatus());
        System.out.println("   findAll() 결과: " + userService.findAll().size() + "명");

        // Hard Delete
        System.out.println("\n>> HARD DELETE (user3 완전 삭제)");
        userService.hardDelete(user3.getId());
        System.out.println("   findAll() 결과: " + userService.findAll().size() + "명");
        System.out.print("   user3 조회: ");
        try {
            userService.findById(user3.getId());
        } catch (Exception e) {
            System.out.println("실패 - " + e.getMessage());
        }

        System.out.println();

        // ==================== CHANNEL CRUD ====================
        System.out.println("[ CHANNEL CRUD ]");
        System.out.println("----------------------------------------");

        // Create
        System.out.println(">> CREATE");
        Channel channel1 = channelService.create("일반채널1", "일반 대화방", ChannelType.PUBLIC, user1.getId());
        Channel channel2 = channelService.create("비밀채널1", "비밀 대화방", ChannelType.PRIVATE, user1.getId());
        Channel channel3 = channelService.create("삭제용채널", "삭제 테스트용", ChannelType.PUBLIC, user1.getId());
        System.out.println("   생성: " + channel1.getName() + ", " + channel2.getName() + ", " + channel3.getName());

        // Read (단건)
        System.out.println("\n>> READ (단건)");
        Channel foundChannel = channelService.findById(channel1.getId());
        System.out.println("   조회: " + foundChannel.getName() + " / " + foundChannel.getType());

        // Read (다건)
        System.out.println("\n>> READ (다건)");
        List<Channel> allChannels = channelService.findAll();
        System.out.println("   전체 채널 수: " + allChannels.size());
        allChannels.forEach(c -> System.out.println("   - " + c.getName()));

        // Update
        System.out.println("\n>> UPDATE");
        channelService.update(channel1.getId(), "수정된채널", null, null);
        Channel updatedChannel = channelService.findById(channel1.getId());
        System.out.println("   수정 후 이름: " + updatedChannel.getName());

        // Delete (channel3 삭제 - channel2는 메시지 테스트에 사용)
        System.out.println("\n>> DELETE (channel3)");
        channelService.delete(channel3.getId());
        System.out.println("   findAll() 결과: " + channelService.findAll().size() + "개");
        System.out.print("   channel3 조회: ");
        try {
            channelService.findById(channel3.getId());
        } catch (Exception e) {
            System.out.println("실패 - " + e.getMessage());
        }

        System.out.println();

        // ==================== MESSAGE CRUD ====================
        System.out.println("[ MESSAGE CRUD ]");
        System.out.println("----------------------------------------");

        // Create (각 채널에 user1, user2가 하나씩)
        System.out.println(">> CREATE");
        Message msg1 = messageService.create("채널1 - alice의 메시지", user1.getId(), channel1.getId());
        Message msg2 = messageService.create("채널1 - bob의 메시지", user2.getId(), channel1.getId());
        Message msg3 = messageService.create("채널2 - alice의 메시지", user1.getId(), channel2.getId());
        Message msg4 = messageService.create("채널2 - bob의 메시지", user2.getId(), channel2.getId());
        System.out.println("   생성: 4개 메시지 (채널별 user1, user2 각 1개)");

        // Read (단건)
        System.out.println("\n>> READ (단건)");
        Message foundMsg = messageService.findById(msg1.getId());
        System.out.println("   조회: \"" + foundMsg.getContent() + "\"");

        // Read (다건)
        System.out.println("\n>> READ (서비스 - findByChannel)");
        System.out.println("   channel1: " + messageService.findByChannel(channel1.getId()).size() + "개");
        System.out.println("   channel2: " + messageService.findByChannel(channel2.getId()).size() + "개");

        // Update
        System.out.println("\n>> UPDATE");
        messageService.update(msg1.getId(), "안녕하세요! (수정됨)");
        Message updatedMsg = messageService.findById(msg1.getId());
        System.out.println("   수정 후: \"" + updatedMsg.getContent() + "\"");
        System.out.println("   수정 여부: " + updatedMsg.isEditedAt());

        // Soft Delete
        System.out.println("\n>> SOFT DELETE (msg2)");
        messageService.softDelete(msg2.getId());
        System.out.println("   findByChannel(channel1) 결과: " + messageService.findByChannel(channel1.getId()).size() + "개");

        // Hard Delete
        System.out.println("\n>> HARD DELETE (msg4)");
        messageService.hardDelete(msg4.getId());
        System.out.println("   findByChannel(channel2) 결과: " + messageService.findByChannel(channel2.getId()).size() + "개");
        System.out.print("   msg4 조회: ");
        try {
            messageService.findById(msg4.getId());
        } catch (Exception e) {
            System.out.println("실패 - " + e.getMessage());
        }

        // ==================== 관계 리스트 확인 ====================
        System.out.println();
        System.out.println("[ 관계 리스트 확인 ]");
        System.out.println("========================================");

        // 1. 채널별 메시지 리스트
        System.out.println("\n>> 채널별 메시지 리스트");
        for (Channel c : channelService.findAll()) {
            System.out.println("\n   [" + c.getName() + "]");
            if (c.getMessages().isEmpty()) {
                System.out.println("   - (메시지 없음)");
            } else {
                for (Message m : c.getMessages()) {
                    String deleted = m.isDeletedAt() ? " [삭제됨]" : "";
                    String edited = m.isEditedAt() ? " [수정됨]" : "";
                    System.out.println("   - [" + m.getSender().getUsername() + "] " + m.getContent() + deleted + edited);
                }
            }
        }

        // 2. 유저별 채널 리스트
        System.out.println("\n>> 유저별 채널 리스트");
        for (User u : List.of(user1, user2)) {
            System.out.println("\n   [" + u.getUsername() + "] - " + u.getStatus());
            if (u.getChannels().isEmpty()) {
                System.out.println("   - (참여 채널 없음)");
            } else {
                u.getChannels().forEach(c ->
                        System.out.println("   - " + c.getName() + " (" + c.getType() + ")")
                );
            }
        }

        // 3. 채널별 멤버 리스트
        System.out.println("\n>> 채널별 멤버 리스트");
        for (Channel c : channelService.findAll()) {
            System.out.println("\n   [" + c.getName() + "] - 오너: " + c.getOwner().getUsername());
            if (c.getMembers().isEmpty()) {
                System.out.println("   - (멤버 없음)");
            } else {
                for (User u : c.getMembers()) {
                    String status = (u.getStatus() != Status.ACTIVE) ? " [" + u.getStatus() + "]" : "";
                    System.out.println("   - " + u.getUsername() + status);
                }
            }
        }
    }
}
