package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.channel.ChannelCreatePrivateDto;
import com.sprint.mission.discodeit.dto.channel.ChannelCreatePublicDto;
import com.sprint.mission.discodeit.dto.channel.response.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import com.sprint.mission.discodeit.entity.type.FileType;
import com.sprint.mission.discodeit.repository.*;
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

		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);


		UserRepository userRepository = context.getBean(UserRepository.class);
		ChannelRepository channelRepository = context.getBean(ChannelRepository.class);
		MessageRepository messageRepository = context.getBean(MessageRepository.class);
		BinaryContentRepository binaryContentRepository = context.getBean(BinaryContentRepository.class);
		UserStatusRepository userStatusRepository = context.getBean(UserStatusRepository.class);
		ReadStatusRepository readStatusRepository = context.getBean(ReadStatusRepository.class);

		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);

		// 유저 생성
		UserResponseDto user1 = userService.create(new UserCreateRequestDto("김현재",
				"fred@gmail.com",
				"123123",
				new BinaryContentCreateDto("얼굴이미지", FileType.IMAGE, "이미지".getBytes())));
		UserResponseDto user2 = userService.create(new UserCreateRequestDto("기면재",
				"rose@gmail.com",
				"123456",
				new BinaryContentCreateDto("얼굴이미지2",FileType.IMAGE,	"이미지".getBytes())));
		UserResponseDto user3 = userService.create(new UserCreateRequestDto("김재현",
				"sonata@gmail.com",
				"123123",
				null));


		// 친구 추가
		userService.addFriend(user1.getUserId(), user2.getUserId());

		//채널 생성
		// 공용
		ChannelResponseDto channel1 = channelService.createPublic(new ChannelCreatePublicDto("스프린트"));
		// 프라이빗
		List<UUID> privateUserList = List.of(user1.getUserId(),user2.getUserId());
		ChannelResponseDto channel2 = channelService.createPrivate(new ChannelCreatePrivateDto("코드잇",privateUserList));

		// 채널 참여
		test(() -> channelService.joinUsers(channel1.getId(), user1.getUserId(),user2.getUserId()));
		test(() ->  channelService.joinUsers(channel2.getId(), user3.getUserId()));

		// 메시지 생성
		MessageResponseDto message1 = messageService.create(new MessageCreateDto(user1.getUserId(),
				channel1.getId(),
				 "안녕하세요",
				new ArrayList<>()));
		MessageResponseDto message2 = messageService.create(new MessageCreateDto(user1.getUserId(),
				channel1.getId(),
				"안녕히 계세요",
				new ArrayList<>()));
		MessageResponseDto message3 = messageService.create(new MessageCreateDto(user2.getUserId(),
				channel2.getId(),
				"안녕!",
				new ArrayList<>()));

		System.out.println("--- 조회 ---");

		// 조회
		test(() -> userService.findUser(user1.getUserId()));
		test(() -> userService.findUser(user2.getUserId()));
		userService.findAllUsers();

		test(() -> channelService.findChannel(channel1.getId()));
		test(() -> channelService.findChannel(channel2.getId()));
		channelService.findAllChannelsByUserId(user1.getUserId());

		test(() -> messageService.findMessage(message1.getId()));
		test(() -> messageService.findMessage(message2.getId()));
		System.out.println("채널의 모든 메시지");
		messageService.findAllMessagesByChannelId(channel1.getId());

		test(() -> userService.findFriends(user1.getUserId()));
		test(() -> userService.findFriends(user2.getUserId()));

		System.out.println("메시지 키워드");
		test(() -> messageService.findMessageByKeyword(channel1.getId(), "안녕"));

		test(() -> channelService.findAllChannelsByUserId(user1.getUserId()));
		test(() -> messageService.findAllMessagesByChannelId(channel2.getId()));

		System.out.println("");

		System.out.println("--- 수정&조회 ---");
		// 수정 & 조회

		test(() -> userService.update(user1.getUserId(), new UserUpdateDto("현재",
				"fredsonata@gmail.com",
				new BinaryContentCreateDto("얼굴이미지3", FileType.IMAGE, "이미지3".getBytes()),
				"1111")));

		test(() -> userService.findUser(user1.getUserId()));



		test(() -> messageService.update(new MessageUpdateDto(message1.getId(),
				user1.getUserId(),
				"Hello")));
		test(() -> messageService.findMessage(message1.getId()));

		test(() -> channelService.findAllChannelsByUserId(user1.getUserId()));
		userService.findAllUsers();
		messageService.findAllMessagesByChannelId(channel1.getId());
		channelService.findAllChannelsByUserId(user1.getUserId());

		System.out.println("--- 삭제&조회 ---");
		//삭제 & 조회
		test(() -> userService.delete(user1.getUserId()));
		System.out.println("--- user1 ---");
		test(() -> userService.findUser(user1.getUserId()));
		System.out.println("--- user1 ---");
		test(() -> userService.findUser(user2.getUserId()));
		System.out.println("--- user2 친구 ---");
		test(() -> userService.findFriends(user2.getUserId()));



		test(() -> messageService.delete(message2.getId()));

		test(() -> messageService.findMessage(message1.getId()));


		test(() -> channelService.delete(channel1.getId()));
		test(() -> channelService.findChannel(channel1.getId()));

		userService.findAllUsers();
		test(() -> messageService.findAllMessagesByChannelId(channel1.getId()));
		test(() -> channelService.findAllChannelsByUserId(user1.getUserId()));
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
