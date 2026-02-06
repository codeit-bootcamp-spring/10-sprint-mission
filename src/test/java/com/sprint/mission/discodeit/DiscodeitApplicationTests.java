package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/*
# 상태 전이 테스트
: 데이터의 생명 주기를 따라가며 테스트하는 방법. CRUD 로직을 짤 때 중요하다.
Happy Path: 생성 > 조회 > 수정 > 삭제 > 조회(없음)
Unhappy Path:
  1. 없는 걸 조회: 생성 안 하고 조회하면?
  2. 없는 걸 수정: 삭제한 뒤에 또 수정하려고 하면?
  3. 없는 걸 삭재: 이미 삭제했는데 또 삭제하려고 하면?
  4. 중복 생성: 아이디가 "ABC"인 유저가 있는데 또 "ABC"를 만들면?
*/

// 통합 테스트
@SpringBootTest
class DiscodeitApplicationTests {

    // @Autowired -> 스프링에서 의존성을 자동으로 주입할 때 사용하는 어노테이션
    // 스프링 컨테이너는 @Autowired 가 붙은 필드, 생성자, 새터 메서드 등에 자동으로 관련 의존성(빈)을 주입한다.
    // 직접적으로 객체를 생성하지 않고, 스프링 컨테이너에게 해당 타입의 빈을 찾아 주입하라고 지시한다.
    // 현재 관리 중인 빈(Bean) 중에서, 이 변수의 타입에 맞는 객체를 찾아 자동으로 할당하라
    @Autowired UserService userService;
    @Autowired ChannelService channelService;
    @Autowired MessageService messageService;
    @Autowired AuthService authService;
    @Autowired BinaryContentService binaryContentService;
    @Autowired ReadStatusService readStatusService;

    @Autowired BinaryContentRepository binaryContentRepository;

    private void printStep(String stepName) {
        System.out.println("\n=== " + stepName + " ===");
    }

    // TODO: 로그인 기능? 비밀번호 같은지 검증? 암호화?
    @Test
    @DisplayName("1. User 도메인 테스트 (생성 -> 조회 -> 수정 -> 삭제 -> 조회)")
    void userLifecycleTest() {
        System.out.println("\n========================================");
        System.out.println("[TEST 1] User 도메인 테스트 시작");
        System.out.println("========================================");

        // 1. Create - 유저 등록 / 회원가입
        printStep("1. [Create] 유저 생성  / 회원가입 ");
        // 1-1. profileImage 없이 생성 -> 기본 프로필 적용
        // (백엔드에서는 null로 저장 -> 프론트에서 null 이면 기본 프로필 보이도록 구현)
        printStep("1-1. 기본 프로필 사진");
        UserCreateRequest createRequest = new UserCreateRequest("testUser", "test@test.com", "1234", null);
        UserResponse createdUser = userService.create(createRequest);

        // TODO: 중복 메서드 -> 헬퍼 메서드로 빼서 구현 필요할지 고려
        System.out.println("-> 생성된 유저: " + createdUser); // 로그 출력 // record는 toString 자동으로 구현해 줌
        assertThat(createdUser.id()).isNotNull(); // id 생성되었는지 확인
        assertThat(createdUser.username()).isEqualTo("testUser"); // 검증 안해도 됨
        assertThat(createdUser.email()).isEqualTo("test@test.com"); // 유저별로 이메일은 고유하므로 검증 시 사용
        assertThat(createdUser.profileImageId()).isNull(); // 기본 프로필인지 검증
        assertThat(createdUser.isOnline()).isTrue(); // 검증 안해도 됨

        // 1-2. profileImage 첨부하여 생성
        printStep("1-2. 프로필 사진 지정");
        String fileName = "test.png";
        byte[] fileContent = {1, 2, 3, 4, 5}; // 임의의 바이너리 데이터

        BinaryContentRequest imageReq = new BinaryContentRequest(fileName, "image/png", fileContent);
        UserCreateRequest userReqWithImage = new UserCreateRequest("testUserWithImage", "userWithImage@test.com", "1234", imageReq);

        UserResponse createdUserWithImage = userService.create(userReqWithImage);

        // (1) 유저 생성 결과 확인
        System.out.println("-> 생성된 유저: " + createdUserWithImage); // 로그 출력
        assertThat(createdUserWithImage.email()).isEqualTo("userWithImage@test.com");
        assertThat(createdUserWithImage.profileImageId()).isNotNull(); // 프로필 사진이 등록되었는지 확인

        // (2) 실제 리포지토리에서 이미지 꺼내서 확인 (DB 조회)
        BinaryContent savedFile = binaryContentRepository.findById(createdUserWithImage.profileImageId())
                .orElseThrow(() -> new IllegalArgumentException("이미지가 저장되지 않았습니다."));

        // (3) 내용물 비교 (업로드한 데이터 == 저장된 데이터)
        assertThat(savedFile.getFileName()).isEqualTo(fileName);
        assertThat(savedFile.getBytes()).isEqualTo(fileContent);

        System.out.println("-> 이미지 파일 저장 검증 완료");

        // 2. Read - 유저 조회
        printStep("2. [Read] 유저 조회");
        // 2-1. findById
        printStep("2-1. (findById) / id로 단일 유저 찾기");
        UserResponse foundUser = userService.find(createdUser.id());

        System.out.println("-> 조회된 유저: " + foundUser); // 로그 출력
        assertThat(foundUser.email()).isEqualTo("test@test.com");

        // 2-2. findAll
        printStep("2-2. (findAll) / 전체 유저 조회");
        List<UserResponse> allUsers = userService.findAll();

        System.out.println("-> 조회된 유저: ");
        for(UserResponse user : allUsers) {
            System.out.println("   - " + user);
        }
        System.out.println("총 유저 수: " + allUsers.size());

        assertThat(allUsers)
                .extracting(UserResponse::id)
                .contains(createdUser.id());

        // 3. Update - 유저 정보 수정
        // TODO: 프로필 사진 수정 or 기본 프로필로 변경 테스트 추가 필요 <- 기능 구현 후 진행
        printStep("3. [Update] 유저 정보 수정");
        UserUpdateRequest updateRequest = new UserUpdateRequest("updatedUser", null, null, null);
        UserResponse updatedUser = userService.update(createdUser.id(), updateRequest);

        System.out.println("-> 수정된 유저: " + updatedUser); // 로그 출력
        assertThat(updatedUser.username()).isEqualTo("updatedUser");
        assertThat(updatedUser.email()).isEqualTo("test@test.com"); // 안 바뀐 것도 유지되는지 확인

        // 4. Delete - 유저 삭제
        printStep("4. [Delete] 유저 삭제");
        userService.delete(createdUser.id());
        System.out.println("-> 유저 삭제 완료 (ID: " + createdUser.id() + ")"); // 로그 출력

        // 5. 삭제 확인 조회
        printStep("5. 삭제 확인 (조회 시도)");
        assertThatThrownBy(() -> userService.find(createdUser.id()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("해당 id의 유저가 존재하지 않습니다. (userId: " + createdUser.id() + " )");
        System.out.println("-> 조회 실패 확인 (예외 발생 성공)");

        // Unhappy Path 테스트
        printStep("Unhappy Path 테스트");
        // TODO: 각각의 테스트 섹션? 마다 Given-When-Then 적용해야 하는지? -> 지금은 위에서 사용한 것 재사용 중
        // 1. 중복 생성:
        printStep("1. 유저 중복 생성");
        assertThatThrownBy(() -> userService.create(userReqWithImage))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 사용자 이름(username)입니다: " + userReqWithImage.username());
        System.out.println("-> 생성 실패 확인 (예외 발생 성공)");

        // 2. 없는 걸 조회:
        printStep("2. 없는 유저 조회");
        assertThatThrownBy(() -> userService.find(createdUser.id()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("해당 id의 유저가 존재하지 않습니다. (userId: " + createdUser.id() + " )");
        System.out.println("-> 조회 실패 확인 (예외 발생 성공)");

        // 3. 없는 걸 수정:
        printStep("3. 없는 유저 수정");
        assertThatThrownBy(() -> userService.update(createdUser.id(), updateRequest))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("해당 유저를 찾을 수 없습니다. id: " + createdUser.id());
        System.out.println("-> 수정 실패 확인 (예외 발생 성공)");

        // 4. 없는 걸 삭재:
        printStep("4. 없는 유저 삭제");
        assertThatThrownBy(() -> userService.delete(createdUser.id()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("해당 유저를 찾을 수 없습니다. id: " + createdUser.id());
        System.out.println("-> 삭제 실패 확인 (예외 발생 성공)");
    }

    @Test
    @DisplayName("2. Channel 도메인 테스트 (생성 -> 조회 -> 수정 -> 삭제 -> 조회)")
    void channelLifecycleTest() {
        System.out.println("\n========================================");
        System.out.println("[TEST 2] Channel 도메인 테스트 시작");
        System.out.println("========================================");

        // Given - 유저 생성
        UserResponse user1 = userService.create(new UserCreateRequest("user1", "u1@gmail.com", "pw", null));
        UserResponse user2 = userService.create(new UserCreateRequest("user2", "u2@gmail.com", "pw", null));
        UserResponse user3 = userService.create(new UserCreateRequest("user3", "u3@gmail.com", "pw", null));
        System.out.println("[준비] 테스트 유저 3명 생성 완료: ");
        for(UserResponse user : List.of(user1, user2, user3)) {
            System.out.println("   - " + user);
        }

        // 1. 생성
        printStep("1. [Create] 채널 생성");
        // 1-1. Public 채널 생성
        printStep("1-1. PUBLIC 채널 생성 (createPublic)");
        PublicChannelCreateRequest pubReq = new PublicChannelCreateRequest("공개채널", "모두 환영");
        ChannelResponse publicChannel = channelService.createPublic(pubReq);

        System.out.println("-> 생성된 PUBLIC 채널: " + publicChannel);
        assertThat(publicChannel.type()).isEqualTo(ChannelType.PUBLIC);

        // 1-2. Private 채널 생성 (user1, user2 초대)
        printStep("1-2. PRIVATE 채널 생성 (createPrivate)");
        PrivateChannelCreateRequest privReq = new PrivateChannelCreateRequest(Set.of(user1.id(), user2.id()));
        ChannelResponse privateChannel = channelService.createPrivate(privReq);

        System.out.println("-> 생성된 PRIVATE 채널: " + privateChannel);
        System.out.println("-> 참여자 id: " + privateChannel.participantIds());
        assertThat(privateChannel.type()).isEqualTo(ChannelType.PRIVATE);

        // 2. 조회
        printStep("2. [Read] 채널 조회");

        printStep("2-1. 특정 채널 조회 (findById)");
        ChannelResponse foundChannel = channelService.find(publicChannel.id());

        System.out.println("-> 조회된 채널: " + foundChannel);
        assertThat(foundChannel.id()).isEqualTo(publicChannel.id());
        assertThat(foundChannel.name()).isEqualTo("공개채널");

        // user1의 채널 목록 조회
        printStep("2-2. 특정 유저 기준 채널 목록 조회 (findAllByUserId)");
        List<ChannelResponse> user1Channels = channelService.findAllByUserId(user1.id());

        System.out.println("-> user1이 볼 수 있는 채널 개수: " + user1Channels.size() + "개");
        // 비공개 채팅은 이름이 null로 설정되므로 이름 지정해서 출력
        user1Channels.forEach(ch -> System.out.println("   - [" + ch.type() + "] " + (ch.name() != null ? ch.name() : "비공개 채팅")));
        assertThat(user1Channels).hasSize(2); // TODO: 추가 체크 사항 고려

        // user3의 채널 목록 조회
        List<ChannelResponse> user3Channels = channelService.findAllByUserId(user3.id());

        System.out.println("-> user3이 볼 수 있는 채널 개수: " + user3Channels.size() + "개");
        user3Channels.forEach(ch -> System.out.println("   - [" + ch.type() + "] " + (ch.name() != null ? ch.name() : "비공개 채팅")));
        assertThat(user3Channels).hasSize(1);

        // 3. 수정 (Public만 가능)
        printStep("3. [Update] 채널 수정");
        printStep("3-1. PUBLIC 채널 수정");
        ChannelUpdateRequest updateReq = new ChannelUpdateRequest("공개채널_수정", "설명수정");
        ChannelResponse updatedChannel = channelService.update(publicChannel.id(), updateReq);

        System.out.println("-> 수정된 채널: " + updatedChannel);
        assertThat(updatedChannel.name()).isEqualTo("공개채널_수정");
        assertThat(updatedChannel.id()).isEqualTo(publicChannel.id()); // id는 동일한지 확인

        printStep("3-2. PRIVATE 채널 수정 시도");
        assertThatThrownBy(() -> channelService.update(privateChannel.id(), updateReq))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비공개(PRIVATE) 채널은 수정할 수 없습니다. name, description 수정 불가능.");
        System.out.println("-> PRIVATE 채널 수정 실패 확인 (예외 발생 성공)");

        // 4. 삭제
        printStep("4. [Delete] 채널 삭제");
        channelService.delete(publicChannel.id());
        System.out.println("-> PUBLIC 채널 삭제 완료: " + publicChannel.id());

        // 5. 삭제 확인 조회
        printStep("5. 삭제 확인 (조회 시도)");
        assertThatThrownBy(() -> channelService.find(publicChannel.id()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("해당 채널이 존재하지 않습니다. id: " + publicChannel.id());
        System.out.println("-> 조회 실패 확인 (예외 발생 성공)");

        // Unhappy Path 테스트
        printStep("Unhappy Path 테스트");
        // 1. 중복 생성:
        printStep("1. 체널 중복 생성");
        printStep("1-1. PUBLIC 체널 중복 생성");
        // 중복 테스트를 위해 채널 성성
        ChannelResponse publicChannelForDuplicatedTest = channelService.createPublic(pubReq);

        assertThatThrownBy(() -> channelService.createPublic(pubReq))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 공개 채널 이름(name)입니다. " + pubReq.name());
        System.out.println("-> 생성 실패 확인 (예외 발생 성공)");

        printStep("1-1. PRIVATE 체널 중복 생성");
        assertThatThrownBy(() -> channelService.createPrivate(privReq))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("동일한 유저로 구성된 비공개 채널이 존재합니다.");
        System.out.println("-> 생성 실패 확인 (예외 발생 성공)");

        // 2. 없는 걸 조회:
        printStep("2. 없는 채널 조회");
        assertThatThrownBy(() -> channelService.find(publicChannel.id()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("해당 채널이 존재하지 않습니다. id: " + publicChannel.id());
        System.out.println("-> 조회 실패 확인 (예외 발생 성공)");

        // 3. 없는 걸 수정:
        printStep("3. 없는 채널 수정");
        assertThatThrownBy(() -> channelService.update(publicChannel.id(), updateReq))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("해당 채널이 존재하지 않습니다. id: " + publicChannel.id());
        System.out.println("-> 수정 실패 확인 (예외 발생 성공)");

        // 4. 없는 걸 삭재:
        printStep("4. 없는 채널 삭제");
        assertThatThrownBy(() -> channelService.delete(publicChannel.id()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("해당 채널이 존재하지 않습니다. id: " + publicChannel.id());
        System.out.println("-> 삭제 실패 확인 (예외 발생 성공)");
    }

    @Test
    @DisplayName("3. Message 도메인 테스트 (생성 -> 조회 -> 수정 -> 삭제 -> 조회)")
    void messageLifecycleTest() {
        System.out.println("\n========================================");
        System.out.println("[TEST 3] Message 도메인 테스트 시작");
        System.out.println("========================================");

        // Given: 유저 및 채널 준비
        UserResponse author = userService.create(new UserCreateRequest("writer", "w@t.gmail.com", "pw", null));
        ChannelResponse channel = channelService.createPublic(new PublicChannelCreateRequest("대화방", "설명"));
        System.out.println("[준비] 작성자(writer) 및 채널(대화방) 생성 완료");

        // 1. Create - 메시지 생성
        printStep("1. [Create] 메시지 생성");
        // 1-1. 일반 텍스트 메시지 생성
        printStep("1-1. 텍스트 메시지 생성");
        MessageCreateRequest textMsgReq = new MessageCreateRequest("안녕하세요!", channel.id(), author.id(), null);
        MessageResponse textMessage = messageService.create(textMsgReq);

        System.out.println("-> 생성된 텍스트 메시지: " + textMessage);
        assertThat(textMessage.content()).isEqualTo("안녕하세요!");
        assertThat(textMessage.attachmentIds()).isEmpty(); // 첨부파일 없는지 확인
        assertThat(textMessage.content()).isEqualTo("안녕하세요!");

        // 1-2. 첨부파일이 포함된 메시지 생성
        printStep("1-2. 첨부파일 포함 메시지 생성");
        BinaryContentRequest fileReq = new BinaryContentRequest("report.pdf", "application/pdf", new byte[]{1, 2, 3, 4});
        MessageCreateRequest fileMsgReq = new MessageCreateRequest("보고서 제출합니다.", channel.id(), author.id(), List.of(fileReq));

        MessageResponse fileMessage = messageService.create(fileMsgReq);

        System.out.println("-> 생성된 파일 메시지: " + fileMessage);
        assertThat(fileMessage.content()).isEqualTo("보고서 제출합니다.");
        assertThat(fileMessage.attachmentIds()).hasSize(1);

        // 2. Read - 메시지 조회
        printStep("2. [Read] 메시지 조회");
        // 2-1. 단건 조회 (findById)
        printStep("2-1. (findById) 단건 조회");
        MessageResponse foundMsg = messageService.findById(textMessage.id());

        System.out.println("-> 조회된 메시지: " + foundMsg);
        assertThat(foundMsg.id()).isEqualTo(textMessage.id());
        assertThat(foundMsg.content()).isEqualTo("안녕하세요!");

        // 2-2. 채널별 조회 (findAllByChannelId)
        printStep("2-2. (findAllByChannelId) 채널 내 전체 조회");
        List<MessageResponse> channelMessages = messageService.findAllByChannelId(channel.id());

        System.out.println("-> 조회된 메시지 개수: " + channelMessages.size() + "개");
        channelMessages.forEach(msg ->
                System.out.println("   - [" + msg.createAt() + "] " + msg.content() + " (첨부파일: " + msg.attachmentIds().size() + "개)")
        );
        // msg.createAt() -> UTC로 표기됨 // TODO: 프론트에서 클라이언트 지역별 시간대로 변경해서 보여주고 ms 삭제

        assertThat(channelMessages).hasSize(2); // 텍스트 메시지 + 파일 메시지
        assertThat(channelMessages)
                .extracting(MessageResponse::content)
                .contains("안녕하세요!", "보고서 제출합니다.");

        // 3. Update - 메시지 수정
        printStep("3. [Update] 메시지 수정");
        MessageUpdateRequest updateReq = new MessageUpdateRequest("반갑습니다! (수정됨)");
        MessageResponse updatedMsg = messageService.update(textMessage.id(), updateReq);

        System.out.println("-> 수정된 메시지: " + updatedMsg);
        assertThat(updatedMsg.content()).isEqualTo("반갑습니다! (수정됨)");
        assertThat(updatedMsg.id()).isEqualTo(textMessage.id());

        // 4. Delete - 메시지 삭제
        printStep("4. [Delete] 메시지 삭제");
        // 4-1. 텍스트 메시지 삭제
        messageService.delete(textMessage.id());
        System.out.println("-> 텍스트 메시지 삭제 완료");

        // 5. 삭제 확인 조회
        printStep("5. 삭제 확인 (조회 시도)");
        assertThatThrownBy(() -> messageService.findById(textMessage.id()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("해당 메시지가 존재하지 않습니다. id: " + textMessage.id());
        System.out.println("-> 조회 실패 확인 (예외 발생 성공)");

        // 채널 목록에서 삭제 확인
        List<MessageResponse> remainingMessages = messageService.findAllByChannelId(channel.id());
        assertThat(remainingMessages).hasSize(1);
        assertThat(remainingMessages.get(0).id()).isEqualTo(fileMessage.id());
        System.out.println("-> 남은 메시지 개수: 1개 (파일 메시지)");

        // Unhappy Path 테스트
        printStep("Unhappy Path 테스트");
        // 1. 중복 생성 -> 같은 내용의 메시지 생성 가능
        // 2. 없는 메시지 조회
        printStep("2. 없는 메시지 조회");
        UUID randomId = UUID.randomUUID();
        assertThatThrownBy(() -> messageService.findById(randomId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("해당 메시지가 존재하지 않습니다. id: " + randomId);
        System.out.println("-> 조회 예외 발생 성공");

        // 3. 없는 메시지 수정
        printStep("3. 없는 메시지 수정");
        assertThatThrownBy(() -> messageService.update(randomId, updateReq))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("해당 메시지가 존재하지 않습니다. id: " + randomId);
        System.out.println("-> 수정 예외 발생 성공");

        // 4. 없는 메시지 삭제
        printStep("4. 없는 메시지 삭제");
        assertThatThrownBy(() -> messageService.delete(randomId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("해당 메시지가 존재하지 않습니다. id: " + randomId);
        System.out.println("-> 삭제 예외 발생 성공");
    }

    // TODO: ReadStatus 테스트 -> 단위 테스트로 진행
    // TODO: BinaryContent 테스트 -> 단위 테스트로 진행
}