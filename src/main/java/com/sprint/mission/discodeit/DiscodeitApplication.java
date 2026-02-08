package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.CreateUserRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {
//		SpringApplication.run(DiscodeitApplication.class, args);
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class,args);

		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean("basicMessageService", MessageService.class);

		// 셋업
		// 1.사용자 생성
		UserResponse userResponse = setupUser(userService);
		System.out.println("사용자 생성: " + userResponse.getId());
		// 2. 채널 생성
		Channel channel = setupChannel(channelService);
		System.out.println("채널 생성: " + channel.getId());

		// 테스트
		messageCreateTest(messageService, channel, userResponse);
		messageCreateWithAttachmentsTest(messageService,channel,userResponse);
		messageFindAllByChannelTest(messageService, channel);
		messageUpdateTest(messageService, channel, userResponse);
		binaryContentTest(context);

	}

//	static User setupUser(UserService userService) {
//		User user = userService.create("woody", "woody@codeit.com", "woody1234");
//		return user;
//	}

	static UserResponse setupUser(UserService userService) {
		String uniqueId = UUID.randomUUID().toString().substring(0, 8);  // 앞 8자리만
		String username = "user_" + uniqueId;

		CreateUserRequest request = new CreateUserRequest(
				username,
				username + "@codeit.com",
				"password123",
				null
		);

		return userService.create(request);
	}

	static Channel setupChannel(ChannelService channelService) {
		Channel channel = channelService.create(ChannelType.PUBLIC, "공지", "공지 채널입니다.");
		System.out.println("생성된 채널 ID: " + channel.getId());
		return channel;
	}

//	static void messageCreateTest(MessageService messageService, Channel channel, UserResponse author) {
//		System.out.println("메시지 생성 시도 - 채널ID: " + channel.getId());
//		Message message = messageService.create("안녕하세요.", channel.getId(), author.getId());
//		System.out.println("메시지 생성: " + message.getId());
//
//	}

	//고도화 된 메시지 생성 테스트
	static void messageCreateTest(MessageService messageService, Channel channel, UserResponse author) {
		System.out.println("\n===== 메시지 생성 테스트 (첨부파일 없음) =====");

		CreateMessageRequest request = new CreateMessageRequest(

				channel.getId(),
				author.getId(),
				"안녕하세요! 첫 번째 메시지입니다.",
				null  // 첨부파일 없음
		);

		MessageResponse response = messageService.create(request);

		System.out.println("✅ 메시지 생성 완료!");
		System.out.println("  - 메시지 ID: " + response.getId());
		System.out.println("  - 내용: " + response.getContent());
		System.out.println("  - 작성자: " + response.getAuthor().getUsername());
		System.out.println("  - 채널 ID: " + response.getChannelId());
		System.out.println("  - 첨부파일 개수: " + (response.getAttachmentIds() != null ? response.getAttachmentIds().size() : 0));
	}

	// 🆕 메시지 생성 테스트 (첨부파일 포함)
	static void messageCreateWithAttachmentsTest(MessageService messageService, Channel channel, UserResponse author) {
		System.out.println("\n===== 메시지 생성 테스트 (첨부파일 포함) =====");

		// 첨부파일 ID 리스트 (실제로는 BinaryContent를 먼저 생성해야 함)
		List<UUID> attachmentIds = new ArrayList<>();
		// attachmentIds.add(UUID.randomUUID());  // 실제 BinaryContent ID 필요

		CreateMessageRequest request = new CreateMessageRequest(
				channel.getId(),
				author.getId(),
				"두 번째 메시지입니다. 첨부파일이 있습니다.",
				attachmentIds  // 빈 리스트
		);

		MessageResponse response = messageService.create(request);

		System.out.println(" :white_check_mark: 메시지 생성 완료!");
		System.out.println("  - 메시지 ID: " + response.getId());
		System.out.println("  - 내용: " + response.getContent());
		System.out.println("  - 첨부파일 개수: " + response.getAttachmentIds().size());
	}

	// 🆕 특정 채널의 메시지 목록 조회 테스트
	static void messageFindAllByChannelTest(MessageService messageService, Channel channel) {
		System.out.println("\n===== 채널별 메시지 조회 테스트 =====");

		List<MessageResponse> messages = messageService.findAllByChannelId(channel.getId());

		System.out.println(" ✅ 메시지 조회 완료! (총 " + messages.size() + "개)");
		for (int i = 0; i < messages.size(); i++) {
			MessageResponse msg = messages.get(i);
			System.out.println("  [" + (i + 1) + "] " + msg.getContent() + " (작성자: " + msg.getAuthor().getUsername() + ")");
		}
	}


	//  메시지 수정 테스트
	static void messageUpdateTest(MessageService messageService, Channel channel, UserResponse author) {
		System.out.println("\n===== 메시지 수정 테스트 =====");

		// 먼저 메시지 생성
		CreateMessageRequest createRequest = new CreateMessageRequest(
				channel.getId(),
				author.getId(),
				"수정 전 메시지",
				null
		);
		MessageResponse created = messageService.create(createRequest);
		System.out.println("생성된 메시지: " + created.getContent());

		// 메시지 수정
		UpdateMessageRequest updateRequest = new UpdateMessageRequest(
				created.getId(),
				"다음으로 넘어가기!"
		);
		MessageResponse updated = messageService.update(updateRequest);

		System.out.println("✅ 메시지 수정 완료!");
		System.out.println("  - 수정 전: 수정 전 메시지");
		System.out.println("  - 수정 후: " + updated.getContent());
	}

	static void binaryContentTest(ConfigurableApplicationContext context){
		BinaryContentService binaryContentService = context.getBean(BinaryContentService.class);

		System.out.println("\n================= BinaryContent 테스트 시작 =================== \n");

		// 1. create 테스트
		binaryContentCreateTest(binaryContentService);

		// 2. find 테스트
		binaryContentFindTest(binaryContentService);

		// 3. findAllByIdIn 테스트
		binaryContentFindAllByIdInTest(binaryContentService);

		// 4. delete 테스트
		binaryContentDeleteTest(binaryContentService);

		System.out.println("\n========================= ✅ Binary Content 테스트 완료============\n");
	}


	// 1. create 테스트
	static void binaryContentCreateTest(BinaryContentService service) {
		System.out.println(":wrench: BinaryContent 생성 테스트");


		try{
			// 텍스트 파일 생성
			CreateBinaryContentRequest textRequest = new CreateBinaryContentRequest(
					"test.txt",
					"text/plain",
					"Hello World!".getBytes(),
					new ArrayList<>()
			);

			BinaryContentResponse textFile = service.create(textRequest);
			System.out.println("✅ 텍스트 파일 생성 완료!");
			System.out.println("ID : " + textFile.getId());
			System.out.println("파일명 : " + textFile.getFileName());
			System.out.println("타입 : " + textFile.getContentType());
			System.out.println("크기 : " + textFile.getSize() + "bytes");

			//이미지 파일 생성(가상 데이터)
			CreateBinaryContentRequest imageRequest = new CreateBinaryContentRequest(
					"profile.png",
					"image.png",
					new byte[1024],
					new ArrayList<>()
			);
			BinaryContentResponse imageFile = service.create(imageRequest);
			System.out.println("✅ 이미지 파일 생성 완료!");
			System.out.println("ID : " + imageFile.getId());
			System.out.println("파일명 : " + imageFile.getFileName());
			System.out.println("타입 : " + imageFile.getContentType());
			System.out.println("크기 : " + imageFile.getSize() + "bytes");
		}catch (Exception e) {
			System.out.println(":test_tube: 생성 실패" + e.getMessage());
		}
		System.out.println();
	}

	// 2. find 테스트
	static void binaryContentFindTest(BinaryContentService service) {
		System.out.println(":mag: BinaryContent 조회 테스트");

		try{
			// 먼저 파일 생성
			CreateBinaryContentRequest request = new CreateBinaryContentRequest(
					"document.pdf",
					"application/pdf",
					"PDF Content.".getBytes(),
					new ArrayList<>()
			);
			BinaryContentResponse created = service.create(request);
			System.out.println("✅ 파일 생성" + created.getFileName());

			// 생성된 파일 조회
			BinaryContentResponse found = service.find(created.getId());
			System.out.println("✅ 파일 조회 성공");
			System.out.println("ID : " + found.getId());
			System.out.println("파일명 : " + found.getFileName());
			System.out.println("타입 : "+ found.getContentType());

			// 존재하지 않는 ID 조회
			try{
				service.find(UUID.randomUUID());
				System.out.println("❌ 예외가 발생해야 함");
			}catch(NoSuchElementException e){
				System.out.println("✅ 존재하지 않는 파일 조회 예외 처리" + e.getMessage());
			}

		}catch (Exception e){
			System.out.println("❌ 조회 실패 : " + e.getMessage());
		}
		System.out.println();
	}

	// 3. findAllByIdIdn 테스트
	static void binaryContentFindAllByIdInTest(BinaryContentService service){
		System.out.println(":mag: BinaryContent 목록 조회 테스트");

		try{
			// 여러 파일 생성
			List<UUID>ids = IntStream.rangeClosed(1,3)
					.mapToObj(i -> new CreateBinaryContentRequest(
							"file" + i + ".txt",
							"text/plain",
							("Content" + i).getBytes(),
							new ArrayList<>()
					))
					.map(service::create)
					.map(BinaryContentResponse::getId)
					.collect(Collectors.toList());

			System.out.println("✅ 3개 파일 생성 완료");

		//ID 목록으로 조회
			List<BinaryContentResponse> files = service.findAllByIdIn(ids);
			System.out.println("✅ 목록 조회 성공!(총" + files.size() + "개");

			files.forEach(file ->
					System.out.println("-" + file.getFileName() + "(" + file.getSize() + "bytes")
			);

			// 일부만 존재하는 ID로 조회
			List<UUID> mixIdIds = new ArrayList<>(ids);
			mixIdIds.add(UUID.randomUUID()); // 존재하지 않는 ID 추가

			List<BinaryContentResponse> mixedFiles = service.findAllByIdIn(mixIdIds);
			System.out.println("\n✅ 혼합 ID 조회 : " + mixedFiles.size() + "개 (존재하는 것만)");

		}catch (Exception e) {
			System.out.println("❌ 목록 조회 실패:" + e.getMessage());
		}
		System.out.println();
	}

	// 4. delete 테스트
	static void binaryContentDeleteTest(BinaryContentService service){
		System.out.println(":wastebucket: BinaryContent 삭제 테스트");

		try{
			// 파일 생성
			CreateBinaryContentRequest request = new CreateBinaryContentRequest(
					"temp.txt",
					"text/plain",
					"Temporary file".getBytes(),
					new ArrayList<>()
			);

			BinaryContentResponse created = service.create(request);
			System.out.println("✅ 임시 파일 생성" + created.getFileName());

			// 삭제
			service.delete(created.getId());
			System.out.println("✅ 파일 삭제 완료");

			// 삭제 확인
			try{
				service.find(created.getId());
				System.out.println("❌ 삭제된 파일이 조회됨");
			} catch (NoSuchElementException e) {
				System.out.println("✅ 삭제 확인 : 파일이 존재하지 않음");
			}

			// 존재하지 않는 파일 삭제 시도
			try{
				service.delete(UUID.randomUUID());
				System.out.println("❌ 예외가 발생해야 함.");
			}catch (NoSuchElementException e){
				System.out.println("✅ 존재하지 않는 파일 삭제 예외 처리 :" + e.getMessage());
			}

		}catch (Exception e){
			System.out.println("❌ 삭제 실패 :" + e.getMessage());
		}
		System.out.println();
	}

//	public static void main(String[] args) {
//		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);
//
//		// 기존 테스트들...
//		// userTest(context);
//		// channelTest(context);
//		// messageTest(context);
//		// userStatusTest(context);
//
//		// BinaryContent 테스트 추가
//		binaryContentTest(context);
//	}
//
//	static void binaryContentTest(ConfigurableApplicationContext context) {
//		BinaryContentService binaryContentService = context.getBean(BinaryContentService.class);
//
//		System.out.println("\n===== BinaryContent 테스트 시작 =====\n");
//
//		// 1. create 테스트
//		binaryContentCreateTest(binaryContentService);
//
//		// 2. find 테스트
//		binaryContentFindTest(binaryContentService);
//
//		// 3. findAllByIdIn 테스트
//		binaryContentFindAllByIdInTest(binaryContentService);
//
//		// 4. delete 테스트
//		binaryContentDeleteTest(binaryContentService);
//
//		System.out.println("\n===== BinaryContent 테스트 완료 =====\n");
//	}
//
//	// 1. create 테스트
//	static void binaryContentCreateTest(BinaryContentService service) {
//		System.out.println("📁 BinaryContent 생성 테스트");
//
//		try {
//			// 텍스트 파일 생성
//			CreateBinaryContentRequest textRequest = new CreateBinaryContentRequest(
//					"test.txt",
//					"text/plain",
//					"Hello World!".getBytes(),
//					new ArrayList<>()
//			);
//
//			BinaryContentResponse textFile = service.create(textRequest);
//			System.out.println("✅ 텍스트 파일 생성 완료!");
//			System.out.println("  ID: " + textFile.getId());
//			System.out.println("  파일명: " + textFile.getFileName());
//			System.out.println("  타입: " + textFile.getContentType());
//			System.out.println("  크기: " + textFile.getSize() + " bytes");
//
//			// 이미지 파일 생성 (가상 데이터)
//			CreateBinaryContentRequest imageRequest = new CreateBinaryContentRequest(
//					"profile.png",
//					"image/png",
//					new byte[1024], // 1KB 가상 이미지
//					new ArrayList<>()
//			);
//
//			BinaryContentResponse imageFile = service.create(imageRequest);
//			System.out.println("\n✅ 이미지 파일 생성 완료!");
//			System.out.println("  ID: " + imageFile.getId());
//			System.out.println("  파일명: " + imageFile.getFileName());
//			System.out.println("  타입: " + imageFile.getContentType());
//			System.out.println("  크기: " + imageFile.getSize() + " bytes");
//
//		} catch (Exception e) {
//			System.out.println("❌ 생성 실패: " + e.getMessage());
//		}
//		System.out.println();
//	}
//
//	// 2. find 테스트
//	static void binaryContentFindTest(BinaryContentService service) {
//		System.out.println("🔍 BinaryContent 조회 테스트");
//
//		try {
//			// 먼저 파일 생성
//			CreateBinaryContentRequest request = new CreateBinaryContentRequest(
//					"document.pdf",
//					"application/pdf",
//					"PDF Content".getBytes(),
//					new ArrayList<>()
//			);
//
//			BinaryContentResponse created = service.create(request);
//			System.out.println("✅ 파일 생성: " + created.getFileName());
//
//			// 생성된 파일 조회
//			BinaryContentResponse found = service.find(created.getId());
//			System.out.println("✅ 파일 조회 성공!");
//			System.out.println("  ID: " + found.getId());
//			System.out.println("  파일명: " + found.getFileName());
//			System.out.println("  타입: " + found.getContentType());
//
//			// 존재하지 않는 ID 조회
//			try {
//				service.find(UUID.randomUUID());
//				System.out.println("❌ 예외가 발생해야 함");
//			} catch (NoSuchElementException e) {
//				System.out.println("✅ 존재하지 않는 파일 조회 예외 처리: " + e.getMessage());
//			}
//
//		} catch (Exception e) {
//			System.out.println("❌ 조회 실패: " + e.getMessage());
//		}
//		System.out.println();
//	}
//
//	// 3. findAllByIdIn 테스트
//	static void binaryContentFindAllByIdInTest(BinaryContentService service) {
//		System.out.println("📋 BinaryContent 목록 조회 테스트");
//
//		try {
//			// 여러 파일 생성
//			List<UUID> ids = new ArrayList<>();
//
//			for (int i = 1; i <= 3; i++) {
//				CreateBinaryContentRequest request = new CreateBinaryContentRequest(
//						"file" + i + ".txt",
//						"text/plain",
//						("Content " + i).getBytes(),
//						new ArrayList<>()
//				);
//
//				BinaryContentResponse created = service.create(request);
//				ids.add(created.getId());
//			}
//
//			System.out.println("✅ 3개 파일 생성 완료");
//
//			// ID 목록으로 조회
//			List<BinaryContentResponse> files = service.findAllByIdIn(ids);
//			System.out.println("✅ 목록 조회 성공! (총 " + files.size() + "개)");
//
//			for (BinaryContentResponse file : files) {
//				System.out.println("  - " + file.getFileName() + " (" + file.getSize() + " bytes)");
//			}
//
//			// 일부만 존재하는 ID로 조회
//			List<UUID> mixedIds = new ArrayList<>(ids);
//			mixedIds.add(UUID.randomUUID()); // 존재하지 않는 ID 추가
//
//			List<BinaryContentResponse> mixedFiles = service.findAllByIdIn(mixedIds);
//			System.out.println("\n✅ 혼합 ID 조회: " + mixedFiles.size() + "개 (존재하는 것만)");
//
//		} catch (Exception e) {
//			System.out.println("❌ 목록 조회 실패: " + e.getMessage());
//		}
//		System.out.println();
//	}
//
//	// 4. delete 테스트
//	static void binaryContentDeleteTest(BinaryContentService service) {
//		System.out.println("🗑️ BinaryContent 삭제 테스트");
//
//		try {
//			// 파일 생성
//			CreateBinaryContentRequest request = new CreateBinaryContentRequest(
//					"temp.txt",
//					"text/plain",
//					"Temporary file".getBytes(),
//					new ArrayList<>()
//			);
//
//			BinaryContentResponse created = service.create(request);
//			System.out.println("✅ 임시 파일 생성: " + created.getFileName());
//
//			// 삭제
//			service.delete(created.getId());
//			System.out.println("✅ 파일 삭제 완료");
//
//			// 삭제 확인
//			try {
//				service.find(created.getId());
//				System.out.println("❌ 삭제된 파일이 조회됨");
//			} catch (NoSuchElementException e) {
//				System.out.println("✅ 삭제 확인: 파일이 존재하지 않음");
//			}
//
//			// 존재하지 않는 파일 삭제 시도
//			try {
//				service.delete(UUID.randomUUID());
//				System.out.println("❌ 예외가 발생해야 함");
//			} catch (NoSuchElementException e) {
//				System.out.println("✅ 존재하지 않는 파일 삭제 예외 처리: " + e.getMessage());
//			}
//
//		} catch (Exception e) {
//			System.out.println("❌ 삭제 실패: " + e.getMessage());
//		}
//		System.out.println();
//	}


}
