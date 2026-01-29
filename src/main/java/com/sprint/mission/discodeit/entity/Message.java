package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Getter;

@Getter
public class Message extends Base {
	private String text;
	private UUID channelId;
	private UUID authorId;
	private List<UUID> attachmentIds;

	public Message(UUID authorId, UUID channelId, String text, List<UUID> attachmentIds) {
		this.authorId = authorId;
		this.channelId = channelId;
		this.text = text;
		this.attachmentIds = attachmentIds != null ? attachmentIds : new ArrayList<>();
	}

	public void updateText(String text) {
		this.text = text;
		updateUpdatedAt(Instant.now());
	}

	@Override
	public String toString() {
		return "{" +
			channelId + ">" +
			authorId + ": " +
			text +
			"(" + getUpdatedAt() + ")}";
	}
}
