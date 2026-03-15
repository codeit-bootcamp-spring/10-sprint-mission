package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "binary_contents")
public class BinaryContent extends BaseEntity {

  @Column(nullable = false, length = 255)
  private String fileName;

  @Column(nullable = false)
  private Long size;

  @Column(nullable = false, length = 100)
  private String contentType;

  public BinaryContent(String fileName, Long size, String contentType) {
    this.fileName = fileName;
    this.size = size;
    this.contentType = contentType;
  }

  public void update(String newFileName, Long newSize, String newContentType) {
    if (newFileName != null && !newFileName.equals(this.fileName)) {
      this.fileName = newFileName;
    }
    if (newSize != null && !newSize.equals(this.size)) {
      this.size = newSize;
    }
    if (newContentType != null && !newContentType.equals(this.contentType)) {
      this.contentType = newContentType;
    }
  }
}