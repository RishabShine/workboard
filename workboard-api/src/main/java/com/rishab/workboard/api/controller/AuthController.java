package com.rishab.workboard.api.controller;

import com.rishab.workboard.api.dto.login.LoginRequest;
import com.rishab.workboard.api.dto.login.LoginResponse;
import com.rishab.workboard.api.dto.login.RegisterRequest;
import com.rishab.workboard.api.dto.request.CreateUserRequest;
import com.rishab.workboard.api.dto.response.common.UserSummaryDto;
import com.rishab.workboard.api.security.jwt.JwtService;
import com.rishab.workboard.api.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager,
                          UserService userService,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        try {
            // verify username/email + password using Spring Security
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsernameOrEmail(), req.getPassword())
            );

            // load the user so we can get the database ID for the JWT
            List<UserSummaryDto> userSearch = userService.searchUsers(req.getUsernameOrEmail());

            // making sure there is one corresponding user
            if (userSearch.size() != 1) {
                throw new UsernameNotFoundException("User not found");
            }

            UserSummaryDto user = userSearch.get(0);

            // generate JWT containing uid + username
            String token = jwtService.generateToken(user.getId(), user.getUsername());

            // 4) Return token to frontend
            return ResponseEntity.ok(new LoginResponse(token));

        } catch (BadCredentialsException e) {
            // wrong username/email or password
            throw new BadCredentialsException("Invalid username/email or password");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest req) {

        // check for existing username
        if (!userService.searchUsers(req.getUsername()).isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Username is already taken");
        }

        // check for existing email
        if (!userService.searchUsers(req.getEmail()).isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email is already in use");
        }

        // 3) Create and save user
        CreateUserRequest userReq = new CreateUserRequest();
        userReq.setUsername(req.getUsername().toLowerCase().trim());
        userReq.setEmail(req.getEmail().toLowerCase().trim());
        userReq.setPassword(passwordEncoder.encode(req.getPassword()));
        //! include bio

        userService.createUser(userReq);

        // 4) Return success
        // frontend should redirect to /login after receiving this
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User registered successfully");
    }

}
