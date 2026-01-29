package com.sprint.mission.discodeit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.sprint.mission.discodeit.dto.MessagePostDTO;
import com.sprint.mission.discodeit.dto.PublicChannelPostDTO;
import com.sprint.mission.discodeit.dto.UserPostDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

@SpringBootApplication
public class DiscodeitApplication {
	static User setupUser(UserService userService) {
		User user = userService.create(
			new UserPostDTO(
				null,
				"woody",
				"woody1234",
				"woody@codeit.com",
				"000-0000-0000",
				"qwer1234"
			)
		);

		return user;
	}

	static Channel setupChannel(ChannelService channelService) {
		Channel channel = channelService.createPublicChannel(new PublicChannelPostDTO(
			"공지", "공지용 채널입니다."
		));
		return channel;
	}

	static void messageCreateTest(MessageService messageService, Channel channel, User author) {
		Message message = messageService.create(
			new MessagePostDTO(
				author.getId(),
				null, // ...
				"안녕하세요.",
				null
			)
		);
		System.out.println("메시지 생성: " + message.getId());
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		// getBean으로 등록된 서비스 객체 가져오기
		UserService userService = context.getBean(BasicUserService.class);
		ChannelService channelService = context.getBean(BasicChannelService.class);
		MessageService messageService = context.getBean(BasicMessageService.class);

		// 셋업
		User user = setupUser(userService);
		Channel channel = setupChannel(channelService);
		channelService.addUser(channel.getId(), user.getId());

		// 테스트
		messageCreateTest(messageService, channel, user);

	}

}
