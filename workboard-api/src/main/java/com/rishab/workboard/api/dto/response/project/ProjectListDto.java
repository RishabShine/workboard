package com.rishab.workboard.api.dto.response.project;

import com.rishab.workboard.api.domain.Project;
import lombok.Getter;

import java.util.List;

@Getter
public class ProjectListDto {

    private List<ProjectOverviewDto> projects;

    public ProjectListDto(List<ProjectOverviewDto> projects) {
        this.projects = projects;
    }

    public void addProject(ProjectOverviewDto project) {

    }

}