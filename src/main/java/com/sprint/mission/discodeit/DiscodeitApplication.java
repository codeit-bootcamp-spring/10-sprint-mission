//package com.sprint.mission.discodeit;
//
//import com.sprint.mission.discodeit.dto.UserCreateDto;
//import com.sprint.mission.discodeit.entity.*;
//import com.sprint.mission.discodeit.service.ChannelService;
//import com.sprint.mission.discodeit.service.MessageService;
//import com.sprint.mission.discodeit.service.UserService;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.ConfigurableApplicationContext;
//
//import java.util.UUID;
//
//@SpringBootApplication
//public class DiscodeitApplication {
//	static User setupUser(UserService userService) {
//		UserCreateDto request = new UserCreateDto("woody", "woody123@naver.com", null);
//		User user = userService.create(request);
//		return user;
//	}
//
//	static Channel setupChannel(ChannelService channelService, UUID ownerId) {
//		Channel channel = channelService.create("공지", IsPrivate.PUBLIC, ownerId);
//		return channel;
//	}
//
//	static void messageCreateTest(MessageService messageService, Channel channel, User author) {
//		Message message = messageService.create(author.getId(), channel.getId(), "안녕하세요.");
//		System.out.println("메시지 생성: " + message.getContent());
//	}
//	public static void main(String[] args) {
//		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);
//
//		UserService userService = context.getBean(UserService.class);
//		ChannelService channelService = context.getBean(ChannelService.class);
//		MessageService messageService = context.getBean(MessageService.class);
//
//
//		// 셋업
//		User user = setupUser(userService);
//		Channel channel = setupChannel(channelService, user.getId());
//		// 테스트
//		messageCreateTest(messageService, channel, user);
//	}
////    private static void printUserMessage(String userName, List<Message> messages) {
////        if (!messages.isEmpty()) {
////            System.out.println("[" + userName + "님이 보낸 메시지 내역]");
////            messages.forEach(msg
////                    -> System.out.printf("- [%s] %s%n", msg.getChannel().getName(), msg.getContent()));
////        } else {
////            System.out.println(userName + "님이 보낸 메시지가 없습니다.");
////        }
////    }
////
////    private static void printUserChannel(String userName, List<Channel> channels) {
////        if (!channels.isEmpty()) {
////            System.out.println("[" + userName + "님의 채널들]");
////            channels.forEach(ch -> System.out.printf("- %s%n", ch.getName()));
////        } else {
////            System.out.println(userName + "님이 보유하신 채널이 없습니다.");
////        }
////    }
////
////    private static void printChannelMessage(String channelName, List<Message> messages) {
////        if (!messages.isEmpty()) {
////            System.out.println("[" + channelName + " 채널의 대화 내용]");
////            messages.forEach(msg -> System.out.printf("- [%s] %s%n", msg.getSender().getName(), msg.getContent()));
////        } else {
////            System.out.println(channelName + "채널에 대화 내용이 없습니다.");
////        }
////    }
////
////    private static void printSearchedMessage(String channelName, String searchContent, List<Message> messages) {
////        if (!messages.isEmpty()) {
////            System.out.println("[" + channelName + "] 채널에 '" + searchContent + "' 내용이 포함된 메시지]");
////            messages.forEach(msg -> System.out.printf("- [%s] : %s%n", msg.getSender().getName(), msg.getContent()));
////        } else {
////            System.out.println("[" + channelName + "] 채널에 '" + searchContent + "' 내용이 없습니다.");
////        }
////    }
//}
