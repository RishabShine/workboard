package com.rishab.workboard.api.service.impl;

import com.rishab.workboard.api.domain.User;
import com.rishab.workboard.api.dto.request.CreateUserRequest;
import com.rishab.workboard.api.dto.request.UpdateUserRequest;
import com.rishab.workboard.api.dto.response.common.UserSummaryDto;
import com.rishab.workboard.api.mapper.Mapper;
import com.rishab.workboard.api.mapper.impl.UserMapperImpl;
import com.rishab.workboard.api.repository.UserRepository;
import com.rishab.workboard.api.s3.ProfileImageStorage;
import com.rishab.workboard.api.service.UserService;
import com.rishab.workboard.api.service.exceptions.ForbiddenException;
import com.rishab.workboard.api.service.exceptions.NotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final Mapper<User, UserSummaryDto> userMapper;
    private final ProfileImageStorage profileImageStorage;

    UserServiceImpl(UserRepository userRepository,
                    UserMapperImpl userMapper,
                    ProfileImageStorage profileImageStorage) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.profileImageStorage = profileImageStorage;
    }

    @Override
    public UserSummaryDto getUser(Long userId, Long currentUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserSummaryDto updateUserProfile(UpdateUserRequest req, Long currentUserId) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (req.getUsername() != null) user.setUsername(req.getUsername());
        if (req.getEmail() != null) user.setEmail(req.getEmail());
        if (req.getBio() != null) user.setBio(req.getBio());

        try {
            User saved = userRepository.save(user);
            return userMapper.toDto(saved);
        } catch (DataIntegrityViolationException e) {
            throw new ForbiddenException("Username or email already in use");
        }
    }

    @Override
    @Transactional
    public void updateUserProfileImage(Long currentUserId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ForbiddenException("File is required");
        }

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new ForbiddenException("Could not read uploaded file");
        }

        // Generate a unique image key (store this in DB, S3 key will be built using prefix + userId)
        // UUID + original extension if present
        String original = file.getOriginalFilename();
        String ext = "";

        // getting file type (png, jpg, ...)
        if (original != null) {
            int dot = original.lastIndexOf('.');
            if (dot >= 0 && dot < original.length() - 1) {
                ext = original.substring(dot).toLowerCase(); // includes the '.'
            }
        }

        String imageKey = UUID.randomUUID() + ext;

        // Upload to S3
        profileImageStorage.uploadProfileImage(currentUserId, imageKey, bytes);

        // Update DB with key
        user.setProfileImageKey(imageKey);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserSummaryDto createUser(CreateUserRequest req) {
        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(req.getPassword());
        user.setBio(req.getBio());

        try {
            User saved = userRepository.save(user);
            return userMapper.toDto(saved);
        } catch (DataIntegrityViolationException e) {
            throw new ForbiddenException("Username or email already in use");
        }
    }

    @Override
    public List<UserSummaryDto> searchUsers(String q, Long currentUserId) {
        // Optional: ensure caller exists (useful pre-JWT)
        userRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (q == null || q.trim().isEmpty()) {
            return List.of();
        }

        String query = q.trim();

        // Replace this with your real method.
        // Recommended repo method:
        // List<User> findTop20ByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(String u, String e);
        List<User> users = userRepository.findTop20ByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query);

        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }
}