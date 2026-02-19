package com.rishab.workboard.api.mapper;

import com.rishab.workboard.api.domain.Milestone;
import com.rishab.workboard.api.domain.Tag;
import com.rishab.workboard.api.domain.Ticket;
import com.rishab.workboard.api.domain.User;
import com.rishab.workboard.api.domain.enums.TicketStatus;
import com.rishab.workboard.api.dto.response.ticket.TicketListItemDto;
import com.rishab.workboard.api.mapper.impl.TicketListItemMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class TicketListItemMapperTest {

    @Autowired
    private TicketListItemMapperImpl ticketListItemMapper;

    @Test
    void toDto_mapsTicketListItemFields() {
        User assignee = new User();
        assignee.setId(10L);
        assignee.setUsername("assignee");
        assignee.setEmail("assignee@example.com");
        assignee.setProfileImageKey("profile-image/10.png");

        Milestone milestone = new Milestone();
        milestone.setId(20L);
        milestone.setName("Sprint 1");

        Ticket ticket = new Ticket();
        ticket.setId(100L);
        ticket.setTitle("Fix bug");
        ticket.setStatus(TicketStatus.BACKLOG);
        ticket.setAssignedTo(assignee);
        ticket.setMilestone(milestone);

        Tag t1 = new Tag();
        t1.setId(1L);
        t1.setName("BUG");
        t1.setColor("red");

        Tag t2 = new Tag();
        t2.setId(2L);
        t2.setName("UI");
        t2.setColor("blue");

        TicketListItemDto dto = ticketListItemMapper.toDto(ticket, 3, List.of(t1, t2));

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(100L);
        assertThat(dto.getTitle()).isEqualTo("Fix bug");
        assertThat(dto.getStatus()).isEqualTo(TicketStatus.BACKLOG);
        assertThat(dto.getNumComments()).isEqualTo(3);

        assertThat(dto.getAssignee()).isNotNull();
        assertThat(dto.getAssignee().getId()).isEqualTo(10L);
        assertThat(dto.getAssignee().getUsername()).isEqualTo("assignee");

        assertThat(dto.getMilestone()).isNotNull();
        assertThat(dto.getMilestone().getId()).isEqualTo(20L);
        assertThat(dto.getMilestone().getName()).isEqualTo("Sprint 1");

        assertThat(dto.getTags()).extracting("id").containsExactly(1L, 2L);
        assertThat(dto.getTags()).extracting("name").containsExactly("BUG", "UI");
    }

}
