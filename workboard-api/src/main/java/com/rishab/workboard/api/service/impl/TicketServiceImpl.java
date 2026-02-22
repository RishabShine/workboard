package com.rishab.workboard.api.service.impl;

import com.rishab.workboard.api.domain.Member;
import com.rishab.workboard.api.domain.Role;
import com.rishab.workboard.api.domain.Ticket;
import com.rishab.workboard.api.domain.id.MemberId;
import com.rishab.workboard.api.dto.request.CreateTicketRequest;
import com.rishab.workboard.api.dto.request.UpdateTicketRequest;
import com.rishab.workboard.api.dto.response.ticket.TicketDetailDto;
import com.rishab.workboard.api.dto.response.ticket.TicketListItemDto;
import com.rishab.workboard.api.mapper.TicketDetailMapper;
import com.rishab.workboard.api.mapper.TicketListItemMapper;
import com.rishab.workboard.api.repository.MemberRepository;
import com.rishab.workboard.api.repository.TagRepository;
import com.rishab.workboard.api.repository.TicketRepository;
import com.rishab.workboard.api.repository.custom.CommentRepositoryCustom;
import com.rishab.workboard.api.service.TicketService;
import com.rishab.workboard.api.service.exceptions.ForbiddenException;
import com.rishab.workboard.api.service.exceptions.NotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TicketServiceImpl implements TicketService {

    private TicketRepository ticketRepository;
    private final MemberRepository memberRepository;
    private final TagRepository tagRepository;
    private final CommentRepositoryCustom commentRepository;
    private final TicketListItemMapper ticketListItemMapper;
    private final TicketDetailMapper ticketDetailMapper;

    TicketServiceImpl(TicketRepository ticketRepository,
                      MemberRepository memberRepository,
                      TagRepository tagRepository,
                      CommentRepositoryCustom commentRepository,
                      TicketListItemMapper ticketListItemMapper,
                      TicketDetailMapper ticketDetailMapper) {
        this.ticketRepository = ticketRepository;
        this.memberRepository = memberRepository;
        this.tagRepository = tagRepository;
        this.commentRepository = commentRepository;
        this.ticketListItemMapper = ticketListItemMapper;
        this.ticketDetailMapper = ticketDetailMapper;
    }

    @Override
    public List<TicketListItemDto> listTickets(Long projectId, Long currentUserId) {
        requireProjectMember(projectId, currentUserId);

        List<Ticket> tickets = ticketRepository.findTicketsByProject(projectId);

        return tickets.stream()
                .map(t -> ticketListItemMapper.toDto(
                        t,
                        commentRepository.getNumComments(t.getId()),
                        tagRepository.findTagsByTicketId(t.getId())
                ))
                .toList();
    }

    @Override
    public TicketDetailDto getTicket(Long ticketId, Long currentUserId) {
        Ticket ticket = requireTicketAccess(ticketId, currentUserId);

        int numComments = commentRepository.getNumComments(ticketId);
        var tags = tagRepository.findTagsByTicketId(ticketId);

        return ticketDetailMapper.toDto(ticket, numComments, tags);
    }

    @Override
    public TicketDetailDto createTicket(Long projectId, CreateTicketRequest req, Long currentUserId) {
        return null;
    }

    @Override
    public TicketDetailDto updateTicket(Long ticketId, UpdateTicketRequest req, Long currentUserId) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new NotFoundException("User not found"));

    }

    /*
    helper methods for project / ticket access
     */
    private Ticket requireTicketAccess(Long ticketId, Long currentUserId) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new NotFoundException("Ticket not found"));

        Long projectId = ticket.getProject().getId();

        if (!memberRepository.isUserInProject(projectId, currentUserId)) {
            throw new ForbiddenException("You are not a member of this project");
        }

        return ticket;
    }

    private void requireProjectMember(Long projectId, Long currentUserId) {
        if (!memberRepository.isUserInProject(projectId, currentUserId)) {
            throw new ForbiddenException("You are not a member of this project");
        }
    }
}
