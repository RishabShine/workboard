package com.rishab.workboard.api.service;

import com.rishab.workboard.api.dto.request.CreateUserRequest;
import com.rishab.workboard.api.dto.request.UpdateUserRequest;
import com.rishab.workboard.api.dto.response.common.UserSummaryDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    UserSummaryDto getUser(Long userId, Long currentUserId);

    UserSummaryDto updateUserProfile(UpdateUserRequest req, Long currentUserId);

    void updateUserProfileImage(Long currentUserId, MultipartFile file);

    UserSummaryDto createUser(CreateUserRequest req);

    List<UserSummaryDto> searchUsers(String q, Long currentUserId);

}
