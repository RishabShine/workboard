package com.rishab.workboard.api.service;

import com.rishab.workboard.api.domain.*;
import com.rishab.workboard.api.domain.enums.TicketStatus;
import com.rishab.workboard.api.dto.request.CreateCommentRequest;
import com.rishab.workboard.api.dto.response.comment.CommentDto;
import com.rishab.workboard.api.repository.*;
import com.rishab.workboard.api.service.exceptions.ForbiddenException;
import com.rishab.workboard.api.service.exceptions.NotFoundException;
import com.rishab.workboard.api.service.impl.CommentServiceImpl;
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
class CommentServiceTest {

    @Autowired private CommentServiceImpl commentService;

    @Autowired private UserRepository userRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private MemberRepository memberRepository;

    @Autowired private TicketRepository ticketRepository;
    @Autowired private CommentRepository commentRepository;

    // ---------------- listComments ----------------

    @Test
    @Transactional
    void listComments_returnsCommentsForTicket_only() {
        long ts = System.currentTimeMillis();

        User owner = createUser("owner_listc_" + ts);
        Project project = createProject("P_listc_" + ts, owner);

        Role ownerRole = createRole("OWNER", project, owner);
        addMember(owner, project, ownerRole);

        Ticket ticket = createTicket(project, owner, "T_listc_" + ts);

        // Comments for this ticket
        Comment c1 = new Comment();
        c1.setTicket(ticket);
        c1.setUser(owner);
        c1.setBody("C1");
        c1 = commentRepository.save(c1);

        Comment c2 = new Comment();
        c2.setTicket(ticket);
        c2.setUser(owner);
        c2.setBody("C2");
        c2 = commentRepository.save(c2);

        // Another project's ticket + comment that should NOT appear
        User otherOwner = createUser("other_owner_listc_" + ts);
        Project otherProject = createProject("P_other_listc_" + ts, otherOwner);
        Role otherRole = createRole("OWNER", otherProject, otherOwner);
        addMember(otherOwner, otherProject, otherRole);

        Ticket otherTicket = createTicket(otherProject, otherOwner, "T_other_" + ts);

        Comment otherComment = new Comment();
        otherComment.setTicket(otherTicket);
        otherComment.setUser(otherOwner);
        otherComment.setBody("SHOULD_NOT_APPEAR");
        commentRepository.save(otherComment);

        // Act
        List<CommentDto> dtos = commentService.listComments(ticket.getId(), owner.getId());

        // Assert
        assertThat(dtos).isNotNull();
        assertThat(dtos).extracting(CommentDto::getId)
                .contains(c1.getId(), c2.getId())
                .doesNotContain(otherComment.getId());
    }

    @Test
    @Transactional
    void listComments_whenNotMember_throwsForbidden() {
        long ts = System.currentTimeMillis();

        User owner = createUser("owner_listc_forb_" + ts);
        Project project = createProject("P_listc_forb_" + ts, owner);
        Role ownerRole = createRole("OWNER", project, owner);
        addMember(owner, project, ownerRole);

        Ticket ticket = createTicket(project, owner, "T_forb_" + ts);

        User outsider = createUser("outsider_listc_" + ts);

        assertThatThrownBy(() -> commentService.listComments(ticket.getId(), outsider.getId()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("not a member");
    }

    @Test
    @Transactional
    void listComments_whenTicketMissing_throwsNotFound() {
        long ts = System.currentTimeMillis();
        User caller = createUser("caller_missing_ticket_" + ts);

        assertThatThrownBy(() -> commentService.listComments(999999999L, caller.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Ticket not found");
    }

    // ---------------- addComment ----------------

    @Test
    @Transactional
    void addComment_createsComment_andReturnsDto() {
        long ts = System.currentTimeMillis();

        User owner = createUser("owner_addc_" + ts);
        Project project = createProject("P_addc_" + ts, owner);
        Role ownerRole = createRole("OWNER", project, owner);
        addMember(owner, project, ownerRole);

        Ticket ticket = createTicket(project, owner, "T_addc_" + ts);

        CreateCommentRequest req = new CreateCommentRequest();
        req.setBody("Hello world");

        // Act
        CommentDto dto = commentService.addComment(ticket.getId(), req, owner.getId());

        // Assert DTO
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isNotNull();
        assertThat(dto.getBody()).isEqualTo("Hello world");
        assertThat(dto.getUser()).isNotNull();
        assertThat(dto.getUser().getId()).isEqualTo(owner.getId());
        assertThat(dto.getReplyTo()).isNull();

        // Assert DB state
        Comment saved = commentRepository.findById(dto.getId())
                .orElseThrow(() -> new AssertionError("Expected comment to exist"));

        assertThat(saved.getTicket().getId()).isEqualTo(ticket.getId());
        assertThat(saved.getUser().getId()).isEqualTo(owner.getId());
        assertThat(saved.getBody()).isEqualTo("Hello world");
        assertThat(saved.getReplyTo()).isNull();
    }

    @Test
    @Transactional
    void addComment_trimsBody() {
        long ts = System.currentTimeMillis();

        User owner = createUser("owner_trim_" + ts);
        Project project = createProject("P_trim_" + ts, owner);
        Role ownerRole = createRole("OWNER", project, owner);
        addMember(owner, project, ownerRole);

        Ticket ticket = createTicket(project, owner, "T_trim_" + ts);

        CreateCommentRequest req = new CreateCommentRequest();
        req.setBody("   hi   ");

        CommentDto dto = commentService.addComment(ticket.getId(), req, owner.getId());

        assertThat(dto.getBody()).isEqualTo("hi");

        Comment saved = commentRepository.findById(dto.getId())
                .orElseThrow(() -> new AssertionError("Expected comment to exist"));
        assertThat(saved.getBody()).isEqualTo("hi");
    }

    @Test
    @Transactional
    void addComment_whenBodyBlank_throwsForbidden() {
        long ts = System.currentTimeMillis();

        User owner = createUser("owner_blank_" + ts);
        Project project = createProject("P_blank_" + ts, owner);
        Role ownerRole = createRole("OWNER", project, owner);
        addMember(owner, project, ownerRole);

        Ticket ticket = createTicket(project, owner, "T_blank_" + ts);

        CreateCommentRequest req = new CreateCommentRequest();
        req.setBody("   ");

        assertThatThrownBy(() -> commentService.addComment(ticket.getId(), req, owner.getId()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("body is required");
    }

    @Test
    @Transactional
    void addComment_whenNotMember_throwsForbidden() {
        long ts = System.currentTimeMillis();

        User owner = createUser("owner_add_forb_" + ts);
        Project project = createProject("P_add_forb_" + ts, owner);
        Role ownerRole = createRole("OWNER", project, owner);
        addMember(owner, project, ownerRole);

        Ticket ticket = createTicket(project, owner, "T_add_forb_" + ts);

        User outsider = createUser("outsider_add_" + ts);

        CreateCommentRequest req = new CreateCommentRequest();
        req.setBody("X");

        assertThatThrownBy(() -> commentService.addComment(ticket.getId(), req, outsider.getId()))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @Transactional
    void addComment_whenReplyToMissing_throwsNotFound() {
        long ts = System.currentTimeMillis();

        User owner = createUser("owner_reply_missing_" + ts);
        Project project = createProject("P_reply_missing_" + ts, owner);
        Role ownerRole = createRole("OWNER", project, owner);
        addMember(owner, project, ownerRole);

        Ticket ticket = createTicket(project, owner, "T_reply_missing_" + ts);

        CreateCommentRequest req = new CreateCommentRequest();
        req.setBody("child");
        req.setReplyTo(999999999L);

        assertThatThrownBy(() -> commentService.addComment(ticket.getId(), req, owner.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Reply-to comment not found");
    }

    @Test
    @Transactional
    void addComment_whenReplyToIsForAnotherTicket_throwsForbidden() {
        long ts = System.currentTimeMillis();

        User owner = createUser("owner_reply_wrong_" + ts);

        Project p1 = createProject("P1_reply_wrong_" + ts, owner);
        Role r1 = createRole("OWNER", p1, owner);
        addMember(owner, p1, r1);
        Ticket t1 = createTicket(p1, owner, "T1_reply_wrong_" + ts);

        Project p2 = createProject("P2_reply_wrong_" + ts, owner);
        Role r2 = createRole("OWNER", p2, owner);
        addMember(owner, p2, r2);
        Ticket t2 = createTicket(p2, owner, "T2_reply_wrong_" + ts);

        Comment parentOnOtherTicket = new Comment();
        parentOnOtherTicket.setTicket(t2);
        parentOnOtherTicket.setUser(owner);
        parentOnOtherTicket.setBody("parent");
        parentOnOtherTicket = commentRepository.save(parentOnOtherTicket);

        CreateCommentRequest req = new CreateCommentRequest();
        req.setBody("child");
        req.setReplyTo(parentOnOtherTicket.getId());

        assertThatThrownBy(() -> commentService.addComment(t1.getId(), req, owner.getId()))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("does not belong to this ticket");
    }

    @Test
    @Transactional
    void addComment_withReplyTo_setsReplyTo() {
        long ts = System.currentTimeMillis();

        User owner = createUser("owner_reply_ok_" + ts);
        Project project = createProject("P_reply_ok_" + ts, owner);
        Role ownerRole = createRole("OWNER", project, owner);
        addMember(owner, project, ownerRole);

        Ticket ticket = createTicket(project, owner, "T_reply_ok_" + ts);

        Comment parent = new Comment();
        parent.setTicket(ticket);
        parent.setUser(owner);
        parent.setBody("parent");
        parent = commentRepository.save(parent);

        CreateCommentRequest req = new CreateCommentRequest();
        req.setBody("child");
        req.setReplyTo(parent.getId());

        CommentDto dto = commentService.addComment(ticket.getId(), req, owner.getId());

        assertThat(dto.getReplyTo()).isEqualTo(parent.getId());

        Comment saved = commentRepository.findById(dto.getId())
                .orElseThrow(() -> new AssertionError("Expected comment to exist"));

        assertThat(saved.getReplyTo()).isNotNull();
        assertThat(saved.getReplyTo().getId()).isEqualTo(parent.getId());
        assertThat(saved.getTicket().getId()).isEqualTo(ticket.getId());
    }

    @Test
    @Transactional
    void addComment_whenTicketMissing_throwsNotFound() {
        long ts = System.currentTimeMillis();
        User caller = createUser("caller_add_missing_ticket_" + ts);

        CreateCommentRequest req = new CreateCommentRequest();
        req.setBody("hi");

        assertThatThrownBy(() -> commentService.addComment(999999999L, req, caller.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Ticket not found");
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

    private Ticket createTicket(Project project, User createdBy, String title) {
        Ticket t = new Ticket();
        t.setProject(project);
        t.setTitle(title);
        t.setCreatedBy(createdBy);
        t.setStatus(TicketStatus.BACKLOG);
        return ticketRepository.save(t);
    }
}