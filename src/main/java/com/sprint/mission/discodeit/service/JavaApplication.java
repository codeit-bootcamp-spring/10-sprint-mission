package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.jcf.*;
import com.sun.jdi.VMMismatchException;

import java.util.List;
import java.util.UUID;

public class JavaApplication {

    private static JCFUserService userService = new JCFUserService();
    private static JCFChannelService channelService = new JCFChannelService();
    private static JCFMessageService messageService = new JCFMessageService(channelService);
    private static JCFInteractionService interactionService = new JCFInteractionService(userService, channelService, messageService);

    public static void main(String[] args) {
        try {
            System.out.println("==============================================");
            System.out.println("🔍 디스코드 프로젝트 상태 변화 검증 테스트");
            System.out.println("==============================================\n");

            // 1 & 2. 유저 및 채널 생성
            User userA = testUserCreation("홍길동", "hong@test.com", "hong.png");
            Channel generalChannel = testChannelCreation("일반채널", "자유로운 대화 공간");

            // 4. 유저 채널 가입 (참여 명단 변화 확인)
            System.out.println("[테스트] 유저 채널 가입");
            System.out.println(" - 가입 전 채널 인원: " + generalChannel.getUsers().size() + "명");
            testJoinChannel(userA, generalChannel);
            System.out.println(" - 가입 후 채널 인원: " + generalChannel.getUsers().size() + "명 (" + generalChannel.getUsers().get(0).getName() + ")");
            System.out.println();

            // 5. 메시지 전송 (메시지 리스트 변화 확인)
            System.out.println("[테스트] 메시지 전송");
            System.out.println(" - 전송 전 채널 메시지 수: " + generalChannel.getMessages().size());
            Message msg1 = testSendMessage(userA, generalChannel, "안녕하세요!");
            System.out.println(" - 전송 후 채널 메시지 수: " + generalChannel.getMessages().size() + " (내용: " + msg1.getContent() + ")");
            System.out.println();

            // 3. 업데이트 동작 (데이터 변경 및 시간 확인)
            System.out.println("[테스트] 유저 이름 업데이트");
            System.out.println(" - 수정 전 이름: " + userA.getName() + " (수정시각: " + userA.getUpdatedAt() + ")");
            testUpdateOperations(userA, generalChannel, msg1); // 내부에서 이름 '수정된이름'으로 변경
            System.out.println(" - 수정 후 이름: " + userA.getName() + " (수정시각: " + userA.getUpdatedAt() + ")");
            System.out.println();

            // 6. 유저 채널 탈퇴 (양방향 참조 제거 확인)
            System.out.println("[테스트] 채널 탈퇴");
            System.out.println(" - 탈퇴 전 유저의 참여 채널 수: " + userA.getChannels().size());
            testLeaveChannel(userA, generalChannel);
            System.out.println(" - 탈퇴 후 유저의 참여 채널 수: " + userA.getChannels().size());
            System.out.println();

            // ------------------------------------------------------
            System.out.println("----------------------------------------------");
            System.out.println("⚙️ 연쇄 삭제(Cascade) 심층 검증");
            System.out.println("----------------------------------------------\n");

            // 7. 유저 삭제 시 채널 반영
            User userC = testUserCreation("삭제될유저", "delete@test.com", "del.png");
            testJoinChannel(userC, generalChannel);
            System.out.println("[테스트] 유저 서비스에서 삭제");
            System.out.println(" - 삭제 전 채널 인원: " + generalChannel.getUsers().size() + "명");
            testDeleteUserCascade(userC, generalChannel);
            System.out.println(" - 삭제 후 채널 인원: " + generalChannel.getUsers().size() + "명 (유저 존재 여부: " + userService.findById(userC.getId()) + ")");
            System.out.println();

            // 8. 채널 삭제 시 메시지 및 유저 참여 리스트 정리
            Channel tempChannel = testChannelCreation("임시채널", "삭제용");
            testJoinChannel(userA, tempChannel);
            Message tempMsg = testSendMessage(userA, tempChannel, "사라질 메시지");
            UUID tempMsgId = tempMsg.getId();

            System.out.println("[테스트] 채널 삭제 시 연쇄 반응");
            System.out.println(" - [Before] 유저 참여 채널 수: " + userA.getChannels().size() + ", 메시지 저장소 존재: " + (messageService.findById(tempMsgId) != null));
            testDeleteChannelCascade(userA, tempChannel, tempMsgId);
            System.out.println(" - [After] 유저 참여 채널 수: " + userA.getChannels().size() + ", 메시지 저장소 존재: " + (messageService.findById(tempMsgId) != null));
            System.out.println();

            System.out.println("==============================================");
            System.out.println("✅ 모든 데이터 변화 대조 검증 완료");
            System.out.println("==============================================");

        } catch (Exception e) {
            System.err.println("\n❌ 테스트 중 검증 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 1. 유저 생성 및 정상 생성 확인
    public static User testUserCreation(String name, String email, String imgUrl) {
        User user = userService.create(name, email, imgUrl);
        if (user != null && user.getId() != null && user.getName().equals(name)) {
            System.out.println("[성공] 유저 생성: " + user.getName());
            return user;
        }
        throw new RuntimeException("유저 생성 검증 실패");
    }

    // 2. 채널 생성 및 정상 생성 확인
    public static Channel testChannelCreation(String name, String description) {
        Channel channel = channelService.create(name, description);
        if (channel != null && channel.getId() != null && channel.getName().equals(name)) {
            System.out.println("[성공] 채널 생성: " + channel.getName());
            return channel;
        }
        throw new RuntimeException("채널 생성 검증 실패");
    }

    // 3. 업데이트 함수 (Optional 동작 및 타임스탬프 확인)
    public static void testUpdateOperations(User u, Channel c, Message m) {
        long userTime = u.getUpdatedAt();
        // 일부 필드만 null로 보내서 기존 데이터 유지 확인 (Optional 동작)
        userService.update(u.getId(), "수정된이름", null, null);
        if (!u.getName().equals("수정된이름") || u.getUpdatedAt() <= userTime)
            throw new RuntimeException("유저 업데이트 검증 실패");

        channelService.update(c.getId(), null, "수정된설명");
        if (!c.getDescription().equals("수정된설명"))
            throw new RuntimeException("채널 업데이트 검증 실패");

        messageService.update(m.getId(), "수정된메시지");
        if (!m.getContent().equals("수정된메시지"))
            throw new RuntimeException("메시지 업데이트 검증 실패");

        System.out.println("[성공] 유저/채널/메시지 업데이트 및 Optional 동작 확인");
    }

    // 4. 유저 채널 가입 및 양방향 참조 확인
    public static void testJoinChannel(User user, Channel channel) {
        interactionService.join(user.getId(), channel.getId());
        boolean userHasChannel = user.getChannels().contains(channel);
        boolean channelHasUser = channel.getUsers().contains(user);

        if (userHasChannel && channelHasUser) {
            System.out.println("[성공] 채널 가입 및 양방향 참조 확인");
        } else {
            throw new RuntimeException("채널 가입 검증 실패");
        }
    }

    // 5. 메시지 전송 및 권한(예외) 확인
    public static Message testSendMessage(User user, Channel channel, String content) {
        try {
            Message msg = messageService.create(content, user, channel);
            if (channel.getMessages().contains(msg)) {
                System.out.println("[성공] 메시지 전송 및 채널 반영 확인");
                return msg;
            }
        } catch (IllegalArgumentException e) {
            System.out.println("[확인] 미가입 유저 차단 로직 작동: " + e.getMessage());
            return null;
        }
        throw new RuntimeException("메시지 전송 검증 실패");
    }

    // 6. 유저 채널 탈퇴 확인
    public static void testLeaveChannel(User user, Channel channel) {
        interactionService.leave(user.getId(), channel.getId());
        if (!user.getChannels().contains(channel) && !channel.getUsers().contains(user)) {
            System.out.println("[성공] 채널 탈퇴 및 참조 제거 확인");
        } else {
            throw new RuntimeException("채널 탈퇴 검증 실패");
        }
    }

    // 7. 유저 삭제 후 채널 참여 명단 확인
    public static void testDeleteUserCascade(User user, Channel channel) {
        interactionService.deleteUser(user.getId());
        if (userService.findById(user.getId()) == null && !channel.getUsers().contains(user)) {
            System.out.println("[성공] 유저 삭제 및 채널 참여 명단 정리 확인");
        } else {
            throw new RuntimeException("유저 연쇄 삭제 검증 실패");
        }
    }

    // 8. 채널 삭제 후 유저의 채널 리스트 및 전체 메시지 정리 확인
    public static void testDeleteChannelCascade(User user, Channel channel, UUID messageId) {
        interactionService.deleteChannel(channel.getId());
        boolean channelRemovedFromUser = !user.getChannels().contains(channel);
        boolean messageRemovedFromService = (messageService.findById(messageId) == null);
        boolean channelRemovedFromService = (channelService.findById(channel.getId()) == null);

        if (channelRemovedFromUser && messageRemovedFromService && channelRemovedFromService) {
            System.out.println("[성공] 채널 삭제 및 유저 리스트/메시지 저장소 정리 확인");
        } else {
            throw new RuntimeException("채널 연쇄 삭제 검증 실패");
        }
    }

    // 9. 메시지 삭제 후 채널 반영 확인
    public static void testDeleteMessage(UUID messageId, Channel channel) {
        messageService.delete(messageId);
        boolean messageExistsInChannel = channel.getMessages().stream()
                .anyMatch(m -> m.getId().equals(messageId));

        if (!messageExistsInChannel && messageService.findById(messageId) == null) {
            System.out.println("[성공] 메시지 삭제 및 채널 메시지 목록 반영 확인");
        } else {
            throw new RuntimeException("메시지 삭제 검증 실패");
        }
    }

    // 10. 채널 메시지 전체 출력 및 반환
    public static List<Message> testPrintAndGetMessages(UUID channelId) {
        List<Message> messages = channelService.getMessageList(channelId);
        System.out.println("--- [채널 메시지 목록] ---");
        messages.forEach(m -> System.out.println("[" + m.getUserId() + "]: " + m.getContent()));
        System.out.println("-----------------------");
        return messages;
    }

    // [추가] 11. 데이터 정합성 확인 (존재하지 않는 ID 조회 시 대응)
    public static void testInvalidIdAccess() {
        UUID fakeId = UUID.randomUUID();
        if (userService.findById(fakeId) == null && channelService.findById(fakeId) == null) {
            System.out.println("[성공] 존재하지 않는 ID 조회 시 null 반환 확인");
        } else {
            throw new RuntimeException("잘못된 ID 접근 제어 실패");
        }
    }
}