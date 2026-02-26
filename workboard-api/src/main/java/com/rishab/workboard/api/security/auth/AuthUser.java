package com.rishab.workboard.api.security.auth;

/*
decoded from jwt, set as auth principal, to pass userId from controller layer
 */
public record AuthUser(Long userId, String username) {}