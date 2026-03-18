package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import com.sprint.mission.discodeit.response.ApiException;
import com.sprint.mission.discodeit.response.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "binary_contents")
public class BinaryContent extends BaseEntity {

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "size", nullable = false)
    private Long size;

    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    public BinaryContent(
            String fileName,
            Long size,
            String contentType
    ) {
        validateContentType(contentType);

        this.fileName = fileName;
        this.size = size;
        this.contentType = contentType;
    }

    private void validateContentType(String contentType) {
        if (!contentType.startsWith("image/")) {
            throw new ApiException(
                    ErrorCode.INVALID_CONTENT_TYPE,
                    "이미지만 업로드 가능합니다"
            );
        }
    }
}
