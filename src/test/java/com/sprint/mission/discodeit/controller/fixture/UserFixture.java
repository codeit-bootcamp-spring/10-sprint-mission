package com.sprint.mission.discodeit.controller.fixture;

import com.sprint.mission.discodeit.dto.UserServiceDTO.UserCreateRequest;
import net.datafaker.Faker;
import net.datafaker.providers.base.Text;

import java.util.Random;

import static net.datafaker.providers.base.Text.DIGITS;
import static net.datafaker.providers.base.Text.EN_UPPERCASE;

public class UserFixture {
    private static final Faker faker = new Faker(new Random(1));

    public static UserCreateRequest getUserCreateRequest() {
        String username = faker.name().name();
        String password = faker.text()
                .text(Text.TextSymbolsBuilder
                        .builder()
                        .len(8)
                        .with(EN_UPPERCASE, 2)
                        .with(DIGITS, 3)
                        .build());
        return new UserCreateRequest(username,
                faker.internet().emailAddress(username),
                password,
            BinaryContentFixture.getBinaryContentCreateRequest());
    }
}
