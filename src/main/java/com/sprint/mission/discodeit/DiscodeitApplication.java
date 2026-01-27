package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DiscodeitApplication {

	static User setupUser(UserService userService) {
		return userService.createUser("woody@codeit.com", "woody1234", "woody", UserStatusType.ONLINE);
	}

	static Channel setupChannel(ChannelService channelService, User owner) {
		return channelService.createChannel("공지", owner.getId(), ChannelType.CHAT);
	}

	static void messageCreateTest(MessageService messageService, Channel channel, User author) {
		Message message = messageService.createMessage("안녕하세요.", author.getId(), channel.getId(), MessageType.CHAT);
		System.out.println("메시지 생성 성공! ID: " + message.getId());
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext context =
				SpringApplication.run(DiscodeitApplication.class, args);

		// 서비스 초기화
		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);

		// 셋업 및 테스트 실행
		try {
			User user = setupUser(userService);
			System.out.println("사용자 셋업 완료: " + user.getNickname());

			Channel channel = setupChannel(channelService, user);
			System.out.println("채널 셋업 완료: " + channel.getChannelName());

			messageCreateTest(messageService, channel, user);

			System.out.println("\n=== 전체 데이터 확인 ===");
			System.out.println("전체 유저 수: " + userService.searchUserAll().size());
			System.out.println("전체 채널 수: " + channelService.searchChannelAll().size());
		} catch (Exception e) {
			System.err.println("테스트 중 오류 발생: " + e.getMessage());
		}
	}

}
