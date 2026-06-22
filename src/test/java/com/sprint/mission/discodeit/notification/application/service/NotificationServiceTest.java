package com.sprint.mission.discodeit.notification.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.notification.domain.entity.Notification;
import com.sprint.mission.discodeit.notification.domain.exception.NotificationErrorCode;
import com.sprint.mission.discodeit.notification.domain.exception.NotificationException;
import com.sprint.mission.discodeit.notification.domain.provider.NotificationUserResolver;
import com.sprint.mission.discodeit.notification.domain.repository.NotificationRepository;
import com.sprint.mission.discodeit.user.domain.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import com.sprint.mission.discodeit.notification.application.mapper.NotificationDomainMapper;
import com.sprint.mission.discodeit.notification.application.mapper.NotificationPayloadMapper;
import com.sprint.mission.discodeit.notification.domain.provider.NotificationNotifier;
import com.sprint.mission.discodeit.support.mapper.DomainMapperContainer;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

  @Mock
  private NotificationRepository repository;

  @Mock
  private NotificationUserResolver userResolver;

  @Spy
  private NotificationDomainMapper mapper = DomainMapperContainer.get(NotificationDomainMapper.class);

  @Spy
  private NotificationPayloadMapper payloadMapper = DomainMapperContainer.get(NotificationPayloadMapper.class);

  @Mock
  private NotificationNotifier notifier;

  @InjectMocks
  private NotificationService service;


  @Test
  @DisplayName("create - 알림을 정상 생성한다.")
  void create_success() {
    UUID receiverId = UUID.randomUUID();
    User user = mock(User.class);
    given(userResolver.getProxyOrThrow(List.of(receiverId))).willReturn(List.of(user));

    service.create(List.of(receiverId), "Test Title", "Test Content");

    ArgumentCaptor<List<Notification>> captor = ArgumentCaptor.forClass(List.class);
    verify(repository).saveAll(captor.capture());

    List<Notification> saved = captor.getValue();
    assertThat(saved).hasSize(1);
    assertThat(saved.get(0).getTitle()).isEqualTo("Test Title");
    assertThat(saved.get(0).getContent()).isEqualTo("Test Content");
    assertThat(saved.get(0).getReceiver()).isEqualTo(user);
  }

  @Test
  @DisplayName("find - 사용자 ID로 알림 목록을 반환한다.")
  void find_success() {
    UUID receiverId = UUID.randomUUID();
    User user = mock(User.class);
    Notification noti = Notification.builder()
        .receiver(user)
        .title("title1")
        .content("content1")
        .build();

    given(repository.findAllByReceiver_Id(receiverId)).willReturn(List.of(noti));

    List<Notification> result = service.find(receiverId);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getTitle()).isEqualTo("title1");
    assertThat(result.get(0).getContent()).isEqualTo("content1");
  }

  @Test
  @DisplayName("delete - 특정 알림을 정상 삭제한다.")
  void delete_success() {
    UUID id = UUID.randomUUID();
    Notification noti = mock(Notification.class);
    User user = mock(User.class);
    given(noti.getReceiver()).willReturn(user);
    given(user.getId()).willReturn(UUID.randomUUID());
    given(repository.findById(id)).willReturn(Optional.of(noti));

    service.delete(id);

    verify(repository).delete(noti);
  }

  @Test
  @DisplayName("delete - 알림이 없으면 예외를 던진다.")
  void delete_fail_not_found() {
    UUID id = UUID.randomUUID();
    given(repository.findById(id)).willReturn(Optional.empty());

    assertThatThrownBy(() -> service.delete(id))
        .isInstanceOf(NotificationException.class)
        .hasFieldOrPropertyWithValue("errorCode", NotificationErrorCode.NOT_FOUND);
  }

  @Test
  @DisplayName("sendToAdmin - 어드민에게 메시지를 전송한다.")
  void sendToAdmin_success() {
    User admin = mock(User.class);
    given(userResolver.getProxyByUsername("admin")).willReturn(admin);

    service.sendToAdmin("Admin Title", List.of("Message 1", "Message 2"));

    ArgumentCaptor<List<Notification>> captor = ArgumentCaptor.forClass(List.class);
    verify(repository).saveAll(captor.capture());

    List<Notification> saved = captor.getValue();
    assertThat(saved).hasSize(2);
    assertThat(saved.get(0).getTitle()).isEqualTo("Admin Title");
    assertThat(saved.get(0).getContent()).isEqualTo("Message 1");
    assertThat(saved.get(1).getContent()).isEqualTo("Message 2");
    assertThat(saved.get(0).getReceiver()).isEqualTo(admin);
  }
}
