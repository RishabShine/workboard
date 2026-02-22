package com.rishab.workboard.api.service;

import com.rishab.workboard.api.dto.request.CreateTicketRequest;
import com.rishab.workboard.api.dto.request.UpdateTicketRequest;
import com.rishab.workboard.api.dto.response.ticket.TicketDetailDto;
import com.rishab.workboard.api.dto.response.ticket.TicketListItemDto;

import java.util.List;

public interface TicketService {

    List<TicketListItemDto> listTickets(Long projectId, Long currentUserId);

    TicketDetailDto getTicket(Long ticketId, Long currentUserId);

    TicketDetailDto createTicket(Long projectId, CreateTicketRequest req, Long currentUserId);

    TicketDetailDto updateTicket(Long ticketId, UpdateTicketRequest req, Long currentUserId);

}
