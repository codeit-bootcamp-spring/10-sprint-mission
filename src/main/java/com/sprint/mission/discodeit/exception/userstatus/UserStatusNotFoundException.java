// UserStatus는 SessionRegistry 기반 online 판단으로 대체되어 더 이상 사용하지 않습니다.
// package com.sprint.mission.discodeit.exception.userstatus;
//
// import com.sprint.mission.discodeit.exception.ErrorCode;
// import java.util.UUID;
//
// public class UserStatusNotFoundException extends UserStatusException {
//
//   public UserStatusNotFoundException() {
//     super(ErrorCode.USER_STATUS_NOT_FOUND);
//   }
//
//   public static UserStatusNotFoundException withId(UUID userStatusId) {
//     UserStatusNotFoundException exception = new UserStatusNotFoundException();
//     exception.addDetail("userStatusId", userStatusId);
//     return exception;
//   }
//
//   public static UserStatusNotFoundException withUserId(UUID userId) {
//     UserStatusNotFoundException exception = new UserStatusNotFoundException();
//     exception.addDetail("userId", userId);
//     return exception;
//   }
// }
