package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.request.auth.AuthLoginRequestDTO;
import com.sprint.mission.discodeit.dto.request.binaryContent.BinaryContentCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.channel.ChannelMemberRequestDTO;
import com.sprint.mission.discodeit.dto.request.channel.PrivateChannelCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.channel.PublicChannelCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.readStatus.ReadStatusUpdateRequestDTO;
import com.sprint.mission.discodeit.dto.request.user.MemberFindRequestDTO;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequestDTO;
import com.sprint.mission.discodeit.dto.response.*;
import com.sprint.mission.discodeit.entity.BinaryContentType;
import com.sprint.mission.discodeit.entity.ReadStatusType;
import com.sprint.mission.discodeit.service.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		UserService userService = context.getBean(UserService.class);
		AuthService authService = context.getBean(AuthService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);
		ReadStatusService readStatusService = context.getBean(ReadStatusService.class);
		UserStatusService userStatusService = context.getBean(UserStatusService.class);
		BinaryContentService binaryContentService = context.getBean(BinaryContentService.class);

		try {
			System.out.println("\n" + "=".repeat(60));
			System.out.println("🚀 [공정 1] USER / AUTH / STATUS 검수");
			System.out.println("=".repeat(60));

			// 1-1. [성공] 유저 생성 (프로필 이미지 포함)
			UserResponseDTO u1 = userService.create(UserCreateRequestDTO.builder()
					.nickname("공장장").email("boss@test.com").password("pw123")
					.binaryContentCreateRequestDTO(new BinaryContentCreateRequestDTO("boss.png", new byte[]{1,2}, BinaryContentType.IMAGE))
					.build());
			System.out.println("✅ [성공] 유저 및 프로필 이미지 생성 완료");

			// 1-3. [성공] 로그인 및 인증
			authService.login(new AuthLoginRequestDTO(u1.getId(), u1.getNickname(), "pw123"));
			System.out.println("✅ [성공] 인증 서비스 로그인 통과");

			// 1-4. [성공] 유저 상태 수동 업데이트
			userStatusService.updateByUserId(u1.getId());
			System.out.println("✅ [성공] 온라인 상태 강제 업데이트 완료");


			System.out.println("\n" + "=".repeat(60));
			System.out.println("🚀 [공정 2] CHANNEL & MEMBERS 권한 검수");
			System.out.println("=".repeat(60));

			// 2-1. [성공] 채널들 생성
			UserResponseDTO u2 = userService.create(UserCreateRequestDTO.builder().nickname("조수").email("as@test.com").password("p").build());
			ChannelResponseDTO pub = channelService.createPublicChannel(new PublicChannelCreateRequestDTO(u1.getId(), "자유게시판", "누구나"));
			ChannelResponseDTO pri = channelService.createPrivateChannel(new PrivateChannelCreateRequestDTO(u1.getId(), List.of(u1.getId())));

			// 2-2. [실패] 권한 검증: 멤버가 아닌 유저가 비공개 채널 참가자 조회 시도
			try {
				userService.findMembersByChannelId(new MemberFindRequestDTO(u2.getId(), pri.getId()));
			} catch (Exception e) {
				System.out.println("⚠️ [실패 확인] 비공개 채널 무단 접근 차단: " + e.getMessage());
			}

			// 2-3. [성공] 멤버 초대 및 퇴장 전수 검사
			channelService.inviteMember(new ChannelMemberRequestDTO(u2.getId(), pub.getId()));
			System.out.println("✅ [성공] 공개 채널 멤버 초대 성공");

			try { channelService.inviteMember(new ChannelMemberRequestDTO(u2.getId(), pub.getId())); }
			catch (Exception e) { System.out.println("⚠️ [실패 확인] 동일 채널 중복 초대 차단: " + e.getMessage()); }


			System.out.println("\n" + "=".repeat(60));
			System.out.println("🚀 [공정 3] MESSAGE & READ_STATUS 데이터 연동");
			System.out.println("=".repeat(60));

			// 3-1. [성공] 메시지 작성 (첨부파일 포함)
			MessageResponseDTO msg = messageService.create(MessageCreateRequestDTO.builder()
					.channelId(pub.getId()).authorId(u1.getId()).message("도면 확인 바람")
					.binaryContentCreateRequestDTOList(List.of(new BinaryContentCreateRequestDTO("blue.pdf", new byte[]{0}, BinaryContentType.FILE)))
					.build());
			System.out.println("✅ [성공] 메시지 발송 및 첨부파일 연동 완료");

			// 3-2. [성공] 조회 기능들
			System.out.println("✅ [성공] 채널별 메시지 조회 건수: " + messageService.findAllByChannelId(pub.getId()).size());
			System.out.println("✅ [성공] 유저별 메시지 조회 건수: " + messageService.findAllByUserId(u1.getId()).size());

			// 3-3. [성공] 읽음 상태 업데이트
			ReadStatusResponseDTO rs = readStatusService.findAllByReadStatusId(u1.getId());
			readStatusService.update(new ReadStatusUpdateRequestDTO(rs.getId(), ReadStatusType.MENTIONED));
			System.out.println("✅ [성공] ReadStatus 갱신 완료");


			System.out.println("\n" + "=".repeat(60));
			System.out.println("🚀 [공정 4] CASCADE & CLEANUP (최종 파괴 테스트)");
			System.out.println("=".repeat(60));

			// 4-1. [성공] 메시지 삭제 시 바이너리 콘텐츠 연쇄 삭제 확인
			UUID fileId = msg.getAttachmentIds().get(0);
			messageService.delete(msg.getId());
			try { binaryContentService.findById(fileId); }
			catch (Exception e) { System.out.println("✅ [연쇄삭제 검증] 메시지 삭제 후 파일 소멸 확인"); }

			// 4-2. [성공] 유저 삭제 시 모든 관련 데이터(상태, 채널멤버) 정리 확인
			userService.delete(u1.getId());
			System.out.println("✅ [성공] 유저 삭제 공정 완료");

			try { userStatusService.findById(u1.getId()); }
			catch (Exception e) { System.out.println("✅ [연쇄삭제 검증] 유저 삭제 후 상태 정보 소멸 확인"); }

			System.out.println("\n" + "=".repeat(60));
			System.out.println("✨ [COMPLETED] 모든 서비스 함수 정밀 검증 종료! 뇽뇽!");
			System.out.println("=".repeat(60));

		} catch (Exception e) {
			System.err.println("\n🚨 [치명적 결함] 테스트 중 예상치 못한 에러: " + e.getMessage());
			e.printStackTrace();
		}
	}
}