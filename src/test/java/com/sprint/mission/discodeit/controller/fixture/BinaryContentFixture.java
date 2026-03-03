package com.sprint.mission.discodeit.controller.fixture;

import com.sprint.mission.discodeit.dto.BinaryContentServiceDTO.BinaryContentCreateRequest;
import net.datafaker.Faker;

import java.util.Random;

public class BinaryContentFixture {
    private static final Faker faker = new Faker(new Random(1));

    public static BinaryContentCreateRequest getBinaryContentCreateRequest() {
        return new BinaryContentCreateRequest(faker.animal().name(),
                "png",
                faker.random().nextRandomBytes(10));
    }
}
