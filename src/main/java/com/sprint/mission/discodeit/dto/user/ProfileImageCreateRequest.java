package com.sprint.mission.discodeit.dto.user;

public record ProfileImageCreateRequest (
   String fileName,
   String contentType,
   byte [] data
) {}
