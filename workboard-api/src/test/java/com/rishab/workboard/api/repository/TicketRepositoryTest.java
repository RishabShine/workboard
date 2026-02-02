package com.rishab.workboard.api.repository;

import com.rishab.workboard.api.domain.Project;
import com.rishab.workboard.api.domain.Ticket;
import com.rishab.workboard.api.domain.enums.TicketStatus;
import com.rishab.workboard.api.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.*;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

//@DataJpaTest
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TicketRepositoryTest {

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    void findTicketsByProject_returnsTicketsForProject() {
        // Arrange
        User u = new User();
        u.setUsername("rishab_test_" + System.currentTimeMillis());
        u.setEmail(("rishab" + System.currentTimeMillis() + "@example.com").toUpperCase());
        u.setPassword("hash");
        u = userRepository.save(u);

        Project p = new Project();
        p.setName("TEST PROJECT");
        p.setCreatedBy(u);
        Project savedProj = projectRepository.save(p);

        Ticket t1 = new Ticket();
        t1.setProject(p);
        t1.setTitle("TICKET 1");
        t1.setCreatedBy(u);
        t1.setStatus(TicketStatus.BACKLOG);

        Ticket t2 = new Ticket();
        t2.setProject(p);
        t2.setTitle("TICKET 2");
        t2.setCreatedBy(u);
        t2.setStatus(TicketStatus.IN_PROGRESS);

        ticketRepository.saveAll(List.of(t1, t2));

        // Act
        List<Ticket> found = ticketRepository.findTicketsByProject(p.getId());

        // Assert
        assertThat(found).hasSize(2);
        assertThat(found).allMatch(t -> t.getProject().getId().equals(savedProj.getId()));
    }
}
