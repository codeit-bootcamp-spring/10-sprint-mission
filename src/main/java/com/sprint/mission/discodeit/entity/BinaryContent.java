package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "binary_contents")
public class BinaryContent extends BaseUpdatableEntity {

  @Size(max = 255)
  @Column(length = 255, nullable = false)
  private String fileName;

  @Size(max = 100)
  @Column(length = 100, nullable = false)
  private String contentType;

  @Column(nullable = false)
  private long size;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private BinaryContentStatus status = BinaryContentStatus.PROCESSING;

  public static BinaryContent create(String fileName, String contentType, long size) {
    return new BinaryContent(fileName, contentType, size);
  }

  private BinaryContent(String fileName, String contentType, long size) {
    this.fileName = fileName;
    this.contentType = contentType;
    this.size = size;
  }

  public void updateStatus(BinaryContentStatus status) {
    this.status = status;
  }

}
