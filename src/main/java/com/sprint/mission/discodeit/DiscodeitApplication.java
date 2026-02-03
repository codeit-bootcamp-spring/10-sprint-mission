package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;
import java.util.UUID;

import static com.sprint.mission.discodeit.entity.ChannelType.TEXT;

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {

		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);


		System.out.println("========= [1. User 고도화 테스트] =========");
		// 등록
		UserCreateRequest userReq = new UserCreateRequest("김철수", "철수", "kcs@example.com", "1234", null);
		UserResponse user1 = userService.createUser(userReq);
		System.out.println("[등록] user1 계정 및 UserStatus 생성 완료");

		// 시간 타입(Instant) 확인
		System.out.println("[시간 확인] 생성 시간 타입: " + user1.createdAt().getClass().getSimpleName());

		// 온라인 상태 확인
		System.out.println("[상태 확인] 현재 온라인 여부: " + user1.isOnline());


		System.out.println("\n========= [2. Channel 고도화 테스트] =========");
		// PRIVATE 채널 생성
		List<UUID> memberIds = List.of(user1.id());
		PrivateChannelCreateRequest pReq = new PrivateChannelCreateRequest(memberIds, TEXT);

		ChannelResponse pChannel = channelService.createPrivateChannel(pReq);;
		System.out.println("[등록] PRIVATE 채널 생성 완료");


		System.out.println("\n========= [3. Message 고도화 테스트] =========");
		// 다중 첨부파일 메시지 생성
		BinaryContentRepository contentRepo = context.getBean(BinaryContentRepository.class);
		BinaryContent realFile = contentRepo.save(new BinaryContent("test_image.png", new byte[]{1, 2, 3}));

		List<UUID> attachmentIds = List.of(realFile.getId());

		MessageCreateRequest mReq = new MessageCreateRequest("첨부파일 테스트", user1.id(), pChannel.id(), attachmentIds);
		MessageResponse msg = messageService.create(mReq);
		System.out.println("[등록] 메시지 생성 완료. 첨부파일 수: " + msg.attachmentIds().size());


		System.out.println("\n========= [4. 연쇄 삭제 테스트] =========");
		// 메시지 삭제 시 첨부파일 삭제 확인
		messageService.deleteById(msg.id());
		System.out.println("[삭제] 메시지 삭제 완료 (연쇄 삭제 로직 실행됨)");

		// 채널 삭제 시 메시지/ReadStatus 삭제 확인
		channelService.deleteByChannelId(pChannel.id());
		System.out.println("[삭제] 채널 삭제 완료 (연쇄 삭제 로직 실행됨)");

		// 유저 삭제 시 UserStatus/BinaryContent 삭제 확인
		userService.deleteUser(user1.id());
		System.out.println("[삭제] 유저 삭제 완료 (연쇄 삭제 로직 실행됨)");

		System.out.println("\n===== 모든 고도화 서비스 테스트 완료 =====");
	}
}