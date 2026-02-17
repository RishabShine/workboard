package com.rishab.workboard.api.mapper;

import com.rishab.workboard.api.domain.Project;
import com.rishab.workboard.api.dto.response.project.ProjectListDto;

import java.util.List;

public interface ProjectListMapper {

    ProjectListDto toDto(List<Project> projects);

}
