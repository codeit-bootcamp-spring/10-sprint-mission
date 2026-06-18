package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "binary_contents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BinaryContent extends BaseUpdatableEntity {

  @Column(name = "file_name", nullable = false)
  private String fileName;

  @Column(nullable = false)
  private Long size;

  @Column(name = "content_type", nullable = false, length = 100)
  private String contentType;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private BinaryContentStatus status;

  public BinaryContent(String fileName, Long size, String contentType) {
    super();
    this.fileName = fileName;
    this.size = size;
    this.contentType = contentType;
    this.status = BinaryContentStatus.PROCESSING;
  }

  public void updateStatus(BinaryContentStatus status) {
    this.status = status;
  }
}
