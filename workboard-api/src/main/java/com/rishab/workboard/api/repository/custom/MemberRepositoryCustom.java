package com.rishab.workboard.api.repository.custom;

import com.rishab.workboard.api.domain.Member;
import com.rishab.workboard.api.domain.Project;
import com.rishab.workboard.api.domain.Role;

import java.util.List;

public interface MemberRepositoryCustom {

    Role getMemberById(Long projectId, Long userId);

    boolean isUserInProject(Long projectId, Long userId);

    List<Project> findAllProjectsByUserId(Long userId);

    List<Member> findAllMembersByProjectId(Long projectId);

}
