package com.rishab.workboard.api.mapper;

import com.rishab.workboard.api.domain.Tag;
import com.rishab.workboard.api.domain.Ticket;
import com.rishab.workboard.api.dto.response.ticket.TicketListItemDto;

import java.util.List;

public interface TicketListItemMapper {

    TicketListItemDto toDto(Ticket ticket, int numComments, List<Tag> tags);

}
