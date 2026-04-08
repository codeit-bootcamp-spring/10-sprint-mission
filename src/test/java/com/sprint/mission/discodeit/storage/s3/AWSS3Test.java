package com.sprint.mission.discodeit.storage.s3;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class AWSS3Test {

    @Autowired
    private S3UploadService s3UploadService;

    @Autowired
    private AwsProperties props;

    @Test
    void 업로드_Test() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.png",
                "image/png",
                "hello s3".getBytes()
        );
        // when
        String url = s3UploadService.store(file);

        // then
        assertThat(url).isNotNull();
        assertThat(url).contains(props.getBucket());
        assertThat(url).startsWith("https://");
    }
}