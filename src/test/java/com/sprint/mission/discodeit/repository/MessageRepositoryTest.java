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
@DisplayName("MessageRepository 슬라이스 테스트")
class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Test
    @DisplayName("성공: 채널 아이디로 모든 메시지 페이징 조회")
    void findAllByChannelId_Success() {
        // TODO: Pageable 객체를 사용하여 페이징과 정렬이 정상적으로 이루어지는지 확인
    }

    @Test
    @DisplayName("성공: 커서 기반(createdAt) 메시지 슬라이스 조회")
    void findAllUseCursorByChannelId_Success() {
        // TODO: 특정 시점(Cursor) 이전의 메시지들만 슬라이스로 조회되는지 확인 (무한 스크롤 용)
    }

    @Test
    @DisplayName("성공: 메시지 아이디 리스트로 첨부파일 목록 조회")
    void findAttachmentsByMessageIds_Success() {
        // TODO: 여러 메시지에 연결된 첨부파일들이 한 번에 fetch 되는지 검증
    }

    @Test
    @DisplayName("실패: 존재하지 않는 채널 아이디로 메시지 조회 시 빈 페이지 반환")
    void findAllByChannelId_Fail_NotFound() {
        // TODO: 빈 결과에 대해 Page가 적절히 반환되는지 확인
    }
}
