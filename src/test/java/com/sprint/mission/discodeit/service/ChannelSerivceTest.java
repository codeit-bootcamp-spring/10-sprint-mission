package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.channel.UpdateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.status.ReadStatus;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.status.ReadStatusRepository;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ChannelServiceTest {

    @Autowired
    private FileChannelService channelService;

    @Autowired
    private ReadStatusRepository readStatusRepository;

    @Autowired
    private MessageRepository messageRepository;

    private UUID testUserId1;
    private UUID testUserId2;

    @BeforeEach
    void setUp() {
        // 테스트용 사용자 ID 생성
        testUserId1 = UUID.randomUUID();
        testUserId2 = UUID.randomUUID();
    }

    // ========== PUBLIC 채널 생성 테스트 ==========

    @Test
    @DisplayName("PUBLIC 채널 생성 - 성공")
    void createPublicChannel_Success() {
        // given: 테스트 데이터 준비
        CreatePublicChannelRequest request = new CreatePublicChannelRequest(
                "일반 채널",
                "PUBLIC 채널 설명"
        );

        // when: 실제 테스트 실행
        Channel channel = channelService.createPublicChannel(request);

        // then: 결과 검증
        assertNotNull(channel);
        assertNotNull(channel.getId());
        assertEquals(ChannelType.PUBLIC, channel.getType());
        assertEquals("일반 채널", channel.getName());
        assertEquals("PUBLIC 채널 설명", channel.getDescription());
        assertNotNull(channel.getCreatedAt());

        System.out.println("PUBLIC 채널 생성 성공: " + channel.getName());
    }

    // ========== PRIVATE 채널 생성 테스트 ==========

    @Test
    @DisplayName("PRIVATE 채널 생성 - ReadStatus 자동 생성 확인")
    void createPrivateChannel_WithReadStatus() {
        // given
        List<UUID> participants = Arrays.asList(testUserId1, testUserId2);
        CreatePrivateChannelRequest request = new CreatePrivateChannelRequest(participants);

        // when
        Channel channel = channelService.createPrivateChannel(request);

        // then
        assertNotNull(channel);
        assertEquals(ChannelType.PRIVATE, channel.getType());
        assertNull(channel.getName());  // PRIVATE 채널은 name 없음
        assertNull(channel.getDescription());  // PRIVATE 채널은 description 없음

        // ReadStatus 생성 확인
        List<ReadStatus> readStatuses = readStatusRepository.findByChannelId(channel.getId());
        assertEquals(2, readStatuses.size());  // 참여자 2명이니 ReadStatus도 2개

        System.out.println("✅ PRIVATE 채널 생성 성공 + ReadStatus 2개 생성 확인");
    }

    // ========== 채널 조회 테스트 ==========

    @Test
    @DisplayName("채널 상세 조회 - PUBLIC 채널")
    void findWithDetails_PublicChannel() {
        // given: PUBLIC 채널 생성
        CreatePublicChannelRequest request = new CreatePublicChannelRequest(
                "테스트 채널",
                "상세 조회 테스트"
        );
        Channel channel = channelService.createPublicChannel(request);

        // when: 상세 조회
        ChannelResponse response = channelService.findWithDetails(channel.getId());

        // then
        assertNotNull(response);
        assertEquals(channel.getId(), response.getId());
        assertEquals("테스트 채널", response.getName());
        assertEquals(ChannelType.PUBLIC, response.getType());

        System.out.println("✅ PUBLIC 채널 상세 조회 성공");
    }

    @Test
    @DisplayName("채널 상세 조회 - PRIVATE 채널 (참여자 정보 포함)")
    void findWithDetails_PrivateChannel_WithParticipants() {
        // given
        List<UUID> participants = Arrays.asList(testUserId1, testUserId2);
        CreatePrivateChannelRequest request = new CreatePrivateChannelRequest(participants);
        Channel channel = channelService.createPrivateChannel(request);

        // when
        ChannelResponse response = channelService.findWithDetails(channel.getId());

        // then
        assertNotNull(response);
        assertEquals(ChannelType.PRIVATE, response.getType());
        assertNotNull(response.getParticipantUserIds());
        assertEquals(2, response.getParticipantUserIds().size());
        assertTrue(response.getParticipantUserIds().contains(testUserId1));
        assertTrue(response.getParticipantUserIds().contains(testUserId2));

        System.out.println("✅ PRIVATE 채널 상세 조회 성공 (참여자 정보 포함)");
    }

    // ========== 사용자별 채널 목록 조회 테스트 ==========

    @Test
    @DisplayName("사용자별 채널 목록 조회 - PUBLIC + PRIVATE 필터링")
    void findAllByUserId_FilteredChannels() {
        // given: PUBLIC 채널 1개, PRIVATE 채널 2개 생성
        channelService.createPublicChannel(new CreatePublicChannelRequest("PUBLIC 1", "설명"));

        channelService.createPrivateChannel(
                new CreatePrivateChannelRequest(Arrays.asList(testUserId1, testUserId2))
        );

        channelService.createPrivateChannel(
                new CreatePrivateChannelRequest(Arrays.asList(testUserId2))
        );

        // when: testUserId1로 조회
        List<ChannelResponse> user1Channels = channelService.findAllByUserId(testUserId1);

        // then: PUBLIC 1개 + 참여한 PRIVATE 1개 = 총 2개
        assertTrue(user1Channels.size() >= 2);

        long publicCount = user1Channels.stream()
                .filter(ch -> ch.getType() == ChannelType.PUBLIC)
                .count();

        long privateCount = user1Channels.stream()
                .filter(ch -> ch.getType() == ChannelType.PRIVATE)
                .count();

        assertTrue(publicCount >= 1);  // PUBLIC 채널 포함
        assertTrue(privateCount >= 1);  // 참여한 PRIVATE 채널만 포함

        System.out.println("✅ 사용자별 채널 필터링 성공: PUBLIC " + publicCount + "개, PRIVATE " + privateCount + "개");
    }

    // ========== 채널 수정 테스트 ==========

    @Test
    @DisplayName("PUBLIC 채널 수정 - 성공")
    void updateChannel_PublicChannel_Success() {
        // given
        Channel channel = channelService.createPublicChannel(
                new CreatePublicChannelRequest("원래 이름", "원래 설명")
        );

        UpdateChannelRequest updateRequest = new UpdateChannelRequest(
                channel.getId(),
                "수정된 이름",
                "수정된 설명"
        );

        // when
        ChannelResponse updated = channelService.updateChannel(updateRequest);

        // then
        assertEquals("수정된 이름", updated.getName());
        assertEquals("수정된 설명", updated.getDescription());

        System.out.println("✅ PUBLIC 채널 수정 성공");
    }

    @Test
    @DisplayName("PRIVATE 채널 수정 시도 - 실패 (예외 발생)")
    void updateChannel_PrivateChannel_ThrowsException() {
        // given
        Channel channel = channelService.createPrivateChannel(
                new CreatePrivateChannelRequest(Arrays.asList(testUserId1))
        );

        UpdateChannelRequest updateRequest = new UpdateChannelRequest(
                channel.getId(),
                "수정 시도",
                null
        );

        // when & then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> channelService.updateChannel(updateRequest)
        );

        assertEquals("PRIVATE 채널은 수정할 수 없습니다.", exception.getMessage());

        System.out.println("✅ PRIVATE 채널 수정 방지 확인");
    }

    // ========== 채널 삭제 테스트 ==========

    @Test
    @DisplayName("채널 삭제 - 관련 ReadStatus도 함께 삭제")
    void deleteChannel_WithRelatedData() {
        // given
        Channel channel = channelService.createPrivateChannel(
                new CreatePrivateChannelRequest(Arrays.asList(testUserId1, testUserId2))
        );
        UUID channelId = channel.getId();

        // ReadStatus 생성 확인
        List<ReadStatus> beforeDelete = readStatusRepository.findByChannelId(channelId);
        assertEquals(2, beforeDelete.size());

        // when: 채널 삭제
        channelService.delete(channelId);

        // then: 채널과 ReadStatus 모두 삭제됨
        assertThrows(IllegalArgumentException.class,
                () -> channelService.find(channelId)
        );

        List<ReadStatus> afterDelete = readStatusRepository.findByChannelId(channelId);
        assertEquals(0, afterDelete.size());

        System.out.println("✅ 채널 삭제 + 관련 ReadStatus 삭제 확인");
    }
}
