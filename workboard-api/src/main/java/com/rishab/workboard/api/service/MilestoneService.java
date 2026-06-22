package com.rishab.workboard.api.service;

import com.rishab.workboard.api.dto.response.common.MilestoneDto;

import java.util.List;

public interface MilestoneService {

    List<MilestoneDto> getMilestonesByProject(Long projectId, Long userId);

    MilestoneDto createMilestone(Long projectId, String name, Long userId);

    MilestoneDto updateMilestone(Long milestoneId, Long projectId, String name, Long userId);

}
