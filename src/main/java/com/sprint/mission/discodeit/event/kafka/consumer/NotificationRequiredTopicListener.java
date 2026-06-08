package com.sprint.mission.discodeit.event.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.enums.BinaryContentStatus;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.exception.file.FileUploadFailException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.storage.s3.S3BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredTopicListener {
    private final ObjectMapper objectMapper;
    private final CacheManager cacheManager;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;
    private final BinaryContentStorage s3BinaryContentStorage;
    private final BinaryContentService binaryContentService;
    @Value("${admin.init.email}")
    private String adminEmail;


    @KafkaListener(topics = "discodeit.MessageCreatedEvent", groupId = "discodeit-group")
    public void consumeMessageCreate(String kafkaEvent){
        try{
            MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);
            log.info("[Kafka 수신 완료]");

            List<ReadStatus> activeStatus = readStatusRepository.findAllByChannelIdAndNotificationEnabledTrue(event.getChannelId());
            List<Notification> notifications = new ArrayList<>();

            Cache notificationCache = cacheManager.getCache("userNotifications");

            for(ReadStatus status : activeStatus){
                UUID subscriberId = status.getUser().getId();

                if(subscriberId.equals(event.getSenderId())) continue;
                User receiverProxy = userRepository.getReferenceById(subscriberId);
                notifications.add(new Notification(receiverProxy,
                        String.format("%s(#%s)", event.getSenderName(), event.getChannelName()),
                        event.getContent()));

                if(notificationCache != null){
                    notificationCache.evict(subscriberId);
                }
            }

            if(!notifications.isEmpty()){
                notificationRepository.saveAll(notifications);
            }
            log.info("메시지 알림 전송 완료: {} 건", notifications.size());
        } catch (JsonProcessingException e){
            log.error("[Kafka 소비 실패] 메시지 파싱 또는 처리 중 에러 발생", e);
        }
    }

    @KafkaListener(topics = "discodeit.RoleUpdatedEvent", groupId = "discodeit-group")
    @CacheEvict(value = "userNotifications", key = "#event.receiverId")
    public void consumeRoleUpdate(String kafkaEvent) {
        try {
            RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);
            log.info("[Kafka 수신 완료]");

            String content = String.format("%s -> %s", event.getOldRole(), event.getNewRole());
            User receiverProxy = userRepository.getReferenceById(event.getReceiverId());
            log.info("권한 업데이트 알림 발송 시작");
            notificationRepository.save(new Notification(
                    receiverProxy,
                    "권한이 변경되었습니다.",
                    content));

            log.info("권한 업데이트 알림 발송 완료");
        } catch (JsonProcessingException e) {
            log.error("[Kafka 소비 실패] 메시지 파싱 또는 처리 중 에러 발생", e);
        }
    }

    @KafkaListener(topics = "discodeit.BinaryContentCreatedEvent", groupId = "discodeit-group")
    public void consumeProfileUpload(String kafkaEvent){
        try{
            BinaryContentCreatedEvent event = objectMapper.readValue(kafkaEvent, BinaryContentCreatedEvent.class);
            UUID binaryContentId = event.getBinaryContentId();
            String requestId = MDC.get("requestId");
            try{
                log.info("비동기 s3 업로드 시작: id = {}", binaryContentId);
                s3BinaryContentStorage.put(binaryContentId, event.getBytes());
                binaryContentService.updateStatus(binaryContentId, BinaryContentStatus.SUCCESS);
                log.info("비동기 s3 업로드 완료: id = {}", binaryContentId);
            } catch (FileUploadFailException e){
                log.error("비동기 s3 업로드 최종 실패 처리 로직: id = {}, requestId = {}", binaryContentId, requestId);
                // DB 상태 변경
                binaryContentService.updateStatus(binaryContentId, BinaryContentStatus.FAIL);
                try {
                    User admin = userRepository.findByEmail(adminEmail)
                            .orElseThrow(() -> new UserNotFoundException(adminEmail));

                    notificationRepository.save(new Notification(
                            admin,
                            "S3 파일 업로드 실패",
                            String.format("RequestId: %s\nBinaryContentId: %s\nError: %s",
                                    requestId, binaryContentId, e.getErrorCode().getMessage())
                    ));
                } catch (Exception ex) {
                    log.error("관리자 알림 전송 실패", ex);
                }
            } finally{
                event.clear();
            }
        } catch (JsonProcessingException e){
            log.error("[Kafka 소비 실패] 메시지 파싱 또는 처리 중 에러 발생", e);
        }
    }
}
