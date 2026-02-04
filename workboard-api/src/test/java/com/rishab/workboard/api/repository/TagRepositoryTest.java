package com.rishab.workboard.api.repository;

import com.rishab.workboard.api.domain.*;
import com.rishab.workboard.api.repository.custom.TagRepositoryCustom;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TagRepositoryTest {

    @Autowired
    TagRepository tagRepository;

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TicketTagRepository ticketTagRepository;

    @Test
    void findTagsByProjectId_returnsOnlyProjectTags() {
        // Arrange
        User user = new User();
        user.setUsername("tag_user_" + System.currentTimeMillis());
        user.setEmail(("tag" + System.currentTimeMillis() + "@example.com").toUpperCase());
        user.setPassword("hash");
        user = userRepository.save(user);

        Project project = new Project();
        project.setName("TAG PROJECT");
        project.setCreatedBy(user);
        Project savedProject = project;
        project = projectRepository.save(project);

        Tag tag1 = new Tag();
        tag1.setName("BUG");
        tag1.setProject(project);

        Tag tag2 = new Tag();
        tag2.setName("FEATURE");
        tag2.setProject(project);

        tagRepository.save(tag1);
        tagRepository.save(tag2);

        // Act
        List<Tag> tags = tagRepository.FindTagsByProjectId(project.getId());

        // Assert
        assertThat(tags).hasSize(2);
        assertThat(tags).allMatch(t -> t.getProject().getId().equals(savedProject.getId()));
    }

    @Test
    @Transactional
    void findTagsByTicketId_returnsTagsLinkedToTicket() {
        // Arrange
        User user = new User();
        user.setUsername("ticket_tag_user_" + System.currentTimeMillis());
        user.setEmail(("ticket_tag" + System.currentTimeMillis() + "@example.com").toUpperCase());
        user.setPassword("hash");
        user = userRepository.save(user);

        Project project = new Project();
        project.setName("TICKET TAG PROJECT");
        project.setCreatedBy(user);
        project = projectRepository.save(project);

        Ticket ticket = new Ticket();
        ticket.setProject(project);
        ticket.setTitle("TAGGED TICKET");
        ticket.setCreatedBy(user);
        ticket = ticketRepository.save(ticket);

        Tag tagA = new Tag();
        tagA.setName("URGENT");
        tagA.setProject(project);

        Tag tagB = new Tag();
        tagB.setName("UI");
        tagB.setProject(project);

        tagRepository.saveAll(List.of(tagA, tagB));

        TicketTag tt1 = new TicketTag(ticket, tagA);
        TicketTag tt2 = new TicketTag(ticket, tagB);
        ticketTagRepository.saveAll(List.of(tt1, tt2));

        // Act
        List<Tag> tags = tagRepository.findTagsByTicketId(ticket.getId());

        // Assert
        assertThat(tags).hasSize(2);
        assertThat(tags).extracting(Tag::getName)
                .containsExactlyInAnyOrder("URGENT", "UI");
    }
}
