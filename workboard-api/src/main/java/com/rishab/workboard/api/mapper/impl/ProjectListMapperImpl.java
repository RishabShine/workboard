package com.rishab.workboard.api.mapper.impl;

import com.rishab.workboard.api.domain.Project;
import com.rishab.workboard.api.dto.response.project.ProjectListDto;
import com.rishab.workboard.api.dto.response.project.ProjectOverviewDto;
import com.rishab.workboard.api.mapper.ProjectListMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProjectListMapperImpl implements ProjectListMapper {

    private final ProjectOverviewMapperImpl projOverviewMapper;

    public ProjectListMapperImpl(ProjectOverviewMapperImpl projOverviewMapper) {
        this.projOverviewMapper = projOverviewMapper;
    }

    //@Override
    public ProjectListDto toDto(List<Project> projects) {

        List<ProjectOverviewDto> projectOverviews = projects.stream()
                .map(projOverviewMapper::toDto)
                .toList();

        return new ProjectListDto(projectOverviews);
    }
}
