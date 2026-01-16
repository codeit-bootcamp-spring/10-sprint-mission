package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;

import java.util.List;


public class JavaApplication {
    public static void main(String[] args) {
        JCFUserService userService = new JCFUserService();
        JCFChannelService channelService = new JCFChannelService(userService);
        JCFMessageService messageService = new JCFMessageService(userService, channelService);

        System.out.println("========= [1. User 도메인 테스트] =========");
        // 1. 등록
        User user1 = userService.create("김철수", "철수", "kcs@example.com", "1234");
        User user2 = userService.create("김영희", "0희", "kyh@example.com", "0000");
        User user3 = userService.create("홍길동", "길동이", "hkd@example.com", "hkd9876");
        System.out.println("[등록] user1, user2, user3 계정 생성 완료");

        // 2. 조회 (단건, 다건)
        System.out.println("[단건 조회]: " + userService.findById(user1.getId()).getName());
        System.out.println("[다건 조회] 전체 유저 수: " + userService.findAll().size());

        // 3. 수정
        userService.update(user1.getId(), "고철수", "고철수 입니다.", "go@example.com");
        userService.updateStatus(user2.getId(), "OFFLINE");
        userService.updatePassword(user3.getId(), "new-password");
        System.out.println("[수정] user1 계정 수정 완료");

        // 4. 수정된 데이터 조회
        User updatedUser = userService.findById(user1.getId());
        System.out.println("[수정 데이터 확인]");
        System.out.println("- 이름: " + updatedUser.getName());
        System.out.println("- 이메일: " + updatedUser.getEmail());
        System.out.println("- 수정시간: " + updatedUser.getUpdatedAt());
        System.out.println("- 현재 상태: " + user2.getStatus());
        System.out.println("- 비밀번호 변경 완료");

        // 5. 삭제
        userService.delete(user1.getId());
        System.out.println("[삭제] user1 계정 삭제 완료");

        // 6. 조회를 통해 삭제 확인
        try {
            userService.findById(user1.getId());
            System.out.println("[삭제 확인]: 실패 (해당 유저가 아직 존재함)");
        } catch (java.util.NoSuchElementException e) {
            System.out.println("[삭제 확인]: 성공 (" + e.getMessage() + ")");
        }

        System.out.println("\n========= [2. Channel 도메인 테스트] =========");
        // 1. 등록
        Channel channel1 = channelService.create("코딩 공부방", "공부합시다", "TEXT", false);
        Channel channel2 = channelService.create("공지방", "공지방입니다", "ANNOUNCEMENT", true);
        System.out.println("[등록] channel1, channel2 채널 생성 완료");

        // 2. 조회 (단건, 다건)
        System.out.println("[단건 조회]: " + channelService.findById(channel1.getId()).getName());
        System.out.println("[다건 조회] 전체 채널 수: " + channelService.findAll().size());

        // 3. 수정
        channelService.update(channel1.getId(), "자바 열공방", "열심히 공부합시다", true);
        System.out.println("[수정] channel1 채널 수정 완료");

        // 4. 수정된 데이터 조회
        Channel updatedChannel = channelService.findById(channel1.getId());
        System.out.println("[수정 데이터 확인]");
        System.out.println("- 이름: " + updatedChannel.getName());
        System.out.println("- 설명: " + updatedChannel.getDescription());
        System.out.println("- 공개여부: " + (updatedChannel.isPublic() ? "공개" : "비공개"));
        System.out.println("- 수정시간: " + updatedChannel.getUpdatedAt());

        // 5. 삭제
        channelService.delete(channel1.getId());
        System.out.println("[삭제] channel1 채널 삭제 완료");

        // 6. 조회를 통해 삭제 확인
        try {
            channelService.findById(channel1.getId());
            System.out.println("[삭제 확인]: 실패 (해당 채널이 아직 존재함)");
        } catch (java.util.NoSuchElementException e) {
            System.out.println("[삭제 확인]:  성공 (" + e.getMessage() + ")");
        }


        System.out.println("\n========= [3. Message 도메인 테스트] =========");
        // 1. 등록
        channel2.addMember(user2);
        channel2.addMember(user3);
        Message message1 = messageService.create("안녕하세요!", user2.getId(), channel2.getId());
        Message message2 = messageService.create("반갑습니다!", user3.getId(), channel2.getId());
        System.out.println("[등록] message1, message2 메시지 생성 완료");

        // 2. 조회 (단건, 다건)
        System.out.println("[단건 조회]: " + messageService.findById(message1.getId()).getContent());
        System.out.println("[다건 조회] 전체 메시지 수: " + messageService.findAll().size());

        // 3. 수정
        messageService.update(message1.getId(), "반가워요");
        System.out.println("[수정] message1 메시지 수정 완료");

        // 4. 수정된 데이터 조회
        Message updatedMsg = messageService.findById(message1.getId());
        System.out.println("[수정 데이터 확인]");
        System.out.println("- 내용: " + updatedMsg.getContent());
        System.out.println("- 수정 여부: " + (updatedMsg.isEdited() ? "수정됨(Edited)" : "수정 안 됨"));
        System.out.println("- 작성자: " + updatedMsg.getUser().getName());

        // 5. 삭제
        messageService.delete(message1.getId());
        System.out.println("[삭제] message1 메시지 삭제 완료");

        // 6. 조회를 통해 삭제 확인
        try {
            messageService.findById(message1.getId());
            System.out.println("[삭제 확인]: 실패 (해당 메시지가 아직 존재함)");
        } catch (java.util.NoSuchElementException e) {
            System.out.println("[삭제 확인]: 성공 (해당 메시지 없음)");
        }

        // 7. 메시지 고정
        Message pinMsg = messageService.create("공지사항입니다.", user2.getId(), channel2.getId());
        messageService.togglePin(pinMsg.getId());
        System.out.println("[메시지 고정 테스트]");
        System.out.println("- 메시지 고정: 고정 여부 -> " + pinMsg.isPinned());

        messageService.togglePin(pinMsg.getId());
        System.out.println("- 메시지 고정 해제: 고정 여부 -> " + pinMsg.isPinned());


        System.out.println("\n========= [4. 심화 요구사항 테스트: 객체 연관 관계 및 정합성] =========");

        User testerA = userService.create("테스터A", "A", "a@test.com", "1111");
        User testerB = userService.create("테스터B", "B", "b@test.com", "2222");
        Channel projectChannel = channelService.create("프로젝트방", "협업용", "TEXT", true);

        // 1. 채널 참가
        channelService.join(projectChannel.getId(), testerA.getId());
        channelService.join(projectChannel.getId(), testerB.getId());
        System.out.println("[참가] 테스터A, 테스터B가 '프로젝트방'에 가입함");

        // 2. 특정 채널에 참여한 유저 목록 조회
        List<User> members = channelService.findMembersByChannelId(projectChannel.getId());
        System.out.println("[검증] 채널 참여 유저 수 (기대값: 2): " + members.size());

        // 3. 특정 유저가 참가한 채널 목록 조회
        List<Channel> testerAChannels = userService.findJoinedChannelsByUserId(testerA.getId());
        System.out.println("[검증] 테스터A가 참여 중인 채널에 '프로젝트방'이 포함됨: " +
                testerAChannels.stream().anyMatch(c -> c.getName().equals("프로젝트방")));

        // 4. 메시지 작성
        Message m1 = messageService.create("테스터A의 메시지", testerA.getId(), projectChannel.getId());
        Message m2 = messageService.create("테스터B의 메시지", testerB.getId(), projectChannel.getId());

        // 5. 특정 채널에 발행된 메시지 목록 조회
        List<Message> channelMessages = channelService.findMessagesByChannelId(projectChannel.getId());
        System.out.println("[검증] 채널 내 메시지 수 (기대값: 2): " + channelMessages.size());

        // 6. 특정 유저가 발행한 메시지 목록 조회
        List<Message> userAMessages = userService.findMessagesByUserId(testerA.getId());
        System.out.println("[검증] 테스터A가 작성한 메시지 내용 확인: " + userAMessages.get(0).getContent());

        // 7. 채널 탈퇴
        channelService.leave(projectChannel.getId(), testerA.getId());
        System.out.println("[탈퇴] 테스터A가 채널에서 나감");
        System.out.println("[검증] 탈퇴 후 채널 멤버 수 (기대값: 1): " +
                channelService.findMembersByChannelId(projectChannel.getId()).size());
        System.out.println("[검증] 탈퇴 후 테스터A의 참여 채널 수 (기대값: 0): " +
                userService.findJoinedChannelsByUserId(testerA.getId()).size());


        System.out.println("\n===== 모든 서비스 테스트 완료 =====");
    }
}