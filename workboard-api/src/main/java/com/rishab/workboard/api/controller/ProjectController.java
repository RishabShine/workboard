package com.rishab.workboard.api.controller;

import com.rishab.workboard.api.dto.request.CreateProjectRequest;
import com.rishab.workboard.api.dto.response.project.ProjectDetailDto;
import com.rishab.workboard.api.dto.response.project.ProjectListDto;
import com.rishab.workboard.api.dto.response.project.ProjectOverviewDto;
import com.rishab.workboard.api.security.auth.AuthUser;
import com.rishab.workboard.api.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDetailDto> getProject(
            @PathVariable Long projectId,
            @AuthenticationPrincipal AuthUser user) {
        ProjectDetailDto result = projectService.getProjectDetail(projectId, user.userId());
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<ProjectListDto> getUsersProjects(
            @AuthenticationPrincipal AuthUser user) {
        ProjectListDto result = projectService.listProjectsForUser(user.userId());
        return ResponseEntity.ok(result);
    }

    /*
    can change this to return a ProjectDetailDto,
    and when creating a new project, redirect to the details page for that project
     */
    @PostMapping
    public ResponseEntity<ProjectOverviewDto> createProject(
            @RequestBody CreateProjectRequest request,
            @AuthenticationPrincipal AuthUser user) {
        ProjectOverviewDto result = projectService.createProject(request, user.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

}
