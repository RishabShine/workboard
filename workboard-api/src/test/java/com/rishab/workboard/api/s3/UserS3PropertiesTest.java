package com.rishab.workboard.api.s3;

import com.rishab.workboard.api.config.s3.UserS3Properties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class UserS3PropertiesTest {

    @Autowired
    private UserS3Properties userProperties;

    /*
    making sure that the profle image keys generate properly
     */
    @Test
    public void buildProfileImageKeyTest() {
        Long userId = 12345L;
        String imageKey = "test";

        String profileImagekey = userProperties.buildProfileImageKey(userId, imageKey);

        assertThat(profileImagekey)
                .isEqualTo("profile-images/12345/test");
    }

}
