package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.auth.LoginResponse;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.exception.CanNotLoginException;
import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.DuplicationEmailException;
import com.sprint.mission.discodeit.exception.DuplicationReadStatusException;
import com.sprint.mission.discodeit.repository.file.FileBinaryContentRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileReadStatusRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@SpringBootApplication
public class DiscodeitApplication {
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		// file 모드일 때만 존재하는 빈들이라, 있을 때만 reset
//		resetAllFilesIfPresent(context);

		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);
		ReadStatusService readStatusService = context.getBean(ReadStatusService.class);
		AuthService authService = context.getBean(AuthService.class);
		BinaryContentService binaryContentService = context.getBean(BinaryContentService.class);

		// 유저 7명 생성 + 다건 조회 출력 + 중복 email 실패 확인
		UserResponse kim = userService.create(new UserCreateRequest("김코딩", "Kim@discodeit.com", "1234", null));
		UserResponse lee = userService.create(new UserCreateRequest("이코딩", "Lee@discodeit.com", "1234", null));
		UserResponse park = userService.create(new UserCreateRequest("박코딩", "Park@discodeit.com", "1234", null));
		UserResponse choi = userService.create(new UserCreateRequest("최코딩", "Choi@discodeit.com", "1234", null));
		UserResponse jung = userService.create(new UserCreateRequest("정코딩", "Jung@discodeit.com", "1234", null));
		UserResponse oh = userService.create(new UserCreateRequest("오코딩", "Oh@discodeit.com", "1234", null));
		UserResponse han = userService.create(new UserCreateRequest("한코딩", "Han@discodeit.com", "1234", null));

		UUID kimId = kim.id();
		UUID leeId = lee.id();
		UUID parkId = park.id();
		UUID choiId = choi.id();
		UUID jungId = jung.id();
		UUID ohId = oh.id();
		UUID hanId = han.id();

		System.out.println("\n유저 다건 조회");
		for (UserResponse u : userService.findAll()) {
			printUser(u);
		}

		// 참가자 출력 id -> userName
		Map<UUID, String> userNameById = userService.findAll().stream()
				.collect(Collectors.toMap(UserResponse::id, UserResponse::userName));

		System.out.println("\n[검증] 중복 email 생성 실패 확인");
		try {
			userService.create(new UserCreateRequest("김코딩2", "Kim@discodeit.com", "9999", null));
			throw new RuntimeException("중복 email 생성이 허용되면 안 됩니다.");
		} catch (DuplicationEmailException expected) {
			System.out.println("중복 email 생성 실패 확인: " + expected.getClass().getSimpleName());
		}

		// 로그인 검증
		System.out.println("\n로그인(Auth) 검증");
		try {
			LoginResponse okLogin = authService.login(new LoginRequest("김코딩", "1234"));
			System.out.println("로그인 성공: id=" + okLogin.id() + ", name=" + okLogin.userName() + ", email=" + okLogin.email());
		} catch (CanNotLoginException e) {
			System.out.println("로그인 성공 케이스에서 실패: " + e.getClass().getSimpleName());
		}

		try {
			authService.login(new LoginRequest("김코딩", "틀린비번"));
			System.out.println("로그인 실패 케이스가 성공하면 안 됩니다.");
		} catch (CanNotLoginException e) {
			System.out.println("로그인 실패 확인: " + e.getClass().getSimpleName());
		}

		// PUBLIC 채널 3개 이상 생성
		System.out.println("\nPUBLIC 채널 생성");

		UUID public1Id = channelService.createPublic(new PublicChannelCreateRequest("public-1", "공용1"));
		UUID public2Id = channelService.createPublic(new PublicChannelCreateRequest("public-2", "공용2"));
		UUID public3Id = channelService.createPublic(new PublicChannelCreateRequest("public-3", "공용3"));

		ChannelResponse public1 = channelService.find(public1Id);
		ChannelResponse public2 = channelService.find(public2Id);
		ChannelResponse public3 = channelService.find(public3Id);

		printChannel(public1, userNameById);
		printChannel(public2, userNameById);
		printChannel(public3, userNameById);

		// PUBLIC 채널에 김/이/박 참가시키기 + 참가자 출력
		System.out.println("\n(추가 검증) PUBLIC 채널 참가자 추가/확인");
		channelService.addUserInChannel(public1Id, kimId);
		channelService.addUserInChannel(public2Id, leeId);
		channelService.addUserInChannel(public3Id, parkId);

		System.out.println("public-1 participants=" +
				channelService.findAllUserInChannel(public1Id).stream()
						.map(id -> userNameById.getOrDefault(id, "unknown(" + id + ")"))
						.toList()
		);
		System.out.println("public-2 participants=" +
				channelService.findAllUserInChannel(public2Id).stream()
						.map(id -> userNameById.getOrDefault(id, "unknown(" + id + ")"))
						.toList()
		);
		System.out.println("public-3 participants=" +
				channelService.findAllUserInChannel(public3Id).stream()
						.map(id -> userNameById.getOrDefault(id, "unknown(" + id + ")"))
						.toList()
		);

		// PRIVATE 채널 3개 이상 생성 + ReadStatus 자동 생성 검증
		System.out.println("\nPRIVATE 채널 생성");

		UUID private1Id = channelService.createPrivate(new PrivateChannelCreateRequest(List.of(kimId, leeId, parkId)));
		UUID private2Id = channelService.createPrivate(new PrivateChannelCreateRequest(List.of(choiId, jungId, ohId)));
		UUID private3Id = channelService.createPrivate(new PrivateChannelCreateRequest(List.of(kimId, hanId)));

		ChannelResponse private1 = channelService.find(private1Id);
		ChannelResponse private2 = channelService.find(private2Id);
		ChannelResponse private3 = channelService.find(private3Id);

		printChannel(private1, userNameById);
		printChannel(private2, userNameById);
		printChannel(private3, userNameById);

		System.out.println("\n[검증] PRIVATE 생성 직후 ReadStatus 자동 생성 확인");
		assertReadStatusExists(readStatusService, kimId, private1Id, "private-1 / 김");
		assertReadStatusExists(readStatusService, leeId, private1Id, "private-1 / 이");
		assertReadStatusExists(readStatusService, parkId, private1Id, "private-1 / 박");

		assertReadStatusExists(readStatusService, choiId, private2Id, "private-2 / 최");
		assertReadStatusExists(readStatusService, jungId, private2Id, "private-2 / 정");
		assertReadStatusExists(readStatusService, ohId, private2Id, "private-2 / 오");

		assertReadStatusExists(readStatusService, kimId, private3Id, "private-3 / 김");
		assertReadStatusExists(readStatusService, hanId, private3Id, "private-3 / 한");

		// park가 참가한 채널 ID 출력 (PUBLIC/PRIVATE 모두 검사)
		System.out.println("\n특정 유저(박코딩)가 참가한 채널 ID 목록 (PUBLIC/PRIVATE 모두 검사)");
		List<UUID> parkJoinedChannelIds =
				channelService.findAllByUserId(parkId).stream()
						.map(ChannelResponse::channelId)
						.filter(chId -> {
							List<UUID> participants = channelService.findAllUserInChannel(chId);
							return participants != null && participants.contains(parkId);
						})
						.toList();

		if (parkJoinedChannelIds.isEmpty()) {
			System.out.println("(empty)");
		} else {
			for (UUID chId : parkJoinedChannelIds) {
				System.out.println("- " + chId);
			}
		}

		// lastMessageTime 검증(메시지 생성 전/후)
		System.out.println("\n(추가 검증) lastMessageTime (public-1)");
		System.out.println("[before]");
		printChannel(channelService.find(public1Id), userNameById);

		UUID m1 = messageService.create(new MessageCreateRequest(public1Id, kimId, "안녕하세요!", List.of())).messageId();
		UUID m2 = messageService.create(new MessageCreateRequest(public1Id, leeId, "반갑습니다", List.of())).messageId();

		System.out.println("[after]");
		printChannel(channelService.find(public1Id), userNameById);

		// 나머지 메시지
		UUID m3 = messageService.create(new MessageCreateRequest(public2Id, parkId, "오늘 회의 몇 시예요?", List.of())).messageId();
		UUID m4 = messageService.create(new MessageCreateRequest(private1Id, kimId, "자료 공유했습니다", List.of())).messageId();
		UUID m5 = messageService.create(new MessageCreateRequest(private3Id, hanId, "확인했습니다", List.of())).messageId();

		System.out.println("\n[조회] public-1");
		printMessages(messageService.findAllByChannelId(public1Id));

		System.out.println("\n[조회] public-2");
		printMessages(messageService.findAllByChannelId(public2Id));

		System.out.println("\n[조회] private-1");
		printMessages(messageService.findAllByChannelId(private1Id));

		System.out.println("\n[조회] private-3");
		printMessages(messageService.findAllByChannelId(private3Id));

		System.out.println("\n[검증] 메시지 삭제 1개 (public-1의 m1 삭제) 후 재조회");
		messageService.delete(m1);

		List<MessageResponse> public1AfterDelete = messageService.findAllByChannelId(public1Id);
		printMessages(public1AfterDelete);

		boolean stillExists = public1AfterDelete.stream().anyMatch(m -> m1.equals(m.messageId()));
		if (stillExists) {
			System.out.println("삭제된 메시지가 여전히 조회됩니다: " + m1);
		} else {
			System.out.println("삭제된 메시지가 조회에서 사라졌습니다.");
		}

		// 첨부파일 2개 포함 메시지 생성/삭제 연쇄 검증 (BinaryContentService: DTO + UUID 반환)
		System.out.println("\n(추가 검증) 첨부파일 2개 포함 메시지 생성/삭제 연쇄 검증");

		UUID a1Id = binaryContentService.create(
				new BinaryContentCreateRequest("a1.txt", "text/plain", "file-a1".getBytes(), null, null)
		);
		UUID a2Id = binaryContentService.create(
				new BinaryContentCreateRequest("a2.txt", "text/plain", "file-a2".getBytes(), null, null)
		);

		System.out.println("첨부 생성: a1Id=" + a1Id + ", a2Id=" + a2Id);

		MessageResponse msgWithAttachments = messageService.create(
				new MessageCreateRequest(public2Id, kimId, "첨부 2개 테스트", List.of(a1Id, a2Id))
		);
		UUID msgId = msgWithAttachments.messageId();

		System.out.println("메시지 생성: msgId=" + msgId);
		System.out.println("message.attachmentIds=" + msgWithAttachments.attachmentIds());

		messageService.delete(msgId);

		boolean a1Still = existsBinary(binaryContentService, a1Id);
		boolean a2Still = existsBinary(binaryContentService, a2Id);

		if (!a1Still && !a2Still) {
			System.out.println("메시지 삭제 시 첨부 BinaryContent 연쇄 삭제 확인");
		} else {
			System.out.println("첨부 BinaryContent가 남아있습니다: a1=" + a1Still + ", a2=" + a2Still);
		}

		// ReadStatus 갱신 검증 + 중복 생성 실패 확인 (ReadStatusService: DTO)
		System.out.println("\nReadStatus 갱신/중복 검증");

		ReadStatusResponse kimPrivate3 = findReadStatus(readStatusService, kimId, private3Id);
		if (kimPrivate3 == null) {
			System.out.println("ReadStatus를 찾지 못했습니다 (private-3 / 김)");
		} else {
			Instant before = kimPrivate3.readAt();
			ReadStatusResponse updated = readStatusService.update(new ReadStatusUpdateRequest(kimPrivate3.id()));
			Instant after = updated.readAt();

			System.out.println("before readAt=" + before);
			System.out.println("after  readAt=" + after);

			if (after != null && before != null && after.isAfter(before)) {
				System.out.println("ReadStatus 갱신 확인");
			} else {
				System.out.println("ReadStatus 갱신이 반영되지 않은 것 같습니다.");
			}
		}

		System.out.println("\n[검증] (userId, channelId) ReadStatus 중복 생성 실패 확인");
		try {
			readStatusService.create(new ReadStatusCreateRequest(kimId, private3Id));
			System.out.println("중복 ReadStatus 생성이 성공하면 안 됩니다.");
		} catch (DuplicationReadStatusException expected) {
			System.out.println("중복 ReadStatus 생성 실패 확인: " + expected.getClass().getSimpleName());
		}

		// 채널 조회 규칙 검증 (findAllByUserId)
		System.out.println("\n채널 조회 규칙 검증 (김코딩 기준)");

		List<ChannelResponse> kimVisible = channelService.findAllByUserId(kimId);
		for (ChannelResponse ch : kimVisible) {
			printChannel(ch, userNameById);
		}

		boolean hasPublic1 = kimVisible.stream().anyMatch(ch -> public1Id.equals(ch.channelId()));
		boolean hasPublic2 = kimVisible.stream().anyMatch(ch -> public2Id.equals(ch.channelId()));
		boolean hasPublic3 = kimVisible.stream().anyMatch(ch -> public3Id.equals(ch.channelId()));

		boolean hasPrivate1 = kimVisible.stream().anyMatch(ch -> private1Id.equals(ch.channelId())); // 참여 O
		boolean hasPrivate2 = kimVisible.stream().anyMatch(ch -> private2Id.equals(ch.channelId())); // 참여 X
		boolean hasPrivate3 = kimVisible.stream().anyMatch(ch -> private3Id.equals(ch.channelId())); // 참여 O

		System.out.println("\n[검증] 포함 여부");
		System.out.println("PUBLIC: 1=" + hasPublic1 + ", 2=" + hasPublic2 + ", 3=" + hasPublic3);
		System.out.println("PRIVATE: private-1(참여)=" + hasPrivate1 + ", private-2(미참여)=" + hasPrivate2 + ", private-3(참여)=" + hasPrivate3);

		// 삭제 연쇄 검증
		System.out.println("\n삭제 연쇄 검증");
		System.out.println("[삭제] private-1 삭제");
		channelService.delete(private1Id);

		// 메시지 연쇄 삭제 확인
		try {
			List<MessageResponse> afterChannelDeleteMsgs = messageService.findAllByChannelId(private1Id);
			if (afterChannelDeleteMsgs.isEmpty()) {
				System.out.println("채널 삭제 후 메시지 연쇄 삭제 확인(private-1)");
			} else {
				System.out.println("채널 삭제 후 메시지가 남아있습니다(private-1): count=" + afterChannelDeleteMsgs.size());
			}
		} catch (ChannelNotFoundException e) {
			System.out.println("채널 삭제 후 메시지 조회가 불가(채널 없음): " + e.getClass().getSimpleName());
		}

		// ReadStatus 연쇄 삭제 확인 (DTO)
		boolean rsKimStill = findReadStatus(readStatusService, kimId, private1Id) != null;
		boolean rsLeeStill = findReadStatus(readStatusService, leeId, private1Id) != null;
		boolean rsParkStill = findReadStatus(readStatusService, parkId, private1Id) != null;

		if (!rsKimStill && !rsLeeStill && !rsParkStill) {
			System.out.println("채널 삭제 후 ReadStatus 연쇄 삭제 확인(private-1)");
		} else {
			System.out.println("채널 삭제 후 ReadStatus가 남아있습니다(private-1): "
					+ "kim=" + rsKimStill + ", lee=" + rsLeeStill + ", park=" + rsParkStill);
		}
	}

	// file 리포지토리 빈이 존재할 때만 reset (jcf 모드에서도 메인이 실행되게)
	private static void resetAllFilesIfPresent(ConfigurableApplicationContext context) {
		context.getBeansOfType(FileUserRepository.class).values().forEach(FileUserRepository::resetFile);
		context.getBeansOfType(FileChannelRepository.class).values().forEach(FileChannelRepository::resetFile);
		context.getBeansOfType(FileMessageRepository.class).values().forEach(FileMessageRepository::resetFile);
		context.getBeansOfType(FileBinaryContentRepository.class).values().forEach(FileBinaryContentRepository::resetFile);
		context.getBeansOfType(FileReadStatusRepository.class).values().forEach(FileReadStatusRepository::resetFile);
		context.getBeansOfType(FileUserStatusRepository.class).values().forEach(FileUserStatusRepository::resetFile);
	}

	// 출력 헬퍼
	private static void printUser(UserResponse user) {
		System.out.println(
				"Id: " + user.id()
						+ ", name: " + user.userName()
						+ ", email: " + user.email()
						+ ", online: " + user.online()
		);
	}

	private static void printChannel(ChannelResponse ch, Map<UUID, String> userNameById) {
		List<String> participantNames =
				(ch.participantIds() == null) ? List.of()
						: ch.participantIds().stream()
						.map(id -> userNameById.getOrDefault(id, "unknown(" + id + ")"))
						.toList();

		System.out.println(
				"Channel{" +
						"id=" + ch.channelId() +
						", name='" + ch.channelName() + '\'' +
						", private=" + ch.isPrivate() +
						", lastMessageTime=" + ch.lastMessageTime() +
						", participants=" + participantNames +
						'}'
		);
	}

	private static void printMessages(List<MessageResponse> messages) {
		if (messages == null || messages.isEmpty()) {
			System.out.println("(empty)");
			return;
		}
		for (MessageResponse m : messages) {
			System.out.println(
					"Message{" +
							"id=" + m.messageId() +
							", channelId=" + m.channelId() +
							", userId=" + m.userId() +
							", content='" + m.content() + '\'' +
							", createdAt=" + m.createdAt() +
							", updatedAt=" + m.updatedAt() +
							'}'
			);
		}
	}

	// ReadStatus 검증/조회 헬퍼 (DTO)
	private static void assertReadStatusExists(ReadStatusService readStatusService, UUID userId, UUID channelId, String label) {
		ReadStatusResponse rs = findReadStatus(readStatusService, userId, channelId);
		if (rs == null) {
			System.out.println("ReadStatus 없음: " + label);
		} else {
			System.out.println("ReadStatus 존재: " + label + " / readStatusId=" + rs.id());
		}
	}

	private static ReadStatusResponse findReadStatus(ReadStatusService readStatusService, UUID userId, UUID channelId) {
		List<ReadStatusResponse> list = readStatusService.findAllByUserId(userId);
		for (ReadStatusResponse rs : list) {
			if (channelId.equals(rs.channelId())) return rs;
		}
		return null;
	}

	// BinaryContent 존재 확인(삭제 연쇄 검증용)
	private static boolean existsBinary(BinaryContentService binaryContentService, UUID id) {
		try {
			binaryContentService.find(id);
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}
}
