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

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {
//		SpringApplication.run(DiscodeitApplication.class, args);
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class,args);

		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean("basicMessageService", MessageService.class);


		// 셋업
		// 1.사용자 생성
		User user = setupUser(userService);
		System.out.println("사용자 생성: " + user.getId());
		// 2. 채널 생성
		Channel channel = setupChannel(channelService);
		System.out.println("채널 생성: " + channel.getId());

		// 테스트
		messageCreateTest(messageService, channel, user);
	}

	static User setupUser(UserService userService) {
		User user = userService.create("woody", "woody@codeit.com", "woody1234");
		return user;
	}

	static Channel setupChannel(ChannelService channelService) {
		Channel channel = channelService.create(ChannelType.PUBLIC, "공지", "공지 채널입니다.");
		System.out.println("생성된 채널 ID: " + channel.getId());
		return channel;
	}

	static void messageCreateTest(MessageService messageService, Channel channel, User author) {
		System.out.println("메시지 생성 시도 - 채널ID: " + channel.getId());
		Message message = messageService.create("안녕하세요.", channel.getId(), author.getId());
		System.out.println("메시지 생성: " + message.getId());
		
	}
	
	
}
