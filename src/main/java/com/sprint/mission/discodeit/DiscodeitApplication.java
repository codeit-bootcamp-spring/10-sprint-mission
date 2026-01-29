package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {
	static User setupUser(UserService userService) {
		User user = userService.create("꼬야", "kkoyap@codeit.com", "kko123456", UUID.randomUUID());
		System.out.println("유저 생성 완료: " + user.getUsername());
		return user;
	}

	static Channel setupChannel(ChannelService channelService) {
		Channel channel = channelService.create("꼬야의 채널", "꼬야의 채널 입니다." , ChannelType.PRIVATE);
		System.out.println("채널 생성 완료: " + channel.getName());
		return channel;
	}

	static void messageCreateTest(MessageService messageService, Channel channel, User author) {
		Message message = messageService.create(author.getId(), channel.getId(), "난 호야입니다~");
		System.out.println("메시지 생성 성공! ID: " + message.getId());
		System.out.println("내용: " + message.getContent());
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext context =
				SpringApplication.run(DiscodeitApplication.class, args);
		try {
			UserService userService = context.getBean(UserService.class);
			ChannelService channelService = context.getBean(ChannelService.class);
			MessageService messageService = context.getBean(MessageService.class);

			User user = setupUser(userService);
			Channel channel = setupChannel(channelService);

			channelService.addChannelByUserId(channel.getId(), user.getId());
			System.out.println("채널 가입 완료");

			messageCreateTest(messageService, channel, user);

			System.out.println("현재 등록된 전체 유저 수: " + userService.findAll().size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
