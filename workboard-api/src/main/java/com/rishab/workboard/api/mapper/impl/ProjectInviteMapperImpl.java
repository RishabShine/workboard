package com.rishab.workboard.api.mapper.impl;

import com.rishab.workboard.api.domain.ProjectInvite;
import com.rishab.workboard.api.dto.response.project.ProjectInviteDto;
import com.rishab.workboard.api.mapper.ProjectInviteMapper;
import org.springframework.stereotype.Component;

@Component
public class ProjectInviteMapperImpl implements ProjectInviteMapper {

    private final UserMapperImpl userMapper;

    ProjectInviteMapperImpl(UserMapperImpl userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public ProjectInviteDto toDto(ProjectInvite invite) {

        ProjectInviteDto inviteDto = new ProjectInviteDto();

        inviteDto.setId(invite.getId());
        inviteDto.setProjectId(invite.getProject().getId());
        inviteDto.setProjectName(invite.getProject().getName());
        inviteDto.setRecipient(userMapper.toDto(invite.getRecipient()));
        inviteDto.setInvitedBy(userMapper.toDto(invite.getInvitedBy()));
        inviteDto.setStatus(invite.getStatus());
        inviteDto.setCreatedAt(invite.getCreatedAt());

        return inviteDto;

    }

}
