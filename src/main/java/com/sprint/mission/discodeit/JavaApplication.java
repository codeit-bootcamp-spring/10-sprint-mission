package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
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
        // 0. 서비스 초기화
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();
        MessageService messageService = new JCFMessageService(userService, channelService);

        System.out.println("=== [시작] 서비스 테스트 ===");
        //================================ 기본 기능 ================================
        System.out.println("==기본 기능==");
        //---------------------------------1. user---------------------------------
        // 1-1. user 등록 및 조회
        User user1 = userService.createUser("전창현", "ckdgus12@gmail.com");
        System.out.println("1-1. 유저 등록 완료: " + user1.getUserName());

        User user2 = userService.createUser("이한수", "gkstn13@naver.com");
        System.out.println("1-1. 유저 등록 완료: " + user2.getUserName());

        User user3 = userService.createUser("이백호", "qorgh@kongju.ac.kr");
        System.out.println("1-1. 유저 등록 완료: " + user3.getUserName());
        System.out.println();

        // 1-2. user 다건 조회
        System.out.println("1-2. 유저 다건 조회: " + "총" + userService.readAllUser().size() + "명");
        System.out.println();

        // 1-3. user 정보 수정 및 조회
        User updatedUser1 = userService.updateUser(user1.getId(), "김수한", "suhan78@gmail.com");
        System.out.println("1-3-1. 유저 정보 수정 완료: " + "\n이름: " + updatedUser1.getUserName()
                + "\n이메일: " + updatedUser1.getUserEmail());
        System.out.println("수정 시각: " + updatedUser1.getUpdatedAt());
        System.out.println();

        User updatedUser2 = userService.updateUser(user2.getId(), "정몽주", null);
        System.out.println("1-3-2. 유저 이름 수정 완료: " + "\n이름: " + updatedUser2.getUserName()
                + "\n이메일: " + updatedUser2.getUserEmail());
        System.out.println("수정 시각: " + updatedUser2.getUpdatedAt());
        System.out.println();

        User updatedUser3 = userService.updateUser(user3.getId(), null, "backho@naver.com");
        System.out.println("1-3-3. 유저 이메일 수정 완료: " + "\n이름: " + updatedUser3.getUserName()
                + "\n이메일: " + updatedUser3.getUserEmail());
        System.out.println("수정 시각: " + updatedUser3.getUpdatedAt());
        System.out.println();

        // 1-4. user 정보 삭제 및 확인
        userService.deleteUser(user3.getId());
        System.out.println("1-4. 유저 정보 삭제 완료");

        // 1-5. user 다건 재조회로 정보 삭제 확인
        System.out.println("1-5. 유저 다건 재조회: " + "총" + userService.readAllUser().size() + "명");
        System.out.println();


        //---------------------------------2. channel---------------------------------
        // 2-1. channel 등록 및 조회
        Channel channel1 = channelService.createChannel(ChannelType.PUBLIC,"채널1", "채널1 입니다.");
        System.out.println("2-1. 채널 등록 완료: " + channel1.getChannelType() + " "
                + channel1.getChannelName());

        Channel channel2 = channelService.createChannel(ChannelType.PRIVATE,"채널2", "채널2 입니다.");
        System.out.println("2-1. 채널 등록 완료: " + channel2.getChannelType() + " "
                + channel2.getChannelName());

        Channel channel3 = channelService.createChannel(ChannelType.PUBLIC,"채널3", "채널3 입니다.");
        System.out.println("2-1. 채널 등록 완료: " + channel3.getChannelType() + " "
                + channel3.getChannelName());
        System.out.println();

        // 2-2. user 다건 조회
        System.out.println("2-2. 채널 다건 조회: " + "총" + channelService.readAllChannel().size() + "개");
        System.out.println();

        // 2-3. channel 정보 수정 및 조회
        Channel updatedChannel1 = channelService.updateChannel(channel1.getId(), ChannelType.PRIVATE,null, null);
        System.out.println("2-3. 채널1 타입 수정 완료: " + updatedChannel1.getChannelType());
        System.out.println("수정 시각: " + updatedChannel1.getUpdatedAt());
        System.out.println();

        Channel updatedChannel2 = channelService.updateChannel(channel2.getId(), null,"채널10", null);
        System.out.println("2-3. 채널2 이름 수정 완료: " + updatedChannel2.getChannelName());
        System.out.println("수정 시각: " + updatedChannel2.getUpdatedAt());
        System.out.println();

        Channel updatedChannel3 = channelService.updateChannel(channel3.getId(), null,null, "채널3을 수정했습니다.");
        System.out.println("2-3. 채널3 설명 수정 완료: " + updatedChannel3.getChannelDescription());
        System.out.println("수정 시각: " + updatedChannel3.getUpdatedAt());
        System.out.println();

        // 2-4. channel 정보 삭제 및 확인
        channelService.deleteChannel(channel3.getId());
        System.out.println("2-4. 채널 삭제 완료");
        System.out.println();

        // 2-5. channel 다건 재조회로 정보 삭제 확인
        System.out.println("2-5. channel 다건 재조회: " + "총" + channelService.readAllChannel().size() + "개");
        System.out.println();

        //---------------------------------3. message---------------------------------
        // 3-1. message 등록 및 조회
        Message message1 = messageService.createMessage("안녕하세요", channel1.getId(), user1.getId());
        System.out.println("3-1. 메시지 등록 완료: [" + user1.getUserName() + "] " + message1.getContent());

        Message message2 = messageService.createMessage("식사 맛있게 하세요", channel1.getId(), user2.getId());
        System.out.println("3-1. 메시지 등록 완료: [" + user2.getUserName() + "] " + message2.getContent());

        Message message3 = messageService.createMessage("수고하셨습니다", channel1.getId(), user2.getId());
        System.out.println("3-1. 메시지 등록 완료: [" + user2.getUserName() + "] " + message3.getContent());
        System.out.println();

        // 3-2. message 다건 조회
        System.out.println("3-2. 메세지 다건 조회: " + "총" + messageService.readAllMessage().size() + "개");
        System.out.println();

        // 3-3. message 수정 및 조회
        Message updatedMessage2 = messageService.updateMessage(message2.getId(), "안녕히가세요");
        System.out.println("3-3. 메세지 내용 수정 완료: [" + user2.getUserName() + "] " + updatedMessage2.getContent());
        System.out.println("수정 시각: " + updatedMessage2.getUpdatedAt());
        System.out.println();
        // 3-4. message 삭제 및 확인
        messageService.deleteMessage(message3.getId());
        System.out.println("3-4. 메시지 삭제 완료");
        System.out.println();

        // 3-5. message 다건 재조회
        System.out.println("3-5. 메세지 다건 재조회: " + "총" + messageService.readAllMessage().size() + "개");
        System.out.println();

        // -----------------------1. 채널의 참가자 목록/메시지 조회-----------------------
        // user의 channel 등록
        channelService.joinChannel(channel1.getId(), user1);
        channelService.joinChannel(channel1.getId(), user2);
        channelService.joinChannel(channel2.getId(), user2);

        // 1-1. channel 참가자 목록(name) 조회
        List<User> userList = userService.readUsersByChannel(channel1.getId());
        List<String> userNames = userList.stream()
                .map(User::getUserName)
                .toList();

        System.out.println(channel1.getChannelName() + "의 참가자 목록: " + userNames);
        System.out.println();

        // 1-2. channel 참가자 메시지 조회
        List<Message> channelMessages = messageService.readMessagesByChannel(channel1.getId());

        List<String> channelMessageContent = channelMessages.stream()
                .map(message ->
                        "[" + message.getUser().getUserName() + "] : " + message.getContent()
                )
                .toList();
        System.out.println("1-2. 채널 메시지 목록 조회");
        System.out.println(channel1.getChannelName() + "의 메시지 목록: " + channelMessageContent);
        System.out.println();


        // -----------------------2. 유저의 채널/메시지 조회-----------------------
        // 2-1. user의 channel 목록 조회
        List<Channel> channels = channelService.readChannelsByUser(user2.getId());
        List<String> channelNames = channels.stream()
                .map(Channel::getChannelName)
                .toList();
        System.out.println("2-1. 유저의 채널 목록 조회");
        System.out.println(user2.getUserName() + "의 채널 목록: " + channelNames);
        System.out.println();


        // 2-2. user의 message 목록 조회
        List<Message> userMessages = messageService.readMessagesByUser(user1.getId());
        List<String> userMessageContent =userMessages.stream()
                .map(message -> "[" + message.getUser().getUserName() + "] : " + message.getContent())
                .toList();
        System.out.println("2-2. 유저의 메시지 목록 조회");
        System.out.println(user1.getUserName() + "의 메시지 목록: " + userMessageContent);
        System.out.println();

        System.out.println("=== [종료] 서비스 테스트 ===");
    }
}
