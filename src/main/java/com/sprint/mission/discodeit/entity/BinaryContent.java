package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/// 현제 메타데이터는 DB에 저장한다.
@Entity
@Table(name = "binary_contents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BinaryContent extends BaseUpdatableEntity {
  /// fileName, size, contentType, id, createdAt을 DB에 저장한다.
  @Column(nullable = false)
  private String fileName;
  @Column(nullable = false)
  private Long size;
  @Column(length = 100, nullable = false)
  private String contentType;

  /// 업로드 상태 필드.
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private BinaryContentStatus status;

  public BinaryContent(String fileName, Long size, String contentType) {
    this.fileName = fileName;
    this.size = size;
    this.contentType = contentType;
    this.status = BinaryContentStatus.PROCESSING;
  }

  /// 상태 업데이트 메서드.
  public void updateStatus(BinaryContentStatus status) {
    this.status = status;
  }
}
