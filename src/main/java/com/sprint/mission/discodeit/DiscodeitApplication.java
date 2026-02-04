package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.CreateUserRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {
//		SpringApplication.run(DiscodeitApplication.class, args);
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class,args);

		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean("basicMessageService", MessageService.class);

		// 셋업
		// 1.사용자 생성
		UserResponse userResponse = setupUser(userService);
		System.out.println("사용자 생성: " + userResponse.getId());
		// 2. 채널 생성
		Channel channel = setupChannel(channelService);
		System.out.println("채널 생성: " + channel.getId());

		// 테스트
		messageCreateTest(messageService, channel, userResponse);
		messageCreateWithAttachmentsTest(messageService,channel,userResponse);
		messageFindAllByChannelTest(messageService, channel);
		messageUpdateTest(messageService, channel, userResponse);


	}

//	static User setupUser(UserService userService) {
//		User user = userService.create("woody", "woody@codeit.com", "woody1234");
//		return user;
//	}

	static UserResponse setupUser(UserService userService) {
		String uniqueId = UUID.randomUUID().toString().substring(0, 8);  // 앞 8자리만
		String username = "user_" + uniqueId;

		CreateUserRequest request = new CreateUserRequest(
				username,
				username + "@codeit.com",
				"password123",
				null
		);

		return userService.create(request);
	}

	static Channel setupChannel(ChannelService channelService) {
		Channel channel = channelService.create(ChannelType.PUBLIC, "공지", "공지 채널입니다.");
		System.out.println("생성된 채널 ID: " + channel.getId());
		return channel;
	}

//	static void messageCreateTest(MessageService messageService, Channel channel, UserResponse author) {
//		System.out.println("메시지 생성 시도 - 채널ID: " + channel.getId());
//		Message message = messageService.create("안녕하세요.", channel.getId(), author.getId());
//		System.out.println("메시지 생성: " + message.getId());
//
//	}

	//고도화 된 메시지 생성 테스트
	static void messageCreateTest(MessageService messageService, Channel channel, UserResponse author) {
		System.out.println("\n===== 메시지 생성 테스트 (첨부파일 없음) =====");

		CreateMessageRequest request = new CreateMessageRequest(

				channel.getId(),
				author.getId(),
				"안녕하세요! 첫 번째 메시지입니다.",
				null  // 첨부파일 없음
		);

		MessageResponse response = messageService.create(request);

		System.out.println("✅ 메시지 생성 완료!");
		System.out.println("  - 메시지 ID: " + response.getId());
		System.out.println("  - 내용: " + response.getContent());
		System.out.println("  - 작성자: " + response.getAuthor().getUsername());
		System.out.println("  - 채널 ID: " + response.getChannelId());
		System.out.println("  - 첨부파일 개수: " + (response.getAttachmentIds() != null ? response.getAttachmentIds().size() : 0));
	}

	// 🆕 메시지 생성 테스트 (첨부파일 포함)
	static void messageCreateWithAttachmentsTest(MessageService messageService, Channel channel, UserResponse author) {
		System.out.println("\n===== 메시지 생성 테스트 (첨부파일 포함) =====");

		// 첨부파일 ID 리스트 (실제로는 BinaryContent를 먼저 생성해야 함)
		List<UUID> attachmentIds = new ArrayList<>();
		// attachmentIds.add(UUID.randomUUID());  // 실제 BinaryContent ID 필요

		CreateMessageRequest request = new CreateMessageRequest(
				channel.getId(),
				author.getId(),
				"두 번째 메시지입니다. 첨부파일이 있습니다.",
				attachmentIds  // 빈 리스트
		);

		MessageResponse response = messageService.create(request);

		System.out.println("✅ 메시지 생성 완료!");
		System.out.println("  - 메시지 ID: " + response.getId());
		System.out.println("  - 내용: " + response.getContent());
		System.out.println("  - 첨부파일 개수: " + response.getAttachmentIds().size());
	}

	// 🆕 특정 채널의 메시지 목록 조회 테스트
	static void messageFindAllByChannelTest(MessageService messageService, Channel channel) {
		System.out.println("\n===== 채널별 메시지 조회 테스트 =====");

		List<MessageResponse> messages = messageService.findAllByChannelId(channel.getId());

		System.out.println("✅ 메시지 조회 완료! (총 " + messages.size() + "개)");
		for (int i = 0; i < messages.size(); i++) {
			MessageResponse msg = messages.get(i);
			System.out.println("  [" + (i + 1) + "] " + msg.getContent() + " (작성자: " + msg.getAuthor().getUsername() + ")");
		}
	}


	//  메시지 수정 테스트
	static void messageUpdateTest(MessageService messageService, Channel channel, UserResponse author) {
		System.out.println("\n===== 메시지 수정 테스트 =====");

		// 먼저 메시지 생성
		CreateMessageRequest createRequest = new CreateMessageRequest(
				channel.getId(),
				author.getId(),
				"수정 전 메시지",
				null
		);
		MessageResponse created = messageService.create(createRequest);
		System.out.println("생성된 메시지: " + created.getContent());

		// 메시지 수정
		UpdateMessageRequest updateRequest = new UpdateMessageRequest(
				created.getId(),
				"다음으로 넘어가기!"
		);
		MessageResponse updated = messageService.update(updateRequest);

		System.out.println("✅ 메시지 수정 완료!");
		System.out.println("  - 수정 전: 수정 전 메시지");
		System.out.println("  - 수정 후: " + updated.getContent());
	}
}
