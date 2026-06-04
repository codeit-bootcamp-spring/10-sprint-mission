package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.basic.BasicBinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BinaryContentStorageEventListener {

    private final BinaryContentStorage  binaryContentStorage;
    private final BasicBinaryContentService binaryContentService;

    /// (5)Thread-2: @Async때문에 별도 스레드로 위임 -> s3 업로드 시작.
    /// 이벤트를 발행한 메인 서비스의 트랜잭션이 커밋되었을때 리스너가 실행되도록 설정
    /// BinaryContentCreatedEvent타입 기준으로 매칭
    /// Thread-1은 BinaryContentService.create()를 commit 시키고 Thread-2에게 이 리스너 위임한다.
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("ioTaskExecutor")
    public void handleBinaryContentStorage(BinaryContentCreatedEvent event) {
        UUID binaryContentId = event.getBinaryContentId();

        /// Thread-1의 MDC와 SecurityContext 정보를 Thread-2에 복사했고,
        /// SecurityContext에 있는 인증정보를 꺼냄.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        /// auth NOT NULL
        /// auth가 인증된 객체인지?
        /// auth내 principal이 DiscodeitUserDetails타입이라면 userDetails 변수로 바로 꺼내서 사용.
        if (auth != null && auth.isAuthenticated()
                && auth.getPrincipal() instanceof DiscodeitUserDetails userDetails) {

            UserDto currentUser = userDetails.getUserDto();

            UUID userId = currentUser.id();
            String username = currentUser.username();

            log.info("[userId={}, username={}] 파일 저장 시작: {}", userId, username, event.getBinaryContentId());
        }
        try {
            /// BinaryCOntentStorage를 통해 바이너리 데이터를 저장.
            binaryContentStorage.put(event.getBinaryContentId(), event.getBytes());
        } catch (Exception e) {
            /// put()실패시 상태 FAIL로 시도하고 바로 종료.
            log.error("S3 업로드 실패");
            updateStatusSafely(binaryContentId, BinaryContentStatus.FAIL);
            return;
        }
        updateStatusSafely(event.binaryContentId, BinaryContentStatus.SUCCESS);
    }

    /// 상태변경중 예외발생을 위한 메서드 분리.
    /// ex)S3 업로드는 성공했지만, status=SUCCESS로 변경중 예외 발생할경우 FAIL 대비
    private void updateStatusSafely(UUID binaryContentId, BinaryContentStatus status) {
        try {
            binaryContentService.updateStatus(binaryContentId, status);
        }catch (Exception e) {
            log.error(
                    "BinaryContent 상태 업데이트 실패. binaryContentId={}, status={}",
                    binaryContentId,
                    status,
                    e
            );
        }
    }




}
