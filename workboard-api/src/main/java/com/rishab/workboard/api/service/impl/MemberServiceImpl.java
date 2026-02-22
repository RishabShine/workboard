package com.rishab.workboard.api.service.impl;

import com.rishab.workboard.api.domain.Member;
import com.rishab.workboard.api.dto.request.CreateProjectInviteRequest;
import com.rishab.workboard.api.dto.request.UpdateMemberRequest;
import com.rishab.workboard.api.dto.response.project.MemberDto;
import com.rishab.workboard.api.dto.response.project.ProjectInviteDto;
import com.rishab.workboard.api.mapper.MemberMapper;
import com.rishab.workboard.api.mapper.ProjectInviteMapper;
import com.rishab.workboard.api.service.MemberService;
import com.rishab.workboard.api.repository.*;
import com.rishab.workboard.api.service.exceptions.ForbiddenException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProjectInviteRepository projectInviteRepository;

    private final MemberMapper memberMapper;
    private final ProjectInviteMapper projectInviteMapper;

    MemberServiceImpl(MemberRepository memberRepository,
                      ProjectRepository projectRepository,
                      UserRepository userRepository,
                      RoleRepository roleRepository,
                      ProjectInviteRepository projectInviteRepository,
                      MemberMapper memberMapper,
                      ProjectInviteMapper projectInviteMapper) {
        this.memberRepository = memberRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.projectInviteRepository = projectInviteRepository;
        this.memberMapper = memberMapper;
        this.projectInviteMapper = projectInviteMapper;
    }

    @Override
    public List<MemberDto> listMembers(Long projectId, Long currentUserId) {
        requireProjectMember(projectId, currentUserId);

        // Assumes you have a scoped repo method. If not, add it:
        // List<Member> findByProjectId(Long projectId);
        List<Member> members = memberRepository.findMemberByProject(projectId);

        return members.stream()
                .map(memberMapper::toDto)
                .toList();
    }

    @Override
    public ProjectInviteDto createInvite(Long projectId, CreateProjectInviteRequest req, Long currentUserId) {
        return null;
    }

    @Override
    @Transactional
    public void acceptInvite(Long inviteId, Long currentUserId) {
    }

    @Override
    @Transactional
    public void rejectInvite(Long inviteId, Long currentUserId) {
    }

    @Override
    @Transactional
    public MemberDto updateMemberRole(Long projectId, UpdateMemberRequest req, Long currentUserId) {
        return null;
    }

    /*
    checks if the current user accepting / rejecting an invite is the intended recipient,
    if not throws an error
     */
    private void requireInviteRecipient(Long inviteId, Long currentUserId) {

    }

    private void requireProjectMember(Long projectId, Long currentUserId) {
        if (!memberRepository.isUserInProject(projectId, currentUserId)) {
            throw new ForbiddenException("You are not a member of this project");
        }
    }

}
