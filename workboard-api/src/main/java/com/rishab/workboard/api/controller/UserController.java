package com.rishab.workboard.api.controller;

import com.rishab.workboard.api.dto.request.UpdateUserRequest;
import com.rishab.workboard.api.dto.response.common.UserSummaryDto;
import com.rishab.workboard.api.security.auth.AuthUser;
import com.rishab.workboard.api.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserSummaryDto> getUser(
            @AuthenticationPrincipal AuthUser user) {
        UserSummaryDto result = userService.getUser(user.userId(), user.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserSummaryDto> getUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal AuthUser user) {
        UserSummaryDto result = userService.getUser(userId, user.userId());
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/updateProfile")
    public ResponseEntity<UserSummaryDto> updateProfile(
            @RequestBody UpdateUserRequest request,
            @AuthenticationPrincipal AuthUser user
    ) {
        UserSummaryDto result = userService.updateUserProfile(request, user.userId());
        return ResponseEntity.ok(result);
    }

    @GetMapping("search/{query}")
    public ResponseEntity<List<UserSummaryDto>> searchUsers(
            @PathVariable String query
    ) {
        List<UserSummaryDto> result = userService.searchUsers(query);
        return ResponseEntity.ok(result);
    }

}
