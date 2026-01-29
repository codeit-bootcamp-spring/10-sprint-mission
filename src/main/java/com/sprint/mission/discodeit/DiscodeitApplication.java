package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class DiscodeitApplication {
	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);
		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);

		loadUser(userService);
		loadChannel(channelService);
		loadMessage(messageService);

		// 셋업
//		try {
//			UserInfoDto user = setUser(userService);
//			System.out.println("user = " + user);
//		} catch (Exception e) {
//			System.out.println(e);
//		}

//		Channel channel = setChannel(channelService, user);
//		// 테스트
//		messageCreateTest(messageService, channel, user);

//		loadUser(userService);
//		loadChannel(channelService);
//		loadMessage(messageService);
	}

	static UserResponse setUser(UserService userService) {
		UserCreateRequest userCreateRequest = new UserCreateRequest("chung@codeit.com", "chung", "chungNick", "chung", "20000401", null);
		return userService.createUser(userCreateRequest);
	}
	static Channel setChannel(ChannelService channelService, User user) {
		Channel channel = channelService.createChannel(user.getId(), ChannelType.PUBLIC, "c1", "c1");
		return channel;
	}
	static void messageCreateTest(MessageService messageService, Channel channel, User author) {
		Message message = messageService.createMessage(channel.getId(), author.getId(), "m1");
		System.out.println("메세지 생성: " + message);

	}

	static void loadUser(UserService userService) {
		System.out.println("\n========== 유저 Load ==========");
		System.out.println("생성된 전체 user ID = " + userService.findAllUsers().stream().map(user -> user.userName() + "(" + user.email() + "): " + user.userId()).toList());
	}
	static void loadChannel(ChannelService channelService) {
		System.out.println("\n========== 채널 Load ==========");
		System.out.println("생성된 전체 channel ID = " + channelService.findAllChannels().stream().map(channel -> channel.getChannelName() + "(owner=" + channel.getOwner().getId() + "): " + channel.getId()).toList());
	}
	static void loadMessage(MessageService messageService) {
		System.out.println("\n========== 메세지 Load ==========");
		System.out.println("생성된 전체 message ID = " + messageService.findAllMessages().stream().map(message -> message.getId() + "(channel=" + message.getChannel().getId() + "): " + message.getContent()).toList());
	}

}
