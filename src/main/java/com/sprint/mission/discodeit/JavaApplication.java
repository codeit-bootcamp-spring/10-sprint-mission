package com.sprint.mission.discodeit;


import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.DiscordService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFDiscordService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.List;
import java.util.UUID;

public class JavaApplication {
//    public static void main(String[] args) {
//        // 0. 서비스 초기화
//        UserService userService = new JCFUserService();
//        ChannelService channelService = new JCFChannelService();
//        MessageService messageService = new JCFMessageService(userService, channelService);
//
//        DiscordService discordService = new JCFDiscordService(userService, channelService, messageService);
//
//        System.out.println("=== [시작] 서비스 테스트 ===");
//        //=============================== 등록/조회/수정 ===============================
//        System.out.println("== 등록/조회/수정 ==");
//        //---------------------------------1. user---------------------------------
//        // 1-1. user 등록 및 조회
//        User user1 = userService.create("박린", "lynnpark2003@ewha.ac.kr");
//        System.out.println("1-1. 유저 등록 완료: " + userService.read(user1.getId()).getName());
//
//        User user2 = userService.create("하현상", "lynnpark2003@ewha.ac.kr");
//        System.out.println("1-1. 유저 등록 완료: " + userService.read(user2.getId()).getName());
//
//        User user3 = userService.create("강미연", "lynnpark2003@ewha.ac.kr");
//        System.out.println("1-1. 유저 등록 완료: " + userService.read(user3.getId()).getName());
//
//        // 1-2. user 다건 조회
//        System.out.println("1-2. 유저 다건 조회: " + "총" + userService.readAll().size() + "명");
//
//        // 1-3. user 정보 수정 및 조회
//        userService.update(user1.getId(), "박몽실", "parklean2003@gmail.com");
//        System.out.println("1-3-1. 유저 정보 수정 완료: " + "\n이름: " + userService.read(user1.getId()).getName()
//                + "\n이메일: " + userService.read(user1.getId()).getEmail());
//        System.out.println("수정 시각: " + userService.read(user1.getId()).getUpdatedAt());
//
//        userService.update(user2.getId(), "하봉구", null);
//        System.out.println("1-3-2. 유저 이름 수정 완료: " + "\n이름: " + userService.read(user2.getId()).getName()
//                + "\n이메일: " + userService.read(user2.getId()).getEmail());
//        System.out.println("수정 시각: " + userService.read(user2.getId()).getUpdatedAt());
//
//        userService.update(user3.getId(), null, "miyeonkang2003@naver.com");
//        System.out.println("1-3-3. 유저 이메일 수정 완료: " + "\n이름: " + userService.read(user3.getId()).getName()
//                + "\n이메일: " + userService.read(user3.getId()).getEmail());
//        System.out.println("수정 시각: " + userService.read(user3.getId()).getUpdatedAt());
//
//        System.out.println();
//
//        //---------------------------------2. channel---------------------------------
//        // 2-1. channel 등록 및 조회
//        Channel channel1 = channelService.create("채널1");
//        System.out.println("2-1. 채널 등록 완료: " + channelService.read(channel1.getId()).getName());
//
//        Channel channel2 = channelService.create("채널2");
//        System.out.println("2-1. 채널 등록 완료: " + channelService.read(channel2.getId()).getName());
//
//        Channel channel3 = channelService.create("채널3");
//        System.out.println("2-1. 채널 등록 완료: " + channelService.read(channel3.getId()).getName());
//
//        // 2-2. user 다건 조회
//        System.out.println("2-2. 채널 다건 조회: " + "총" + channelService.readAll().size() + "개");
//
//        // 2-3. channel 정보 수정 및 조회
//        channelService.update(channel2.getId(), "채널2");
//        System.out.println("2-3. 채널 이름 수정 완료: " + channelService.read(channel2.getId()).getName());
//        System.out.println("수정 시각: " + channelService.read(channel2.getId()).getUpdatedAt());
//        System.out.println();
//
//
//        //---------------------------------3. message---------------------------------
//        // 3-1. message 등록 및 조회
//        Message message1 = messageService.create(channel1.getId(), user1.getId(), "멍멍!");
//        System.out.println("3-1. 메시지 등록 완료: [" + user1.getName() + "] " + message1.getText());
//
//        Message message2 = messageService.create(channel1.getId(), user2.getId(), "냐옹~");
//        System.out.println("3-1. 메시지 등록 완료: [" + user2.getName() + "] " + message2.getText());
//
//        Message message3 = messageService.create(channel1.getId(), user2.getId(), "짹짹");
//        System.out.println("3-1. 메시지 등록 완료: [" + user2.getName() + "] " + message3.getText());
//
//        // 3-2. message 다건 조회
//        System.out.println("3-2. 메세지 다건 조회: " + "총" + messageService.readAll().size() + "개");
//
//        // 3-3. message 수정 및 조회
//        messageService.update(message2.getId(), "안녕하세요.");
//        System.out.println("3-3. 메세지 내용 수정 완료: [" + user2.getName() + "] " + messageService.read(message2.getId()).getText());
//        System.out.println("수정 시각: " + messageService.read(message2.getId()).getUpdatedAt());
//
//        System.out.println();
//        System.out.println();
//
//        //================================ 추가 기능 ================================
//        System.out.println("==추가 기능==");
//        // -----------------------1. 채널의 참가자 목록/메시지 조회-----------------------
//        // user의 channel 등록
//        user1.joinChannel(channel1);
//        user2.joinChannel(channel1);
//        user2.joinChannel(channel2);
//
//        // 1-1. channel 참가자 목록(name) 조회
//        List<User> userList = discordService.getUsersByChannel(channel1.getId());
//        List<String> userNames = userList.stream()
//                .map(User::getName)
//                .toList();
//        System.out.println("1-1. 채널 참가자 목록 조회");
//        System.out.println(channel1.getName() + "의 참가자 목록: " + userNames);
//        System.out.println();
//
//        // 1-2. channel 참가자 메시지(sender, text) 조회
//        List<Message> channelMessages = discordService.getMessagesByChannel(channel1.getId());
//        List<String> channelMessageContext = channelMessages.stream()
//                .map(message -> "[" + message.getSender().getName() + "] : " + message.getText())
//                .toList();
//        System.out.println("1-2. 채널 메시지 목록 조회");
//        System.out.println(channel1.getName() + "의 메시지 목록: " + channelMessageContext);
//        System.out.println();
//
//
//        // -----------------------2. 유저의 채널/메시지 조회-----------------------
//        // 2-1. user의 channel 목록 조회
//        List<Channel> channels = discordService.getChannelsByUser(user2.getId());
//        List<String> channelNames = channels.stream()
//                .map(Channel::getName)
//                .toList();
//        System.out.println("2-1. 유저의 채널 목록 조회");
//        System.out.println(user2.getName() + "의 채녈 목록: " + channelNames);
//        System.out.println();
//
//        // 2-2. user의 message 목록 조회
//        List<Message> userMessages = discordService.getMessagesByUser(user1.getId());
//        List<String> userMessageContext = userMessages.stream()
//                .map(message -> "[" + message.getSender().getName() + "] : " + message.getText())
//                .toList();
//        System.out.println("2-2. 유저의 메시지 목록 조회");
//        System.out.println(user1.getName() + "의 메시지 목록: " + userMessageContext);
//
//        System.out.println();
//        System.out.println();
//
//
//        //================================ 삭제 ================================
//        System.out.println("== 삭제 ==");
//        //---------------------------------1. user---------------------------------
//        discordService.deleteUser(user1.getId());
//
//        // 1-1. user의 channel 목록 조회
//        System.out.println("1-1. 유저1 삭제 후 채널 참가자 목록 조회");
//        List<User> userListAfterUserDelete = discordService.getUsersByChannel(channel1.getId());
//        List<String> userNamesAfterUserDelete = userListAfterUserDelete.stream()
//                .map(User::getName)
//                .toList();
//        System.out.println(channel1.getName() + "의 참가자 목록: " + userNamesAfterUserDelete);
//        System.out.println();
//
//        // 1-2. user의 message 목록 조회
//        System.out.println("1-2. 유저1 삭제 후 메시지 목록 조회");
//        List<Message> userMessagesAfterUserDelete = discordService.getMessagesByChannel(channel1.getId());
//        List<String> userMessagesContextAfterUserDelete = userMessagesAfterUserDelete.stream()
//                .map(m -> "[" + m.getSender().getName() + "] : " + m.getText())
//                .toList();
//        System.out.println(channel1.getName() + "의 메시지 목록: " + userMessagesContextAfterUserDelete);
//        System.out.println();
//
//
//        //---------------------------------2. channel---------------------------------
//        discordService.deleteChannel(channel1.getId());
//
//        // 2-1. user의 channel 목록 조회
//        System.out.println("2-1. 채널1 삭제 후 유저의 채널 목록 조회");
//        List<Channel> channelsAfterChannelDelete = discordService.getChannelsByUser(user2.getId());
//        List<String> channelNamesAfterChannelDelete = channelsAfterChannelDelete.stream()
//                .map(Channel::getName)
//                .toList();
//        System.out.println(user2.getName() + "의 채널 목록: " + channelNamesAfterChannelDelete);
//        System.out.println();
//
//        // 2-2. user의 message 목록 조회
//        List<Message> userMessagesAfterChannelDelete = discordService.getMessagesByUser(user2.getId());
//        List<String> userMessageContextAfterChannelDelete = userMessagesAfterChannelDelete.stream()
//                .map(message -> "[" + message.getSender().getName() + "] : " + message.getText())
//                .toList();
//        System.out.println("2-2. 유저의 메시지 목록 조회");
//        System.out.println(user2.getName() + "의 메시지 목록: " + userMessageContextAfterChannelDelete);
//        System.out.println();
//
//        //---------------------------------3. message--------------------------------
//        // 3-1. message 삭제 및 확인
//        System.out.println("3-1. 메세지 다건 재조회: " + "총 " + messageService.readAll().size() + "개");
//
//        messageService.delete(message2.getId());
//        System.out.println("3-2. 메시지 내용 삭제 완료");
//
//        // 3-2. message 다건 조회
//        System.out.println("3-3. 메세지 다건 재조회: " + "총 " + messageService.readAll().size() + "개");
//        System.out.println();
//        System.out.println();
//


        // ****************************미션 2****************************
        static User setupUser(UserService userService) {
            User user = userService.create("lynn", "lynn@codeit.com", "lynn1234");
            return user;
        }

        static Channel setupChannel(ChannelService channelService) {
            Channel channel = channelService.create("공지 채널");
            return channel;
        }

        static void messageCreate(MessageService messageService, Channel channel, User sender) {
            Message message = messageService.create(channel.getId(), sender.getId(), "안녕하세요.");
            System.out.println("================================");
            System.out.println("메시지 생성이 완료되었습니다!");
            System.out.println("채널: " + message.getChannel().getName());
            System.out.println("보낸 이: " + message.getSender().getName());
            System.out.println("내용: " + message.getText());
            System.out.println("메시지 ID: " + message.getId());
            System.out.println("================================");
        }

        public static void main(String[] args) {
            // 서비스 초기화
            UserRepository userRepository = new FileUserRepository();
            ChannelRepository channelRepository = new FileChannelRepository();
            MessageRepository messageRepository = new FileMessageRepository();

            // Basic*Service 구현체를 초기화
            UserService userService = new BasicUserService(userRepository);
            ChannelService channelService = new BasicChannelService(channelRepository);
            MessageService messageService = new BasicMessageService(messageRepository, userService, channelService);

            // 셋업
            User user = setupUser(userService);
            Channel channel = setupChannel(channelService);

            // 테스트
            messageCreate(messageService, channel, user);
        }

    }

