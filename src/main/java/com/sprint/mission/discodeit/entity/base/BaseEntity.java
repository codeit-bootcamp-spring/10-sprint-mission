package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@MappedSuperclass//JPA에서 부모 클래스의 필드를 자식 엔티티 테이블에 매핑
@Getter
@EntityListeners(AuditingEntityListener.class) //이게 있어야 @CreatedDate와 @LastModifiedDate가 동작한다.
public abstract class BaseEntity {//공통 기능을 제공하는 부모 클래스이기 때문에 abstract로 설정.

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @CreatedDate
    private Instant createdAt;

}
