package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.user.LoginRequestDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {
	static UserResponseDto setupUser(UserService userService) {
		return userService.create(
				new UserCreateRequestDto(
						"woody",
						"woody@codeit.com",
						"woody1234",
						null));
	}

	static ChannelResponseDto setupPublicChannel(ChannelService channelService) {
		return channelService.createPublicChannel(
				new PublicChannelCreateRequestDto("공지", "공지 채널입니다.")
		);
	}

	static ChannelResponseDto setupPrivateChannel(ChannelService channelService, List<UUID> userIds) {
		return channelService.createPrivateChannel(
				new PrivateChannelCreateRequestDto(userIds)
		);
	}

	static void messageCreateTest(MessageService messageService, ChannelResponseDto channel, UserResponseDto author) {
		MessageResponseDto message = messageService.create(
				new MessageCreateRequestDto("안녕하세요.", channel.id(), author.id(), null)
		);
		System.out.println("메시지 생성: " + message.id() + " " + message.content() + "in " + channel.name());
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext context =
				SpringApplication.run(DiscodeitApplication.class, args);

		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);
		AuthService authService = context.getBean(AuthService.class);

		// 셋업
		UserResponseDto user1 = setupUser(userService);
		ChannelResponseDto channel1 = setupPublicChannel(channelService);

		List<UUID> userIds = new ArrayList<>();
		userIds.add(user1.id());
		ChannelResponseDto channel2 = setupPrivateChannel(channelService, userIds);
		// 테스트
		messageCreateTest(messageService, channel1, user1);
		messageCreateTest(messageService, channel2, user1);

	}

}
