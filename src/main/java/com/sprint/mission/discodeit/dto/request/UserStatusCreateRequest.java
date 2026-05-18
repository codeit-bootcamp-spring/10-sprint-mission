// UserStatus는 SessionRegistry 기반 online 판단으로 대체되어 더 이상 사용하지 않습니다.
// package com.sprint.mission.discodeit.dto.request;
//
// import jakarta.validation.constraints.NotNull;
// import jakarta.validation.constraints.PastOrPresent;
// import java.time.Instant;
// import java.util.UUID;
//
// public record UserStatusCreateRequest(
//     @NotNull(message = "userId is required.")
//     UUID userId,
//
//     @NotNull(message = "lastActiveAt is required.")
//     @PastOrPresent(message = "lastActiveAt must be past or present.")
//     Instant lastActiveAt
// ) {
//
// }
