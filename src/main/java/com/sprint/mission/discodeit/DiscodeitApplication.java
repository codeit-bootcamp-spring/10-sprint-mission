package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentDTO;
import com.sprint.mission.discodeit.dto.channeldto.ChannelResponseDTO;
import com.sprint.mission.discodeit.dto.channeldto.PublicChannelCreateDTO;
import com.sprint.mission.discodeit.dto.channeldto.UpdateUpdateRequestDTO;
import com.sprint.mission.discodeit.dto.messagedto.MessageCreateRequestDTO;
import com.sprint.mission.discodeit.dto.messagedto.MessageResponseDTO;
import com.sprint.mission.discodeit.dto.messagedto.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.dto.userdto.UserCreateRequestDTO;
import com.sprint.mission.discodeit.dto.userdto.UserResponseDTO;
import com.sprint.mission.discodeit.dto.userdto.UserUpdateDTO;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {
	private static final Logger log = LoggerFactory.getLogger(DiscodeitApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(DiscodeitApplication.class, args);
	}

	@Bean
	CommandLineRunner smokeTestRunner(
			UserService userService,
			ChannelService channelService,
			MessageService messageService,
			ChannelRepository channelRepository
	) {
		return args -> {
			if (!Boolean.parseBoolean(System.getProperty("discodeit.smokeTest", "true"))) {
				return;
			}

			runSmokeTest(userService, channelService, messageService, channelRepository);
		};
	}

	private static void runSmokeTest(
			UserService userService,
			ChannelService channelService,
			MessageService messageService,
			ChannelRepository channelRepository
	) {
		log.info("SmokeTest: start");

		log.info("SmokeTest: create user alice");
		UserResponseDTO alice = userService.create(
				new UserCreateRequestDTO(
						"alice",
						"alice@example.com",
						"pass1234",
						new BinaryContentDTO("image/png", new byte[]{1, 2, 3})
				)
		);

		log.info("SmokeTest: create user bob");
		UserResponseDTO bob = userService.create(
				new UserCreateRequestDTO(
						"bob",
						"bob@example.com",
						"pass1234",
						null
				)
		);

		log.info("SmokeTest: create public channel general");
		ChannelResponseDTO publicChannelDTO = channelService.createPublicChannel(
				new PublicChannelCreateDTO(ChannelType.PUBLIC, "general", "General channel")
		);

		log.info("SmokeTest: resolve channelId for general");
		UUID channelId = publicChannelDTO.id();

		log.info("SmokeTest: join channel (alice, bob)");
		channelService.join(channelId, alice.id());
		channelService.join(channelId, bob.id());

		log.info("SmokeTest: create message");
		MessageResponseDTO message = messageService.create(
				new MessageCreateRequestDTO(
						"hello world",
						channelId,
						alice.id(),
						List.of()
				)
		);

		log.info("SmokeTest: update message");
		messageService.update(
				new MessageUpdateRequestDto(
						message.id(),
						"hello again",
						List.of(new BinaryContentDTO("text/plain", new byte[]{9})),
						List.of()
				)
		);

		log.info("SmokeTest: list messages by channel");
		messageService.findallByChannelId(channelId);

		log.info("SmokeTest: update channel");
		channelService.update(
				new UpdateUpdateRequestDTO(channelId, "general-updated", "General channel updated")
		);

		log.info("SmokeTest: update user alice");
		userService.update(
				new UserUpdateDTO(
						alice.id(),
						"alice",
						"alice2@example.com",
						"pass5678",
						null
				)
		);

		log.info("SmokeTest: delete message");
		messageService.delete(message.id());
		log.info("SmokeTest: delete channel");
		channelService.delete(channelId);
		log.info("SmokeTest: delete alice & bob");
		userService.delete(alice.id());
		userService.delete(bob.id());

		log.info("SmokeTest: done");
	}
}
