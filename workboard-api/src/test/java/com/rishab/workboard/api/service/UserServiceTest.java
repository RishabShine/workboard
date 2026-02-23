package com.rishab.workboard.api.service;

import com.rishab.workboard.api.domain.User;
import com.rishab.workboard.api.dto.request.CreateUserRequest;
import com.rishab.workboard.api.dto.request.UpdateUserRequest;
import com.rishab.workboard.api.dto.response.common.UserSummaryDto;
import com.rishab.workboard.api.repository.UserRepository;
import com.rishab.workboard.api.service.exceptions.ForbiddenException;
import com.rishab.workboard.api.service.exceptions.NotFoundException;
import com.rishab.workboard.api.service.impl.UserServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserServiceTest {

    @Autowired private UserServiceImpl userService;
    @Autowired private UserRepository userRepository;

    @Test
    @Transactional
    void createUser_persistsUser_andReturnsSummaryDto() {
        long ts = System.currentTimeMillis();

        CreateUserRequest req = new CreateUserRequest();
        req.setUsername("user_" + ts);
        req.setEmail(("user_" + ts + "@example.com"));
        req.setPassword("hash");
        req.setBio("hello");

        UserSummaryDto dto = userService.createUser(req);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isNotNull();
        assertThat(dto.getUsername()).isEqualTo(req.getUsername());
        assertThat(dto.getEmail()).isEqualTo(req.getEmail());

        User saved = userRepository.findById(dto.getId())
                .orElseThrow(() -> new AssertionError("Expected user to exist"));

        assertThat(saved.getUsername()).isEqualTo(req.getUsername());
        assertThat(saved.getEmail()).isEqualTo(req.getEmail());
        assertThat(saved.getBio()).isEqualTo(req.getBio());
    }

    @Test
    @Transactional
    void createUser_whenDuplicateUsername_throwsForbidden() {
        long ts = System.currentTimeMillis();

        // Seed user
        User u1 = new User();
        u1.setUsername("dup_" + ts);
        u1.setEmail(("dup_" + ts + "@example.com"));
        u1.setPassword("hash");
        userRepository.save(u1);

        // Attempt to create user with same username but different email
        CreateUserRequest req = new CreateUserRequest();
        req.setUsername(u1.getUsername());
        req.setEmail(("other_" + ts + "@example.com"));
        req.setPassword("hash");

        assertThatThrownBy(() -> userService.createUser(req))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("already in use");
    }

    @Test
    @Transactional
    void createUser_whenDuplicateEmail_throwsForbidden() {
        long ts = System.currentTimeMillis();

        // Seed user
        User u1 = new User();
        u1.setUsername("u_" + ts);
        u1.setEmail(("dupemail_" + ts + "@example.com"));
        u1.setPassword("hash");
        userRepository.save(u1);

        // Attempt to create user with different username but same email
        CreateUserRequest req = new CreateUserRequest();
        req.setUsername("another_" + ts);
        req.setEmail(u1.getEmail());
        req.setPassword("hash");

        assertThatThrownBy(() -> userService.createUser(req))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("already in use");
    }

    @Test
    @Transactional
    void getUser_returnsUserSummary() {
        long ts = System.currentTimeMillis();

        User u = new User();
        u.setUsername("get_" + ts);
        u.setEmail(("get_" + ts + "@example.com"));
        u.setPassword("hash");
        u = userRepository.save(u);

        UserSummaryDto dto = userService.getUser(u.getId(), u.getId());

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(u.getId());
        assertThat(dto.getUsername()).isEqualTo(u.getUsername());
        assertThat(dto.getEmail()).isEqualTo(u.getEmail());
    }

    @Test
    @Transactional
    void getUser_whenMissing_throwsNotFound() {
        long ts = System.currentTimeMillis();

        User caller = new User();
        caller.setUsername("caller_" + ts);
        caller.setEmail(("caller_" + ts + "@example.com"));
        caller.setPassword("hash");
        User savedCaller = userRepository.save(caller);

        assertThatThrownBy(() -> userService.getUser(999999999L, savedCaller.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    @Transactional
    void updateUserProfile_updatesProvidedFields_only() {
        long ts = System.currentTimeMillis();

        User u = new User();
        u.setUsername("upd_" + ts);
        u.setEmail(("upd_" + ts + "@example.com"));
        u.setPassword("hash");
        u.setBio("old");
        u = userRepository.save(u);

        UpdateUserRequest req = new UpdateUserRequest();
        req.setUsername("upd_new_" + ts);
        req.setBio("new bio");
        // req.setEmail(null) => no change

        UserSummaryDto dto = userService.updateUserProfile(req, u.getId());

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(u.getId());
        assertThat(dto.getUsername()).isEqualTo("upd_new_" + ts);

        User saved = userRepository.findById(u.getId())
                .orElseThrow(() -> new AssertionError("Expected user to exist"));

        assertThat(saved.getUsername()).isEqualTo("upd_new_" + ts);
        assertThat(saved.getBio()).isEqualTo("new bio");
        assertThat(saved.getEmail()).isEqualTo("upd_" + ts + "@example.com"); // unchanged
    }

    @Test
    @Transactional
    void updateUserProfile_whenDuplicateUsername_throwsForbidden() {
        long ts = System.currentTimeMillis();

        User u1 = new User();
        u1.setUsername("u1_" + ts);
        u1.setEmail(("u1_" + ts + "@example.com"));
        u1.setPassword("hash");
        u1 = userRepository.save(u1);

        User u2 = new User();
        u2.setUsername("u2_" + ts);
        u2.setEmail(("u2_" + ts + "@example.com"));
        u2.setPassword("hash");
        User savedU2 = userRepository.save(u2);

        UpdateUserRequest req = new UpdateUserRequest();
        req.setUsername(u1.getUsername()); // collide

        assertThatThrownBy(() -> userService.updateUserProfile(req, savedU2.getId()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("already in use");
    }

    @Test
    @Transactional
    void searchUsers_returnsMatchingUsers_byUsernameOrEmail() {
        long ts = System.currentTimeMillis();

        // caller must exist
        User caller = new User();
        caller.setUsername("caller_search_" + ts);
        caller.setEmail(("caller_search_" + ts + "@example.com"));
        caller.setPassword("hash");
        caller = userRepository.save(caller);

        User match1 = new User();
        match1.setUsername("alpha_" + ts);
        match1.setEmail(("alpha_" + ts + "@example.com"));
        match1.setPassword("hash");
        match1 = userRepository.save(match1);

        User match2 = new User();
        match2.setUsername("beta_" + ts);
        match2.setEmail(("special_" + ts + "@example.com"));
        match2.setPassword("hash");
        match2 = userRepository.save(match2);

        User noMatch = new User();
        noMatch.setUsername("zzz_" + ts);
        noMatch.setEmail(("zzz_" + ts + "@example.com"));
        noMatch.setPassword("hash");
        noMatch = userRepository.save(noMatch);

        // Act: search by username fragment
        List<UserSummaryDto> byUsername = userService.searchUsers("alpha", caller.getId());

        assertThat(byUsername).extracting(UserSummaryDto::getId)
                .contains(match1.getId())
                .doesNotContain(noMatch.getId());

        // Act: search by email fragment
        List<UserSummaryDto> byEmail = userService.searchUsers("special", caller.getId());

        assertThat(byEmail).extracting(UserSummaryDto::getId)
                .contains(match2.getId())
                .doesNotContain(noMatch.getId());
    }

    @Test
    @Transactional
    void searchUsers_whenQueryBlank_returnsEmptyList() {
        long ts = System.currentTimeMillis();

        User caller = new User();
        caller.setUsername("caller_blank_" + ts);
        caller.setEmail(("caller_blank_" + ts + "@example.com"));
        caller.setPassword("hash");
        caller = userRepository.save(caller);

        assertThat(userService.searchUsers("   ", caller.getId())).isEmpty();
        assertThat(userService.searchUsers(null, caller.getId())).isEmpty();
    }

    @Test
    @Transactional
    void searchUsers_whenCallerMissing_throwsNotFound() {
        assertThatThrownBy(() -> userService.searchUsers("bob", 999999999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");
    }
}