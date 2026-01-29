package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);
		BasicChannelService channelService = context.getBean(BasicChannelService.class, args);
		BasicUserService userService = context.getBean(BasicUserService.class, args);
		BasicMessageService messageService = context.getBean(BasicMessageService.class, args);

		System.out.println(userService.findAll());

	}

}
