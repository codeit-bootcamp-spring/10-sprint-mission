package com.sprint.mission.discodeit;


import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class JavaApplication {
    public static void main(String[] args) {
        // 0. 서비스 초기화
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();
        MessageService messageService = new JCFMessageService();

        System.out.println("=== [시작] 서비스 테스트 ===");
        //---------------------------------1. user---------------------------------
        // 1-1. user 등록 및 조회
        User user1 = userService.create("박린", "lynnpark2003@ewha.ac.kr");
        System.out.println("1-1. 유저 등록 완료: " + userService.read(user1.getId()).getName());

        User user2 = userService.create("하현상", "lynnpark2003@ewha.ac.kr");
        System.out.println("1-1. 유저 등록 완료: " + userService.read(user2.getId()).getName());

        User user3 = userService.create("강미연", "lynnpark2003@ewha.ac.kr");
        System.out.println("1-1. 유저 등록 완료: " + userService.read(user3.getId()).getName());

        // 1-2. user 다건 조회
        System.out.println("1-2. 유저 다건 조회: " + "총" + userService.readAll().size() + "명");

        // 1-3. user 정보 수정 및 조회
        userService.update(user1.getId(), "박몽실", "parklean2003@gmail.com");
        System.out.println("1-3-1. 유저 정보 수정 완료: " + "\n이름: " + userService.read(user1.getId()).getName()
                + "\n이메일: " + userService.read(user1.getId()).getEmail());
        System.out.println("수정 시각: " + userService.read(user1.getId()).getUpdatedAt());

        userService.updateName(user2.getId(), "하봉구");
        System.out.println("1-3-2. 유저 이름 수정 완료: " + "\n이름: " + userService.read(user2.getId()).getName()
                + "\n이메일: " + userService.read(user2.getId()).getEmail());
        System.out.println("수정 시각: " + userService.read(user2.getId()).getUpdatedAt());

        userService.updateEmail(user3.getId(), "miyeonkang2003@naver.com");
        System.out.println("1-3-3. 유저 이메일 수정 완료: " + "\n이름: " + userService.read(user3.getId()).getName()
                + "\n이메일: " + userService.read(user3.getId()).getEmail());
        System.out.println("수정 시각: " + userService.read(user3.getId()).getUpdatedAt());

        // 1-4. user 정보 삭제 및 확인
        userService.delete(user3.getId());
        if (userService.read(user3.getId()) == null) {
            System.out.println("1-4. 유저 정보 삭제 완료");
        }

        // 1-5. user 다건 재조회로 정보 삭제 확인
        System.out.println("1-5. 유저 다건 재조회: " + "총" + userService.readAll().size() + "명");

        //NullPointerException 처리 추가 필요
        System.out.println();

        //---------------------------------2. channel---------------------------------
        // 2-1. channel 등록 및 조회
        Channel channel1 = channelService.create("채널1");
        System.out.println("2-1. 채널 등록 완료: " + channelService.read(channel1.getId()).getName());

        Channel channel2 = channelService.create("채널2");
        System.out.println("2-1. 채널 등록 완료: " + channelService.read(channel2.getId()).getName());

        Channel channel3 = channelService.create("채널3");
        System.out.println("2-1. 채널 등록 완료: " + channelService.read(channel3.getId()).getName());

        // 2-2. user 다건 조회
        System.out.println("2-2. 채널 다건 조회: " + "총" + channelService.readAll().size() + "개");

        // 2-3. channel 정보 수정 및 조회
        channelService.update(channel2.getId(), "채널2");
        System.out.println("2-3. 채널 이름 수정 완료: " + channelService.read(channel2.getId()).getName());
        System.out.println("수정 시각: " + channelService.read(channel2.getId()).getUpdatedAt());

        // 2-4. channel 정보 삭제 및 확인
        channelService.delete(channel3.getId());
        if (channelService.read(channel3.getId()) == null) {
            System.out.println("2-4. 채널 정보 삭제 완료");
        }

        // 2-5. channel 다건 재조회로 정보 삭제 확인
        System.out.println("2-5. channel 다건 재조회: " + "총" + channelService.readAll().size() + "개");

        System.out.println();
        //NullPointerException 처리 추가 필요


        //---------------------------------3. message---------------------------------
        // 3-1. message 등록 및 조회
        Message message1 = messageService.create(channel1, user1, "멍멍!");
        System.out.println("3-1. 메시지 등록 완료: [" + user1.getName() + "] " + message1.getText());

        Message message2 = messageService.create(channel1, user2, "냐옹~");
        System.out.println("3-1. 메시지 등록 완료: [" + user2.getName() + "] " + message2.getText());

        // 3-2. message 다건 조회
        System.out.println("3-2. 메세지 다건 조회: " + "총" + messageService.readAll().size() + "개");

        // 3-3. message 수정 및 조회
        messageService.update(message2.getId(), "안녕하세요.");
        System.out.println("3-3. 메세지 내용 수정 완료: [" + user2.getName() + "] " + messageService.read(message2.getId()).getText());
        System.out.println("수정 시각: " + messageService.read(message2.getId()).getUpdatedAt());

        // 3-4. message 삭제 및 확인
        messageService.delete(message1.getId());
        if (messageService.read(message1.getId()) == null) {
            System.out.println("3-4. 메시지 내용 삭제 완료");
        }

        // 3-5. message 다건 조회
        System.out.println("3-5. 메세지 다건 재조회: " + "총" + messageService.readAll().size() + "개");

        System.out.println("=== [종료] 서비스 테스트 ===");
    }
}
