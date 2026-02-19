package com.rishab.workboard.api.repository;

import com.rishab.workboard.api.domain.Milestone;
import com.rishab.workboard.api.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MilestoneRepository extends JpaRepository<Milestone, Long> {
    List<Milestone> getMilestonesByProject(Project project);
}
