package com.rishab.workboard.api.service.impl;

import com.rishab.workboard.api.domain.*;
import com.rishab.workboard.api.domain.enums.InviteStatus;
import com.rishab.workboard.api.dto.request.CreateProjectInviteRequest;
import com.rishab.workboard.api.dto.request.UpdateMemberRequest;
import com.rishab.workboard.api.dto.response.project.MemberDto;
import com.rishab.workboard.api.dto.response.project.ProjectInviteDto;
import com.rishab.workboard.api.mapper.MemberMapper;
import com.rishab.workboard.api.mapper.ProjectInviteMapper;
import com.rishab.workboard.api.service.MemberService;
import com.rishab.workboard.api.repository.*;
import com.rishab.workboard.api.service.exceptions.ForbiddenException;
import com.rishab.workboard.api.service.exceptions.InviteAlreadyPendingException;
import com.rishab.workboard.api.service.exceptions.NotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
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
        List<Member> members = memberRepository.findAllMembersByProjectId(projectId);

        return members.stream()
                .map(memberMapper::toDto)
                .toList();
    }

    /*
    this method will create an invite, or if there is an existing invite with the same recipient, project
    and is PENDING, will return a InviteAlreadyPendingException
     */
    @Override
    @Transactional
    public ProjectInviteDto createInvite(Long projectId, CreateProjectInviteRequest req, Long currentUserId) {
        requireProjectMember(projectId, currentUserId);

        Long recipientId = req.getRecipientUserId();

        // recipient must exist
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new NotFoundException("Recipient user not found"));

        // can't invite someone already in the project
        if (memberRepository.isUserInProject(projectId, recipientId)) {
            throw new ForbiddenException("User is already a member of this project");
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found"));
        User inviter = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("Invite sender not found"));

        ProjectInvite invite = new ProjectInvite();
        invite.setInvitedBy(inviter);
        invite.setProject(project);
        invite.setRecipient(recipient);
        // status is by defualt PENDING

        try {
            ProjectInvite saved = projectInviteRepository.save(invite);
            return projectInviteMapper.toDto(saved);
        } catch (DataIntegrityViolationException e) {
            // recipient + project + pending must be unique
            throw new InviteAlreadyPendingException();
        }
    }

    @Override
    @Transactional
    public void acceptInvite(Long inviteId, Long currentUserId) {
        ProjectInvite invite = requireInviteRecipient(inviteId, currentUserId);

        if (invite.getStatus() != InviteStatus.PENDING) {
            throw new ForbiddenException("Invite is not pending");
        }

        Project project = invite.getProject();
        User recipient = invite.getRecipient();

        // If already a member, just mark accepted
        if (!memberRepository.isUserInProject(project.getId(), recipient.getId())) {

            // TODO: improve logic for getting and creating default role
            Role memberRole = roleRepository.findByProjectIdAndName(project.getId(), "MEMBER")
                    .orElseGet(() -> {
                        Role role = new Role();
                        role.setProject(project);
                        role.setName("MEMBER");
                        role.setCreatedBy(project.getCreatedBy()); // satisfy not-null
                        return roleRepository.save(role);
                    });

            Member member = new Member(recipient, project);
            member.setRole(memberRole);

            memberRepository.save(member);
        }

        invite.setStatus(InviteStatus.ACCEPTED);
        projectInviteRepository.save(invite);
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
    private ProjectInvite requireInviteRecipient(Long inviteId, Long currentUserId) {
        ProjectInvite invite = projectInviteRepository.findById(inviteId)
                .orElseThrow(() -> new NotFoundException("Invite not found"));

        if (!invite.getRecipient().getId().equals(currentUserId)) {
            throw new ForbiddenException("You are not the recipient of this invite");
        }

        return invite;
    }

    private void requireProjectMember(Long projectId, Long currentUserId) {
        if (!memberRepository.isUserInProject(projectId, currentUserId)) {
            throw new ForbiddenException("You are not a member of this project");
        }
    }

}
