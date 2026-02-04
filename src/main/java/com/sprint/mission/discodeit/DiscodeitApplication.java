package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDTO;
import com.sprint.mission.discodeit.dto.channel.ChannelCreatePrivateDTO;
import com.sprint.mission.discodeit.dto.channel.ChannelCreatePublicDTO;
import com.sprint.mission.discodeit.dto.channel.ChannelDTO;
import com.sprint.mission.discodeit.dto.message.MessageCreateDTO;
import com.sprint.mission.discodeit.dto.message.MessageDTO;
import com.sprint.mission.discodeit.dto.user.UserCreateDTO;
import com.sprint.mission.discodeit.dto.user.UserDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class);
		BasicChannelService channelService = context.getBean(BasicChannelService.class);
		BasicUserService userService = context.getBean(BasicUserService.class);
		BasicMessageService messageService = context.getBean(BasicMessageService.class);

		System.out.println(userService.findAll());

		UserDTO userDTO = userCreateTest(userService);
		ChannelDTO channelDTO = channelCreateTest(channelService, userService);
		MessageDTO messageDTO = messageCreateTest(messageService, channelDTO.channelId(), userDTO.userId());

		System.out.println(userDTO);
		System.out.println(channelDTO);
		System.out.println(messageDTO);
	}

	public static UserDTO userCreateTest(UserService userService) {
		byte[] bytes = {};
		UserCreateDTO userCreateDTO = new UserCreateDTO(
				"테스트용 유저",
				"test@emial.com",
				"12345",
				new BinaryContentDTO("테스트 파일1", ".txt", bytes)
		);
		UUID userId = userService.create(userCreateDTO).userId();
		return userService.findById(userId);
	}

	public static ChannelDTO channelCreateTest(ChannelService channelService, UserService userService) {
		List<UUID> userList = new ArrayList<>();
		for (UserDTO userDTO : userService.findAll()) {
			userList.add(userDTO.userId());
		}
		ChannelCreatePrivateDTO channelCreatePrivateDTO = new ChannelCreatePrivateDTO(
				userList,
				new ArrayList<>()
		);

		ChannelCreatePublicDTO channelCreatePublicDTO = new ChannelCreatePublicDTO(
				"테스트용 공용 채널",
				"테스트용 공용 채널입니다.",
				userList,
				new ArrayList<>()
		);

		UUID privateChannelId = channelService.createPrivateChannel(channelCreatePrivateDTO).channelId();
		UUID publicChannelId = channelService.createPublicChannel(channelCreatePublicDTO).channelId();

		return channelService.findById(publicChannelId);
	}

	public static MessageDTO messageCreateTest(MessageService messageService, UUID channelId, UUID userId) {
		MessageCreateDTO messageCreateDTO = new MessageCreateDTO(
				"테스트용 메시지 입니다.",
				channelId,
				userId,
				new ArrayList<>()
		);
		UUID messageId = messageService.create(messageCreateDTO).messageId();

		return messageService.findById(messageId);
	}
}
