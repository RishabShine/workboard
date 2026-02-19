package com.rishab.workboard.api.dto.login;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    private String usernameOrEmail;

    private String password;

}
