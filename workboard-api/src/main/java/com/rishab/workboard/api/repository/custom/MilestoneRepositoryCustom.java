package com.rishab.workboard.api.repository.custom;

import com.rishab.workboard.api.domain.Milestone;

import java.util.List;

public interface MilestoneRepositoryCustom {

    List<Milestone> getMilestonesByProjectId(Long projectId);

}
