package com.sprint.mission.discodeit.binarycontent.entity;

import com.sprint.mission.discodeit.common.basicentity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "binary_contents")
@Getter
@NoArgsConstructor
public class BinaryContent extends BaseEntity {

  @Column(nullable = false)
  private String fileName;

  @Column(nullable = false)
  private Long size;

  @Column(nullable = false, length = 100)
  private String contentType;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private BinaryContentStatus status = BinaryContentStatus.PROCESSING;

  public BinaryContent(String fileName, Long size, String contentType) {
    this.fileName = fileName;
    this.size = size;
    this.contentType = contentType;
  }

  public void updateStatus(BinaryContentStatus status) {
    this.status = status;
  }
}
