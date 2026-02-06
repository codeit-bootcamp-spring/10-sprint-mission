package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {
	@Component
	@RequiredArgsConstructor
	public static class SetUp {
		public final UserService userService;
		public final UserRepository userRepository;
		public final ChannelService channelService;
		public final ChannelRepository channelRepository;
		public final MessageService messageService;
		public final MessageRepository messageRepository;
	}

	public static class Dtos {
		public Dtos() {}

		// user create
		public static UserDto.createRequest user1Create() {
			return new UserDto.createRequest("AAA", "aaa", "A씨", "AAA@gmail.com");
		}
		public static UserDto.createRequest user1DuplicateMailCreate() {
			return new UserDto.createRequest("A", "aaa", "A씨", "AAA@gmail.com");
		}
		public static UserDto.createRequest user2Create() {
			return new UserDto.createRequest("BBB", "bbb", "B씨", "BBB@naver.com");
		}
		public static UserDto.createRequest user3Create() {
			return new UserDto.createRequest("CCC", "ccc", "C씨", "CCC@icloud.com");
		}

		// user update
		public static UserDto.updateRequest user1Update() {
			return new UserDto.updateRequest("AAA","q1w2e3!", "A말씨", "aaa@outlook.com");
		}
		public static UserDto.updateRequest user2DuplicateAccountCheckUpdate() {
			return new UserDto.updateRequest("AAA", "123", "B명씨", "b_+@hanmail.net");
		}
		public static UserDto.updateRequest user3DuplicateMailCheckUpdate() {
			return new UserDto.updateRequest(null, null, "CC씨", "BBB@naver.com");
		}

		// channel create
		public static ChannelDto.createPublicRequest channel1Create() {
			return new ChannelDto.createPublicRequest(ChannelType.PUBLIC, "경도방", "2030 경도모임방입니다");
		}
		public static ChannelDto.createPublicRequest channel1DuplicateTitleCreate() {
			return new ChannelDto.createPublicRequest(ChannelType.PUBLIC, "경도방", "2번째 방");
		}
		public static ChannelDto.createPublicRequest channel2Create() {
			return new ChannelDto.createPublicRequest(ChannelType.PUBLIC, "두쫀쿠 헌터방", "두쫀쿠 위치 제보받아요");
		}

		// channel update
		public static ChannelDto.updatePublicRequest channel1Update() {
			return new ChannelDto.updatePublicRequest("런닝맨", "아침 6시부터 달려요");
		}
		public static ChannelDto.updatePublicRequest channel2DuplicateTitleCheckUpdate() {
			return new ChannelDto.updatePublicRequest("런닝맨", "런닝맨 2번째 방");
		}

		// message create
		public static MessageDto.createRequest messageCreate(UUID channelId, UUID userId, String text) {
			return new MessageDto.createRequest(channelId, userId, text);
		}

		// message update
		public static MessageDto.updateRequest messageUpdate(String text) {
			return new MessageDto.updateRequest(text);
		}

	}

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class);
		SetUp setUp = context.getBean(SetUp.class);

		// --- User ---
		userServiceCRUDTest(setUp);

		// --- Channel ---
		channelServiceCRUDTest(setUp);

		// --- Message ---
		messageServiceCRUDTest(setUp);

		// 유저의 채널참여,채널탈퇴,계정삭제 테스트
		joinAndLeaveChannelTestAndUserDeleteTest(setUp);
	}

	private static void userServiceCRUDTest(SetUp setUp) {
		SetUp userTest;
		userTest = setUp;
		System.out.println("-- User --");

		// 생성
		System.out.println("<생성>");
		UserDto.response user1 = userTest.userService.createUser(Dtos.user1Create(), null);
		UserDto.response user2 = userTest.userService.createUser(Dtos.user2Create(), null);
		UserDto.response user3 = userTest.userService.createUser(Dtos.user3Create(), null);
		try {
			userTest.userService.createUser(Dtos.user1Create(), null);
		} catch (Exception e) {
			System.out.println("\t= accountId 중복체크: " + e);
		}
		try {
			userTest.userService.createUser(Dtos.user1DuplicateMailCreate(), null);
		} catch (Exception e) {
			System.out.println("\t= mail 중복체크: " + e);
		}

		// 조회
		System.out.println("<조회>");
		System.out.println("- 단일:");
		System.out.println("\t= uuid 조회: " + userTest.userService.findUser(user1.uuid()));
		System.out.println("\t= accountId 조회: " + userTest.userService.findUserByAccountId("BBB"));
		System.out.println("\t= mail 조회: " + userTest.userService.findUserByMail("CCC@icloud.com"));
		System.out.println("- 다중:");
		userTest.userService.findAllUsers().forEach(System.out::println);

		// 수정
		System.out.println("<수정>");
		System.out.println("\t= 성공: " + userTest.userService.updateUser(user1.uuid(), Dtos.user1Update(), null));
		try {
			userTest.userService.updateUser(user2.uuid(), Dtos.user2DuplicateAccountCheckUpdate(), null);
		} catch (Exception e) {
			System.out.println("\t= accountId 중복체크: " + e);
		}
		try {
			userTest.userService.updateUser(user3.uuid(), Dtos.user3DuplicateMailCheckUpdate(), null);
		} catch (Exception e) {
			System.out.println("\t= mail 중복체크: " + e);
		}

		// 삭제
		System.out.println("<삭제>");
		System.out.print("\t= uuid 삭제: ");
		userTest.userService.deleteUser(user1.uuid());
		userTest.userService.deleteUser(user2.uuid());
		userTest.userService.deleteUser(user3.uuid());
		System.out.println("... 성공");
		System.out.println("- 삭제 후 전체조회:");
		userTest.userService.findAllUsers().forEach(System.out::println);
		System.out.println();
	}

	private static void channelServiceCRUDTest(SetUp setUp) {
		SetUp channelTest;
		channelTest = setUp;
		System.out.println("-- Channel --");

		// 생성
		System.out.println("<생성>");
		ChannelDto.response channel1 = channelTest.channelService.createChannel(Dtos.channel1Create());
		ChannelDto.response channel2 = channelTest.channelService.createChannel(Dtos.channel2Create());
		try {
			channelTest.channelService.createChannel(Dtos.channel1DuplicateTitleCreate());
		} catch (Exception e) {
			System.out.println("\ttitle 중복체크: " + e);
		}

		// 조회
		System.out.println("<조회>");
		System.out.println("- 단일:");
		System.out.println("\t= uuid 조회: " + channelTest.channelService.findChannel(channel1.uuid()));
		System.out.println("\t= title 조회: " + channelTest.channelService.findChannelByTitle("경도방"));
		try {
			channelTest.channelService.findChannel(UUID.randomUUID());
		} catch (Exception e) {
			System.out.println("\t= uuid 조회 에러체크: " + e);
		}
		try {
			channelTest.channelService.findChannelByTitle("야구관람방");
		} catch (Exception e) {
			System.out.println("\t= title 조회 에러체크: " + e);
		}

		// 수정
		System.out.println("<수정>");
		System.out.println("\t= 성공: " +
				channelTest.channelService.updateChannel(channel1.uuid(), Dtos.channel1Update()));
		try {
			channelTest.channelService.updateChannel(channel2.uuid(), Dtos.channel2DuplicateTitleCheckUpdate());
		} catch (Exception e) {
			System.out.println("\t= title 중복체크: " + e);
		}

		// 삭제
		System.out.println("<삭제>");
		System.out.print("\t= uuid 삭제: ");
		channelTest.channelService.deleteChannel(channel1.uuid());
		channelTest.channelService.deleteChannel(channel2.uuid());
		System.out.println("... 성공");
		System.out.println();
	}

	private static void messageServiceCRUDTest(SetUp setUp) {
		SetUp messageTest;
		messageTest = setUp;
		System.out.println("-- Message --");

		// 생성
		System.out.println("<생성>");
		UserDto.response user1 = messageTest.userService.createUser(Dtos.user1Create(), null);
		ChannelDto.response channel1 = messageTest.channelService.createChannel(Dtos.channel1Create());
		ChannelDto.response channel2 = messageTest.channelService.createChannel(Dtos.channel2Create());

		try {
			messageTest.messageService.createMessage(
					Dtos.messageCreate(channel1.uuid(), user1.uuid(), "저도 참여 가능할까요?"), null);
		} catch (Exception e) {
			System.out.println("\t= 채널소속유저 검증 체크: " + e);
		}
		messageTest.channelService.joinChannel(channel1.uuid(), user1.uuid());
		messageTest.channelService.joinChannel(channel2.uuid(), user1.uuid());
		MessageDto.response msg1 = messageTest.messageService.createMessage(
				Dtos.messageCreate(channel1.uuid(), user1.uuid(), "저도 참여 가능할까요?"), null);
		MessageDto.response msg2 = messageTest.messageService.createMessage(
				Dtos.messageCreate(channel2.uuid(), user1.uuid(), "두쫀쿠 어디가면 안기다리고 먹을수 있죠?"), null);
		System.out.println("\t= 성공: " + msg1);

		// 조회
		System.out.println("<조회>");
		System.out.println("- 단일:");
		System.out.println("\t= uuid 조회: " + messageTest.messageService.findMessage(msg1.uuid()));
		try {
			messageTest.messageService.findMessage(UUID.randomUUID());
		} catch (Exception e) {
			System.out.println("\t= uuid 조회 에러체크: " + e);
		}
		System.out.println("\t= channel 조회: " + messageTest.messageService.findAllByChannelId(channel1.uuid()));
		System.out.println("- channel1 다중:");
		messageTest.messageService.findAllByChannelId(channel1.uuid())
				.forEach(System.out::println);

		// 수정
		System.out.println("<수정>");
		System.out.println("\t= 성공: " + messageTest.messageService.updateMessage(
				msg2.uuid(), Dtos.messageUpdate("기상!!!!!!!")));

		// 삭제
		System.out.println("<삭제>");
		System.out.println("\t= 삭제 전 채널조회 메세지 수: "
				+ messageTest.messageService.findAllByChannelId(channel1.uuid()).toArray().length);
		System.out.print("\t= uuid 삭제: ");
		messageTest.messageService.deleteMessage(msg1.uuid());
		System.out.println("... 성공");
		System.out.println("\t= 삭제 후 채널조회 메세지 수: "
				+ messageTest.messageService.findAllByChannelId(channel1.uuid()).toArray().length);
		System.out.println();

		// 테스트파일 전부 삭제
		messageTest.userService.deleteUser(user1.uuid());
		messageTest.channelService.deleteChannel(channel1.uuid());
		messageTest.channelService.deleteChannel(channel2.uuid());
	}

	private static void joinAndLeaveChannelTestAndUserDeleteTest(SetUp setUp) {
		SetUp test;
		test = setUp;
		System.out.println("-- 유저의 채널참여,채널탈퇴,계정삭제 테스트 --");

		ChannelDto.response channel1 = test.channelService.createChannel(Dtos.channel1Create());
		ChannelDto.response channel2 = test.channelService.createChannel(Dtos.channel2Create());
		UserDto.response user1 = test.userService.createUser(Dtos.user1Create(), null);
		UserDto.response user2 = test.userService.createUser(Dtos.user2Create(), null);

		// user1 채널 참여 전
		System.out.println("User 1(AAA) 채널 참여 전:");
		System.out.println("\t= User1 채널참여 리스트: " + test.userService.findUser(user1.uuid()).joinedChannels());
		System.out.println("\t= Channel1 참여된 유저: " + test.channelService.findChannel(channel1.uuid()).participantIds());
		System.out.println("\t= Channel2 참여된 유저: " + test.channelService.findChannel(channel2.uuid()).participantIds());

		// user1/user2 채널 참여
		test.channelService.joinChannel(channel1.uuid(), user1.uuid());
		test.channelService.joinChannel(channel2.uuid(), user1.uuid());
		test.channelService.joinChannel(channel1.uuid(), user2.uuid());

		// user1 채널 참여 후
		System.out.println("User 1(AAA) 채널 참여 후:");
		System.out.println("\t= User1 채널참여 리스트: " + test.userService.findUser(user1.uuid()).joinedChannels());
		System.out.println("\t= Channel1 소속된 유저: " + test.channelService.findChannel(channel1.uuid()).participantIds());
		System.out.println("\t= Channel2 소속된 유저: " + test.channelService.findChannel(channel2.uuid()).participantIds());

		// user1 메세지 보내기 전
		System.out.println("User 1(AAA) 메세지 보내기 전:");
		System.out.println("\t= User1 보낸 메세지: " + test.userService.findUser(user1.uuid()).messageHistory());
		System.out.println("\t= Channel1 메세지 목록: ");
		test.messageService.findAllByChannelId(channel1.uuid()).forEach(System.out::println);
		System.out.println("\t= Channel2 메세지 목록: ");
		test.messageService.findAllByChannelId(channel2.uuid()).forEach(System.out::println);

		// user1 메세지 전달
		test.messageService.createMessage(
				Dtos.messageCreate(channel1.uuid(), user1.uuid(), "저도 참여 가능할까요?"),null);
		test.messageService.createMessage(
				Dtos.messageCreate(channel1.uuid(), user1.uuid(), "어디로 가야 하나요?"),null);
		test.messageService.createMessage(
				Dtos.messageCreate(channel2.uuid(), user1.uuid(), "두쫀쿠 어디가면 안기다리고 먹을수 있죠?"),null);
		test.messageService.createMessage(
				Dtos.messageCreate(channel1.uuid(), user2.uuid(), "호수공원으로 오세요"),null);

		// user1 메세지 보낸 후
		System.out.println("User 1(AAA) 메세지 보낸 후:");
		System.out.println("\t= User1 보낸 메세지: " + test.userService.findUser(user1.uuid()).messageHistory());
		System.out.println("\t= Channel1 메세지 목록: ");
		test.messageService.findAllByChannelId(channel1.uuid()).forEach(System.out::println);
		System.out.println("\t= Channel2 메세지 목록: ");
		test.messageService.findAllByChannelId(channel2.uuid()).forEach(System.out::println);

		// channel2 삭제 전
		System.out.println("Channel 2 삭제 전:");
		System.out.println("\t= User1 보낸 메세지: " + test.userService.findUser(user1.uuid()).messageHistory());
		System.out.println("\t= User1 채널참여 리스트: " + test.userService.findUser(user1.uuid()).joinedChannels());

		// channel2 삭제
		test.channelService.deleteChannel(channel2.uuid());

		// channel2 삭제 후
		System.out.println("Channel 2 삭제 후:");
		System.out.println("\t= User1 보낸 메세지: " + test.userService.findUser(user1.uuid()).messageHistory());
		System.out.println("\t= User1 채널참여 리스트: " + test.userService.findUser(user1.uuid()).joinedChannels());

		// user1 삭제 전
		System.out.println("User 1(AAA) 삭제 전:");
		System.out.println("\t= User1 보낸 메세지: " + test.userService.findUser(user1.uuid()).messageHistory());
		System.out.println("\t= Channel1 소속된 유저: " + test.channelService.findChannel(channel1.uuid()).participantIds());

		// user1 삭제
		test.userService.deleteUser(user1.uuid());

		// user1 삭제 후
		System.out.println("User 1(AAA) 삭제 후:");
		System.out.println("\t= Channel1 소속된 유저: " + test.channelService.findChannel(channel1.uuid()).participantIds());

		// 테스트파일 전부 삭제
		test.userService.deleteUser(user2.uuid());
		test.channelService.deleteChannel(channel1.uuid());
	}
}
