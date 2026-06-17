package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Table(name = "binary_contents")
@Getter
public class BinaryContent extends BaseUpdatableEntity {

  @Column(nullable = false, length = 255)
  private String fileName;

  @Column(nullable = false)
  private long size;

  @Column(nullable = false, length = 100)
  private String contentType;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private BinaryContentStatus status;

  public BinaryContent(String fileName, long size, String contentType) {
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
