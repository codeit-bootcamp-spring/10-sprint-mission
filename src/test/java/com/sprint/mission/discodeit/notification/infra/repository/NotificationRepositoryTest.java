package com.sprint.mission.discodeit.notification.infra.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.notification.domain.entity.Notification;
import com.sprint.mission.discodeit.support.base.BaseRepositoryTest;
import com.sprint.mission.discodeit.user.domain.entity.User;
import com.sprint.mission.discodeit.user.domain.entity.constant.UserRole;
import com.sprint.mission.discodeit.user.infra.repository.UserJpaRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class NotificationRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private NotificationJpaRepository notificationRepository;

  @Autowired
  private UserJpaRepository userJpaRepository;

  @Test
  @DisplayName("findAllByReceiver_Id - 수신자 ID로 알림 목록을 엔티티 그래프(receiver 포함)와 함께 정상 조회한다.")
  void findAllByReceiver_Id() {
    User user = User.builder()
        .username("testuser")
        .email("test@example.com")
        .role(UserRole.USER)
        .build();
    userJpaRepository.save(user);

    Notification noti1 = Notification.builder()
        .receiver(user)
        .title("title1")
        .content("content1")
        .build();
    Notification noti2 = Notification.builder()
        .receiver(user)
        .title("title2")
        .content("content2")
        .build();
    notificationRepository.saveAll(List.of(noti1, noti2));

    flushAndClear();
    clear();

    List<Notification> result = notificationRepository.findAllByReceiver_Id(user.getId());

    assertThat(result).hasSize(2);
    assertThat(result.get(0).getReceiver().getUsername()).isEqualTo("testuser");
    ensureQueryCount(1);
  }
}
