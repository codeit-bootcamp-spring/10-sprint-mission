package com.sprint.mission.discodeit.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("ChannelRepository 슬라이스 테스트")
class ChannelRepositoryTest {

    @Autowired
    private ChannelRepository channelRepository;

    @Test
    @DisplayName("성공: 특정 사용자가 접근 가능한 모든 채널 조회 (PUBLIC + 가입된 PRIVATE)")
    void findAllAccessibleByUserId_Success() {
        // TODO: PUBLIC 채널들과 특정 유저가 참여 중인 PRIVATE 채널이 모두 조회되는지 확인
    }

    @Test
    @DisplayName("성공: 가입되지 않은 PRIVATE 채널은 조회되지 않음")
    void findAllAccessibleByUserId_ExcludesUnjoinedPrivate() {
        // TODO: 다른 유저들의 PRIVATE 채널이 결과에서 제외되는지 확인
    }

    @Test
    @DisplayName("성공: 참여자가 1명 이하인 비공개 채널 삭제 벌크 연산")
    void deleteEmptyOrLonelyChannels_Success() {
        // TODO: 참여자가 없는 PRIVATE 채널이 정상적으로 삭제되는지 확인 (벌크 연산)
    }

    @Test
    @DisplayName("실패: 참여자가 2명 이상인 비공개 채널은 삭제되지 않음")
    void deleteEmptyOrLonelyChannels_Fails_ForActiveChannels() {
        // TODO: 정상적으로 운영 중인 채널이 삭제되지 않는지 검증
    }
}
