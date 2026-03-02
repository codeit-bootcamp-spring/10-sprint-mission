package com.sprint.mission.discodeit.dto.binarycontent;

import com.sprint.mission.discodeit.entity.BinaryContentOwnerType;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "첨부 파일 생성 정보")
public record BinaryContentRequest (
        @Schema(description = "첨부 파일 소유자 타입", example = "USER")
        BinaryContentOwnerType type,
        @Schema(description = "업로드할 첨부 파일", type = "string", format = "binary")
        MultipartFile file
) {

    public static BinaryContentRequest of(BinaryContentOwnerType type, MultipartFile file) {
        return new BinaryContentRequest(type, file);
    }
}
