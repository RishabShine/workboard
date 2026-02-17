package com.rishab.workboard.api.s3;

import org.springframework.web.multipart.MultipartFile;

public interface ProfileImageStorage {

    public void uploadProfileImage(Long userId, String imageKey, byte[] image);

    public byte[] getProfileImage(Long userId, String imageKey);

}
