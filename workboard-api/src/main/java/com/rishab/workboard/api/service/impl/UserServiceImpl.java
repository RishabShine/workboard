package com.rishab.workboard.api.service.impl;

import com.rishab.workboard.api.dto.request.CreateUserRequest;
import com.rishab.workboard.api.dto.request.UpdateUserRequest;
import com.rishab.workboard.api.dto.response.common.UserSummaryDto;
import com.rishab.workboard.api.service.UserService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    public UserSummaryDto getUser(Long userId, Long currentUserId) {
        return null;
    }

    public UserSummaryDto updateUserProfile(UpdateUserRequest req, Long currentUserId) {
        return null;
    }

    public void updateUserProfileImage(Long currentUserId, MultipartFile file) {
    }

    public UserSummaryDto createUser(CreateUserRequest req) {
        return null;
    }

    public List<UserSummaryDto> searchUsers(String q, Long currentUserId) {
        return null;
    }

}
