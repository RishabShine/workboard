package com.rishab.workboard.api.s3.impl;

import com.rishab.workboard.api.config.s3.UserS3Properties;
import com.rishab.workboard.api.s3.ProfileImageStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Component
public class S3ProfileImageStorage implements ProfileImageStorage {

    private S3Client s3;
    private UserS3Properties s3Users;

    @Autowired
    public S3ProfileImageStorage(S3Client s3Client, UserS3Properties userS3Properties) {
        this.s3 = s3Client;
        this.s3Users = userS3Properties;
    }

    public void uploadProfileImage(Long userId, String imageKey, byte[] image) {

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(s3Users.getBucketName())
                .key(s3Users.buildProfileImageKey(userId, imageKey))
                .contentType("image/jpeg")
                .build();
        s3.putObject(objectRequest, RequestBody.fromBytes(image));
    }

    public byte[] getProfileImage(Long userId, String imageKey) {

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(s3Users.getBucketName())
                .key(s3Users.buildProfileImageKey(userId, imageKey))
                .build();

        ResponseInputStream<GetObjectResponse> res = s3.getObject(getObjectRequest);

        try {
            return res.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
