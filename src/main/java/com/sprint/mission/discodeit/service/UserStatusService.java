// UserStatus는 SessionRegistry 기반 online 판단으로 대체되어 더 이상 사용하지 않습니다.
// package com.sprint.mission.discodeit.service;
//
// import com.sprint.mission.discodeit.dto.data.UserStatusDto;
// import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
// import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
// import java.util.List;
// import java.util.UUID;
//
// public interface UserStatusService {
//
//   UserStatusDto create(UserStatusCreateRequest request);
//
//   UserStatusDto find(UUID userStatusId);
//
//   List<UserStatusDto> findAll();
//
//   UserStatusDto update(UUID userStatusId, UserStatusUpdateRequest request);
//
//   UserStatusDto updateByUserId(UUID userId, UserStatusUpdateRequest request);
//
//   void delete(UUID userStatusId);
// }
