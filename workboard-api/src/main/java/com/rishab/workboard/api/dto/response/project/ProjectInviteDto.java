package com.rishab.workboard.api.dto.response.project;

import com.rishab.workboard.api.domain.enums.InviteStatus;
import com.rishab.workboard.api.dto.response.common.UserSummaryDto;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class ProjectInviteDto {

    private Long id;

    private Long projectId;

    private String projectName;

    private UserSummaryDto recipient;

    private UserSummaryDto invitedBy;

    private InviteStatus status;

    private OffsetDateTime createdAt;

}