package com.rishab.workboard.api.service;

import com.rishab.workboard.api.dto.request.CreateProjectInviteRequest;
import com.rishab.workboard.api.dto.request.UpdateMemberRequest;
import com.rishab.workboard.api.dto.response.project.MemberDto;
import com.rishab.workboard.api.dto.response.project.ProjectInviteDto;

import java.util.List;

public interface MemberService {

    List<MemberDto> listMembers(Long projectId, Long currentUserId);

    ProjectInviteDto createInvite(Long projectId, CreateProjectInviteRequest req, Long currentUserId);

    void acceptInvite(Long inviteId, Long currentUserId);

    void rejectInvite(Long inviteId, Long currentUserId);

    MemberDto updateMemberRole(Long projectId, UpdateMemberRequest req, Long currentUserId);

}
