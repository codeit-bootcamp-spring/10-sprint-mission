//package com.sprint.mission.discodeit;
//
//import com.sprint.mission.discodeit.dto.*;
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.ChannelType;
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.repository.*;
//import com.sprint.mission.discodeit.repository.file.*;
//import com.sprint.mission.discodeit.service.ChannelService;
//import com.sprint.mission.discodeit.service.MessageService;
//import com.sprint.mission.discodeit.service.UserService;
//import com.sprint.mission.discodeit.service.basic.BasicChannelService;
//import com.sprint.mission.discodeit.service.basic.BasicMessageService;
//import com.sprint.mission.discodeit.service.basic.BasicUserService;
//
//public class JavaApplication {
//    static UserResponseDto setupUser(UserService userService) {
//        UserCreateDto dto = new UserCreateDto("이정혁", "justin101423@naver.com", "이정혁1234", null);
//        return userService.create(dto);
//    }
//
//    static ChannelResponseDto setupChannel(ChannelService channelService) {
//        ChannelCreateDto dto = new ChannelCreateDto(ChannelType.PUBLIC, "공지", "공지 채널입니다.");
//        return channelService.create(dto);
//    }
//
//    static void messageCreateTest(MessageService messageService, ChannelResponseDto channel, UserResponseDto author) {
//        MessageCreateDto dto = new MessageCreateDto("안녕하세요.", channel.getId(), author.getId());
//        MessageResponseDto message = messageService.create(dto);
//        System.out.println("메시지 생성: " + message.getId());
//    }
//
//    public static void main(String[] args) {
//        // 레포지토리 초기화
//        UserRepository userRepository = new FileUserRepository();
//        ChannelRepository channelRepository = new FileChannelRepository();
//        MessageRepository messageRepository = new FileMessageRepository();
//        UserStatusRepository userStatusRepository = new FileUserStatusRepository();
//        BinaryContentRepository binaryContentRepository = new FileBinaryContentRepository();
//        ReadStatusRepository readStatusRepository = new FileReadStatusRepository();
//
//        // 서비스 초기화
//        UserService userService = new BasicUserService(userRepository, userStatusRepository, binaryContentRepository);
//        ChannelService channelService = new BasicChannelService(channelRepository, readStatusRepository);
//        MessageService messageService = new BasicMessageService(messageRepository, channelRepository, userRepository, readStatusRepository);
//
//        // 셋업
//        UserResponseDto user = setupUser(userService);
//        ChannelResponseDto channel = setupChannel(channelService);
//        // 테스트
//        messageCreateTest(messageService, channel, user);
//    }
//}
