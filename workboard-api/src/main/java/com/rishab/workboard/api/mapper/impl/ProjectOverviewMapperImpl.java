package com.rishab.workboard.api.mapper.impl;

import com.rishab.workboard.api.domain.Project;
import com.rishab.workboard.api.dto.response.project.ProjectOverviewDto;
import com.rishab.workboard.api.mapper.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ProjectOverviewMapperImpl implements Mapper<Project, ProjectOverviewDto> {

    private final ModelMapper modelMapper;

    public ProjectOverviewMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public ProjectOverviewDto toDto(Project project) {
        return modelMapper.map(project, ProjectOverviewDto.class);
    }

}
