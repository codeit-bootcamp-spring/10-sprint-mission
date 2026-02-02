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

		// =========================
		// Service Bean 가져오기
		// =========================
		var userService = context.getBean(com.sprint.mission.discodeit.service.UserService.class);
		var channelService = context.getBean(com.sprint.mission.discodeit.service.ChannelService.class);
		var messageService = context.getBean(com.sprint.mission.discodeit.service.MessageService.class);
		var readStatusService = context.getBean(com.sprint.mission.discodeit.service.ReadStatusService.class);
		var userStatusService = context.getBean(com.sprint.mission.discodeit.service.UserStatusService.class);

		// =========================
		// 0) Repository 구현체 확인
		// =========================
		System.out.println("===== Repository Bean Check =====");
		System.out.println("UserRepository         = " + context.getBean(com.sprint.mission.discodeit.repository.UserRepository.class).getClass());
		System.out.println("ChannelRepository       = " + context.getBean(com.sprint.mission.discodeit.repository.ChannelRepository.class).getClass());
		System.out.println("MessageRepository       = " + context.getBean(com.sprint.mission.discodeit.repository.MessageRepository.class).getClass());
		System.out.println("ReadStatusRepository    = " + context.getBean(com.sprint.mission.discodeit.repository.ReadStatusRepository.class).getClass());
		System.out.println("UserStatusRepository    = " + context.getBean(com.sprint.mission.discodeit.repository.UserStatusRepository.class).getClass());
		System.out.println("=================================\n");

		// =========================
		// 1) UserService 테스트
		// - 유저 생성 시 UserStatus 자동 생성됨
		// =========================
		System.out.println("===== UserService Test =====");

		var alice = userService.createUser(
				new CreateUserRequestDTO(
						"alice@gmail.com",
						"Alice",
						"1234",
						null
				)
		);

		var bob = userService.createUser(
				new CreateUserRequestDTO(
						"bob@gmail.com",
						"Bob",
						"1234",
						null
				)
		);

		UUID aliceId = alice.userId();
		UUID bobId = bob.userId();

		System.out.println("유저 생성 후(findAll): " + userService.findAll());
		System.out.println("Alice userStatus(auto): " + userService.findByUserId(aliceId).userStatus());
		System.out.println("Bob userStatus(auto): " + userService.findByUserId(bobId).userStatus());

		// 유저 수정
		userService.updateUser(
				new UpdateUserRequestDTO(
						aliceId,
						"Charlie",
						UserStatusType.DO_NOT_DISTURB,
						null
				)
		);
		System.out.println("유저 수정 후(findByUserId): " + userService.findByUserId(aliceId));
		System.out.println();

		// =========================
		// 2) ChannelService 테스트
		// - private 채널 생성 시 ReadStatus 자동 생성
		// - public 채널은 join 해야 ReadStatus 생성
		// =========================
		System.out.println("===== ChannelService Test =====");

		// (A) 비공개 채널 생성: joinedUserIds만 받음
		var privateChannel = channelService.createPrivateChannel(
				new CreatePrivateChannelRequestDTO(
						List.of(aliceId, bobId)
				)
		);
		UUID privateChannelId = privateChannel.channelId();

		System.out.println("비공개 채널 생성 후(findByChannelId): " + channelService.findByChannelId(privateChannelId));

		// ✅ 비공개 채널은 자동 ReadStatus 생성되어야 함
		System.out.println("비공개 채널 생성 직후 Alice ReadStatus: " + readStatusService.findAllByUserId(aliceId));
		System.out.println("비공개 채널 생성 직후 Bob ReadStatus: " + readStatusService.findAllByUserId(bobId));

		// (B) 공개 채널 생성
		var publicChannel = channelService.createPublicChannel(
				new CreatePublicChannelRequestDTO(
						"공지 채널",
						"공지 올리는 곳"
				)
		);
		UUID publicChannelId = publicChannel.channelId();

		System.out.println("공개 채널 생성 후(findByChannelId): " + channelService.findByChannelId(publicChannelId));

		// ✅ join 전: ReadStatus 없어야 함(목록 출력로 확인)
		System.out.println("공개 채널 join 전 Bob ReadStatus: " + readStatusService.findAllByUserId(bobId));

		// join 후: ReadStatus 생성되어야 함
		channelService.joinChannel(publicChannelId, bobId);
		System.out.println("공개 채널 join 후 Bob ReadStatus: " + readStatusService.findAllByUserId(bobId));

		// 채널 수정
		channelService.updateChannel(
				new UpdateChannelRequestDTO(
						publicChannelId,
						"공지사항 채널",
						"공지/안내",
						null
				)
		);
		System.out.println("공개 채널 수정 후(findByChannelId): " + channelService.findByChannelId(publicChannelId));
		System.out.println();

		// =========================
		// 3) MessageService 테스트 (공개 채널에 메시지)
		// =========================
		System.out.println("===== MessageService Test =====");

		var m1 = messageService.createMessage(
				new CreateMessageRequestDTO(
						"Hello",
						bobId,
						publicChannelId,
						List.of()
				)
		);

		var m2 = messageService.createMessage(
				new CreateMessageRequestDTO(
						"공지 확인 부탁드립니다.",
						bobId,
						publicChannelId,
						List.of()
				)
		);

		UUID m1Id = m1.messageId();
		UUID m2Id = m2.messageId();

		System.out.println("채널 메시지(findAllByChannelId): " + messageService.findAllByChannelId(publicChannelId));
		System.out.println("Bob 메시지(findAllByUserId): " + messageService.findAllByUserId(bobId));

		// 메시지 수정
		messageService.updateMessage(
				new UpdateMessageRequestDTO(
						m1Id,
						"안녕하세요! 수정된 메시지입니다."
				)
		);
		System.out.println("m1 수정 후(findByMessageId): " + messageService.findByMessageId(m1Id));

		// 메시지 삭제
		messageService.deleteMessage(m2Id);
		System.out.println("m2 삭제 후(채널 메시지): " + messageService.findAllByChannelId(publicChannelId));
		System.out.println();

		// =========================
		// 4) ReadStatusService 테스트
		// - 공개 채널 join 후 생성된 ReadStatus 찾아서 update
		// =========================
		System.out.println("===== ReadStatusService Test =====");

		ReadStatusResponseDTO publicRs = readStatusService.findAllByUserId(bobId).stream()
				.filter(rs -> rs.channelId().equals(publicChannelId))
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("공개 채널 join 후 ReadStatus가 생성되지 않았습니다."));

		UUID rsId = publicRs.statusId();

		readStatusService.updateReadStatus(
				new UpdateReadStatusRequestDTO(
						rsId,
						Instant.now()
				)
		);

		System.out.println("ReadStatus 수정 후(findById): " + readStatusService.findById(rsId));
		System.out.println();

		// =========================
		// 5) UserStatusService 테스트
		// - 유저 생성 시 자동 생성되므로 update만 확인
		// =========================
		System.out.println("===== UserStatusService Test =====");

		userStatusService.updateStatusByUserId(
				bobId,
				new UpdateStatusByUserIdRequestDTO(
						bobId,
						UserStatusType.DO_NOT_DISTURB
				)
		);

		System.out.println("Bob status 변경 후(UserService로 확인): " + userService.findByUserId(bobId).userStatus());
		System.out.println();

		// =========================
		// 6) Cleanup (선택)
		// =========================
		System.out.println("===== Cleanup =====");

		channelService.deleteChannel(publicChannelId);
		channelService.deleteChannel(privateChannelId);

		userService.deleteUser(aliceId);

		System.out.println("최종 유저 목록(findAll): " + userService.findAll());
		System.out.println("\n===== Test Done =====");
	}
}
