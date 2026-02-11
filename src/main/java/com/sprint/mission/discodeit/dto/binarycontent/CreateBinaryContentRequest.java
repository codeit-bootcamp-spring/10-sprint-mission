package com.sprint.mission.discodeit.dto.binarycontent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateBinaryContentRequest {
    private String fileName;
    private String contentType;
    private byte[] data;
    private List<UUID> attachmentid;  // 메시지의 첨부파일을 담당하는 필드.
}
