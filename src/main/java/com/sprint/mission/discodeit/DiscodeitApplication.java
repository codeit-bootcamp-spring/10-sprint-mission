package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.channel.CreatePrivateChannelRequestDTO;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequestDTO;
import com.sprint.mission.discodeit.dto.channel.UpdateChannelRequestDTO;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequestDTO;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequestDTO;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDTO;
import com.sprint.mission.discodeit.dto.readstatus.UpdateReadStatusRequestDTO;
import com.sprint.mission.discodeit.dto.user.CreateUserRequestDTO;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequestDTO;
import com.sprint.mission.discodeit.dto.userstatus.UpdateStatusByUserIdRequestDTO;
import com.sprint.mission.discodeit.entity.UserStatusType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context =
				SpringApplication.run(DiscodeitApplication.class, args);

	}
}
