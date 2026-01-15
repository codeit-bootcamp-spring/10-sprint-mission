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

public class JavaApplication {

    public static void main(String[] args) {
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService(userService);
        MessageService messageService = new JCFMessageService(channelService, userService);

        final String HR = "============================================================";
        final String SR = "------------------------------------------------------------";
        final String BR = "===============================";

        // =========================================================
        // 회원 테스트
        // =========================================================
        System.out.println("\n" + HR);
        System.out.println("회원 테스트");
        System.out.println(HR);

        System.out.println("\n[회원 등록]");
        User user1 = userService.createUser("홍길동", "hong@test.com");
        User user2 = userService.createUser("김철수", "kim@test.com");
        User user3 = userService.createUser("박민수", "park@test.com");
        System.out.println("  ✔ 등록 완료: 홍길동 / 김철수 / 박민수");
        System.out.println("  • " + user1);
        System.out.println("  • " + user2);
        System.out.println("  • " + user3);

        System.out.println("\n[회원 단건 조회]");
        System.out.println("  • " + userService.findUserById(user1.getId()));
        System.out.println("  • " + userService.findUserById(user2.getId()));
        System.out.println("  • " + userService.findUserById(user3.getId()));

        System.out.println("\n[회원 다건 조회]");
        List<User> usersAll1 = userService.findAll();
        System.out.println("  총 " + usersAll1.size() + "명");
        usersAll1.forEach(u -> System.out.println("  • " + u));

        System.out.println("\n[회원 수정]");
        System.out.println("  - 김철수 → 철수킴");
        userService.updateUserNickname(user2.getId(), "철수킴");

        System.out.println("\n[회원 수정 후 조회]");
        System.out.println("  • " + userService.findUserById(user2.getId()));

        System.out.println("\n[회원 삭제]");
        System.out.println("  - 박민수");
        userService.deleteUser(user3.getId());
        System.out.println("  ✔ 삭제 완료");

        System.out.println("\n[회원 다건 조회 - 삭제 후]");
        List<User> usersAll2 = userService.findAll();
        System.out.println("  총 " + usersAll2.size() + "명");
        usersAll2.forEach(u -> System.out.println("  • " + u));

        // =========================================================
        // 채널 테스트
        // =========================================================
        System.out.println("\n" + BR);
        System.out.println("\n" + HR);
        System.out.println("채널 테스트");
        System.out.println(HR);

        System.out.println("\n[채널 생성]");
        Channel channel1 = channelService.createChannel("1번 채널", user1);
        Channel channel2 = channelService.createChannel("2번 채널", user2);
        Channel channel3 = channelService.createChannel("3번 채널", user1);

        System.out.println("  ✔ 채널 생성 완료 (3개)");
        System.out.println("  • " + channel1);
        System.out.println("  • " + channel2);
        System.out.println("  • " + channel3);

        System.out.println("\n[채널 단건 조회]");
        System.out.println("  • " + channelService.findChannelById(channel1.getId()));
        System.out.println("  • " + channelService.findChannelById(channel2.getId()));
        System.out.println("  • " + channelService.findChannelById(channel3.getId()));

        System.out.println("\n[채널 다건 조회]");
        List<Channel> channelsAll1 = channelService.findAll();
        System.out.println("  총 " + channelsAll1.size() + "개");
        channelsAll1.forEach(ch -> System.out.println("  • " + ch));

        System.out.println("\n[채널 수정]");
        System.out.println("  - 2번 채널 → 22222번 채널");
        channelService.updateChannelName(channel2.getId(), user2.getId(), "22222번 채널");

        System.out.println("\n[채널 수정 후 조회]");
        System.out.println("  • " + channelService.findChannelById(channel2.getId()));

        System.out.println("\n[회원 다건 조회 - 채널 상태 반영 확인]");
        List<User> usersAll3 = userService.findAll();
        System.out.println("  총 " + usersAll3.size() + "명");
        usersAll3.forEach(u -> System.out.println("  • " + u));

        System.out.println("\n[채널 삭제]");
        System.out.println("  - 3번 채널");
        channelService.deleteChannel(channel3.getId(), user1.getId());
        System.out.println("  ✔ 삭제 완료");

        System.out.println("\n[채널 다건 조회 - 삭제 후]");
        List<Channel> channelsAll2 = channelService.findAll();
        System.out.println("  총 " + channelsAll2.size() + "개");
        channelsAll2.forEach(ch -> System.out.println("  • " + ch));

        System.out.println("\n[회원 다건 조회 - 채널 삭제 반영 확인]");
        List<User> usersAll4 = userService.findAll();
        System.out.println("  총 " + usersAll4.size() + "명");
        usersAll4.forEach(u -> System.out.println("  • " + u));

        System.out.println("\n[채널 유저 조회]");
        System.out.println("  채널명: " + channel1.getName());
        channelService.getMembers(channel1.getId()).forEach(u -> System.out.println("  • " + u));

        System.out.println("\n[채널 참가]");
        System.out.println("  - " + channel1.getName() + " ← " + user2.getNickname());
        channelService.joinChannel(channel1.getId(), user2.getId());
        System.out.println("  ✔ 참가 완료");

        System.out.println("\n[채널 유저 조회 - 참가 후]");
        System.out.println("  채널명: " + channel1.getName());
        channelService.getMembers(channel1.getId()).forEach(u -> System.out.println("  • " + u));

        // =========================================================
        // 메시지 테스트 (채널별)
        // =========================================================
        System.out.println("\n" + BR);
        System.out.println("\n" + HR);
        System.out.println("메시지 테스트 (채널별)");
        System.out.println(HR);

        List<Channel> channelList = channelService.findAll();
        User tester = userService.createUser("테스터", "tester@test.com");
        System.out.println("\n[TESTER 생성]");
        System.out.println("  • " + tester);

        for (Channel channel : channelList) {
            System.out.println("\n" + SR);
            System.out.println("[채널] " + channel.getName());
            System.out.println(SR);

            System.out.println("\n  [채널 가입] 테스터 → " + channel.getName());
            channelService.joinChannel(channel.getId(), tester.getId());
            System.out.println("  ✔ 가입 완료");

            System.out.println("\n  [회원 단건 조회] (테스터 상태 확인)");
            System.out.println("  • " + userService.findUserById(tester.getId()));

            System.out.println("\n  [메시지 생성]");
            messageService.createMessage(channel.getId(), tester.getId(), "이곳은 " + channel.getName() + " 입니다.");
            messageService.createMessage(channel.getId(), tester.getId(), "안녕하세요.");
            messageService.createMessage(channel.getId(), tester.getId(), "저는 테스터입니다.");
            Message updateTestMessage = messageService.createMessage(channel.getId(), tester.getId(), "수정 테스트 메시지입니다.");
            Message deleteTestMessage = messageService.createMessage(channel.getId(), tester.getId(), "삭제 테스트 메시지입니다.");
            System.out.println("  ✔ 생성 완료 (5개)");

            System.out.println("\n  [메시지 조회]");
            List<String> readView1 = messageService.readMessagesByChannelId(channel.getId());
            System.out.println("  총 " + readView1.size() + "개");
            readView1.forEach(s -> System.out.println("  • " + s));

            System.out.println("\n  [메시지 상세 조회]");
            List<Message> detailView1 = messageService.findMessagesByChannelId(channel.getId());
            System.out.println("  총 " + detailView1.size() + "개");
            detailView1.forEach(m -> System.out.println("  • " + m));

            System.out.println("\n  [메시지 수정]");
            messageService.updateMessageContent(
                    channel.getId(),
                    tester.getId(),
                    updateTestMessage.getId(),
                    "수정에 성공했습니다."
            );
            System.out.println("  ✔ 수정 완료");

            System.out.println("\n  [메시지 조회 - 수정 후]");
            List<String> readView2 = messageService.readMessagesByChannelId(channel.getId());
            System.out.println("  총 " + readView2.size() + "개");
            readView2.forEach(s -> System.out.println("  • " + s));

            System.out.println("\n  [메시지 상세 조회 - 수정 후]");
            List<Message> detailView2 = messageService.findMessagesByChannelId(channel.getId());
            System.out.println("  총 " + detailView2.size() + "개");
            detailView2.forEach(m -> System.out.println("  • " + m));

            System.out.println("\n  [메시지 삭제]");
            messageService.deleteMessage(
                    channel.getId(),
                    tester.getId(),
                    deleteTestMessage.getId()
            );
            System.out.println("  ✔ 삭제 완료");
            System.out.println("  - 삭제 대상 객체: " + deleteTestMessage);

            System.out.println("\n  [메시지 조회 - 삭제 후]");
            List<String> readView3 = messageService.readMessagesByChannelId(channel.getId());
            System.out.println("  총 " + readView3.size() + "개");
            readView3.forEach(s -> System.out.println("  • " + s));

            System.out.println("\n  [메시지 상세 조회 - 삭제 후]");
            List<Message> detailView3 = messageService.findMessagesByChannelId(channel.getId());
            System.out.println("  총 " + detailView3.size() + "개");
            detailView3.forEach(m -> System.out.println("  • " + m));

            System.out.println("\n  [채널 탈퇴] 테스터 ← " + channel.getName());
            channelService.leaveChannel(channel.getId(), tester.getId());
            System.out.println("  ✔ 탈퇴 완료");

            System.out.println("\n  [회원 단건 조회] (테스터 상태 확인)");
            System.out.println("  • " + userService.findUserById(tester.getId()));
        }

        System.out.println("\n" + HR);
        System.out.println("메시지 테스트 종료 후 - 회원 다건 조회");
        System.out.println(HR);
        List<User> usersAll5 = userService.findAll();
        System.out.println("  총 " + usersAll5.size() + "명");
        usersAll5.forEach(u -> System.out.println("  • " + u));

        // =========================================================
        // 유저별 메시지 생성/조회
        // =========================================================
        System.out.println("\n" + HR);
        System.out.println("유저별 메시지 생성/조회");
        System.out.println(HR);

        System.out.println("\n[유저별 메시지 생성]");
        System.out.println("  - " + channel1.getName() + " / " + user1.getNickname() + " 메시지 생성");
        messageService.createMessage(channel1.getId(), user1.getId(), "안녕하세요.");
        messageService.createMessage(channel1.getId(), user1.getId(), "유저별 메시지 생성 테스트입니다.");
        messageService.createMessage(channel1.getId(), user1.getId(), "저는 홍길동입니다.");

        System.out.println("  - " + channel1.getName() + " / " + channel2.getName() + " / " + user2.getNickname() + " 메시지 생성");
        // (원본 코드 유지) channel1에도 user2 메시지 작성
        messageService.createMessage(channel1.getId(), user2.getId(), "안녕하세요!");
        messageService.createMessage(channel1.getId(), user2.getId(), "유저별 메시지 생성 테스트입니다!");
        messageService.createMessage(channel1.getId(), user2.getId(), "저는 철수킴입니다!");
        // channel2에도 작성
        messageService.createMessage(channel2.getId(), user2.getId(), "안녕하세요.");
        messageService.createMessage(channel2.getId(), user2.getId(), "유저별 메시지 생성 테스트입니다.");
        messageService.createMessage(channel2.getId(), user2.getId(), "저는 철수킴입니다.");

        System.out.println("  - 1번 채널 / 테스터 참가 + 메시지 생성");
        channelService.joinChannel(channel1.getId(), tester.getId());
        messageService.createMessage(channel1.getId(), tester.getId(), "안녕하세요.");
        messageService.createMessage(channel1.getId(), tester.getId(), "유저별 메시지 생성 테스트입니다.");
        messageService.createMessage(channel1.getId(), tester.getId(), "저는 테스터입니다.");

        System.out.print("\n[유저별 작성한 메시지 조회]");
        List<User> userList = List.of(user1, user2, tester);
        for (User user : userList) {
            System.out.println("\n" + SR);
            System.out.println("[작성자] " + user.getNickname());
            System.out.println(SR);

            List<String> byUser = messageService.readMessagesByUserId(user.getId());
            System.out.println("  총 " + byUser.size() + "개");
            byUser.forEach(s -> System.out.println("  • " + s));
        }

        System.out.println("\n" + HR);
        System.out.println("테스트 종료");
        System.out.println(HR);
    }
}
