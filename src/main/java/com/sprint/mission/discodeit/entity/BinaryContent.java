package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name="binary_contents")
@NoArgsConstructor
public class BinaryContent extends BaseEntity{

  //private static final long serialVersionUID = 1L;
  //private UUID id;
  @Column(name="file_name", nullable = false, length=255)
  private String fileName;

  @Column(nullable = false)
  private Long size;

  @Column(name="content_type", nullable = false, length=100)
  private String contentType;


  // 생성자(id/createdAt 은 BaseEntity 상속)
  public BinaryContent(String fileName, Long size, String contentType) {
    this.fileName = fileName;
    this.size = size;
    this.contentType = contentType;
  }
}
