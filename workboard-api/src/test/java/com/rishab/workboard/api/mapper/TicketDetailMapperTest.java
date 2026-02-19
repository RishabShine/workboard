package com.rishab.workboard.api.mapper;

import com.rishab.workboard.api.domain.Milestone;
import com.rishab.workboard.api.domain.Tag;
import com.rishab.workboard.api.domain.Ticket;
import com.rishab.workboard.api.domain.User;
import com.rishab.workboard.api.domain.enums.TicketStatus;
import com.rishab.workboard.api.dto.response.ticket.TicketDetailDto;
import com.rishab.workboard.api.mapper.impl.TicketDetailMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class TicketDetailMapperTest {

    @Autowired
    private TicketDetailMapperImpl ticketDetailMapper;

    @Test
    void toDto_mapsTicketDetailFields() {
        User createdBy = new User();
        createdBy.setId(1L);
        createdBy.setUsername("creator");
        createdBy.setEmail("creator@example.com");
        createdBy.setProfileImageKey("profile-image/1.png");

        User assignee = new User();
        assignee.setId(2L);
        assignee.setUsername("assignee");
        assignee.setEmail("assignee@example.com");
        assignee.setProfileImageKey("profile-image/2.png");

        Milestone milestone = new Milestone();
        milestone.setId(3L);
        milestone.setName("Backlog");

        Ticket ticket = new Ticket();
        ticket.setId(50L);
        ticket.setTitle("Implement auth");
        ticket.setBody("Add JWT login");
        ticket.setStatus(TicketStatus.IN_PROGRESS);
        ticket.setAssignedTo(assignee);
        ticket.setMilestone(milestone);
        ticket.setCreatedBy(createdBy);
        ticket.setCreatedAt(OffsetDateTime.now());

        Tag tag = new Tag();
        tag.setId(9L);
        tag.setName("FEATURE");
        tag.setColor("blue");

        TicketDetailDto dto = ticketDetailMapper.toDto(ticket, 7, List.of(tag));

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(50L);
        assertThat(dto.getTitle()).isEqualTo("Implement auth");
        assertThat(dto.getBody()).isEqualTo("Add JWT login");
        assertThat(dto.getStatus()).isEqualTo(TicketStatus.IN_PROGRESS);
        assertThat(dto.getCreatedAt()).isEqualTo(ticket.getCreatedAt());
        assertThat(dto.getNumComments()).isEqualTo(7);

        assertThat(dto.getCreatedBy()).isNotNull();
        assertThat(dto.getCreatedBy().getId()).isEqualTo(1L);
        assertThat(dto.getCreatedBy().getUsername()).isEqualTo("creator");

        assertThat(dto.getAssignee()).isNotNull();
        assertThat(dto.getAssignee().getId()).isEqualTo(2L);

        assertThat(dto.getMilestone()).isNotNull();
        assertThat(dto.getMilestone().getId()).isEqualTo(3L);

        assertThat(dto.getTags()).extracting("id").containsExactly(9L);
        assertThat(dto.getTags()).extracting("name").containsExactly("FEATURE");
    }

}
