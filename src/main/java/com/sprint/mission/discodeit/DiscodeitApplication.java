package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {
	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);
		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);

		AuthService authService = context.getBean(AuthService.class);

		// 유저 load
		loadUser(userService);

		// 유저 생성
//		try {
//			User user = setUser(userService);
//			System.out.println("\n user = " + user);
//		} catch (Exception e) {
//			System.out.println(e);
//		}

		// 로그인?
		UserResponse loginUser = userLogin(authService);
		System.out.println("로그인한 유저 : " + loginUser.userName() + "(" + loginUser.email() + ") : " + loginUser.userId());

		// 채널 load
		loadChannel(loginUser, channelService);
		loadMessage(loginUser, channelService, messageService);

//		// 채널 생성
//		try {
//			Channel channel = setChannel(loginUser, userService, channelService);
//			System.out.println("\n channel = " + channel);
//		} catch (Exception e) {
//			System.out.println(e);
//		}

		// 테스트
//		messageCreateTest(loginUser, channelService, messageService);

		loadUser(userService);
		loadChannel(loginUser, channelService);
		loadMessage(loginUser, channelService, messageService);
	}

	// 로그인
	static UserResponse userLogin(AuthService authService) {
		System.out.println("\n========== hyun 로그인 ==========");
		String un = "hyun"; // hyun / chung /
		String pw = "hyun1234"; // hyun1234 / chung /
		LoginRequest loginRequest = new LoginRequest(un, pw);
		return authService.login(loginRequest);
	}
	// 유저 생성
	static User setUser(UserService userService) {
//		UserCreateRequest userCreateRequest = new UserCreateRequest("hyun@codeit.com", "hyun", "hyunNick", "hyun1234", "20000401", null);
		UserCreateRequest userCreateRequest = new UserCreateRequest("chung@codeit.com", "chung", "chungNick", "chung", "20000401", null);
		return userService.createUser(userCreateRequest);
	}
	// 채널 생성
	static Channel setChannel(UserResponse loginUser, ChannelService channelService) {
		UUID loginUserId = loginUser.userId();
		String channelName = "C4";
		String channelDescription = "C4D";
		PublicChannelCreateRequest publicChannelCreateRequest = new PublicChannelCreateRequest(loginUserId, channelName, channelDescription);
//		PrivateChannelCreateRequest privateChannelCreateRequest = new PrivateChannelCreateRequest(loginUserId, null);
		return channelService.createPublicChannel(publicChannelCreateRequest);
//		return channelService.createPrivateChannel(privateChannelCreateRequest);
	}
	// 메시지 생성
	static void messageCreateTest(UserResponse loginUser, ChannelService channelService, MessageService messageService) {
		UUID loginUserId = loginUser.userId();
		List<ChannelResponse> channels = channelService.findAllByUserId(loginUserId);
		if (channels.isEmpty()) {
			System.out.println("참여 채널 없음");
			return;
		}

		// 특정 채널 1개
		UUID channelId = channels.get(0).id();
		MessageCreateRequest messageCreateRequest = new MessageCreateRequest(channelId, loginUserId, "m1", null);
		Message message = messageService.createMessage(messageCreateRequest);
		System.out.println("메세지 생성: " + message);

	}

	// 전체 유저 load
	static void loadUser(UserService userService) {
		System.out.println("\n========== 유저 Load ==========");
		System.out.println("생성된 전체 user ID = " + userService.findAllUsers().stream().map(user -> user.userName() + "(" + user.email() + "): " + user.userId()).toList());
	}
	// 채널 load
	static void loadChannel(UserResponse loginUser, ChannelService channelService) {
		System.out.println("\n========== 채널 Load ==========");
		UUID loginUserId = loginUser.userId();

		List<ChannelResponse> channels = channelService.findAllByUserId(loginUserId);
		System.out.println("유저가 보는 채널 전체 : " + channels.stream().map(c -> c.channelName() + "(owner=" + c.ownerId() + ") : " + c.id()).toList());
	}
	static void loadMessage(UserResponse loginUser, ChannelService channelService, MessageService messageService) {
		System.out.println("\n========== 메세지 Load ==========");
		UUID loginUserId = loginUser.userId();
		List<ChannelResponse> channels = channelService.findAllByUserId(loginUserId);

		if (channels.isEmpty()) {
			System.out.println("참여 채널 없음");
			return;
		}

		// 특정 채널 1개
		UUID channelId = channels.get(0).id();
		System.out.println("특정 채널의 메세지 목록 = " + messageService.findAllByChannelId(channelId).stream().map(m -> m.getId() + ": " + m.getContent()).toList());

		System.out.println("생성된 전체 message ID = " + messageService.findAllMessages().stream().map(message -> message.getId() + "(channel=" + message.getChannel().getId() + ", " + "author= " + message.getAuthor().getId() + "): " + message.getContent()).toList());
	}

}
