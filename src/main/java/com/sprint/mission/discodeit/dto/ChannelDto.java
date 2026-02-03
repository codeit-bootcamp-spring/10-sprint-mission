package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ChannelType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ChannelDto {
//    public record CreatePublicRequest(
//            @NotBlank
//            String name,
//            String description
//
//    ) {}
//    public record CreatePrivateRequest(
//            List<UUID> memberIds
//
//    ) {}

    public record CreateRequest(
            @NotNull
            ChannelType type,
            String name,
            String description,
            Set<UUID> memberIds // 유저 중복 검출
    ) {
        @AssertTrue(message = "공개 채널은 이름이 필수이고, 비공개 채널은 멤버가 필수입니다.")
        public boolean isValid() {
        if (type == ChannelType.PUBLIC) {
            return StringUtils.hasText(name); // 이름 필수
        } else if (type == ChannelType.PRIVATE) {
            return memberIds != null && !memberIds.isEmpty(); // 멤버 필수
        }
        return false;
    }}

    public record Response(
            UUID id,
            Instant createdAt,
            Instant updatedAt,
            ChannelType type,
            String name,
            String description,
            List<UUID> memberIds,
            Instant lastMessageAt
    ) {}

    public record UpdatePublicRequest(
            @NotBlank
            String newName,
            String newDescription

    ){}
}
