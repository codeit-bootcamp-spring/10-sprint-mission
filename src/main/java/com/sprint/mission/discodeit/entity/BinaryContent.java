package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "BINARY_CONTENTS")
public class BinaryContent extends BaseUpdatableEntity {

  @Column(nullable = false)
  private String fileName;

  @Column(nullable = false)
  private long size;

  @Column(nullable = false)
  private String contentType;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private BinaryContentStatus status = BinaryContentStatus.PROCESSING;

  public BinaryContent(String fileName, long size, String contentType) {
    this.fileName = fileName;
    this.size = size;
    this.contentType = contentType;
  }

  public void updateStatus(BinaryContentStatus status) {
    updateIfChanged(this.status, status, val -> this.status = val);
  }
}
