package com.rishab.workboard.api.mapper.impl;

import com.rishab.workboard.api.dto.response.ticket.TicketListItemDto;
import com.rishab.workboard.api.dto.response.ticket.TicketPageDto;
import com.rishab.workboard.api.mapper.TicketPageMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TicketPageMapperImpl implements TicketPageMapper {

    @Override
    public TicketPageDto toDto(List<TicketListItemDto> tickets) {
        TicketPageDto ticketPage = new TicketPageDto();
        ticketPage.setTickets(tickets);
        return ticketPage;
    }

}
