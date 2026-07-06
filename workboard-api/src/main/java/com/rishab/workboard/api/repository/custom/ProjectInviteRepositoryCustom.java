package com.rishab.workboard.api.repository.custom;

import com.rishab.workboard.api.domain.ProjectInvite;

import java.util.List;

public interface ProjectInviteRepositoryCustom {

    public List<ProjectInvite> getInvites(Long currentUserId);

}
