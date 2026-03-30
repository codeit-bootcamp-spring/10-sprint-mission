package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "binary_contents")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class BinaryContent extends BaseEntity {

    @Column(length = 255, nullable = false)
    private String fileName;

    @Column(nullable = false)
    private long size;

    @Column(length = 100, nullable = false)
    private String contentType;
}
