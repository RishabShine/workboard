package com.rishab.workboard.api.mapper.impl;

import com.rishab.workboard.api.domain.Milestone;
import com.rishab.workboard.api.domain.Project;
import com.rishab.workboard.api.domain.Role;
import com.rishab.workboard.api.domain.Tag;
import com.rishab.workboard.api.dto.response.project.ProjectDetailDto;
import com.rishab.workboard.api.mapper.ProjectDetailMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProjectDetailMapperImpl implements ProjectDetailMapper {

    private final RoleMapperImpl roleMapper;
    private final TagMapperImpl tagMapper;
    private final MilestoneMapperImpl milestoneMapper;

    public ProjectDetailMapperImpl(RoleMapperImpl roleMapper,
                                   TagMapperImpl tagMapper,
                                   MilestoneMapperImpl milestoneMapper) {
        this.roleMapper = roleMapper;
        this.tagMapper = tagMapper;
        this.milestoneMapper = milestoneMapper;
    }

    @Override
    public ProjectDetailDto toDto(Project project,
                                  List<Milestone> milestones,
                                  List<Tag> tags,
                                  Role userRole) {

        ProjectDetailDto projectDetails = new ProjectDetailDto();
        projectDetails.setId(project.getId());
        projectDetails.setName(project.getName());
        projectDetails.setUserRole(roleMapper.toDto(userRole));
        projectDetails.setTags(
                tags.stream()
                        .map(tagMapper::toDto)
                        .toList()
        );
        projectDetails.setMilestones(
                milestones.stream()
                        .map(milestoneMapper::toDto)
                        .toList()
        );

        return projectDetails;
    }
}
