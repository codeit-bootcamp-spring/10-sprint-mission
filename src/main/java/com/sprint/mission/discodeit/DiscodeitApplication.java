package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.auth.AuthLoginRequestDto;
import com.sprint.mission.discodeit.dto.channel.ChannelRequestUpdateDto;
import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelRequestCreateDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelRequestCreateDto;
import com.sprint.mission.discodeit.dto.common.BinaryContentParam;
import com.sprint.mission.discodeit.dto.message.MessageRequestCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageRequestUpdateDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.user.UserRequestCreateDto;
import com.sprint.mission.discodeit.dto.user.UserRequestUpdateDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;

import com.sprint.mission.discodeit.service.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.List;


@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		// 0. 서비스 초기화
		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);
		AuthService authService = context.getBean(AuthService.class);
		BinaryContentService binaryContentService = context.getBean(BinaryContentService.class);

		System.out.println("=== [시작] 서비스 테스트 ===");
//        //================================ 기본 기능 ================================
		System.out.println("==기본 기능==");
//        //---------------------------------1. user---------------------------------
//        // 1-1. user 등록 및 조회
		byte[] imageData1 = "test-image1".getBytes(StandardCharsets.UTF_8);
		BinaryContentParam bcp1 = new BinaryContentParam(imageData1, "image/jpg");
		UserRequestCreateDto userRequest1 = new UserRequestCreateDto
				("전창현", "qwr123@gmail.com", "animal12", bcp1);
		UserResponseDto user1 = userService.create(userRequest1);
		System.out.println("1-1. 유저 등록 완료: " + user1.username() +
				"\n프로필 이미지 id: " + user1.profileId() + "\n생성시간: " + user1.createdAt());
		System.out.println("온라인 여부: " + user1.online());

		byte[] imageData2 = "test-image2".getBytes(StandardCharsets.UTF_8);
		BinaryContentParam bcp2 = new BinaryContentParam(imageData2, "image/png");
		UserRequestCreateDto userRequest2 = new UserRequestCreateDto
				("김한수", "gkstn78@naver.com", "babari7", bcp2);
		UserResponseDto user2 = userService.create(userRequest2);
		System.out.println("1-1. 유저 등록 완료: " + user2.username() +
				"\n프로필 이미지 id: " + user2.profileId() + "\n생성시간: " + user2.createdAt());
		System.out.println("온라인 여부: " + user2.online());

		UserRequestCreateDto userRequest3 = new UserRequestCreateDto
				("이백호", "qorgh534@kongju.co.kr", "holymoly13", null);
		UserResponseDto user3 = userService.create(userRequest3);
		System.out.println("1-1. 유저 등록 완료: " + user3.username() +
				"\n프로필 이미지 id: " + user3.profileId() + "\n생성시간: " + user3.createdAt());
		System.out.println("온라인 여부: " + user3.online());
		System.out.println();


		// 1-2. user 다건 조회
		System.out.println("1-2. 유저 다건 조회: " + "총" + userService.findAll().size() + "명");
		System.out.println();

		// 1-3. user 정보 수정 및 조회
		UserRequestUpdateDto userRequestUpdate1 = new UserRequestUpdateDto
				(user1.id(),"김수한", "suhan78@gmail.com", "man5", null);
		UserResponseDto userUpdate1 = userService.update(userRequestUpdate1);
		System.out.println("1-3-1. 유저 정보 수정 완료: " + "\n이름: " + userUpdate1.username()
				+ "\n이메일: " + userUpdate1.email() + "\n프로필 이미지 id: " + userUpdate1.profileId());
		System.out.println();

		UserRequestUpdateDto userRequestUpdate2 = new UserRequestUpdateDto
				(user2.id(),"정몽주", null, null, null);
		UserResponseDto userUpdate2 = userService.update(userRequestUpdate2);
		System.out.println("1-3-2. 유저 이름 수정 완료: " + "\n이름: " + userUpdate2.username()
				+ "\n이메일: " + userUpdate2.email() + "\n프로필 이미지 id: " + userUpdate2.profileId());
		System.out.println();

		UserRequestUpdateDto userRequestUpdate3 = new UserRequestUpdateDto
				(user3.id(),null, "ahdwn45@naver.com", null, null);
		UserResponseDto userUpdate3 = userService.update(userRequestUpdate3);
		System.out.println("1-3-3. 유저 이메일 수정 완료: " + "\n이름: " + userUpdate3.username()
				+ "\n이메일: " + userUpdate3.email() + "\n프로필 이미지 id: " + userUpdate3.profileId());
		System.out.println();

		byte[] imageData3 = "test-image3".getBytes(StandardCharsets.UTF_8);
		BinaryContentParam bcp3 = new BinaryContentParam(imageData3, "image/jpg");
		UserRequestUpdateDto userRequestUpdate4 = new UserRequestUpdateDto
				(user3.id(),null, null, null, bcp3);
		UserResponseDto userUpdate4 = userService.update(userRequestUpdate4);
		System.out.println("1-3-3. 유저 프로필 이미지 수정 완료: " + "\n이름: " + userUpdate4.username()
				+ "\n이메일: " + userUpdate3.email() + "\n프로필 이미지 id: " + userUpdate4.profileId());
		System.out.println();

		// 1-4. user 정보 삭제 및 확인
		userService.delete(user3.id());
		System.out.println("1-4. 유저 정보 삭제 완료");

		// 1-5. user 다건 재조회로 정보 삭제 확인
		System.out.println("1-5. 유저 다건 재조회: " + "총" + userService.findAll().size() + "명");
		System.out.println();

		// 1-6. user 로그인
		AuthLoginRequestDto alRequest = new AuthLoginRequestDto("김수한", "man5");
		User loginUser = authService.login(alRequest);
		System.out.println("로그인 성공: " + loginUser.getUserName());
		System.out.println();

		//---------------------------------2. channel---------------------------------
		// 2-1. channel 등록 및 조회
		PublicChannelRequestCreateDto public1 = new PublicChannelRequestCreateDto
				("공용채널1", "퍼블릭1입니다.");
		ChannelResponseDto channel1 = channelService.createPublic(public1);
		System.out.println("2-1. 채널 등록 완료: " + channel1.type() + " "
				+ channel1.channelName());

		PrivateChannelRequestCreateDto private1 = new PrivateChannelRequestCreateDto(List.of(user1.id(), user2.id()));
		ChannelResponseDto channel2 = channelService.createPrivate(private1);
		System.out.println("2-1. 채널 등록 완료: " + channel2.type() + " 채널에 참가한 유저id 목록: " +
				"총" + channel2.joinedUserIds().size() + "명");

		PrivateChannelRequestCreateDto private2 = new PrivateChannelRequestCreateDto(List.of(user1.id()));
		ChannelResponseDto channel3 = channelService.createPrivate(private2);
		System.out.println("2-1. 채널 등록 완료: " + channel3.type() + " 채널에 참가한 유저id 목록: "+
				"총" + channel3.joinedUserIds().size() + "명");
		System.out.println();

		// 2-2. user가 속한 채널 조회
		System.out.println("2-2. user1이 속한 채널 조회(Public채널 포함): " + "총" +
				channelService.findAllByUserId(user1.id()).size() + "개");
		System.out.println("2-2. user2가 속한 채널 조회(Public채널 포함): " + "총" +
				channelService.findAllByUserId(user2.id()).size() + "개");
		System.out.println();

		// 2-3. channel 정보 수정 및 조회
		ChannelRequestUpdateDto crUpdate = new ChannelRequestUpdateDto
				(channel1.id(), "단체채널1", "단체 채널입니다.");
		ChannelResponseDto updatedChannel = channelService.updateChannel(crUpdate);
		System.out.println("2-3. Public 채널 수정 완료: " + updatedChannel.channelName());
		System.out.println();

		// 2-4. channel 정보 삭제 및 확인
		channelService.delete(channel3.id());
		System.out.println("2-4. private 채널 삭제 완료");
		System.out.println();

		// 2-5. channel  user가 속한 채널 재조회
		System.out.println("2-5. user1이 속한 채널 조회(Public채널 포함): " + "총" +
				channelService.findAllByUserId(user1.id()).size() + "개");
		System.out.println("2-5. user2가 속한 채널 조회(Public채널 포함): " + "총" +
				channelService.findAllByUserId(user2.id()).size() + "개");
		System.out.println();

//        ---------------------------------3. message---------------------------------
		// 3-1. message 등록 및 조회
		byte[] file1 = "test-file1".getBytes(StandardCharsets.UTF_8);
		BinaryContentParam bcpFile1 = new BinaryContentParam(file1, "image/jpeg");
		byte[] file2 = "test-file2".getBytes(StandardCharsets.UTF_8);
		BinaryContentParam bcpFile2 = new BinaryContentParam(file2, "image/png");
		MessageRequestCreateDto messageRequest1 = new MessageRequestCreateDto
				("안녕하세요", channel1.id(), user1.id(), List.of(bcpFile1, bcpFile2));
		MessageResponseDto message1 = messageService.create(messageRequest1);
		System.out.println("3-1. 메시지 등록 완료: [" + channel1.channelName() +
				"] [" + user1.username() + "] " + message1.content() + " " +
				"(첨부파일 " + message1.attachmentIds().size() + "개)");

		MessageRequestCreateDto messageRequest2 = new MessageRequestCreateDto
				("식사 맛있게 하세요", channel2.id(), user2.id(), null);
		MessageResponseDto message2 = messageService.create(messageRequest2);
		System.out.println("3-1. 메시지 등록 완료: [private채널]" +
				" [" + user2.username() + "] " + message2.content() + " " +
				"(첨부파일 " + message2.attachmentIds().size() + "개)");

		byte[] file3 = "test-file3".getBytes(StandardCharsets.UTF_8);
		BinaryContentParam bcpFile3 = new BinaryContentParam(file3, "image/jpg");
		MessageRequestCreateDto messageRequest3 = new MessageRequestCreateDto
				("수고하셨습니다", channel2.id(), user1.id(), List.of(bcpFile3));
		MessageResponseDto message3 = messageService.create(messageRequest3);
		System.out.println("3-1. 메시지 등록 완료: [private채널]"  +
				" [" + user1.username() + "] " + message3.content() + " " +
				"(첨부파일 " + message3.attachmentIds().size() + "개)");
		System.out.println();

		// 3-2. 채널에 속한 message 조회
		System.out.println("3-2. 공용채널1에 속한 메세지 조회: " + "총" +
				messageService.findByChannelId(channel1.id()).size() + "개");
		System.out.println("3-2. private채널에 속한 메세지 조회: " + "총" +
				messageService.findByChannelId(channel2.id()).size() + "개");
		System.out.println();

		// 3-3. message 수정 및 조회
		MessageRequestUpdateDto mrUpdate = new MessageRequestUpdateDto(message2.id(),"안녕히가세요");
		MessageResponseDto updatedMessage = messageService.update(mrUpdate);
		System.out.println("3-3. 메세지 내용 수정 완료: [" + user2.username() + "] " + updatedMessage.content());
		System.out.println();

		// 3-4. message 삭제 및 확인
		messageService.delete(message3.id());
		System.out.println("3-4. 메시지 삭제 완료");
		System.out.println();

		// 3-5. 채널에 속한 message 재조회
		System.out.println("3-5. private채널에 속한 message 조회: " + "총" +
				messageService.findByChannelId(channel2.id()).size() + "개");
		System.out.println();

		// 3-6. user의 message 목록 조회
		List<MessageResponseDto> userMessages = messageService.readMessagesByUser(user1.id());
		System.out.println("3-6. 유저의 메시지 목록 조회");
		System.out.println(userUpdate1.username() + "의 메시지 목록: " + "총" + userMessages.size() + "개");
		userMessages.forEach(m ->
				System.out.println("[" + userUpdate1.username() + "]: " + m.content())
		);
		System.out.println();


		System.out.println("=== [종료] 서비스 테스트 ===");
	}

}
