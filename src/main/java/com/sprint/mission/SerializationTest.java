package com.sprint.mission;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SerializationTest {
    public static void main(String[] args) {
        // ============================================================
        // 15. 직렬화 확인 - Channel
        // ============================================================
        System.out.println("=== [15] Serialization Check - Channel ===");
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data/channel/channel.ser"))) {
            List<Channel> channels = (List<Channel>) ois.readObject();
            System.out.println("Channels loaded: " + channels.size());
            channels.forEach(ch -> System.out.println("  id=" + ch.getId() + ", name=" + ch.getName()
                    + ", members=" + ch.getMembersList().size() + ", messages=" + ch.getMessageList().size()));
        } catch (Exception e) {
            System.out.println("Channel serialization failed: " + e.getMessage());
        }
        System.out.println();

        // ============================================================
        // 16. 직렬화 확인 - User
        // ============================================================
        System.out.println("=== [16] Serialization Check - User ===");
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data/user/user.ser"))) {
            List<User> users = (List<User>) ois.readObject();
            System.out.println("Users loaded: " + users.size());
            users.forEach(u -> System.out.println("  id=" + u.getId() + ", name=" + u.getName()
                    + ", channels=" + u.getChannels().size() + ", messages=" + u.getMessageList().size()));
        } catch (Exception e) {
            System.out.println("User serialization failed: " + e.getMessage());
        }
        System.out.println();

        // ============================================================
        // 17. 직렬화 확인 - Message
        // ============================================================
        System.out.println("=== [17] Serialization Check - Message ===");
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data/message/message.ser"))) {
            Map<UUID, Message> messages = (Map<UUID, Message>) ois.readObject();
            System.out.println("Messages loaded: " + messages.size());
            messages.values().forEach(m -> System.out.println("  id=" + m.getId() + ", sender=" + m.getSender().getName()
                    + ", channel=" + m.getChannel().getName() + ", content=" + m.getContents()));
        } catch (Exception e) {
            System.out.println("Message serialization failed: " + e.getMessage());
        }

        System.out.println("\n========================================");
        System.out.println("===== TEST END =====");
        System.out.println("========================================");
    }
}
