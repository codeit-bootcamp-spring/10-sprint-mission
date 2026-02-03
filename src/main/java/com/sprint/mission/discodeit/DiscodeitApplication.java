package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {
	public static class SetUp {
		public UserService userService;
		public UserRepository userRepository;
		public ChannelService channelService;
		public ChannelRepository channelRepository;
		public MessageService messageService;
		public MessageRepository messageRepository;

		public SetUp(ApplicationContext context) {
			this.userService = context.getBean(UserService.class);
			this.userRepository = context.getBean(UserRepository.class);
			this.channelService = context.getBean(ChannelService.class);
			this.channelRepository = context.getBean(ChannelRepository.class);
			this.messageService = context.getBean(MessageService.class);
			this.messageRepository = context.getBean(MessageRepository.class);
		}
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class);
		SetUp setUp = new SetUp(context);

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
		User user1 = userTest.userService.createUser("AAA", "aaa", "A씨", "AAA@gmail.com");
		User user2 = userTest.userService.createUser("BBB", "bbb", "B씨", "BBB@naver.com");
		User user3 = userTest.userService.createUser("CCC", "ccc", "C씨", "CCC@icloud.com");
		try {
			userTest.userService.createUser("AAA", "a", "A씨", "A@gmail.com");
		} catch (Exception e) {
			System.out.println("\t= accountId 중복체크: " + e);
		}
		try {
			userTest.userService.createUser("AAAA", "a", "A씨", "AAA@gmail.com");
		} catch (Exception e) {
			System.out.println("\t= mail 중복체크: " + e);
		}

		// 조회
		System.out.println("<조회>");
		System.out.println("- 단일:");
		System.out.println("\t= uuid 조회: " + userTest.userService.findUser(user1.getId()));
		System.out.println("\t= accountId 조회: " + userTest.userService.findUserByAccountId("BBB"));
		System.out.println("\t= mail 조회: " + userTest.userService.findUserByMail("CCC@icloud.com"));
		System.out.println("- 다중:");
		System.out.println(userTest.userService.findAllUsers());

		// 수정
		System.out.println("<수정>");
		System.out.println("\t= 성공: " + userTest.userService.updateUser(
				user1.getId(), null, "q1w2e3!", "A말씨", "aaa@outlook.com"));
		try {
			userTest.userService.updateUser(user2.getId(), "AAA", "123", "B명씨", "b_+@hanmail.net");
		} catch (Exception e) {
			System.out.println("\t= accountId 중복체크: " + e);
		}
		try {
			userTest.userService.updateUser(user3.getId(), null, null, "CC씨", "BBB@naver.com");
		} catch (Exception e) {
			System.out.println("\t= mail 중복체크: " + e);
		}

		// 삭제
		System.out.println("<삭제>");
		System.out.print("\t= uuid 삭제: ");
		userTest.userService.deleteUser(user1.getId());
		System.out.println("... 성공");
		System.out.print("\t= accountId 삭제: ");
		userTest.userService.deleteUserByAccountId(user2.getAccountId());
		System.out.println("... 성공");
		System.out.print("\t= mail계정 삭제: ");
		userTest.userService.deleteUserByMail(user3.getMail());
		System.out.println("... 성공");
		System.out.print("\t= accountId 삭제 에러체크: ");
		try {
			userTest.userService.deleteUserByAccountId("에엥");
		} catch (Exception e) {
			System.out.println(e + "... 성공");
		}
		System.out.print("\t= mail계정 삭제 에러체크: ");
		try {
			userTest.userService.deleteUserByMail("에엥@naver.com");
		} catch (Exception e) {
			System.out.println(e + "... 성공");
		}
		System.out.println("- 삭제 후 전체조회:");
		System.out.println(userTest.userService.findAllUsers());
		System.out.println();
	}

	private static void channelServiceCRUDTest(SetUp setUp) {
		SetUp channelTest;
		channelTest = setUp;
		System.out.println("-- Channel --");

		// 생성
		System.out.println("<생성>");
		Channel channel1 = channelTest.channelService.createChannel("멍때리는 방", "머어어어엉~");
		Channel channel2 = channelTest.channelService.createChannel("잠자는 방", "채팅시 강퇴");
		try {
			channelTest.channelService.createChannel("멍때리는 방", "2번째 방");
		} catch (Exception e) {
			System.out.println("\ttitle 중복체크: " + e);
		}

		// 조회
		System.out.println("<조회>");
		System.out.println("- 단일:");
		System.out.println("\t= uuid 조회: " + channelTest.channelService.findChannel(channel1.getId()));
		System.out.println("\t= title 조회: " + channelTest.channelService.findChannelByTitle("잠자는 방"));
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
		System.out.println("- 다중:");
		System.out.println(channelTest.channelService.findAllChannels());

		// 수정
		System.out.println("<수정>");
		System.out.println("\t= 성공: " + channelTest.channelService.updateChannel(
				channel1.getId(), "런닝맨", "아침 6시부터 달려요"));
		try {
			channelTest.channelService.updateChannel(channel2.getId(), "런닝맨", "런닝맨 2번째 방");
		} catch (Exception e) {
			System.out.println("\t= title 중복체크: " + e);
		}

		// 삭제
		System.out.println("<삭제>");
		System.out.print("\t= uuid 삭제: ");
		channelTest.channelService.deleteChannel(channel1.getId());
		System.out.println("... 성공");
		System.out.print("\t= title 삭제: ");
		channelTest.channelService.deleteChannelByTitle("잠자는 방");
		System.out.println("... 성공");
		System.out.print("\t= title 삭제 에러체크: ");
		try {
			channelTest.channelService.deleteChannelByTitle("몬헌방");
		} catch (Exception e) {
			System.out.println(e + "... 성공");
		}
		System.out.println("- 삭제 후 전체조회:");
		System.out.println(channelTest.channelService.findAllChannels());
		System.out.println();
	}

	private static void messageServiceCRUDTest(SetUp setUp) {
		SetUp messageTest;
		messageTest = setUp;
		System.out.println("-- Message --");

		// 생성
		System.out.println("<생성>");
		User user1 = messageTest.userService.createUser("AAA", "aaa", "A씨", "AAA@gmail.com");
		Channel channel1 = messageTest.channelService.createChannel("멍때리는 방", "머어어어엉~");
		Channel channel2 = messageTest.channelService.createChannel("두쫀쿠 헌터방", "판매위치 제보받아요");

		try {
			messageTest.messageService.createMessage(
					channel1.getId(), user1.getId(), "Zz...");
		} catch (Exception e) {
			System.out.println("\t= 채널소속유저 검증 체크: " + e);
		}
		messageTest.channelService.joinChannel(channel1.getId(), user1.getId());
		messageTest.channelService.joinChannel(channel2.getId(), user1.getId());
		Message msg1 = messageTest.messageService.createMessage(
				channel1.getId(), user1.getId(), "Zz...");
		Message msg2 = messageTest.messageService.createMessage(
				channel2.getId(), user1.getId(), "어디에 있나요. 두쫀쿠.");
		System.out.println("\t= 성공: " + msg1);

		// 조회
		System.out.println("<조회>");
		System.out.println("- 단일:");
		System.out.println("\t= uuid 조회: " + messageTest.messageService.findMessage(msg1.getId()));
		try {
			messageTest.messageService.findMessage(UUID.randomUUID());
		} catch (Exception e) {
			System.out.println("\t= uuid 조회 에러체크: " + e);
		}
		System.out.println("\t= channel 조회: " + messageTest.messageService.findAllByChannelId(channel1.getId()));
		System.out.println("- 다중:");
		System.out.println(messageTest.messageService.findAllMessages());

		// 수정
		System.out.println("<수정>");
		System.out.println("\t= 성공: " + messageTest.messageService.updateMessage(
				msg2.getId(), "제보 받았습니다!"));

		// 삭제
		System.out.println("<삭제>");
		System.out.println("\t= 삭제 전 채널조회 메세지 수: "
				+ messageTest.channelService.findChannel(channel1.getId()).getMessages().toArray().length);
		System.out.print("\t= uuid 삭제: ");
		messageTest.messageService.deleteMessage(msg1.getId());
		System.out.println("... 성공");
		System.out.println("\t= 삭제 후 채널조회 메세지 수: "
				+ messageTest.channelService.findChannel(channel1.getId()).getMessages().toArray().length);
		System.out.println();

		// 테스트파일 전부 삭제
		messageTest.userService.deleteUser(user1.getId());
		messageTest.channelService.deleteChannel(channel1.getId());
		messageTest.channelService.deleteChannel(channel2.getId());
	}

	private static void joinAndLeaveChannelTestAndUserDeleteTest(SetUp setUp) {
		SetUp test;
		test = setUp;
		System.out.println("-- 유저의 채널참여,채널탈퇴,계정삭제 테스트 --");

		Channel channel1 = test.channelService.createChannel("경도방", "2030 경도모임방입니다");
		Channel channel2 = test.channelService.createChannel("두쫀쿠 헌터방", "두쫀쿠 위치 제보받아요");
		User user1 = test.userService.createUser("AAA", "aaa", "A씨", "AAA@gmail.com");
		User user2 = test.userService.createUser("BBB", "bbb", "B씨", "BBB@naver.com");

		// user1 채널 참여 전
		System.out.println("User 1(AAA) 채널 참여 전:");
		System.out.println("\t= User1 채널참여 리스트: " + test.userService.findUser(user1.getId()).getJoinedChannels());
		System.out.println("\t= Channel1 참여된 유저: " + test.channelService.findChannel(channel1.getId()).getParticipants());
		System.out.println("\t= Channel2 참여된 유저: " + test.channelService.findChannel(channel2.getId()).getParticipants());

		// user1/user2 채널 참여
		test.channelService.joinChannel(channel1.getId(), user1.getId());
		test.channelService.joinChannel(channel2.getId(), user1.getId());
		test.channelService.joinChannel(channel1.getId(), user2.getId());

		// user1 채널 참여 후
		System.out.println("User 1(AAA) 채널 참여 후:");
		System.out.println("\t= User1 채널참여 리스트: " + test.userService.findUser(user1.getId()).getJoinedChannels());
		System.out.println("\t= Channel1 소속된 유저: " + test.channelService.findChannel(channel1.getId()).getParticipants());
		System.out.println("\t= Channel2 소속된 유저: " + test.channelService.findChannel(channel2.getId()).getParticipants());

		// user1 메세지 보내기 전
		System.out.println("User 1(AAA) 메세지 보내기 전:");
		System.out.println("\t= User1 보낸 메세지: " + test.userService.findUser(user1.getId()).getMessageHistory());
		System.out.println("\t= Channel1 메세지 목록: " + test.channelService.findChannel(channel1.getId()).getMessages());
		System.out.println("\t= Channel2 메세지 목록: " + test.channelService.findChannel(channel2.getId()).getMessages());

		// user1 메세지 전달
		test.messageService.createMessage(channel1.getId(), user1.getId(),
				"저도 참여 가능할까요?");
		test.messageService.createMessage(channel1.getId(), user1.getId(),
				"어디로 가야 하나요?");
		test.messageService.createMessage(channel2.getId(), user1.getId(),
				"두쫀쿠 어디가면 안기다리고 먹을수 있죠?");
		test.messageService.createMessage(channel1.getId(), user2.getId(),
				"호수공원으로 오세요");


		// user1 메세지 보낸 후
		System.out.println("User 1(AAA) 메세지 보낸 후:");
		System.out.println("\t= User1 보낸 메세지: " + test.userService.findUser(user1.getId()).getMessageHistory());
		System.out.println("\t= Channel1 메세지 목록: " + test.channelService.findChannel(channel1.getId()).getMessages());
		System.out.println("\t= Channel2 메세지 목록: " + test.channelService.findChannel(channel2.getId()).getMessages());

		// channel2 삭제 전
		System.out.println("Channel 2 삭제 전:");
		System.out.println("\t= User1 보낸 메세지: " + test.userService.findUser(user1.getId()).getMessageHistory());
		System.out.println("\t= User1 채널참여 리스트: " + test.userService.findUser(user1.getId()).getJoinedChannels());

		// channel2 삭제
		test.channelService.deleteChannel(channel2.getId());

		// channel2 삭제 후
		System.out.println("Channel 2 삭제 후:");
		System.out.println("\t= User1 보낸 메세지: " + test.userService.findUser(user1.getId()).getMessageHistory());
		System.out.println("\t= User1 채널참여 리스트: " + test.userService.findUser(user1.getId()).getJoinedChannels());

		// user1 삭제 전
		System.out.println("User 1(AAA) 삭제 전:");
		System.out.println("\t= User1 보낸 메세지: " + test.userService.findUser(user1.getId()).getMessageHistory());
		System.out.println("\t= Channel1 소속된 유저: " + test.channelService.findChannel(channel1.getId()).getParticipants());

		// user1 삭제
		test.userService.deleteUser(user1.getId());

		// user1 삭제 후
		System.out.println("User 1(AAA) 삭제 후:");
		System.out.println("\t= Channel1 소속된 유저: " + test.channelService.findChannel(channel1.getId()).getParticipants());

		// 테스트파일 전부 삭제
		test.userService.deleteUser(user2.getId());
		test.channelService.deleteChannel(channel1.getId());
	}
}
