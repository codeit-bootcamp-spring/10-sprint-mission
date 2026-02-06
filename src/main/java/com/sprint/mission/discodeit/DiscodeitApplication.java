package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentInfo;
import com.sprint.mission.discodeit.dto.channel.*;
import com.sprint.mission.discodeit.dto.message.MessageCreateInfo;
import com.sprint.mission.discodeit.dto.message.MessageInfo;
import com.sprint.mission.discodeit.dto.message.MessageUpdateInfo;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusInfo;
import com.sprint.mission.discodeit.dto.user.*;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusInfo;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);
		// context로부터 Bean 조회하여 서비스 구현체 할당
		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);
		AuthService authService = context.getBean(AuthService.class);
		BinaryContentService contentService = context.getBean(BinaryContentService.class);
		ReadStatusService readStatusService = context.getBean(ReadStatusService.class);
		UserStatusService userStatusService = context.getBean(UserStatusService.class);

		printN("유저 테스트 시작");
		userServiceTest(userService);

		printN("채널 테스트 시작");
		channelServiceTest(userService, channelService);

		printN("메세지 테스트 시작");
		messageServiceTest(userService, channelService, messageService);

		printN("AuthService 테스트 시작");
		authServiceTest(userService, authService);

		printN("BinaryContentService 테스트 시작");
		binaryContentServiceTest(userService, contentService);

		printN("ReadStatusService 테스트 시작");
		readStatusServiceTest(userService, channelService, readStatusService);

		printN("UserStatusService 테스트 시작");
		userStatusServiceTest(userService, userStatusService);
	}

	private static void userServiceTest(UserService userService) {
		// setUp
		UserInfo user1 = userService.createUser(new UserCreateInfo("user1", "password1", "email1", new byte[]{1}));
		UserInfo user2 = userService.createUser(new UserCreateInfo("user2", "password2", "email2", new byte[]{2}));

		// 단건 조회
		UserInfoWithStatus findUser = userService.findUser(user1.userId());
		if(findUser.userId().equals(user1.userId()))
			System.out.println("단건 조회 테스트 성공");
		else
			System.out.println("단건 조회 테스트 실패");

		// 다건 조회
		if(userService.findAll().size() == 2)
			System.out.println("다건 조회 테스트 성공");
		else
			System.out.println("다건 조회 테스트 실패");

		// 수정
		UserInfo user2Updated = userService.updateUser(new UserUpdateInfo(user2.userId(), "user2_updated", null,
				null, null, null));
		if (user2.email().equals(user2Updated.email()))
			System.out.println("수정 테스트 성공");
		else
			System.out.println("수정 테스트 실패");

		// 수정 된 데이터 조회
		System.out.printf("수정된 user2 이름 : %s, 이메일 : %s%n", user2Updated.userName(),
				user2Updated.email());

		// 삭제
		int prevSize = userService.findAll().size();
		userService.deleteUser(user1.userId());
		if(userService.findAll().size() == prevSize - 1)
			System.out.println("삭제 테스트 성공");
		else
			System.out.println("삭제 테스트 실패");

		// 조회를 통해 삭제되었는지 확인
		try {
			userService.findUser(user1.userId());
		} catch (NoSuchElementException e) {
			System.out.println("user1 삭제 성공하여 조회x");
		}

		// 중복 생성
		UserInfo user3 = userService.createUser(new UserCreateInfo("user3", "password3", "email3", new byte[]{3}));
		try {
			userService.createUser(new UserCreateInfo("user3", "password3", "email3", new byte[]{3}));
			System.out.println("중복 처리 테스트 실패");
		} catch (IllegalStateException e) {
			System.out.println("중복 처리 테스트 성공");
		}

		// 없는 사용자 조회
		try {
			userService.findUser(UUID.randomUUID());
			System.out.println("없는 사용자 조회 테스트 실패");
		} catch (NoSuchElementException e) {
			System.out.println("없는 사용자 조회 테스트 성공");
		}

		// 반복테스트를 위해 제거
		userService.deleteUser(user2.userId());
		userService.deleteUser(user3.userId());
	}

	private static void channelServiceTest(UserService userService, ChannelService channelService) {
		// setUp
		UserInfo user1 = userService.createUser(new UserCreateInfo("user1", "password1", "email1", new byte[]{1}));
		UserInfo user2 = userService.createUser(new UserCreateInfo("user2", "password2", "email2", new byte[]{2}));

		PublicChannelInfo channel1 = channelService.createPublicChannel(new PublicChannelCreateInfo("channel1", "description1"));
		ArrayList<UUID> userList = new ArrayList<>(List.of(user1.userId(), user2.userId()));
		PrivateChannelInfo channel2 = channelService.createPrivateChannel(new PrivateChannelCreateInfo(userList));

		// 단건 조회
		ChannelInfo findChannel = channelService.findChannel(channel1.channelId());
		if(channel1.channelId().equals(findChannel.channelId()))
			System.out.println("단건 조회 테스트 성공");
		else
			System.out.println("단건 조회 테스트 실패");

		// 다건 조회
		int channelSize = channelService.findAll().size();
		System.out.println("채널 크기 : " + channelSize);
		if(channelSize == 2)
			System.out.println("다건 조회 테스트 성공");
		else
			System.out.println("다건 조회 테스트 실패");

		// 수정
		ChannelInfo channel1Updated = channelService.updateChannel(new UpdateChannelInfo(channel1.channelId(), "channel1_updated", null));
		if(channel1Updated.channelId().equals(channel1.channelId())
		&& channel1Updated.channelName().equals("channel1_updated"))
			System.out.println("수정 테스트 성공");
		else
			System.out.println("수정 테스트 실패");

		// 수정 된 데이터 조회
		System.out.printf("수정된 channel2 이름 : %s, 채널타입 : %s, 설명 : %s%n"
				,channel1Updated.channelName(), channel1Updated.channelType(), channel1Updated.description());

		// 삭제
		int prevSize = channelService.findAll().size();
		channelService.deleteChannel(channel1.channelId());
		if(channelService.findAll().size() == prevSize-1)
			System.out.println("삭제 테스트 성공");
		else
			System.out.println("삭제 테스트 실패");

		// 조회를 통해 삭제되었는지 확인
		try {
			channelService.findChannel(channel1.channelId());
		} catch (NoSuchElementException e) {
			System.out.println("channel1 삭제 성공하여 조회x");
		}

		// 채널 중복 테스트
		PublicChannelInfo channel3 = channelService.createPublicChannel(new PublicChannelCreateInfo("channel3", "description3"));
		try {
			channelService.createPublicChannel(new PublicChannelCreateInfo("channel3", "description3"));
			System.out.println("중복 처리 테스트 실패");
		} catch (IllegalStateException e) {
			System.out.println("중복 처리 테스트 성공");
		}

		// 없는 채널 조회
		try {
			channelService.findChannel(java.util.UUID.randomUUID());
			System.out.println("없는 채널 조회 테스트 실패");
		} catch (NoSuchElementException e) {
			System.out.println("없는 채널 조회 테스트 성공");
		}

		// 반복테스트를 위해 제거
		userService.deleteUser(user1.userId());
		userService.deleteUser(user2.userId());
		channelService.deleteChannel(channel2.channelId());
		channelService.deleteChannel(channel3.channelId());
	}

	private static void messageServiceTest(UserService userService, ChannelService channelService, MessageService messageService) {
		// setUp
		UserInfo user1 = userService.createUser(new UserCreateInfo("user1", "password1", "email1", new byte[]{1}));
		UserInfo user2 = userService.createUser(new UserCreateInfo("user2", "password2", "email2", new byte[]{2}));

		PublicChannelInfo channel1 = channelService.createPublicChannel(new PublicChannelCreateInfo("channel1", "description1"));
		ArrayList<UUID> userList = new ArrayList<>(List.of(user1.userId(), user2.userId()));
		PrivateChannelInfo channel2 = channelService.createPrivateChannel(new PrivateChannelCreateInfo(userList));

		List<byte[]> attachments = new ArrayList<>(List.of(new byte[]{1, 2}));
		MessageInfo message1 = messageService.createMessage(new MessageCreateInfo("content1", user1.userId(),
				channel2.channelId(), attachments));
		MessageInfo message2 = messageService.createMessage(new MessageCreateInfo("content2", user2.userId(),
				channel2.channelId(), attachments));
		// 단건 조회
		MessageInfo findMessage = messageService.findMessage(message1.messageId());
		if(findMessage.equals(message1)) System.out.println("단건 조회 테스트 성공");
		else System.out.println("단건 조회 테스트 실패");

		// 다건 조회
		int messagesSize = messageService.findAll().size();
		if(messagesSize == 2) System.out.println("다건 조회 테스트 성공");
		else System.out.println("다건 조회 테스트 실패");

		// 수정
		MessageInfo message2Updated = messageService.updateMessage(new MessageUpdateInfo(message2.messageId(), "message2_updated"));
		if(message2.messageId().equals(message2Updated.messageId())
				&& message2Updated.content().equals("message2_updated"))
			System.out.println("수정 테스트 성공");
		else
			System.out.println("수정 테스트 실패");

		// 수정 된 데이터 조회
		System.out.printf("수정된 message2 내용 : %s\n", message2Updated.content());

		// 삭제
		int prevSize = messageService.findAll().size();
		messageService.deleteMessage(message1.messageId());
		if(messageService.findAll().size() == prevSize - 1)
			System.out.println("삭제 테스트 성공");
		else
			System.out.println("삭제 테스트 실패");

		// 조회를 통해 삭제되었는지 확인
		try {
			messageService.findMessage(message1.messageId());
		} catch (NoSuchElementException e) {
			System.out.println("message1 삭제 성공하여 조회x");
		}
		// 없는 메세지 조회
		try {
			messageService.findMessage(java.util.UUID.randomUUID());
			System.out.println("없는 메세지 조회 테스트 실패");
		} catch (NoSuchElementException e) {
			System.out.println("없는 메세지 조회 테스트 성공");
		}

		// 반복테스트를 위해 제거
		messageService.deleteMessage(message2.messageId());
		userService.deleteUser(user1.userId());
		userService.deleteUser(user2.userId());
		channelService.deleteChannel(channel1.channelId());
		channelService.deleteChannel(channel2.channelId());
	}

	private static void authServiceTest(UserService userService, AuthService authService) {
		String userName = "user1";
		String password = "password1";
		UserInfo user1 = userService.createUser(new UserCreateInfo(userName, password, "email1", new byte[]{1}));
		UserInfo loginInfo = authService.login(new UserLoginInfo(userName, password));
		System.out.println("login 성공 id : " + loginInfo.userId() + ", email : " + loginInfo.email());
		userService.deleteUser(user1.userId());
	}

	private static void binaryContentServiceTest(UserService userService, BinaryContentService binaryContentService) {
		// setUp
		UserInfo user1 = userService.createUser(new UserCreateInfo("user1", "password1", "email1", new byte[]{1}));
		UserInfo user2 = userService.createUser(new UserCreateInfo("user2", "password2", "email2", new byte[]{2}));

		// 다건 조회
		List<BinaryContentInfo> contentList = binaryContentService.findAll();
		contentList.forEach(bc -> System.out.println("id : " + bc.contentId() + ", createdAt : " + bc.createdAt()));

		// 단건 조회
		BinaryContentInfo findContent = binaryContentService.findBinaryContent(contentList.get(0).contentId());
		System.out.println("id : " + findContent.contentId() + ", createdAt : " + findContent.createdAt());

		userService.deleteUser(user1.userId());
		userService.deleteUser(user2.userId());
	}

	private static void readStatusServiceTest(UserService userService, ChannelService channelService, ReadStatusService readStatusService) {
		// setUp
		UserInfo user1 = userService.createUser(new UserCreateInfo("user1", "password1", "email1", new byte[]{1}));
		UserInfo user2 = userService.createUser(new UserCreateInfo("user2", "password2", "email2", new byte[]{2}));

		ArrayList<UUID> userList = new ArrayList<>(List.of(user1.userId(), user2.userId()));
		PrivateChannelInfo channel2 = channelService.createPrivateChannel(new PrivateChannelCreateInfo(userList));

		// user1의 전체 ReadStatus 조회
		List<ReadStatusInfo> statuses = readStatusService.findAllByUserId(user1.userId());
		statuses.forEach(rs -> System.out.println(rs.channelId()));

		// status1 의 id로 단건 조회
		ReadStatusInfo findStatus = readStatusService.find(statuses.get(0).statusId());
		System.out.println(findStatus);

		userService.deleteUser(user1.userId());
		userService.deleteUser(user2.userId());
		channelService.deleteChannel(channel2.channelId());
	}

	private static void userStatusServiceTest(UserService userService, UserStatusService userStatusService) {

		UserInfo userInfo = userService.createUser(new UserCreateInfo("name1", "password1", "email1", null));
		UserStatusInfo statusInfo = userStatusService.findUserStatus(userInfo.statusId());
		// 유저 생성 후 status 조회
		System.out.println("id : " + statusInfo.statusId() + ", userId: " + statusInfo.userId() + ", isOnline: " + statusInfo.isOnline());

		// 유저 삭제에 따른 UserStatus 삭제 테스트
		userService.deleteUser(userInfo.userId());
		try {
			userStatusService.findUserStatus(userInfo.statusId());
			System.out.println("삭제 테스트 실패");
		} catch (NoSuchElementException e) {
			System.out.println("삭제 테스트 성공");
		}
	}

	private static void printN(String message) {
		System.out.println();
		System.out.println("-".repeat(5) + message + "-".repeat(5));
	}

}
