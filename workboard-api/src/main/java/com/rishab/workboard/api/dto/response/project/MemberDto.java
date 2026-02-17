package com.rishab.workboard.api.dto.response.project;

import com.rishab.workboard.api.dto.response.common.RoleDto;
import com.rishab.workboard.api.dto.response.common.UserSummaryDto;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class MemberDto {

    private UserSummaryDto user;

    private RoleDto role;

    private String email;

    private OffsetDateTime joinedOn;

}
