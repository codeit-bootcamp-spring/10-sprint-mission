package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.channel.ChannelCreatePublicRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
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

import java.lang.module.Configuration;
import java.util.List;

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);
		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);

		try {
			System.out.println("\n" + "=".repeat(40));
			System.out.println("🚀 디스코드잇 통합 테스트 시작");
			System.out.println("=".repeat(40));

			// 2. 유저 생성 테스트 (UserCreateRequest record 사용)
			UserCreateRequest userRequest = new UserCreateRequest(
					"woody",
					"woody@codeit.com",
					"password123",
					null
			);
			UserResponse userResponse = userService.create(userRequest);
			System.out.println("✅ [유저 생성] 이름: " + userResponse.userName() + " (ID: " + userResponse.userId() + ")");

			// 3. 채널 생성 테스트 (ChannelCreatePublicRequest record 사용)
			ChannelCreatePublicRequest channelRequest = new ChannelCreatePublicRequest(
					ChannelType.PUBLIC,
					"공지사항",
					"필독 공지사항 채널입니다."
			);
			Channel channel = channelService.createPublic(channelRequest);
			System.out.println("✅ [채널 생성] 이름: " + channel.getName() + " (ID: " + channel.getId() + ")");

			// 4. 메시지 발송 테스트 (MessageCreateRequest record 사용)
			MessageCreateRequest messageRequest = new MessageCreateRequest(
					"안녕하세요! 심화 요구사항 테스트 메시지입니다.",
					channel.getId(),
					userResponse.userId(),
					null
			);
			Message message = messageService.create(messageRequest);
			System.out.println("✅ [메시지 발송] 내용: " + message.getContent());

			// 5. 데이터 조회 테스트 (저장소에서 다시 읽어오기)
			List<Message> messages = messageService.findallByChannelId(channel.getId());
			System.out.println("\n🔍 [데이터 조회 결과]");
			System.out.println("- 채널 내 메시지 개수: " + messages.size());
			if (!messages.isEmpty()) {
				System.out.println("- 마지막 메시지 내용: " + messages.get(messages.size() - 1).getContent());
			}

			System.out.println("\n" + "=".repeat(40));
			System.out.println("✨ 모든 테스트가 성공적으로 완료되었습니다!");
			System.out.println("=".repeat(40) + "\n");

		} catch (Exception e) {
			System.err.println("\n❌ 테스트 중 에러 발생!");
			e.printStackTrace();
		}



		System.out.println("충돌확인용 코드");

	}

}
