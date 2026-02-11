package com.sprint.mission.discodeit.dto;

public record BinaryContentDto(
	String fileName,
	byte[] data
) {
}
