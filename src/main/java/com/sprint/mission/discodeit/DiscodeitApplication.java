package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DiscodeitApplication {
	static UserDto.Response setupUser(UserService userService) {
		UserDto.CreateRequest createRequest = new UserDto.CreateRequest(
				"꼬야",
				"kkoya@naver.com",
				"12345678910",
				null
		);
		UserDto.Response response = userService.create(createRequest);
		if (response != null) System.out.println("유저 생성 완료: " + response.username());
		return response;
	}

	static ChannelDto.Response setupChannel(ChannelService channelService) {
		ChannelDto.CreatePublicRequest createPublicRequest = new ChannelDto.CreatePublicRequest(
				"꼬야의 개방 채널",
				"꼬야의 소통방이다!!!"
		);
		ChannelDto.Response response = channelService.createPublicChannel(createPublicRequest);
		if (response != null) System.out.println("채널 생성 완료: " + response.name());
		return response;
	}

	static void messageCreateTest(MessageService messageService, ChannelDto.Response channel, UserDto.Response author) {
		MessageDto.CreateRequest request = new MessageDto.CreateRequest(
				author.id(),
				channel.id(),
				"안냥 난 꼬야입니다!",
				null // 첨부파일은 일단 null로 처리
		);

		MessageDto.Response message = messageService.create(request);
		if (message != null) {
			System.out.println("메시지 생성 성공! ID: " + message.id()) ;
			System.out.println("내용: " + message.content());
		}
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		try {
			UserService userService = context.getBean(UserService.class);
			ChannelService channelService = context.getBean(ChannelService.class);
			MessageService messageService = context.getBean(MessageService.class);

			// 1. 유저 생성 (결과를 userResponse에 저장)
			UserDto.Response userResponse = setupUser(userService);

			// 2. 채널 생성 (결과를 channelResponse에 저장)
			ChannelDto.Response channelResponse = setupChannel(channelService);

			if (userResponse != null && channelResponse != null) {
				// 3. 생성된 유저의 ID와 채널의 ID를 연결
				channelService.addChannelByUserId(channelResponse.id(), userResponse.id());
				System.out.println(userResponse.username() + "님이 채널에 가입했습니다.");

				// 4. 메시지 테스트 시 생성된 유저와 채널 정보를 그대로 넘겨줌
				messageCreateTest(messageService, channelResponse, userResponse);
			}

			System.out.println("현재 등록된 전체 유저 수: " + userService.findAll().size());

		} catch (Exception e) {
			System.err.println("테스트 중 에러 발생: " + e.getMessage());
		}
	}
}
