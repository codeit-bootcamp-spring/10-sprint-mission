package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {
        System.out.println("========== 디스코드잇 서비스 테스트 시작 ==========\n");

        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService(userService);
        MessageService messageService = new JCFMessageService(userService, channelService);

        // ========== User 기능 테스트 ==========
        System.out.println("========== User 기능 테스트 ==========\n");

        // 유저 생성
        System.out.println(">> 유저 생성 테스트");
        User user1 = userService.createUser("임지호", "1234", "jiho@codeit.com");
        User user2 = userService.createUser("홍길동", "5678", "hong@codeit.com");
        User user3 = userService.createUser("김철수", "abcd", "kim@codeit.com");
        UUID user1Id = user1.getId();
        UUID user2Id = user2.getId();
        UUID user3Id = user3.getId();

        System.out.println("\n------------------------------------------\n");

        // 유저 단건 조회
        System.out.println(">> 유저 단건 조회 테스트");
        Optional<User> foundUser1 = userService.findUserById(user1Id);
        foundUser1.ifPresent(user -> System.out.println("조회된 유저: " + user.getUsername()));
        Optional<User> foundUser2 = userService.findUserById(user2Id);
        foundUser2.ifPresent(user -> System.out.println("조회된 유저: " + user.getUsername()));
        Optional<User> foundUser3 = userService.findUserById(user3Id);
        foundUser3.ifPresent(user -> System.out.println("조회된 유저: " + user.getUsername()));

        System.out.println("\n------------------------------------------\n");

        // 유저 전체 조회
        System.out.println(">> 유저 전체 조회 테스트");
        List<User> allUsers = userService.findAllUsers();
        System.out.println("총 유저 수: " + allUsers.size() + "명");
        for (User u : allUsers) {
            System.out.println("- " + u.getUsername() + " (" + u.getEmail() + ")");
        }

        System.out.println("\n------------------------------------------\n");

        // 유저 정보 수정
        System.out.println(">> 유저 정보 수정 테스트");
        User updatedUser = userService.updateUserInfo(user1Id, "아임지호", "newjiho@codeit.com");
        System.out.println("수정된 유저: " + updatedUser);

        System.out.println("\n------------------------------------------\n");

        // 유저 비밀번호 변경
        System.out.println(">> 유저 비밀번호 변경 테스트");
        updatedUser = userService.changePassword(user1Id, "newpass1234");
        System.out.println("비밀번호 변경 완료: " + updatedUser.getPassword());

        System.out.println("\n------------------------------------------\n");

        // 유저 정보 수정 검증 테스트
        System.out.println(">> 유저 정보 수정 검증 테스트");
        System.out.println("둘 다 null인 경우:");
        userService.updateUserInfo(user2Id, null, null);

        System.out.println("\n이름에 공백 포함:");
        userService.updateUserInfo(user2Id, "홍 길동", "test@test.com");

        System.out.println("\n이메일 형식 오류:");
        userService.updateUserInfo(user2Id, "홍길동", "invalidemail");

        System.out.println("\n정상 수정:");
        userService.updateUserInfo(user2Id, "뉴홍길동", "newhong@codeit.com");


        System.out.println("\n========== Channel 기능 테스트 ==========\n");

        // 채널 생성
        System.out.println(">>채널 생성 테스트");
        Channel channel1 = channelService.createChannel("본방");
        Channel channel2 = channelService.createChannel("공지방");
        Channel channel3 = channelService.createChannel("수다방");
        UUID channel1Id = channel1.getId();
        UUID channel2Id = channel2.getId();
        UUID channel3Id = channel3.getId();

        System.out.println("\n------------------------------------------\n");

        // 채널 단건 조회
        System.out.println(">>채널 단건 조회 테스트");
        Optional<Channel> foundChannel = channelService.findChannelById(channel1Id);
        foundChannel.ifPresent(channel ->
                System.out.println("조회된 채널: " + channel.getChannelName()));

        System.out.println("\n------------------------------------------\n");

        // 채널 전체 조회
        System.out.println(">>채널 전체 조회 테스트");
        List<Channel> allChannels = channelService.findAllChannels();
        System.out.println("총 채널 수: " + allChannels.size() + "개");
        for (Channel c : allChannels) {
            System.out.println("- " + c.getChannelName());
        }

        System.out.println("\n------------------------------------------\n");

        // 채널 수정
        System.out.println(">>채널 수정 테스트");
        Channel updatedChannel = channelService.updateChannel(channel1Id, "메인방");
        System.out.println("변경된 채널명: " + updatedChannel.getChannelName());

        System.out.println("\n------------------------------------------\n");

        // 채널 입장
        System.out.println(">>채널 입장 테스트");
        channelService.joinChannel(user1Id, channel1Id);
        channelService.joinChannel(user2Id, channel1Id);
        channelService.joinChannel(user3Id, channel1Id);

        channelService.joinChannel(user1Id, channel2Id);
        channelService.joinChannel(user2Id, channel2Id);

        System.out.println("\n------------------------------------------\n");

        // 중복 입장 테스트
        System.out.println(">>중복 입장 방지 테스트");
        try {
            channelService.joinChannel(user1Id, channel1Id);
        } catch (IllegalArgumentException e) {
            System.out.println("에러 발생: " + e.getMessage());
        }

        System.out.println("\n------------------------------------------\n");

        // 채널 참가자 조회
        System.out.println(">>채널 참가자 조회 테스트");
        List<User> participants = channelService.findParticipants(channel1Id);
        System.out.println("[" + updatedChannel.getChannelName() + "] 참가자 수: " + participants.size() + "명");
        for (User p : participants) {
            System.out.println("- " + p.getUsername() + " (" + p.getEmail() + ")");
        }

        System.out.println("\n------------------------------------------\n");

        // 특정 유저가 속해있는 채널
        Optional<User> testUser = userService.findUserById(user1Id);
        if (testUser.isPresent()) {
            System.out.println(testUser.get().getUsername() + "님이 참여 중인 채널:");
            for (Channel c : testUser.get().getMyChannels()) {
                System.out.println("- " + c.getChannelName());
            }
        }


        System.out.println("\n========== Message 기능 테스트 ==========\n");

        // 메시지 생성
        System.out.println(">> 메시지 생성");
        Message msg1 = messageService.createMessage("안녕하세요! 지호입니다.", channel1Id, user1Id);
        Message msg2 = messageService.createMessage("반갑습니다~ 길동이에요.", channel1Id, user2Id);
        Message msg3 = messageService.createMessage("오늘 점심 뭐 드실래요?", channel1Id, user2Id);
        Message msg4 = messageService.createMessage("저는 김철수입니다.", channel1Id, user3Id);

        Message msg5 = messageService.createMessage("공지사항입니다.", channel2Id, user1Id);
        Message msg6 = messageService.createMessage("확인했습니다.", channel2Id, user2Id);

        UUID msg1Id = msg1.getId();
        UUID msg2Id = msg2.getId();
        UUID msg5Id = msg5.getId();

        System.out.println("\n------------------------------------------\n");

        // 메시지 단건 조회
        System.out.println(">>메시지 단건 조회 테스트");
        Optional<Message> foundMessage = messageService.findMessageById(msg1Id);
        foundMessage.ifPresent(message ->
                System.out.println("조회된 메시지: " + message.getContent()));

        System.out.println("\n------------------------------------------\n");

        // 메시지 전체 조회
        System.out.println(">>메시지 전체 조회 테스트");
        List<Message> allMessages = messageService.findAllMessages();
        System.out.println("전체 메시지 수: " + allMessages.size() + "개");

        System.out.println("\n------------------------------------------\n");

        // 특정 채널의 메시지 조회
        System.out.println(">>특정 채널의 메시지 조회 테스트");
        List<Message> channelMessages = messageService.findMessagesByChannelId(channel1Id);
        System.out.println("[" + updatedChannel.getChannelName() + "] 메시지 수: " + channelMessages.size() + "개");
        for (Message m : channelMessages) {
            System.out.println("- " + m.getUser().getUsername() + ": " + m.getContent());
        }

        System.out.println("\n------------------------------------------\n");

        // 특정 유저의 메시지 조회
        System.out.println(">>특정 유저의 메시지 조회 테스트");
        List<Message> userMessages = messageService.findMessagesByUserId(user2Id);
        Optional<User> msgUser = userService.findUserById(user2Id);
        if (msgUser.isPresent()) {
            System.out.println(msgUser.get().getUsername() + "님의 메시지 수: " + userMessages.size() + "개");
            for (Message m : userMessages) {
                System.out.println("📝 내용: " + m.getContent() + " | 채널: " + m.getChannel().getChannelName());
            }
        }

        System.out.println("\n------------------------------------------\n");

        // 메시지 수정
        System.out.println(">>메시지 수정 테스트");
        Message updatedMessage = messageService.updateMessage(msg1Id, "메시지를 잘못 보내서 수정했어요");
        System.out.println("수정된 메시지: " + updatedMessage.getContent());

        System.out.println("\n------------------------------------------\n");

        // 메시지 삭제
        System.out.println(">>메시지 삭제 테스트");
        messageService.deleteMessage(msg2Id);

        System.out.println("\n삭제 후 채널 메시지 확인:");
        channelMessages = messageService.findMessagesByChannelId(channel1Id);
        System.out.println("현재 메시지 수: " + channelMessages.size() + "개");

        System.out.println("\n------------------------------------------\n");

        //특정 유저가 작성한 메시지 열람
        Optional<User> testUser2 = userService.findUserById(user1Id);
        if (testUser2.isPresent()) {
            System.out.println(testUser2.get().getUsername() + "님이 작성한 메시지:");
            for (Message m : testUser2.get().getMyMessages()) {
                System.out.println("- " + m.getContent());
            }
        }

        System.out.println("\n====================\n");

        // 유저 삭제 (연관된 채널의 participants에서도 제거되는지 확인)
        System.out.println("삭제 전 [" + updatedChannel.getChannelName() + "] 참가자 수: "
                + channelService.findParticipants(channel1Id).size() + "명");

        userService.deleteUser(user3Id);

        System.out.println("삭제 후 [" + updatedChannel.getChannelName() + "] 참가자 수: "
                + channelService.findParticipants(channel1Id).size() + "명");

        participants = channelService.findParticipants(channel1Id);
        System.out.println("현재 참가자:");
        for (User p : participants) {
            System.out.println("- " + p.getUsername());
        }

        System.out.println("\n------------------------------------------\n");

        // 채널 삭제 (연관된 유저의 myChannels에서도 제거되는지 확인)
        Optional<User> testUser3 = userService.findUserById(user1Id);
        testUser3.ifPresent(user ->
                System.out.println("삭제 전 " + user.getUsername() + "님의 참여 채널 수: "
                + user.getMyChannels().size() + "개"));

        channelService.deleteChannel(channel2Id);

        testUser3 = userService.findUserById(user1Id);
        if (testUser3.isPresent()) {
            System.out.println("삭제 후 " + testUser3.get().getUsername() + "님의 참여 채널 수: "
                    + testUser3.get().getMyChannels().size() + "개");
            System.out.println("현재 참여 채널:");
            for (Channel c : testUser3.get().getMyChannels()) {
                System.out.println("- " + c.getChannelName());
            }
        }

        System.out.println("\n------------------------------------------\n");

        // 존재하지 않는 데이터 조회 시 에러 처리
        System.out.println(">> 존재하지 않는 데이터 조회 테스트");
        try {
            userService.findUserById(user3Id)
                    .orElseThrow(() -> new IllegalArgumentException("해당 사용자 없음"));
        } catch (IllegalArgumentException e) {
            System.out.println("에러 발생: " + e.getMessage());
        }

        try {
            channelService.findChannelById(channel2Id)
                    .orElseThrow(() -> new IllegalArgumentException("해당 채널 없음"));
        } catch (IllegalArgumentException e) {
            System.out.println("에러 발생: " + e.getMessage());
        }


        // 최종 상태
        System.out.println("\n========== 최종 상태 확인 ==========\n");

        System.out.println(">> 남아있는 유저:");
        allUsers = userService.findAllUsers();
        for (User u : allUsers) {
            System.out.println("- " + u.getUsername() + " (채널 " + u.getMyChannels().size()
                    + "개 참여, 메시지 " + u.getMyMessages().size() + "개 작성)");
        }

        System.out.println("\n>> 남아있는 채널:");
        allChannels = channelService.findAllChannels();
        for (Channel c : allChannels) {
            System.out.println("- " + c.getChannelName() + " (참가자 "
                    + c.getParticipants().size() + "명, 메시지 "
                    + c.getChannelMessages().size() + "개)");
        }

        System.out.println("\n>> 남아있는 메시지:");
        allMessages = messageService.findAllMessages();
        System.out.println("총 메시지 수: " + allMessages.size() + "개");

        System.out.println("\n========== 모든 테스트 종료 ==========");
    }
}