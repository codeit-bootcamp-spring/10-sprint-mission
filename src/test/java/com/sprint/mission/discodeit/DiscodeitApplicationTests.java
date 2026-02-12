package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@TestMethodOrder(MethodOrderer.MethodName.class) // 순서대로 실행 (옵션)
class DiscodeitApplicationTests {

    @Autowired UserService userService;
    @Autowired ChannelService channelService;
    @Autowired MessageService messageService;
    @Autowired UserStatusService userStatusService;
    @Autowired ReadStatusService readStatusService;
    @Autowired BinaryContentService binaryContentService;

    @Test
    @DisplayName("1. [메인 로직] 유저 생성 -> 채널 -> 메시지 -> 상태 변경 -> 수정 -> 삭제")
    void Test() {
        System.out.println("\n========== [1. 통합 시나리오 테스트 시작] ==========\n");

        // ---------------------------------------------------------
        // Step 1: 유저 생성 및 상태(UserStatus) 확인
        // ---------------------------------------------------------
        System.out.println("--- [Step 1] 유저 생성 및 상태 확인 ---");
        BinaryContentDto profileImg = new BinaryContentDto("avatar.png", "image/png", 512, "image_data".getBytes());
        CreateUserRequestDto userReqA = new CreateUserRequestDto("woody", "woody@test.com", "1234", profileImg);
        CreateUserRequestDto userReqB = new CreateUserRequestDto("buzz", "buzz@test.com", "1234", null);

        UserDto userA = userService.create(userReqA);
        UserDto userB = userService.create(userReqB);

        // 검증
        assertThat(userA.getUsername()).isEqualTo("woody");
        assertThat(userA.getProfileImageId()).isNotNull(); // 프로필 이미지 저장 확인
        assertThat(userA.isOnline()).isTrue(); // 온라인 확인

        BinaryContentDto savedProfile = binaryContentService.find(userA.getProfileImageId());
        assertThat(savedProfile.getFileName()).isEqualTo("avatar.png");
        System.out.println("✅ 유저 A 프로필 이미지 조회 검증 완료 (파일명: avatar.png)");

        System.out.println("✅ 유저 A, B 생성 완료. (UserStatus Online 확인됨)");

        // ---------------------------------------------------------
        // Step 2: 채널 생성 (공개 & 비공개)
        // ---------------------------------------------------------
        System.out.println("\n--- [Step 2] 채널 생성 ---");
        // 공개 채널
        CreatePublicChannelRequestDto publicReq = new CreatePublicChannelRequestDto("잡담방", "떠드는 곳");
        ChannelDto publicChannel = channelService.createPublic(publicReq);

        // 비공개 채널 (A, B 참여)
        CreatePrivateChannelRequestDto privateReq = new CreatePrivateChannelRequestDto(List.of(userA.getId(), userB.getId()));
        ChannelDto privateChannel = channelService.createPrivate(privateReq);

        // 검증
        assertThat(publicChannel.getType()).isEqualTo(ChannelType.PUBLIC);
        assertThat(privateChannel.getType()).isEqualTo(ChannelType.PRIVATE);
        // 비공개 채널 참여자 수 확인 (A, B 두 명)
        assertThat(privateChannel.getParticipantIds()).hasSize(2);
        System.out.println("✅ 공개/비공개 채널 생성 완료.");

        // ---------------------------------------------------------
        // Step 3: 메시지 전송 및 첨부파일
        // ---------------------------------------------------------
        System.out.println("\n--- [Step 3] 메시지 전송 (첨부파일 포함) ---");
        BinaryContentDto fileDto = new BinaryContentDto("homework.doc", "application/msword", 1024, "doc_data".getBytes());
        CreateMessageRequestDto msgReq = new CreateMessageRequestDto("숙제 제출합니다.", privateChannel.getId(), userA.getId(), List.of(fileDto));

        MessageDto sentMsg = messageService.create(msgReq, msgReq.getAttachments());

        // 검증
        MessageDto foundMsg = messageService.find(sentMsg.getId());
        assertThat(foundMsg.getContent()).isEqualTo("숙제 제출합니다.");
        assertThat(foundMsg.getAttachmentIds()).hasSize(1); // 첨부파일 1개 확인

        UUID attachmentId = foundMsg.getAttachmentIds().get(0);
        BinaryContentDto savedFile = binaryContentService.find(attachmentId);
        assertThat(savedFile.getFileName()).isEqualTo("homework.doc");
        assertThat(savedFile.getContentType()).isEqualTo("application/msword");
        System.out.println("✅ 메시지 첨부파일 조회 검증 완료 (파일명: homework.doc)");

        System.out.println("✅ 메시지 및 첨부파일 전송/조회 성공.");

        // ---------------------------------------------------------
        // Step 4: ReadStatus 업데이트 및 확인
        // ---------------------------------------------------------
        System.out.println("\n--- [Step 4] 읽음 상태(ReadStatus) 업데이트 ---");
        // 유저 B가 비공개 채널의 메시지를 읽음 처리
        List<ReadStatusDto> bStatuses = readStatusService.findAllByUserId(userB.getId());
        // 비공개 채널에 대한 ReadStatus 찾기
        UUID readStatusId = bStatuses.stream()
                .filter(rs -> rs.getChannelId().equals(privateChannel.getId()))
                .findFirst().orElseThrow().getId();

        // 업데이트 수행
        UpdateReadStatusRequestDto updateReadReq = new UpdateReadStatusRequestDto();
        ReadStatusDto updatedStatus = readStatusService.update(readStatusId, updateReadReq);

        // 마지막 읽은 시간 검증
        assertThat(updatedStatus.getLastReadAt()).isAfter(Instant.MIN);
        System.out.println("✅ 읽음 상태 시간 업데이트 확인 완료.");

        // ---------------------------------------------------------
        // Step 5: 수정 기능 (User & Message & Channel)
        // ---------------------------------------------------------
        System.out.println("\n--- [Step 5] 데이터 수정 테스트 ---");

        // 1. 유저 이름 수정
        UpdateUserRequestDto updateUserReq = new UpdateUserRequestDto("woody_modified", null, null, null);
        UserDto updatedUser = userService.update(userA.getId(), updateUserReq);
        assertThat(updatedUser.getUsername()).isEqualTo("woody_modified");

        // 2. 메시지 내용 수정
        UpdateMessageRequestDto updateMsgReq = new UpdateMessageRequestDto("숙제 제출합니다. (수정됨)");
        MessageDto updatedMsg = messageService.update(sentMsg.getId(), updateMsgReq);
        assertThat(updatedMsg.getContent()).contains("(수정됨)");

        System.out.println("✅ 유저 및 메시지 수정 성공.");

        // ---------------------------------------------------------
        // Step 6: 삭제 기능
        // ---------------------------------------------------------
        System.out.println("\n--- [Step 6] 삭제 및 연쇄 삭제 확인 ---");

        // 메시지 삭제
        messageService.delete(sentMsg.getId());
        assertThatThrownBy(() -> messageService.find(sentMsg.getId()))
                .isInstanceOf(java.util.NoSuchElementException.class);

        assertThatThrownBy(() -> binaryContentService.find(attachmentId))
                .isInstanceOf(java.util.NoSuchElementException.class);
        System.out.println("✅ 첨부파일 연쇄 삭제 확인 완료.");

        // 유저 B + UserStatus 삭제
        userService.delete(userB.getId());
        assertThatThrownBy(() -> userService.find(userB.getId()))
                .isInstanceOf(java.util.NoSuchElementException.class);
        // UserStatus 삭제 확인
        assertThat(userStatusService.findAll().stream().anyMatch(s -> s.getUserId().equals(userB.getId()))).isFalse();

        System.out.println("✅ 삭제 로직 검증 성공.");

        System.out.println("\n========== [통합 시나리오 테스트 종료] ==========\n");
    }

    @Test
    @DisplayName("2. [예외 처리] 잘못된 요청 방어 로직 검증")
    void exceptionTest() {
        System.out.println("\n========== [2. 예외 처리 테스트 시작] ==========\n");

        // 1. 없는 채널에 메시지 보내기
        System.out.println("1. 존재하지 않는 채널 ID로 메시지 전송 시도...");
        CreateUserRequestDto userReq = new CreateUserRequestDto("ex_user", "ex@test.com", "1234", null);
        UserDto user = userService.create(userReq);

        CreateMessageRequestDto badMsgReq = new CreateMessageRequestDto("Fail", UUID.randomUUID(), user.getId(), null);

        assertThatThrownBy(() -> messageService.create(badMsgReq, null))
                .isInstanceOf(java.util.NoSuchElementException.class)
                .hasMessageContaining("Channel not found");
        System.out.println("   -> 예외 발생 확인 OK (Channel not found)");

        // 2. 중복 이메일 가입 시도
        System.out.println("2. 중복 이메일 가입 시도...");
        CreateUserRequestDto dupReq = new CreateUserRequestDto("dup", "ex@test.com", "1234", null); // 위와 동일 이메일

        assertThatThrownBy(() -> userService.create(dupReq))
                .isInstanceOf(IllegalArgumentException.class);
        System.out.println("   -> 예외 발생 확인 OK (Email already exists)");

        System.out.println("\n========== [예외 처리 테스트 종료] ==========\n");
    }
}