package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.DiscordEntity;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class CheckValidation {
    public static void checkUserChannelNull(User user, Channel channel){
        Objects.requireNonNull(user, "유효하지 않은 유저입니다.");
        Objects.requireNonNull(channel, "유효하지 않은 채널입니다");
    }

    public static <T extends DiscordEntity> T readEntity(List<T> data, UUID uuid, Supplier<? extends RuntimeException> exSupplier){
       return data.stream()
               .filter(t -> uuid.equals(t.getId()))
               .findFirst()
               .orElseThrow(exSupplier);
    }
}
