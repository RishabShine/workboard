package com.rishab.workboard.api.controller;

import com.rishab.workboard.api.dto.request.CreateMilestoneRequest;
import com.rishab.workboard.api.dto.request.UpdateMilestoneRequest;
import com.rishab.workboard.api.dto.response.common.MilestoneDto;
import com.rishab.workboard.api.security.auth.AuthUser;
import com.rishab.workboard.api.service.MilestoneService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/milestones")
public class MilestoneController {

    private final MilestoneService milestoneService;

    public MilestoneController(MilestoneService milestoneService) {
        this.milestoneService = milestoneService;
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<List<MilestoneDto>> getMilestonesByProject(
            @PathVariable Long projectId,
            @AuthenticationPrincipal AuthUser user
    ) {
        List<MilestoneDto> result =  milestoneService.getMilestonesByProject(projectId, user.userId());
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<MilestoneDto> createMilestone(
            @RequestBody CreateMilestoneRequest request,
            @AuthenticationPrincipal AuthUser user
    ) {
        MilestoneDto result = milestoneService.createMilestone(
                request.getProjectId(), request.getName(), user.userId()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PatchMapping("/update")
    public ResponseEntity<MilestoneDto> updateMilestone(
            @RequestBody UpdateMilestoneRequest request,
            @AuthenticationPrincipal AuthUser user
    ) {
        MilestoneDto result = milestoneService.updateMilestone(
                request.getId(),
                request.getProjectId(),
                request.getName(),
                user.userId()
        );

        return ResponseEntity.ok(result);
    }

}
