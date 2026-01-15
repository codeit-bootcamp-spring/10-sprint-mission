package com.sprint.mission.discodeit;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {
//        //  서비스 인스턴스 생성
//        JCFUserService userService = new JCFUserService();
//        JCFChannelService channelService = new JCFChannelService();
//        JCFMessageService messageService = new JCFMessageService(userService, channelService);
//
//        //  1. 유저 생성
//        User u1 = new User("최종인", "jongin");
//        User u2 = new User("감수빈", "soobeen");
//        userService.createUser(u1);
//        userService.createUser(u2); //데이터 상 유저 등록.
//
//        //  2. 채널 생성
//        Channel ch1 = new Channel("일반");
//        Channel ch2 = new Channel("공지사항");
//        channelService.createChannel(ch1);
//        channelService.createChannel(ch2);
//
//        System.out.println("=== 유저 및 채널 등록 완료 ===");
//        System.out.println("유저 목록: " + userService.getUserAll());
//        System.out.println("채널 목록: " + channelService.getChannelAll());
//
//        //  3. 메시지 생성
//        Message m1 = new Message("안녕하세요!", u1, ch1);
//        Message m2 = new Message("반가워요~", u2, ch1);
//        Message m3 = new Message("공지사항 올립니다.", u1, ch2);
//
//        messageService.createMessage(m1);
//        messageService.createMessage(m2);
//        messageService.createMessage(m3);
//
//        System.out.println("\n=== 메시지 등록 완료 ===");
//        messageService.getMessageAll().forEach(message ->
//                System.out.println(message.getSender().getUserName() + " → [" + message.getChannel().getChannelName() + "] " + message.getContent())
//        );
//
//        // 4. 채널별 메시지 조회
//        System.out.println("\n===(채널: 일반) 메세지 목록 ===");
//        messageService.getMessagesByChannelName("공지사항").forEach(message->
//                System.out.println(message.getSender().getUserName() + ": " + message.getContent()));
//
//        // 5. 사용자별 메시지 조회
//        System.out.println("\n=== (작성자: 최종인) 메시지 목록 ===");
//        messageService.getMessagesBySenderName("최종인").forEach(message ->
//                System.out.println("[" + message.getChannel().getChannelName() + "] " + message.getContent())
//        );
//
//        // 6. 메시지 수정
//        System.out.println("\n=== 메시지 수정 테스트 ===");
//        m1.update("안녕하세요! 수정된 메시지입니다.");
//        messageService.updateMessage(m1);
//        System.out.println("수정 결과: " + messageService.getMessageById(m1.getId()).getContent());
//
//         //7. 유저/채널 삭제
//        System.out.println("\n=== 유저 & 채널 삭제 테스트 ===");
//        userService.deleteUser(u1.getId());
//        channelService.deleteChannel(ch1.getId());
//        System.out.println("남은 유저: " + userService.getUserAll());
//        System.out.println("남은 채널: " + channelService.getChannelAll());

//        //  서비스 생성
//        JCFUserService userService = new JCFUserService();
//        JCFChannelService channelService = new JCFChannelService();
//        JCFMessageService messageService = new JCFMessageService(userService, channelService); // 의존성 주입
//
////  유효한 User/Channel 등록
//        User u1 = userService.createUser("최종인", "jongin");
//        User u2 = userService.createUser("김민수", "minsu");
//        Channel ch1 = channelService.createChannel("공지사항");
//        Channel ch2 = channelService.createChannel("잡담방");
//
//        //유저 업데이트. -> 이제는 getId() 로 들어나지 않게 받음.
//        userService.updateUser(u1.getId(), "김수빈", "subin");
//        System.out.println(u1.getUserName());
//        //userService.getUserByAlias("sunidn");
//
//
//
////  메시지 생성 - 정상
//        System.out.println("\n[메시지 생성 테스트 - 정상]");
//        Message m1 = messageService.createMessage("생성자 권한 서비스로 테스트",u1,ch1);
//        Message m2 = messageService.createMessage("생성자 권한 테스트 2", u2 ,ch2);
//        System.out.println("메시지 생성 성공: " + m1.getContent());
//
////  추가 메시지 (여러 명, 여러 채널)
//        Message m3 = messageService.createMessage("반가워요!", u2, ch1);
//        Message m4 = messageService.createMessage("이 채널 진짜 조용하네요.", u1, ch2);
//// 추가 메세지 업데이트
//        messageService.updateMessage(m3.getId(), "아쉽네요!");
//
//
////  전체 메시지 조회
//        System.out.println("\n[전체 메시지 목록]");
//        for (Message msg : messageService.getMessageAll()) {
//            System.out.printf("- [%s] %s: %s%n",
//                    msg.getChannel().getChannelName(),
//                    msg.getSender().getUserName(),
//                    msg.getContent());
//        }
//
////  채널별 메시지 조회
//        System.out.println("\n[공지사항 채널 메시지]");
//        for (Message msg : messageService.getMessagesByChannelName("공지사항")) {
//            System.out.printf("- %s: %s%n", msg.getSender().getUserName(), msg.getContent());
//        }
//
////  사용자별 메시지 조회
//        System.out.println("\n[사용자별 메시지 - 최종인]");
//        for (Message msg : messageService.getMessagesBySenderName("최종인")) {
//            System.out.printf("- (%s) %s%n", msg.getChannel().getChannelName(), msg.getContent());
//        }
//// 채널 업데이트 후 확인
//        Channel ch10 = channelService.createChannel("긴급");
//        channelService.updateChannel(ch10.getId(), "특별 공지방");
//        System.out.println(ch10.getChannelName());
//
//
//        System.out.println("같은 이름으로  채널 생성/ 변경 시 오류");
//        //Channel ch11 = channelService.createChannel("긴급");
//        //channelService.updateChannel(ch10.getId(), "공지사항");
//            //Channel ch11 = channelService.createChannel("");
            //  서비스 생성
//            JCFUserService userService = new JCFUserService();
//            JCFChannelService channelService = new JCFChannelService();
//            JCFMessageService messageService = new JCFMessageService(userService, channelService);
//
//            System.out.println("\n===  User / Channel / Message 생성 ===");
//            //  User 생성
//            User u1 = userService.createUser("최종인", "jongin");
//            System.out.println(" 사용자 생성: " + u1.getUserName() + " (" + u1.getId() + ")");
//            User u2 = userService.createUser("최종인", "jongin98");
//
//
//            //  Channel 생성
//            Channel ch1 = channelService.createChannel("공지사항");
//            System.out.println(" 채널 생성: " + ch1.getChannelName() + " (" + ch1.getId() + ")");
//
//              //Message 생성
//            Message m1 = messageService.createMessage("첫 번째 메시지입니다.",u1.getId(), ch1.getId());
//            System.out.println(" 메시지 생성: " + m1.getContent() + " (" + m1.getId() + ")");
//
////            Message m1 = messageService.createMessage("테스트 메세지!!!", u1, ch1);
//
//            System.out.println("메시지 저장 시점: " + m1.getCreatedAt());
//            System.out.println("Map에서 꺼낸 후 시점: " + messageService.findmsgOrThrow(m1.getId()).getCreatedAt());
//
//            System.out.println("\n=== ID 기반 조회 (공통 find...OrThrow 메서드 테스트) ===");
//            User foundUser = userService.findUserOrThrow(u1.getId());
//            Channel foundChannel = channelService.findChannelOrThrow(ch1.getId());
//            Message foundMessage = messageService.findmsgOrThrow(m1.getId());
//
//            System.out.println("조회된 User: " + foundUser.getUserName());
//            System.out.println("조회된 Channel: " + foundChannel.getChannelName());
//            System.out.println("조회된 Message: " + foundMessage.getContent());
//
//            System.out.println("\n===  ID 기반 수정 (update 메서드 테스트) ===");
//            userService.updateUser(u1.getId(), "김민수", "minsu");
//            channelService.updateChannel(ch1.getId(), "일반공지");
//            messageService.updateMessage(m1.getId(), "수정된 메시지 내용입니다.");
//
//
//            System.out.println("=== " + u1.getUserName() + "이(가) 보낸 메시지 목록 ===");
//            UUID userId = u1.getId();
//            List<Message> messages = messageService.getMessagesBySenderId(userId);
//            for (Message m : messages) {
//                System.out.println(m.getContent());
//            }
//
//            System.out.println(" 사용자 이름 변경: " + userService.findUserOrThrow(u1.getId()).getUserName());
//            System.out.println("️ 채널 이름 변경: " + channelService.findChannelOrThrow(ch1.getId()).getChannelName());
//            System.out.println(" 메시지 내용 변경: " + messageService.findmsgOrThrow(m1.getId()).getContent());
//
//            System.out.println("\n=== ID 기반 삭제 (delete 메서드 테스트) ===");
//            userService.deleteUser(u1.getId());
//            channelService.deleteChannel(ch1.getId());
//            messageService.deleteMessage(m1.getId());
//
//            System.out.println(" 모든 데이터 삭제 완료");
//
//            System.out.println("\n===  예외 발생 테스트 (삭제 후 조회 시도) ===");
//            try {
//                    userService.findUserOrThrow(u1.getId());
//            } catch (NoSuchElementException e) {
//                    System.out.println(" 사용자 조회 실패: " + e.getMessage());
//            }
//
//            try {
//                    channelService.findChannelOrThrow(u1.getId());
//            } catch (NoSuchElementException e) {
//                    System.out.println(" 채널 조회 실패: " + e.getMessage());
//            }
//
//            try {
//                    messageService.findmsgOrThrow(m1.getId());
//            } catch (NoSuchElementException e) {
//                    System.out.println(" 메시지 조회 실패: " + e.getMessage());
//            }
//
//            System.out.println("\n 모든 공통 메서드 테스트 완료!");

        JCFUserService userService = new JCFUserService();
        JCFChannelService channelService = new JCFChannelService();
        JCFMessageService messageService = new JCFMessageService(userService, channelService);

        User u1 = userService.createUser("홍길동", "gildong");
        User u2 = userService.createUser("김철수", "chulsoo");
        User u3 = userService.createUser("이영희", "younghee");
        Channel ch1 = channelService.createChannel("공지사항");

        //참가시키기
        u1.joinChannel(ch1);
        u2.joinChannel(ch1);

        // 특정 채널의 참가자 조회
        List<User> participants = channelService.getUsersInChannel(ch1.getId());

        System.out.println("[ " + ch1.getChannelName()+ " ] 참가자" );
        for(User u : participants) {
            System.out.println("- " + u.getAlias());
        }

        // 탈뢰 후 다시 확인
        u2.leaveChannel(ch1);
        System.out.println("\n📢 [" + ch1.getChannelName() + "] 참가자 (탈퇴 후):");
        for (User u : participants) {
            System.out.println("- " + u.getAlias());
        }

        System.out.println("===");
        System.out.println(ch1.getParticipants());

        // 메세지 여러개 보내보자
        Message m1 = messageService.createMessage("첫번째 메세지 입니다.", u1.getId(), ch1.getId());
        Message m2 = messageService.createMessage("두번째 메세지 입니다.", u1.getId(), ch1.getId());
        Message m3 = messageService.createMessage("세번째 메세지 입니다.", u1.getId(), ch1.getId());

        System.out.println(userService.getMessageByUser(u1.getId()));

        System.out.println(u1.getAlias()+ "가 참가한 채널 목록");
        System.out.println(userService.getChannelsByUser(u1.getId()));




    }
}
