package com.rishab.workboard.api.mapper;

import com.rishab.workboard.api.dto.response.ticket.TicketListItemDto;
import com.rishab.workboard.api.dto.response.ticket.TicketPageDto;
import com.rishab.workboard.api.mapper.impl.TicketPageMapperImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class TicketPageMapperTest {

    @Autowired
    private TicketPageMapperImpl ticketPageMapper;

    @Test
    void toDto_wrapsTicketList() {
        TicketListItemDto t1 = new TicketListItemDto();
        t1.setId(1L);
        t1.setTitle("A");

        TicketListItemDto t2 = new TicketListItemDto();
        t2.setId(2L);
        t2.setTitle("B");

        TicketPageDto dto = ticketPageMapper.toDto(List.of(t1, t2));

        assertThat(dto).isNotNull();
        assertThat(dto.getTickets()).hasSize(2);
        assertThat(dto.getTickets()).extracting("id").containsExactly(1L, 2L);
        assertThat(dto.getTickets()).extracting("title").containsExactly("A", "B");
    }

}
