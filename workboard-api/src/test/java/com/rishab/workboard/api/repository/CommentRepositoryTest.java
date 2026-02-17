package com.rishab.workboard.api.repository;

import com.rishab.workboard.api.domain.Comment;
import com.rishab.workboard.api.domain.Project;
import com.rishab.workboard.api.domain.Ticket;
import com.rishab.workboard.api.domain.User;
import com.rishab.workboard.api.domain.enums.TicketStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    void getNumComments_returnsCorrectCount() {
        // Arrange
        User user = new User();
        user.setUsername("user_" + System.currentTimeMillis());
        user.setEmail(("user" + System.currentTimeMillis() + "@example.com").toUpperCase());
        user.setPassword("hash");
        user = userRepository.save(user);

        Project project = new Project();
        project.setName("COMMENT TEST PROJECT");
        project.setCreatedBy(user);
        project = projectRepository.save(project);

        Ticket ticket = new Ticket();
        ticket.setProject(project);
        ticket.setTitle("COMMENT TEST TICKET");
        ticket.setCreatedBy(user);
        ticket.setStatus(TicketStatus.BACKLOG);
        ticket = ticketRepository.save(ticket);

        Comment c1 = new Comment();
        c1.setTicket(ticket);
        c1.setUser(user);
        c1.setBody("First comment");

        Comment c2 = new Comment();
        c2.setTicket(ticket);
        c2.setUser(user);
        c2.setBody("Second comment");

        commentRepository.save(c1);
        commentRepository.save(c2);

        // Act
        int count = commentRepository.getNumComments(ticket.getId());

        // Assert
        assertThat(count).isEqualTo(2);
    }
}
