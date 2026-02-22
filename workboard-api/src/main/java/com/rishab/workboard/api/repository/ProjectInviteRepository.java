package com.rishab.workboard.api.repository;

import com.rishab.workboard.api.domain.ProjectInvite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectInviteRepository extends JpaRepository<ProjectInvite, Long>  {
}
