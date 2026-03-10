package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.util.UUID;

@MappedSuperclass//JPA에서 부모 클래스의 필드를 자식 엔티티 테이블에 매핑
@Getter
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @CreatedDate
    private Instant createdAt;

    protected BaseEntity() {
//        this.id = UUID.randomUUID(); //@GeneratedValue와 같이쓰면 충돌난다.
        this.createdAt = Instant.now();
    }
}
