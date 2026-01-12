package com.sprint.mission.discodeit;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.jcf.*;

import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {
        //  서비스 인스턴스 생성
        JCFUserService userService = new JCFUserService();
        JCFChannelService channelService = new JCFChannelService();
        JCFMessageService messageService = new JCFMessageService();

        //  1. 유저 생성
        User u1 = new User("최종인", "뚱토리");
        User u2 = new User("감수빈", "너굴");
        userService.createUser(u1);
        userService.createUser(u2); //데이터 상 유저 등록.

        //  2. 채널 생성
        Channel ch1 = new Channel("일반");
        Channel ch2 = new Channel("공지사항");
        channelService.createChannel(ch1);
        channelService.createChannel(ch2);

        System.out.println("=== 유저 및 채널 등록 완료 ===");
        System.out.println("유저 목록: " + userService.getUserAll());
        System.out.println("채널 목록: " + channelService.getChannelAll());

        //  3. 메시지 생성
        Message m1 = new Message("안녕하세요!", u1, ch1);
        Message m2 = new Message("반가워요~", u2, ch1);
        Message m3 = new Message("공지사항 올립니다.", u1, ch2);

        messageService.createMessage(m1);
        messageService.createMessage(m2);
        messageService.createMessage(m3);

        System.out.println("\n=== 메시지 등록 완료 ===");
        messageService.getMessageAll().forEach(message ->
                System.out.println(message.getSender().getUserName() + " → [" + message.getChannel().getChannelName() + "] " + message.getContent())
        );

        // 4. 채널별 메시지 조회
        System.out.println("\n=== (채널: 일반) 메시지 목록 ===");
        messageService.getMessagesByChannelName("일반").forEach(message ->
                System.out.println(message.getSender().getUserName() + ": " + message.getContent())
        );

        // 5. 사용자별 메시지 조회
        System.out.println("\n=== (작성자: 최종인) 메시지 목록 ===");
        messageService.getMessagesBySenderName("최종인").forEach(message ->
                System.out.println("[" + message.getChannel().getChannelName() + "] " + message.getContent())
        );

        // 6. 메시지 수정
        System.out.println("\n=== 메시지 수정 테스트 ===");
        m1.update("안녕하세요! 수정된 메시지입니다.");
        messageService.updateMessage(m1);
        System.out.println("수정 결과: " + messageService.getMessageById(m1.getId()).getContent());

        // 7. 유저/채널 삭제
        System.out.println("\n=== 유저 & 채널 삭제 테스트 ===");
        userService.deleteUser(u2.getId());
        channelService.deleteChannel(ch2.getId());
        System.out.println("남은 유저: " + userService.getUserAll());
        System.out.println("남은 채널: " + channelService.getChannelAll());




    }
}
