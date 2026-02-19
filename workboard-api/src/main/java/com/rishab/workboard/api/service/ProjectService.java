package com.rishab.workboard.api.service;

import com.rishab.workboard.api.dto.request.CreateProjectRequest;
import com.rishab.workboard.api.dto.request.UpdateProjectRequest;
import com.rishab.workboard.api.dto.response.project.ProjectDetailDto;
import com.rishab.workboard.api.dto.response.project.ProjectListDto;
import com.rishab.workboard.api.dto.response.project.ProjectOverviewDto;

public interface ProjectService {

    ProjectDetailDto getProjectDetail(Long projectId, Long currentUserId);

    ProjectListDto listProjectsForUser(Long currentUserId);

    ProjectOverviewDto createProject(CreateProjectRequest req, Long currentUserId);

    ProjectOverviewDto updateProject(Long projectId, UpdateProjectRequest req, Long currentUserId);

}
