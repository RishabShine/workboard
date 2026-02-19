package com.rishab.workboard.api.mapper;

import com.rishab.workboard.api.domain.ProjectInvite;
import com.rishab.workboard.api.dto.response.project.ProjectInviteDto;

public interface ProjectInviteMapper {

    ProjectInviteDto toDto(ProjectInvite invite);

}