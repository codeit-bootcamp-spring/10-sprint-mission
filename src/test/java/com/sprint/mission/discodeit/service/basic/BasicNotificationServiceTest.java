package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.exception.notification.NotificationForbiddenException;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BasicNotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private BasicNotificationService notificationService;

    @Test
    @DisplayName("알림을 생성하면 수신자/제목/내용으로 엔티티를 저장하고 DTO를 반환한다.")
    void create_success() {
        // given
        UUID receiverId = UUID.randomUUID();
        Notification saved = new Notification(receiverId, "제목", "내용");
        UUID id = UUID.randomUUID();
        ReflectionTestUtils.setField(saved, "id", id);
        NotificationDto dto = new NotificationDto(id, Instant.now(), receiverId, "제목", "내용");

        given(notificationRepository.save(any(Notification.class))).willReturn(saved);
        given(notificationMapper.toDto(saved)).willReturn(dto);

        // when
        NotificationDto result = notificationService.create(receiverId, "제목", "내용");

        // then
        assertThat(result).isEqualTo(dto);
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    @DisplayName("수신자 ID로 알림 목록을 조회한다.")
    void findAllByReceiverId_success() {
        // given
        UUID receiverId = UUID.randomUUID();
        Notification entity = new Notification(receiverId, "t", "c");
        NotificationDto dto = new NotificationDto(UUID.randomUUID(), Instant.now(), receiverId, "t", "c");
        given(notificationRepository.findAllByReceiverIdOrderByCreatedAtAsc(receiverId))
                .willReturn(List.of(entity));
        given(notificationMapper.toDto(entity)).willReturn(dto);

        // when
        List<NotificationDto> result = notificationService.findAllByReceiverId(receiverId);

        // then
        assertThat(result).containsExactly(dto);
    }

    @Test
    @DisplayName("본인 알림은 삭제할 수 있다.")
    void delete_ownNotification_success() {
        // given
        UUID requesterId = UUID.randomUUID();
        UUID notificationId = UUID.randomUUID();
        Notification entity = new Notification(requesterId, "t", "c");
        ReflectionTestUtils.setField(entity, "id", notificationId);
        given(notificationRepository.findById(notificationId)).willReturn(Optional.of(entity));

        // when
        notificationService.delete(notificationId, requesterId);

        // then
        verify(notificationRepository).delete(entity);
    }

    @Test
    @DisplayName("타인의 알림 삭제 시 NotificationForbiddenException이 발생한다.")
    void delete_othersNotification_throwsForbidden() {
        // given
        UUID ownerId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        UUID notificationId = UUID.randomUUID();
        Notification entity = new Notification(ownerId, "t", "c");
        given(notificationRepository.findById(notificationId)).willReturn(Optional.of(entity));

        // when & then
        assertThatThrownBy(() -> notificationService.delete(notificationId, requesterId))
                .isInstanceOf(NotificationForbiddenException.class);
        verify(notificationRepository, never()).delete(any());
    }

    @Test
    @DisplayName("존재하지 않는 알림 삭제 시 NotificationNotFoundException이 발생한다.")
    void delete_missingNotification_throwsNotFound() {
        // given
        UUID notificationId = UUID.randomUUID();
        given(notificationRepository.findById(notificationId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> notificationService.delete(notificationId, UUID.randomUUID()))
                .isInstanceOf(NotificationNotFoundException.class);
    }
}
