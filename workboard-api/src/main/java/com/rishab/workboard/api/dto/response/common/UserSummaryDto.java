package com.rishab.workboard.api.dto.response.common;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSummaryDto {

    private Long id;

    private String username;

    String email;

    private String profileImageKey;

}
