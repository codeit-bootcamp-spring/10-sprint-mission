package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;

public class Channel extends BaseEntity {
    private String name;
    private String description;
    private String type;        // 채팅 채널(TEXT), 공지 채널(ANNOUNCEMENT), 음성 채널(VOICE)
    private boolean isPublic;   // 공개/비공개 여부
    private final List<User> members = new ArrayList<>();

    public Channel(String name, String description, String type, boolean isPublic) {
        super();
        this.name = name;
        this.description = description;
        this.type = (type != null) ? type : "TEXT";
        this.isPublic = isPublic;
    }

    public void update(String name, String description, boolean isPublic) {
        this.name = name;
        this.description = description;
        this.isPublic = isPublic;
        this.updated(); // 직접 System.currentTimeMillis()를 쓰지 않고 부모의 메서드 활용
    }

    public boolean isMember(User user){
        return members.contains(user);
    }

    public void addMember(User user){
        if (isMember(user)) {
            throw new IllegalStateException("이미 채널에 가입된 멤버");
        }
        this.members.add(user);
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public boolean isPublic() { return isPublic; }

    @Override
    public String toString() {
        return "Channel{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", isPublic=" + isPublic +
                ", members=" + members +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
