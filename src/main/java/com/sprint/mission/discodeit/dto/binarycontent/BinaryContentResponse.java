package com.sprint.mission.discodeit.dto.binarycontent;

import com.sprint.mission.discodeit.entity.status.BinaryContent;
import lombok.*;

import javax.swing.text.AbstractDocument;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BinaryContentResponse {
    private UUID id;
    private String fileName;
    private String contentType;
    private int size;
    private List<UUID> attachmentId;
    private Instant createdAt;
    private Instant updatedAt;

    //data는 보안/크기 이유로 응답에서 제외
    // 다운로드 API 구현할 예정
    
    public static BinaryContentResponse from(BinaryContent content) {
        return BinaryContentResponse.builder()
                .id(content.getId())
                .fileName(content.getFileName())
                .contentType(content.getContentType())
                .size(content.getSize())
                .attachmentId(content.getAttachmentId())
                .createdAt(content.getCreatedAt())
                .updatedAt(content.getUpdatedAt())
                .build();
    }
}
