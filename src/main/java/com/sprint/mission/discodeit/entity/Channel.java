package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class Channel extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private String type;        // 채팅 채널(TEXT), 공지 채널(ANNOUNCEMENT), 음성 채널(VOICE)
    private boolean isPublic;   // 공개/비공개 여부
    private final List<User> members = new ArrayList<>();
    private final List<Message> messages = new ArrayList<>();

    public Channel(String name, String description, String type, boolean isPublic) {
        super();
        this.name = name;
        this.description = description;
        this.type = (type != null) ? type : "TEXT";
        this.isPublic = isPublic;
    }

    // 채널 정보 수정
    public void update(String name, String description, boolean isPublic) {
        this.name = name;
        this.description = description;
        this.isPublic = isPublic;
        this.updated(); // BaseEntity의 시간 갱신
    }

    // 채널 멤버인지 확인
    public boolean isMember(User user){
        return members.contains(user);
    }

    // 채널 멤버 초대
    public void addMember(User user){
        this.members.add(user);
    }

    // 채널 멤버 삭제
    public void removeMember(User user){
        this.members.remove(user);
    }

    // 메시지 추가
    public  void addMessage(Message message){
        this.messages.add(message);
    }

    // 메시지 삭제
    public  void removeMessage(Message message){
        this.messages.remove(message);
    }

    // --- getter ---
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public boolean isPublic() { return isPublic; }
    public List<User> getMembers() { return Collections.unmodifiableList(members); }
    public List<Message> getMessages() { return Collections.unmodifiableList(messages); }

    @Override
    public String toString() {
        return "Channel{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", isPublic=" + isPublic +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
