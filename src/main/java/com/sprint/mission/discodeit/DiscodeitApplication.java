package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentProfileRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static com.sprint.mission.discodeit.entity.ChannelType.TEXT;

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {

		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);
		BinaryContentRepository contentRepo = context.getBean(BinaryContentRepository.class);
		UserStatusRepository userStatusRepo = context.getBean(UserStatusRepository.class);
		ReadStatusRepository readStatusRepo = context.getBean(ReadStatusRepository.class);


		System.out.println("\n========= [1. User 및 인증 테스트] =========");
		// 유저 생성
		BinaryContent profileImg1 = contentRepo.save(new BinaryContent("profile1.png", new byte[]{0}));
		BinaryContentProfileRequest profileDto1 = new BinaryContentProfileRequest(profileImg1.getFileName(), profileImg1.getData());
		UserCreateRequest userReq1 = new UserCreateRequest("UserA", "nickA", "a@test.com", "1234", profileDto1);
		UserResponse user1 = userService.create(userReq1);
		System.out.println("[등록] user1 계정 및 UserStatus 생성 완료");

		// 시간 타입(Instant) 확인
		System.out.println("[시간 확인] 생성 시간 타입: " + user1.createdAt().getClass().getSimpleName());

		// 온라인 상태 확인
		System.out.println("[상태 확인] 현재 온라인 여부: " + user1.isOnline());

		// 중복 가입 테스트
		shouldFail(() -> userService.create(userReq1), "이메일 중복 가입 차단");


		System.out.println("\n========= [2. Channel 및 읽음 상태 테스트] =========");
		// PRIVATE 채널 생성
		ChannelResponse pChannel1 = channelService.createPrivateChannel(new PrivateChannelCreateRequest(List.of(user1.id()), TEXT));
		System.out.println("[등록] PRIVATE 채널 생성 완료");

		// PRIVATE 채널 수정 금지 테스트
		shouldFail(() -> channelService.update(pChannel1.id(), null), "PRIVATE 채널 수정 금지");


		System.out.println("\n========= [3. Message 및 첨부파일 테스트] =========");
		// 첨부파일 메시지 생성
		BinaryContent file1 = contentRepo.save(new BinaryContent("test1.png", new byte[]{1,2,3}));
		MessageResponse msg1 = messageService.create(new MessageCreateRequest("첨부파일1", user1.id(), pChannel1.id(), List.of(file1.getId())));

		System.out.println("[등록] 메시지 생성 완료. 첨부파일 수: " + msg1.attachmentIds().size());


		System.out.println("\n========= [4. 삭제 및 무결성 테스트] =========");
		// --- 4-1. 채널 삭제 시 (Message, ReadStatus 삭제 확인) ---
		UUID deletedMsgId = msg1.id();
		UUID deletedChannelId = pChannel1.id();
		channelService.deleteById(pChannel1.id());
		System.out.println("---- 4-1.[삭제] 채널 삭제 완료 ----");

		// 채널 삭제 시 해당 채널의 메시지 삭제 확인
		shouldFail(() -> messageService.findById(deletedMsgId), "채널 삭제 시 메시지 연쇄 삭제");

		// 채널 삭제 시 해당 채널의 ReadStatus 삭제 확인
		shouldFail(() -> {
			if (!readStatusRepo.findAllByChannelId(deletedChannelId).isEmpty()) {
				throw new RuntimeException("해당 데이터가 존재합니다.");
			}
			throw new NoSuchElementException("해당 데이터를 찾을 수 없습니다.");
		}, "채널 삭제 시 ReadStatus 연쇄 삭제");

		// --- 4-2. 메시지 직접 삭제 시 (첨부파일 삭제 확인) ---
		// 메시지 생성
		BinaryContent file2 = contentRepo.save(new BinaryContent("test2.png", new byte[]{4,5,6}));
		ChannelResponse pChannel2 = channelService.createPrivateChannel(new PrivateChannelCreateRequest(List.of(user1.id()), TEXT));
		MessageResponse msg2 = messageService.create(new MessageCreateRequest("첨부파일2", user1.id(), pChannel2.id(), List.of(file2.getId())));

		UUID attachmentId = file2.getId();
		messageService.deleteById(msg2.id()); // 메시지 삭제
		System.out.println("\n---- 4-2.[삭제] 메시지 삭제 완료 ----");

		// 메시지 삭제 시 첨부파일 삭제 확인
		shouldFail(() -> contentRepo.findById(attachmentId).orElseThrow(), "메시지 삭제 시 첨부파일 연쇄 삭제");


		// --- 4-3. 유저 삭제 시 (Profile Image, UserStatus 삭제 확인) ---
		UUID userId = user1.id();
		UUID profileId = user1.profileId();

		userService.deleteById(userId);
		System.out.println("\n---- 4-3.[삭제] 유저 삭제 완료 ----");

		// 유저 삭제 시 프로필 이미지 삭제 확인
		shouldFail(() -> {
			if (profileId != null) contentRepo.findById(profileId).orElseThrow();
			else throw new NoSuchElementException();
		}, "유저 삭제 시 프로필 이미지 연쇄 삭제");

		// 유저 삭제 시 UserStatus 삭제 확인
		shouldFail(() -> {
			if (!userStatusRepo.findByUserId(userId).isEmpty()) {
				throw new RuntimeException("해당 데이터가 존재합니다.");
			}
			throw new NoSuchElementException("해당 데이터를 찾을 수 없습니다.");
		}, "유저 삭제 시 UserStatus 연쇄 삭제");

		// 유저 삭제 시 해당 유저의 모든 ReadStatus 기록 삭제 확인
		shouldFail(() -> {
			if (!readStatusRepo.findAllByUserId(userId).isEmpty()) {
				throw new RuntimeException("해당 데이터가 존재합니다.");
			}
			throw new NoSuchElementException("해당 데이터를 찾을 수 없습니다.");
		}, "유저 삭제 시 모든 ReadStatus 연쇄 삭제");


		System.out.println("\n========= [테스트 종료] =========");
	}


	private static void shouldFail(Runnable action, String testName) {
		try {
			action.run();
			System.err.println("[" + testName + "] 실패: 예외 발생해야 하는데 정상 실행됨..");
		} catch (Exception e) {
			System.out.println("[" + testName + "] 성공: 의도한 에러 발생 -> " + e.getMessage());
		}
	}
}