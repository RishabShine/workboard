package com.rishab.workboard.api.repository;

import com.rishab.workboard.api.domain.*;
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
class TicketTagRepositoryTest {

    @Autowired
    private TicketTagRepository ticketTagRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

//    @Test
//    @Transactional
//    void deleteAllByTicketId_removesAllTicketTagsForThatTicket() {
//
//        // Arrange
//        long ts = System.currentTimeMillis();
//
//        User user = new User();
//        user.setUsername("tt_user_" + ts);
//        user.setEmail(("tt" + ts + "@example.com").toUpperCase());
//        user.setPassword("hash");
//        user = userRepository.save(user);
//
//        Project project = new Project();
//        project.setName("TT PROJECT");
//        project.setCreatedBy(user);
//        project = projectRepository.save(project);
//
//        Ticket ticket = new Ticket();
//        ticket.setProject(project);
//        ticket.setTitle("Ticket");
//        ticket.setCreatedBy(user);
//        ticket = ticketRepository.save(ticket);
//
//        Tag tag1 = new Tag();
//        tag1.setName("BUG");
//        tag1.setProject(project);
//
//        Tag tag2 = new Tag();
//        tag2.setName("FEATURE");
//        tag2.setProject(project);
//
//        tagRepository.saveAll(List.of(tag1, tag2));
//
//        TicketTag tt1 = new TicketTag(ticket, tag1);
//        TicketTag tt2 = new TicketTag(ticket, tag2);
//        ticketTagRepository.saveAll(List.of(tt1, tt2));
//
//        // sanity check
//        assertThat(ticketTagRepository.findAll()).hasSize(2);
//
//        // Act
//        ticketTagRepository.deleteAllByTicketId(ticket.getId());
//
//        // Assert
//        List<TicketTag> remaining = ticketTagRepository.findAll();
//        assertThat(remaining).isEmpty();
//    }
}