package com.rishab.workboard.api.dto.request;

import com.rishab.workboard.api.domain.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMemberRequest {

    private Long userId;

    private Long roleId;

    // project id in path

}
