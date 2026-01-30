package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDto;
import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {
	static UserResponseDto setupUser(UserService userService) {
		return userService.create(new UserCreateDto("woody", "woody@codeit.com", "woody1234",null));
	}

	static ChannelResponseDto setupChannel(ChannelService channelService) {
		return channelService.create(new PublicChannelCreateDto("공지", "공지 채널입니다."));
	}

	static void messageCreateTest(MessageService messageService, UUID channelId, UUID authorId) {
		MessageResponseDto messageResponseDto= messageService.create(new MessageCreateDto("안녕하세요.", channelId, authorId,null));
		System.out.println("메시지 생성: " + messageResponseDto);
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);
		// TODO context에서 Bean을 조회하여 각 서비스 구현체 할당 코드 작성하세요.
		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);

		// 셋업
		UUID userId1 = setupUser(userService).userId();
		UUID channelId1 = setupChannel(channelService).id();
		// 테스트
		messageCreateTest(messageService, channelId1,userId1);
		//사용자
		userService.update(userId1,new UserUpdateDto("john",null,null,null));
		UUID userId2 = userService.create(new UserCreateDto("kim","asdf@naer","123",null)).userId();
		userService.findAll().forEach(System.out::println);
		//채널
		channelService.update(channelId1,new ChannelUpdateDto("공지방!","공지방으로~~변경"));
		UUID channelId2 = channelService.create(new PrivateChannelCreateDto(List.of(userId1,userId2))).id();//비공개 채널
		channelService.findAllByUserId(userId2).forEach(System.out::println);
		//메세지
		UUID messageId1 = messageService.create(new MessageCreateDto("나도 안녕!",channelId1,userId2,null)).id();
		messageService.findAllByChannelId(channelId1).forEach(System.out::println);
		System.out.println("-------------");
		messageService.update(messageId1,new MessageUpdateDto("나도 안녕한다!!!!"));
		messageService.findAllByChannelId(channelId1).forEach(System.out::println);
		UUID messageId2 = messageService.create(new MessageCreateDto("비밀 안녕",channelId2,userId2,null)).id();
		UUID messageId3 = messageService.create(new MessageCreateDto("비밀 안녕2",channelId2,userId1,null)).id();
		UUID messageId4 = messageService.create(new MessageCreateDto("비밀 안녕3",channelId2,userId2,null)).id();
		System.out.println("------비밀방 메세지 목록----------");
		messageService.findAllByChannelId(channelId2).forEach(System.out::println);
		System.out.println("------메세지 삭제 비밀방-------");
		messageService.delete(messageId2);
		messageService.findAllByChannelId(channelId2).forEach(System.out::println);
		System.out.println("--------유저1이 속한 채널목록----------");
		channelService.findAllByUserId(userId1).forEach(System.out::println);
		System.out.println("-----비밀채널 삭제 후 목록 조회------");
		channelService.delete(channelId2);
		channelService.findAllByUserId(userId1).forEach(System.out::println);

		System.out.println("-------유저 삭제--------");
		//삭제
		userService.delete(userId1);
		userService.findAll().forEach(System.out::println);
		channelService.findAllByUserId(userId2).forEach(System.out::println);//멤버에서 삭제된 사용자 제외
		messageService.findAllByChannelId(channelId1).forEach(System.out::println);//메세지는 그대로 남아있음


	}
}
