package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class Common implements Serializable{
    private final UUID id;
    private final long createdAt;
    private long updatedAt;


    public Common(){
        id = UUID.randomUUID();

        createdAt = System.currentTimeMillis();
        updatedAt = createdAt;
        // id, createdAt, updatedAt을 제외한 필드는 생성자의 파라미터를 통해 초기화하세요.
    }
    /*
    * 각 필드를 반환하는 Getter 함수를 정의하세요.
      필드를 수정하는 update 함수를 정의하세요.*/
    public UUID getId(){
        return id;
    }

    public long getCreatedAt(){
        return createdAt;
    }

    public long getUpdatedAt(){
        return updatedAt;
    }

    public void setUpdatedAt(){
        updatedAt = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Common common = (Common) o;
        return Objects.equals(id, common.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    @Override
    public String toString() {
        return "ID: " + id;
    }
}
