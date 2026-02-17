package com.rishab.workboard.api.mapper;

import com.rishab.workboard.api.dto.response.ticket.TicketListItemDto;
import com.rishab.workboard.api.dto.response.ticket.TicketPageDto;

import java.util.List;

public interface TicketPageMapper {

    TicketPageDto toDto(List<TicketListItemDto> tickets);

}
