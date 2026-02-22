package com.rishab.workboard.api.service;

import com.rishab.workboard.api.domain.*;
import com.rishab.workboard.api.domain.enums.InviteStatus;
import com.rishab.workboard.api.domain.id.MemberId;
import com.rishab.workboard.api.dto.request.CreateProjectInviteRequest;
import com.rishab.workboard.api.dto.request.UpdateMemberRequest;
import com.rishab.workboard.api.dto.response.project.MemberDto;
import com.rishab.workboard.api.dto.response.project.ProjectInviteDto;
import com.rishab.workboard.api.repository.*;
import com.rishab.workboard.api.service.exceptions.ForbiddenException;
import com.rishab.workboard.api.service.exceptions.InviteAlreadyPendingException;
import com.rishab.workboard.api.service.exceptions.NotFoundException;
import com.rishab.workboard.api.service.impl.MemberServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberServiceTest {

    @Autowired private MemberServiceImpl memberService;

    @Autowired private UserRepository userRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private MemberRepository memberRepository;
    @Autowired private ProjectInviteRepository projectInviteRepository;

    // ---------------- listMembers ----------------

    @Test
    @Transactional
    void listMembers_returnsAllMembersForProject() {
        long ts = System.currentTimeMillis();

        User owner = createUser("owner_list_" + ts);
        Project project = createProject("P_list_" + ts, owner);

        Role ownerRole = createRole("OWNER", project, owner);
        addMember(owner, project, ownerRole);

        User member2 = createUser("member2_" + ts);
        Role memberRole = createRole("MEMBER", project, owner);
        addMember(member2, project, memberRole);

        // create another project + member that should NOT show up
        User otherOwner = createUser("otherOwner_" + ts);
        Project otherProject = createProject("P_other_" + ts, otherOwner);
        Role otherRole = createRole("OWNER", otherProject, otherOwner);
        addMember(otherOwner, otherProject, otherRole);

        // Act
        List<MemberDto> dtos = memberService.listMembers(project.getId(), owner.getId());

        // Assert
        assertThat(dtos).isNotNull();
        assertThat(dtos).extracting(dto -> dto.getUser().getId())
                .containsExactlyInAnyOrder(owner.getId(), member2.getId())
                .doesNotContain(otherOwner.getId());
    }

    @Test
    @Transactional
    void listMembers_whenNotProjectMember_throwsForbidden() {
        long ts = System.currentTimeMillis();

        User owner = createUser("owner_list_forb_" + ts);
        Project project = createProject("P_list_forb_" + ts, owner);

        Role ownerRole = createRole("OWNER", project, owner);
        addMember(owner, project, ownerRole);

        User outsider = createUser("outsider_list_" + ts);

        assertThatThrownBy(() -> memberService.listMembers(project.getId(), outsider.getId()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("not a member");
    }

    // ---------------- createInvite ----------------

    @Test
    @Transactional
    void createInvite_createsPendingInvite() {
        long ts = System.currentTimeMillis();

        User inviter = createUser("inviter_" + ts);
        Project project = createProject("P_invite_" + ts, inviter);

        Role ownerRole = createRole("OWNER", project, inviter);
        addMember(inviter, project, ownerRole);

        User recipient = createUser("recipient_" + ts);

        CreateProjectInviteRequest req = new CreateProjectInviteRequest();
        req.setRecipientUserId(recipient.getId());

        // Act
        ProjectInviteDto dto = memberService.createInvite(project.getId(), req, inviter.getId());

        // Assert (DTO)
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isNotNull();
        assertThat(dto.getProjectId()).isEqualTo(project.getId());
        assertThat(dto.getRecipient().getId()).isEqualTo(recipient.getId());
        assertThat(dto.getInvitedBy().getId()).isEqualTo(inviter.getId());
        assertThat(dto.getStatus()).isEqualTo(InviteStatus.PENDING);

        // Assert (DB)
        ProjectInvite saved = projectInviteRepository.findById(dto.getId())
                .orElseThrow(() -> new AssertionError("Expected invite to exist"));

        assertThat(saved.getProject().getId()).isEqualTo(project.getId());
        assertThat(saved.getRecipient().getId()).isEqualTo(recipient.getId());
        assertThat(saved.getInvitedBy().getId()).isEqualTo(inviter.getId());
        assertThat(saved.getStatus()).isEqualTo(InviteStatus.PENDING);
    }

    @Test
    @Transactional
    void createInvite_whenInviterNotProjectMember_throwsForbidden() {
        long ts = System.currentTimeMillis();

        User owner = createUser("owner_inv_forb_" + ts);
        Project project = createProject("P_inv_forb_" + ts, owner);

        Role ownerRole = createRole("OWNER", project, owner);
        addMember(owner, project, ownerRole);

        User outsider = createUser("outsider_inv_" + ts);
        User recipient = createUser("recipient_inv_" + ts);

        CreateProjectInviteRequest req = new CreateProjectInviteRequest();
        req.setRecipientUserId(recipient.getId());

        assertThatThrownBy(() -> memberService.createInvite(project.getId(), req, outsider.getId()))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @Transactional
    void createInvite_whenRecipientAlreadyMember_throwsForbidden() {
        long ts = System.currentTimeMillis();

        User inviter = createUser("inviter_already_" + ts);
        Project project = createProject("P_already_" + ts, inviter);
        Role ownerRole = createRole("OWNER", project, inviter);
        addMember(inviter, project, ownerRole);

        User recipient = createUser("recipient_already_" + ts);
        Role memberRole = createRole("MEMBER", project, inviter);
        addMember(recipient, project, memberRole);

        CreateProjectInviteRequest req = new CreateProjectInviteRequest();
        req.setRecipientUserId(recipient.getId());

        assertThatThrownBy(() -> memberService.createInvite(project.getId(), req, inviter.getId()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("already a member");
    }

    @Test
    @Transactional
    void createInvite_whenDuplicatePendingInvite_throwsInviteAlreadyPendingException() {
        long ts = System.currentTimeMillis();

        User inviter = createUser("inviter_dup_" + ts);
        Project project = createProject("P_dup_" + ts, inviter);
        Role ownerRole = createRole("OWNER", project, inviter);
        addMember(inviter, project, ownerRole);

        User recipient = createUser("recipient_dup_" + ts);

        // Seed a pending invite directly in DB
        ProjectInvite existing = new ProjectInvite();
        existing.setProject(project);
        existing.setRecipient(recipient);
        existing.setInvitedBy(inviter);
        existing.setStatus(InviteStatus.PENDING);
        projectInviteRepository.save(existing);

        CreateProjectInviteRequest req = new CreateProjectInviteRequest();
        req.setRecipientUserId(recipient.getId());

        assertThatThrownBy(() -> memberService.createInvite(project.getId(), req, inviter.getId()))
                .isInstanceOf(InviteAlreadyPendingException.class);
    }

    // ---------------- acceptInvite ----------------

    @Test
    @Transactional
    void acceptInvite_createsMembershipAndMarksAccepted() {
        long ts = System.currentTimeMillis();

        User owner = createUser("owner_acc_" + ts);
        Project project = createProject("P_acc_" + ts, owner);
        Role ownerRole = createRole("OWNER", project, owner);
        addMember(owner, project, ownerRole);

        User recipient = createUser("recipient_acc_" + ts);

        ProjectInvite invite = new ProjectInvite();
        invite.setProject(project);
        invite.setRecipient(recipient);
        invite.setInvitedBy(owner);
        invite.setStatus(InviteStatus.PENDING);
        invite = projectInviteRepository.save(invite);

        // Ensure default MEMBER role does NOT exist so acceptInvite must create it
        // (If your DB already has it from other tests, we can still assert membership creation.)

        // Act
        memberService.acceptInvite(invite.getId(), recipient.getId());

        // Assert invite status updated
        ProjectInvite savedInvite = projectInviteRepository.findById(invite.getId())
                .orElseThrow(() -> new AssertionError("Expected invite to exist"));

        assertThat(savedInvite.getStatus()).isEqualTo(InviteStatus.ACCEPTED);

        // Assert membership exists
        Member member = memberRepository.findById(new MemberId(recipient.getId(), project.getId()))
                .orElseThrow(() -> new AssertionError("Expected membership to exist"));

        assertThat(member.getRole()).isNotNull();
        assertThat(member.getRole().getName()).isEqualTo("MEMBER");
    }

    @Test
    @Transactional
    void acceptInvite_whenNotRecipient_throwsForbidden() {
        long ts = System.currentTimeMillis();

        User owner = createUser("owner_acc_forb_" + ts);
        Project project = createProject("P_acc_forb_" + ts, owner);
        Role ownerRole = createRole("OWNER", project, owner);
        addMember(owner, project, ownerRole);

        User recipient = createUser("recipient_acc_forb_" + ts);
        User attacker = createUser("attacker_acc_forb_" + ts);

        ProjectInvite invite = new ProjectInvite();
        invite.setProject(project);
        invite.setRecipient(recipient);
        invite.setInvitedBy(owner);
        invite.setStatus(InviteStatus.PENDING);
        ProjectInvite savedInvite = projectInviteRepository.save(invite);

        assertThatThrownBy(() -> memberService.acceptInvite(savedInvite.getId(), attacker.getId()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("not the recipient");
    }

    @Test
    @Transactional
    void acceptInvite_whenNotPending_throwsForbidden() {
        long ts = System.currentTimeMillis();

        User owner = createUser("owner_acc_np_" + ts);
        Project project = createProject("P_acc_np_" + ts, owner);
        Role ownerRole = createRole("OWNER", project, owner);
        addMember(owner, project, ownerRole);

        User recipient = createUser("recipient_acc_np_" + ts);

        ProjectInvite invite = new ProjectInvite();
        invite.setProject(project);
        invite.setRecipient(recipient);
        invite.setInvitedBy(owner);
        invite.setStatus(InviteStatus.ACCEPTED);
        ProjectInvite savedInvite = projectInviteRepository.save(invite);

        assertThatThrownBy(() -> memberService.acceptInvite(savedInvite.getId(), recipient.getId()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("not pending");
    }

    @Test
    @Transactional
    void acceptInvite_whenInviteMissing_throwsNotFound() {
        long ts = System.currentTimeMillis();

        User user = createUser("missing_inv_" + ts);

        assertThatThrownBy(() -> memberService.acceptInvite(999999999L, user.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Invite not found");
    }

    // ---------------- rejectInvite ----------------

    @Test
    @Transactional
    void rejectInvite_marksDeclined() {
        long ts = System.currentTimeMillis();

        User owner = createUser("owner_rej_" + ts);
        Project project = createProject("P_rej_" + ts, owner);
        Role ownerRole = createRole("OWNER", project, owner);
        addMember(owner, project, ownerRole);

        User recipient = createUser("recipient_rej_" + ts);

        ProjectInvite invite = new ProjectInvite();
        invite.setProject(project);
        invite.setRecipient(recipient);
        invite.setInvitedBy(owner);
        invite.setStatus(InviteStatus.PENDING);
        invite = projectInviteRepository.save(invite);

        memberService.rejectInvite(invite.getId(), recipient.getId());

        ProjectInvite saved = projectInviteRepository.findById(invite.getId())
                .orElseThrow(() -> new AssertionError("Expected invite to exist"));

        assertThat(saved.getStatus()).isEqualTo(InviteStatus.DECLINED);
    }

    @Test
    @Transactional
    void rejectInvite_whenNotRecipient_throwsForbidden() {
        long ts = System.currentTimeMillis();

        User owner = createUser("owner_rej_forb_" + ts);
        Project project = createProject("P_rej_forb_" + ts, owner);
        Role ownerRole = createRole("OWNER", project, owner);
        addMember(owner, project, ownerRole);

        User recipient = createUser("recipient_rej_forb_" + ts);
        User attacker = createUser("attacker_rej_forb_" + ts);

        ProjectInvite invite = new ProjectInvite();
        invite.setProject(project);
        invite.setRecipient(recipient);
        invite.setInvitedBy(owner);
        invite.setStatus(InviteStatus.PENDING);
        ProjectInvite savedInvite = projectInviteRepository.save(invite);

        assertThatThrownBy(() -> memberService.rejectInvite(savedInvite.getId(), attacker.getId()))
                .isInstanceOf(ForbiddenException.class);
    }

    // ---------------- updateMemberRole ----------------

    @Test
    @Transactional
    void updateMemberRole_updatesRole() {
        long ts = System.currentTimeMillis();

        User owner = createUser("owner_upd_" + ts);
        Project project = createProject("P_upd_" + ts, owner);
        Role ownerRole = createRole("OWNER", project, owner);
        addMember(owner, project, ownerRole);

        User member = createUser("member_upd_" + ts);
        Role oldRole = createRole("MEMBER", project, owner);
        addMember(member, project, oldRole);

        Role newRole = createRole("QA", project, owner);

        UpdateMemberRequest req = new UpdateMemberRequest();
        req.setUserId(member.getId());
        req.setRoleId(newRole.getId());

        MemberDto dto = memberService.updateMemberRole(project.getId(), req, owner.getId());

        assertThat(dto).isNotNull();
        assertThat(dto.getUser().getId()).isEqualTo(member.getId());
        assertThat(dto.getRole().getId()).isEqualTo(newRole.getId());

        Member saved = memberRepository.findById(new MemberId(member.getId(), project.getId()))
                .orElseThrow(() -> new AssertionError("Expected membership to exist"));

        assertThat(saved.getRole().getId()).isEqualTo(newRole.getId());
    }

    @Test
    @Transactional
    void updateMemberRole_whenNotProjectMember_throwsForbidden() {
        long ts = System.currentTimeMillis();

        User owner = createUser("owner_upd_forb_" + ts);
        Project project = createProject("P_upd_forb_" + ts, owner);
        Role ownerRole = createRole("OWNER", project, owner);
        addMember(owner, project, ownerRole);

        User outsider = createUser("outsider_upd_" + ts);

        UpdateMemberRequest req = new UpdateMemberRequest();
        req.setUserId(owner.getId());
        req.setRoleId(ownerRole.getId());

        assertThatThrownBy(() -> memberService.updateMemberRole(project.getId(), req, outsider.getId()))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @Transactional
    void updateMemberRole_whenRoleFromAnotherProject_throwsForbidden() {
        long ts = System.currentTimeMillis();

        User owner = createUser("owner_cross_" + ts);
        Project project = createProject("P_cross_" + ts, owner);
        Role ownerRole = createRole("OWNER", project, owner);
        addMember(owner, project, ownerRole);

        User member = createUser("member_cross_" + ts);
        Role memberRole = createRole("MEMBER", project, owner);
        addMember(member, project, memberRole);

        // create role on a different project
        User otherOwner = createUser("otherOwner_cross_" + ts);
        Project otherProject = createProject("P_other_cross_" + ts, otherOwner);
        Role otherRole = createRole("OTHER_ROLE", otherProject, otherOwner);

        UpdateMemberRequest req = new UpdateMemberRequest();
        req.setUserId(member.getId());
        req.setRoleId(otherRole.getId());

        assertThatThrownBy(() -> memberService.updateMemberRole(project.getId(), req, owner.getId()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("Role does not belong");
    }

    // ---------------- helpers ----------------

    private User createUser(String username) {
        long ts = System.currentTimeMillis();
        User u = new User();
        u.setUsername(username);
        u.setEmail((username + "_" + ts + "@example.com").toUpperCase());
        u.setPassword("hash");
        return userRepository.save(u);
    }

    private Project createProject(String name, User createdBy) {
        Project p = new Project();
        p.setName(name);
        p.setCreatedBy(createdBy);
        return projectRepository.save(p);
    }

    private Role createRole(String name, Project project, User createdBy) {
        Role r = new Role();
        r.setName(name);
        r.setProject(project);
        r.setCreatedBy(createdBy);
        return roleRepository.save(r);
    }

    private void addMember(User user, Project project, Role role) {
        Member m = new Member(user, project);
        m.setRole(role);
        memberRepository.save(m);
    }
}