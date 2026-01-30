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
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {

		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		UserRepository userRepository = context.getBean(UserRepository.class);
		ChannelRepository channelRepository = context.getBean(ChannelRepository.class);
		MessageRepository messageRepository = context.getBean(MessageRepository.class);

		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);
		// 유저 생성
		User user1 = userService.create("김현재");
		User user2 = userService.create("기면재");

		// 친구 추가
		userService.addFriend(user1.getId(), user2.getId());

		//채널 생성
		Channel channel1 = channelService.createPublic("스프린트");
		Channel channel2 = channelService.createPublic("코드잇");

		// 채널 참여
		test(() -> channelService.joinUsers(channel1.getId(), user1.getId(),user2.getId()));
		test(() ->  channelService.joinUsers(channel2.getId(), user1.getId()));

		// 메시지 생성
		Message message1 = messageService.create(user1.getId(), "안녕하세요", channel1.getId());
		Message message2 = messageService.create(user1.getId(), "안녕히 계세요", channel1.getId());
		Message message3 = messageService.create(user2.getId(), "안녕!", channel2.getId());

		System.out.println("--- 조회 ---");

		// 조회
		test(() -> userService.findUser(user1.getId()));
		test(() -> userService.findUser(user2.getId()));
		userService.findAllUsers();

		test(() -> channelService.findChannel(channel1.getId()));
		test(() -> channelService.findChannel(channel2.getId()));
		channelService.findAllChannel();

		test(() -> messageService.findMessage(message1.getId()));
		test(() -> messageService.findMessage(message2.getId()));
		messageService.findAllMessages();

		test(() -> userService.findFriends(user1.getId()));
		test(() -> userService.findFriends(user2.getId()));

		test(() -> messageService.findMessageByKeyword(channel1.getId(), "안녕"));

		test(() -> channelService.findAllChannelsByUserId(user1.getId()));
		test(() -> messageService.findAllMessagesByChannelId(channel1.getId()));

		System.out.println("");

		System.out.println("--- 수정&조회 ---");
		// 수정 & 조회

		test(() -> userService.update(user1.getId(), "현재"));
		test(() -> userService.findUser(user2.getId()));

		test(() -> channelService.update(channel1.getId(), "코드잇"));
		test(() -> channelService.findChannel(channel1.getId()));


		test(() -> messageService.update(message1.getId(), user1.getId(), "Hello"));
		test(() -> messageService.update(message1.getId(), user2.getId(), "Hi"));
		test(() -> messageService.findMessage(message1.getId()));

		test(() -> channelService.findAllChannelsByUserId(user1.getId()));
		userService.findAllUsers();
		messageService.findAllMessages();
		channelService.findAllChannel();

		System.out.println("--- 삭제&조회 ---");
		//삭제 & 조회
		test(() -> userService.delete(user1.getId()));
		test(() -> userService.findUser(user1.getId()));
		test(() -> userService.findFriends(user2.getId()));

		test(() -> messageService.delete(message2.getId()));
		test(() -> messageService.findMessage(message1.getId()));


		test(() -> channelService.delete(channel1.getId()));
		test(() -> channelService.findChannel(channel1.getId()));

		userService.findAllUsers();
		messageService.findAllMessages();
		channelService.findAllChannel();
	}

	private static void test(Runnable action){
		try{
			action.run();
		}
		catch (Exception e){
			System.out.println(e.getMessage());
		}
	}

}
