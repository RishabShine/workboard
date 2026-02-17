package com.rishab.workboard.api.config.s3;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "aws.s3.buckets.users")
public class UserS3Properties {

    private String bucketName;
    private String profileImagesPrefix;

    public String buildProfileImageKey(Long userId, String imageKey) {
        return profileImagesPrefix + userId + "/" + imageKey;
    }
}

