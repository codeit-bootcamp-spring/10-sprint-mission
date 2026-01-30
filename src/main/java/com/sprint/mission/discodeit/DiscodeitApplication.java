package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
//import com.sprint.mission.discodeit.view.BasicChannelMessageView; // 동작확인 임시제거
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import java.sql.SQLOutput;
import java.util.Optional;

@SpringBootApplication
public class DiscodeitApplication {
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);
		FileUserRepository fileUserRepository = context.getBean(FileUserRepository.class);
		FileChannelRepository fileChannelRepository = context.getBean(FileChannelRepository.class);
		FileMessageRepository fileMessageRepository = context.getBean(FileMessageRepository.class);

		fileUserRepository.resetUserFile();
		fileChannelRepository.resetChannelFile();
		fileMessageRepository.resetMessageFile();

		UserResponse user = userService.create(
				new UserCreateRequest(
						"김코딩",
						"kim@codeit.com",
						"123123",
						null
				)
		);

		System.out.println(user);


	}

}