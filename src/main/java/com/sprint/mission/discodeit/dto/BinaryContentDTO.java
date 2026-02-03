package com.sprint.mission.discodeit.dto;

public record BinaryContentDTO(
	String fileName,
	byte[] data
) {
}
