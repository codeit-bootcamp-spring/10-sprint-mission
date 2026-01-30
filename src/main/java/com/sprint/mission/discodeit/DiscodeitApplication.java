package com.sprint.mission.discodeit;

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

import java.util.List;
import java.util.NoSuchElementException;

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);
		// context로부터 Bean 조회하여 서비스 구현체 할당
		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);

		printN("유저 테스트 시작");
		userServiceTest(userService);
		printN("채널 테스트 시작");
		channelServiceTest(userService, channelService);
		printN("메세지 테스트 시작");
		messageServiceTest(userService, channelService, messageService);
	}

	private static void userServiceTest(UserService userService) {
		// setUp
		User user1 = userService.createUser("user1", "1234", "");
		User user2 = userService.createUser("user2", "4567", "");

		// 단건 조회
		if(user1.equals(userService.getUser(user1.getId())))
			System.out.println("단건 조회 테스트 성공");
		else
			System.out.println("단건 조회 테스트 실패");

		// 다건 조회
		if(userService.getAllUsers().size() == 2)
			System.out.println("다건 조회 테스트 성공");
		else
			System.out.println("다건 조회 테스트 실패");

		// 수정
		user2 = userService.updateUser(user2.getId(), "user2_수정", "45678", "user2email");
		User user2Updated = userService.getUser(user2.getId());
		if (user2.getUserName().equals(user2Updated.getUserName())
				&& user2.getEmail().equals(user2Updated.getEmail()))
			System.out.println("수정 테스트 성공");
		else
			System.out.println("수정 테스트 실패");

		// 수정 된 데이터 조회
		System.out.printf(
				"수정된 user2 이름 : %s, 비밀번호 : %s, 이메일 : %s%n", user2Updated.getUserName(),
				user2Updated.getPassword(), user2Updated.getEmail());

		// 삭제
		int prevSize = userService.getAllUsers().size();
		userService.deleteUser(user1.getId());
		if(userService.getAllUsers().size() == prevSize - 1)
			System.out.println("삭제 테스트 성공");
		else
			System.out.println("삭제 테스트 실패");

		// 조회를 통해 삭제되었는지 확인
		try {
			userService.getUser(user1.getId());
		} catch (NoSuchElementException e) {
			System.out.println("user1 삭제 성공하여 조회x");
		}

		// 중복 생성
		User user3 = userService.createUser("user3", "4567", "");
		try {
			userService.createUser("user3", "4567", "");
			System.out.println("중복 처리 테스트 실패");
		} catch (IllegalStateException e) {
			System.out.println("중복 처리 테스트 성공");
		}

		// 없는 사용자 조회
		try {
			userService.getUser(java.util.UUID.randomUUID());
			System.out.println("없는 사용자 조회 테스트 실패");
		} catch (NoSuchElementException e) {
			System.out.println("없는 사용자 조회 테스트 성공");
		}

		// 반복테스트를 위해 제거
		userService.deleteUser(user2.getId());
		userService.deleteUser(user3.getId());
	}

	private static void channelServiceTest(UserService userService, ChannelService channelService) {
		// setUp
		User user1 = userService.createUser("user1", "1234", "");
		User user2 = userService.createUser("user2", "4567", "");
		Channel channel1 = channelService.createChannel("channel1", ChannelType.PUBLIC, "channel1 description");
		Channel channel2 = channelService.createChannel("channel2", ChannelType.PRIVATE, "channel2 description");
		channelService.joinChannel(channel1.getId(), user1.getId());
		channelService.joinChannel(channel1.getId(), user2.getId());
		// join을 메모리에 반영
		user1 = userService.getUser(user1.getId());
		user2 = userService.getUser(user2.getId());
		channel1 = channelService.getChannel(channel1.getId());
		// 단건 조회
		if(channel1.getChannelName().equals("user1")) System.out.println("단건 조회 테스트 성공");
		else System.out.println("단건 조회 테스트 실패");

		// 다건 조회
		List<Channel> channels = channelService.getAllChannels();
		if(channels.size() == 2) System.out.println("다건 조회 테스트 성공");
		else System.out.println("다건 조회 테스트 실패");

		// 수정
		channel2 = channelService.updateChannel(channel2.getId(), "channel2_수정됨", ChannelType.PRIVATE, "수정된 description");
		Channel updatedChannel = channelService.getChannel(channel2.getId());
		if(channel2.equals(updatedChannel)
				&& channel2.getChannelName().equals(updatedChannel.getChannelName()))
			System.out.println("수정 테스트 성공");
		else
			System.out.println("수정 테스트 실패");

		// 수정 된 데이터 조회
		System.out.printf("수정된 channel2 이름 : %s, 채널타입 : %s, 설명 : %s%n"
				, channel2.getChannelName(), channel2.getChannelType(), channel2.getDescription());

		// 삭제
		int prevSize = channelService.getAllChannels().size();
		channelService.deleteChannel(channel2.getId());
		if(channelService.getAllChannels().size() == prevSize - 1)
			System.out.println("삭제 테스트 성공");
		else
			System.out.println("삭제 테스트 실패");

		// 조회를 통해 삭제되었는지 확인
		try {
			channelService.getChannel(channel1.getId());
		} catch (NoSuchElementException e) {
			System.out.println("channel1 삭제 성공하여 조회x");
		}

		// 채널 중복 테스트
		Channel channel3 = channelService.createChannel("channel3", ChannelType.PUBLIC, "channel3 description 중복");
		try {
			channelService.createChannel("channel3", ChannelType.PUBLIC, "channel3 description 중복");
			System.out.println("중복 처리 테스트 실패");
		} catch (IllegalStateException e) {
			System.out.println("중복 처리 테스트 성공");
		}

		// 없는 채널 조회
		try {
			channelService.getChannel(java.util.UUID.randomUUID());
			System.out.println("없는 채널 조회 테스트 실패");
		} catch (NoSuchElementException e) {
			System.out.println("없는 채널 조회 테스트 성공");
		}

		// 반복테스트를 위해 제거
		channelService.deleteChannel(channel1.getId());
		channelService.deleteChannel(channel3.getId());
		userService.deleteUser(user1.getId());
		userService.deleteUser(user2.getId());
	}

	private static void messageServiceTest(UserService userService, ChannelService channelService, MessageService messageService) {
		// setUp
		User user1 = userService.createUser("user1", "1234", "");
		Channel channel1 = channelService.createChannel("channel1", ChannelType.PUBLIC, "channel1 description");
		Message message1 = messageService.createMessage("message1 content", user1.getId(), channel1.getId());
		Message message2 = messageService.createMessage("message2 content", user1.getId(), channel1.getId());
		// 단건 조회
		message1 = messageService.getMessage(message1.getId());
		if(message1.getContent().equals("message1 content")) System.out.println("단건 조회 테스트 성공");
		else System.out.println("단건 조회 테스트 실패");

		// 다건 조회
		List<Message> messages = messageService.getAllMessages();
		if(messages.size() == 2) System.out.println("다건 조회 테스트 성공");
		else System.out.println("다건 조회 테스트 실패");

		// 수정
		message2 = messageService.updateMessage(message2.getId(), "message2 content updated");
		Message updatedMessage = messageService.getMessage(message2.getId());
		System.out.println(message2.getContent());
		System.out.println(updatedMessage.getContent());
		if(message2.equals(updatedMessage)
				&& message2.getContent().equals(updatedMessage.getContent()))
			System.out.println("수정 테스트 성공");
		else
			System.out.println("수정 테스트 실패");

		// 수정 된 데이터 조회
		System.out.printf("수정된 message2 내용 : %s\n", updatedMessage.getContent());

		// 삭제
		int prevSize = messageService.getAllMessages().size();
		messageService.deleteMessage(message1.getId());
		if(messageService.getAllMessages().size() == prevSize - 1)
			System.out.println("삭제 테스트 성공");
		else
			System.out.println("삭제 테스트 실패");

		// 조회를 통해 삭제되었는지 확인
		try {
			messageService.getMessage(message1.getId());
		} catch (NoSuchElementException e) {
			System.out.println("message1 삭제 성공하여 조회x");
		}
		// 없는 메세지 조회
		try {
			messageService.getMessage(java.util.UUID.randomUUID());
			System.out.println("없는 메세지 조회 테스트 실패");
		} catch (NoSuchElementException e) {
			System.out.println("없는 메세지 조회 테스트 성공");
		}

		// 반복테스트를 위해 제거
		messageService.deleteMessage(message2.getId());
		userService.deleteUser(user1.getId());
		channelService.deleteChannel(channel1.getId());
	}

	private static void printN(String message) {
		System.out.println();
		System.out.println("-".repeat(5) + message + "-".repeat(5));
	}

}
