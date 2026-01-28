package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record UserCreateDto (

    String username,
    String email,
    String password,
    BinaryContentDto binaryContentDto


) {}



