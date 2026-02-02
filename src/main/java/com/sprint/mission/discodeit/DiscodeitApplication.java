package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.CreateUserRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
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

	public static void main(String[] args) {
//		SpringApplication.run(DiscodeitApplication.class, args);
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class,args);

		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean("basicMessageService", MessageService.class);


		// 셋업
		// 1.사용자 생성
		UserResponse userResponse = setupUser(userService);
		System.out.println("사용자 생성: " + userResponse.getId());
		// 2. 채널 생성
		Channel channel = setupChannel(channelService);
		System.out.println("채널 생성: " + channel.getId());

		// 테스트
		messageCreateTest(messageService, channel, userResponse);
	}

//	static User setupUser(UserService userService) {
//		User user = userService.create("woody", "woody@codeit.com", "woody1234");
//		return user;
//	}

	static UserResponse setupUser(UserService userService) {
		String uniqueId = UUID.randomUUID().toString().substring(0, 8);  // 앞 8자리만
		String username = "user_" + uniqueId;

		CreateUserRequest request = new CreateUserRequest(
				username,
				username + "@codeit.com",
				"password123",
				null
		);

		return userService.create(request);
	}

	static Channel setupChannel(ChannelService channelService) {
		Channel channel = channelService.create(ChannelType.PUBLIC, "공지", "공지 채널입니다.");
		System.out.println("생성된 채널 ID: " + channel.getId());
		return channel;
	}

	static void messageCreateTest(MessageService messageService, Channel channel, UserResponse author) {
		System.out.println("메시지 생성 시도 - 채널ID: " + channel.getId());
		Message message = messageService.create("안녕하세요.", channel.getId(), author.getId());
		System.out.println("메시지 생성: " + message.getId());
		
	}
	
	
}
