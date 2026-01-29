package com.sprint.mission.discodeit.entity;

import java.util.UUID;

import lombok.Getter;

@Getter
public class BinaryContent extends Base {
	private UUID userId; // 해당 프로필을 설정한 유저의 아이디
	private UUID messageId; // 해당 파일이 첨부된 메시지의 아이디

	public BinaryContent(UUID userId, UUID messageId) {
		this.userId = userId;
		this.messageId = messageId;
	}
}
