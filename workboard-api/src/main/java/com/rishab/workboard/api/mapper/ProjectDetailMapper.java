package com.rishab.workboard.api.mapper;

import com.rishab.workboard.api.domain.Milestone;
import com.rishab.workboard.api.domain.Project;
import com.rishab.workboard.api.domain.Role;
import com.rishab.workboard.api.domain.Tag;
import com.rishab.workboard.api.dto.response.project.ProjectDetailDto;

import java.util.List;

public interface ProjectDetailMapper {

    ProjectDetailDto toDto(Project project, List<Milestone> milestones, List<Tag> tags, Role userRole);

}
