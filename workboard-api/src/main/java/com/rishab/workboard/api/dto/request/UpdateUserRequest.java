package com.rishab.workboard.api.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {

    private String bio;

    private String username;

    private String email;

}
