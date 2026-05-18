// UserStatus는 SessionRegistry 기반 online 판단으로 대체되어 더 이상 사용하지 않습니다.
// package com.sprint.mission.discodeit.exception.userstatus;
//
// import com.sprint.mission.discodeit.exception.ErrorCode;
// import java.util.UUID;
//
// public class DuplicateUserStatusException extends UserStatusException {
//
//   public DuplicateUserStatusException() {
//     super(ErrorCode.DUPLICATE_USER_STATUS);
//   }
//
//   public static DuplicateUserStatusException withUserId(UUID userId) {
//     DuplicateUserStatusException exception = new DuplicateUserStatusException();
//     exception.addDetail("userId", userId);
//     return exception;
//   }
// }
