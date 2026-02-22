package com.rishab.workboard.api.service;

import com.rishab.workboard.api.domain.*;
import com.rishab.workboard.api.domain.enums.TicketStatus;
import com.rishab.workboard.api.domain.id.MemberId;
import com.rishab.workboard.api.dto.request.CreateTicketRequest;
import com.rishab.workboard.api.dto.request.UpdateTicketRequest;
import com.rishab.workboard.api.dto.response.ticket.TicketDetailDto;
import com.rishab.workboard.api.dto.response.ticket.TicketListItemDto;
import com.rishab.workboard.api.repository.*;
import com.rishab.workboard.api.service.exceptions.ForbiddenException;
import com.rishab.workboard.api.service.exceptions.NotFoundException;
import com.rishab.workboard.api.service.impl.TicketServiceImpl;
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
class TicketServiceTest {

    @Autowired private TicketServiceImpl ticketService;

    @Autowired private UserRepository userRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private MemberRepository memberRepository;

    @Autowired private MilestoneRepository milestoneRepository;
    @Autowired private TagRepository tagRepository;

    @Autowired private TicketRepository ticketRepository;
    @Autowired private TicketTagRepository ticketTagRepository;

    @Test
    @Transactional
    void createTicket_setsAllFields_andCreatesTicketTags() {
        // Arrange
        long ts = System.currentTimeMillis();

        User owner = createUser("owner_" + ts);
        Project project = createProject("P_" + ts, owner);

        Role ownerRole = createRole("OWNER", project, owner);
        addMember(owner, project, ownerRole);

        User assignee = createUser("assignee_" + ts);
        Role memberRole = createRole("MEMBER", project, owner);
        addMember(assignee, project, memberRole);

        Milestone milestone = new Milestone();
        milestone.setName("M1");
        milestone.setProject(project);
        milestone = milestoneRepository.save(milestone);

        Tag tag1 = new Tag();
        tag1.setName("BUG");
        tag1.setColor("red");
        tag1.setProject(project);
        tag1 = tagRepository.save(tag1);

        Tag tag2 = new Tag();
        tag2.setName("UI");
        tag2.setColor("blue");
        tag2.setProject(project);
        tag2 = tagRepository.save(tag2);

        CreateTicketRequest req = new CreateTicketRequest();
        req.setTitle("T1");
        req.setBody("Body");
        req.setStatus(TicketStatus.BACKLOG);
        req.setAssignedToUserId(assignee.getId());
        req.setMilestoneId(milestone.getId());
        req.setTagIds(List.of(tag1.getId(), tag2.getId()));

        // Act
        TicketDetailDto dto = ticketService.createTicket(project.getId(), req, owner.getId());

        // Assert (DTO)
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isNotNull();
        assertThat(dto.getTitle()).isEqualTo("T1");
        assertThat(dto.getBody()).isEqualTo("Body");
        assertThat(dto.getStatus()).isEqualTo(TicketStatus.BACKLOG);

        assertThat(dto.getAssignee()).isNotNull();
        assertThat(dto.getAssignee().getId()).isEqualTo(assignee.getId());

        assertThat(dto.getMilestone()).isNotNull();
        assertThat(dto.getMilestone().getId()).isEqualTo(milestone.getId());

        assertThat(dto.getTags()).hasSize(2);
        assertThat(dto.getTags()).extracting("id")
                .containsExactlyInAnyOrder(tag1.getId(), tag2.getId());

        // Assert (DB state)
        Ticket saved = ticketRepository.findById(dto.getId())
                .orElseThrow(() -> new AssertionError("Expected ticket to exist"));

        assertThat(saved.getProject().getId()).isEqualTo(project.getId());
        assertThat(saved.getCreatedBy().getId()).isEqualTo(owner.getId());
        assertThat(saved.getAssignedTo().getId()).isEqualTo(assignee.getId());
        assertThat(saved.getMilestone().getId()).isEqualTo(milestone.getId());

        List<Tag> persistedTags = tagRepository.findTagsByTicketId(saved.getId());
        assertThat(persistedTags).extracting(Tag::getId)
                .containsExactlyInAnyOrder(tag1.getId(), tag2.getId());
    }

    @Test
    @Transactional
    void updateTicket_updatesFields_andReplacesTags() {
        // Arrange
        long ts = System.currentTimeMillis();

        User owner = createUser("owner2_" + ts);
        Project project = createProject("P2_" + ts, owner);

        Role ownerRole = createRole("OWNER", project, owner);
        addMember(owner, project, ownerRole);

        User assignee1 = createUser("assigneeA_" + ts);
        Role memberRole = createRole("MEMBER", project, owner);
        addMember(assignee1, project, memberRole);

        User assignee2 = createUser("assigneeB_" + ts);
        addMember(assignee2, project, memberRole);

        Milestone m1 = new Milestone();
        m1.setName("M1");
        m1.setProject(project);
        m1 = milestoneRepository.save(m1);

        Milestone m2 = new Milestone();
        m2.setName("M2");
        m2.setProject(project);
        m2 = milestoneRepository.save(m2);

        Tag t1 = new Tag(); t1.setName("A"); t1.setColor("c1"); t1.setProject(project);
        Tag t2 = new Tag(); t2.setName("B"); t2.setColor("c2"); t2.setProject(project);
        Tag t3 = new Tag(); t3.setName("C"); t3.setColor("c3"); t3.setProject(project);
        tagRepository.saveAll(List.of(t1, t2, t3));

        Ticket ticket = new Ticket();
        ticket.setProject(project);
        ticket.setTitle("OLD");
        ticket.setBody("OLD_BODY");
        ticket.setStatus(TicketStatus.BACKLOG);
        ticket.setCreatedBy(owner);
        ticket.setAssignedTo(assignee1);
        ticket.setMilestone(m1);
        ticket = ticketRepository.save(ticket);

        ticketTagRepository.saveAll(List.of(
                new TicketTag(ticket, t1),
                new TicketTag(ticket, t2)
        ));

        // Act
        UpdateTicketRequest req = new UpdateTicketRequest();
        req.setTitle("NEW");
        req.setBody("NEW_BODY");
        req.setStatus(TicketStatus.COMPLETED);
        req.setAssignedToUserId(assignee2.getId());
        req.setMilestoneId(m2.getId());
        req.setTagIds(List.of(t3.getId())); // replace tags with just C

        TicketDetailDto dto = ticketService.updateTicket(ticket.getId(), req, owner.getId());

        // Assert (DTO)
        assertThat(dto.getTitle()).isEqualTo("NEW");
        assertThat(dto.getBody()).isEqualTo("NEW_BODY");
        assertThat(dto.getStatus()).isEqualTo(TicketStatus.COMPLETED);
        assertThat(dto.getAssignee().getId()).isEqualTo(assignee2.getId());
        assertThat(dto.getMilestone().getId()).isEqualTo(m2.getId());
        assertThat(dto.getTags()).extracting("id").containsExactly(t3.getId());

        // Assert (DB tags replaced)
        List<Tag> persisted = tagRepository.findTagsByTicketId(ticket.getId());
        assertThat(persisted).extracting(Tag::getId).containsExactly(t3.getId());
    }

    @Test
    @Transactional
    void createTicket_whenCurrentUserNotMember_throwsForbidden() {
        // Arrange
        long ts = System.currentTimeMillis();

        User owner = createUser("owner3_" + ts);
        Project project = createProject("P3_" + ts, owner);

        Role ownerRole = createRole("OWNER", project, owner);
        addMember(owner, project, ownerRole);

        User outsider = createUser("outsider_" + ts);

        CreateTicketRequest req = new CreateTicketRequest();
        req.setTitle("X");
        req.setBody("Y");
        req.setStatus(TicketStatus.BACKLOG);

        // Act + Assert
        assertThatThrownBy(() -> ticketService.createTicket(project.getId(), req, outsider.getId()))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @Transactional
    void listTickets_returnsAllTicketsForProject() {
        long ts = System.currentTimeMillis();

        User user = createUser("list_user_" + ts);
        Project project = createProject("LIST_P_" + ts, user);

        Role ownerRole = createRole("OWNER", project, user);
        addMember(user, project, ownerRole);

        // create 2 tickets in this project
        Ticket t1 = new Ticket();
        t1.setProject(project);
        t1.setTitle("T1");
        t1.setCreatedBy(user);
        t1 = ticketRepository.save(t1);

        Ticket t2 = new Ticket();
        t2.setProject(project);
        t2.setTitle("T2");
        t2.setCreatedBy(user);
        t2 = ticketRepository.save(t2);

        // create another project's ticket that should NOT appear
        User other = createUser("other_" + ts);
        Project otherProject = createProject("OTHER_P_" + ts, other);
        Role otherRole = createRole("OWNER", otherProject, other);
        addMember(other, otherProject, otherRole);

        Ticket otherTicket = new Ticket();
        otherTicket.setProject(otherProject);
        otherTicket.setTitle("SHOULD_NOT_APPEAR");
        otherTicket.setCreatedBy(other);
        ticketRepository.save(otherTicket);

        // Act
        List<TicketListItemDto> items = ticketService.listTickets(project.getId(), user.getId());

        // Assert
        assertThat(items).isNotNull();
        assertThat(items).extracting(TicketListItemDto::getId)
                .contains(t1.getId(), t2.getId())
                .doesNotContain(otherTicket.getId());
    }

    @Test
    @Transactional
    void listTickets_whenNotMember_throwsForbidden() {
        long ts = System.currentTimeMillis();

        User owner = createUser("owner_list_" + ts);
        Project project = createProject("LIST_FORBIDDEN_" + ts, owner);
        Role ownerRole = createRole("OWNER", project, owner);
        addMember(owner, project, ownerRole);

        User outsider = createUser("outsider_list_" + ts);

        assertThatThrownBy(() -> ticketService.listTickets(project.getId(), outsider.getId()))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @Transactional
    void getTicket_returnsTicketDetailForMember() {
        long ts = System.currentTimeMillis();

        User owner = createUser("owner_get_" + ts);
        Project project = createProject("GET_P_" + ts, owner);
        Role ownerRole = createRole("OWNER", project, owner);
        addMember(owner, project, ownerRole);

        Milestone milestone = new Milestone();
        milestone.setName("M1");
        milestone.setProject(project);
        milestone = milestoneRepository.save(milestone);

        Tag tag = new Tag();
        tag.setName("BUG");
        tag.setColor("red");
        tag.setProject(project);
        tag = tagRepository.save(tag);

        Ticket ticket = new Ticket();
        ticket.setProject(project);
        ticket.setTitle("GET_TITLE");
        ticket.setBody("GET_BODY");
        ticket.setStatus(TicketStatus.BACKLOG);
        ticket.setCreatedBy(owner);
        ticket.setMilestone(milestone);
        ticket = ticketRepository.save(ticket);

        ticketTagRepository.save(new TicketTag(ticket, tag));

        // Act
        TicketDetailDto dto = ticketService.getTicket(ticket.getId(), owner.getId());

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(ticket.getId());
        assertThat(dto.getTitle()).isEqualTo("GET_TITLE");
        assertThat(dto.getBody()).isEqualTo("GET_BODY");
        assertThat(dto.getStatus()).isEqualTo(TicketStatus.BACKLOG);

        assertThat(dto.getMilestone()).isNotNull();
        assertThat(dto.getMilestone().getId()).isEqualTo(milestone.getId());

        assertThat(dto.getTags()).extracting("id").contains(tag.getId());
    }

    @Test
    @Transactional
    void getTicket_whenNotMember_throwsForbidden() {
        long ts = System.currentTimeMillis();

        User owner = createUser("owner_get_forb_" + ts);
        Project project = createProject("GET_FORB_P_" + ts, owner);
        Role ownerRole = createRole("OWNER", project, owner);
        addMember(owner, project, ownerRole);

        Ticket ticket = new Ticket();
        ticket.setProject(project);
        ticket.setTitle("X");
        ticket.setCreatedBy(owner);
        Ticket savedTicket = ticketRepository.save(ticket);

        User outsider = createUser("outsider_get_" + ts);

        assertThatThrownBy(() -> ticketService.getTicket(savedTicket.getId(), outsider.getId()))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    @Transactional
    void getTicket_whenTicketMissing_throwsNotFound() {
        long ts = System.currentTimeMillis();

        User user = createUser("user_missing_ticket_" + ts);
        // no need to create project; ticket lookup fails first in requireTicketAccess()

        assertThatThrownBy(() -> ticketService.getTicket(999999999L, user.getId()))
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
}