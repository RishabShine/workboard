package com.rishab.workboard.api.service.impl;

import com.rishab.workboard.api.domain.*;
import com.rishab.workboard.api.dto.request.CreateTicketRequest;
import com.rishab.workboard.api.dto.request.UpdateTicketRequest;
import com.rishab.workboard.api.dto.response.ticket.TicketDetailDto;
import com.rishab.workboard.api.dto.response.ticket.TicketListItemDto;
import com.rishab.workboard.api.mapper.TicketDetailMapper;
import com.rishab.workboard.api.mapper.TicketListItemMapper;
import com.rishab.workboard.api.repository.*;
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

    private final TicketRepository ticketRepository;
    private final MemberRepository memberRepository;
    private final TagRepository tagRepository;
    private final CommentRepositoryCustom commentRepository;
    private final TicketListItemMapper ticketListItemMapper;
    private final TicketDetailMapper ticketDetailMapper;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final MilestoneRepository milestoneRepository;
    private final TicketTagRepository ticketTagRepository;

    TicketServiceImpl(TicketRepository ticketRepository,
                      MemberRepository memberRepository,
                      TagRepository tagRepository,
                      CommentRepositoryCustom commentRepository,
                      UserRepository userRepository,
                      ProjectRepository projectRepository,
                      MilestoneRepository milestoneRepository,
                      TicketTagRepository ticketTagRepository,
                      TicketListItemMapper ticketListItemMapper,
                      TicketDetailMapper ticketDetailMapper) {
        this.ticketRepository = ticketRepository;
        this.memberRepository = memberRepository;
        this.tagRepository = tagRepository;
        this.commentRepository = commentRepository;
        this.ticketListItemMapper = ticketListItemMapper;
        this.ticketDetailMapper = ticketDetailMapper;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.milestoneRepository = milestoneRepository;
        this.ticketTagRepository = ticketTagRepository;
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
    @Transactional
    public TicketDetailDto createTicket(Long projectId, CreateTicketRequest req, Long currentUserId) {
        requireProjectMember(projectId, currentUserId);

        Project projectRef = projectRepository.getReferenceById(projectId);
        User creatorRef = userRepository.getReferenceById(currentUserId);

        Ticket ticket = new Ticket();
        ticket.setProject(projectRef);
        ticket.setCreatedBy(creatorRef);
        ticket.setTitle(req.getTitle());
        ticket.setBody(req.getBody());
        ticket.setStatus(req.getStatus());

        // assignedTo (optional)
        if (req.getAssignedToUserId() != null) {
            requireProjectMember(projectId, req.getAssignedToUserId()); // assignee must be in project
            ticket.setAssignedTo(userRepository.getReferenceById(req.getAssignedToUserId()));
        }

        // milestone (optional)
        if (req.getMilestoneId() != null) {
            Milestone milestone = milestoneRepository.findById(req.getMilestoneId())
                    .orElseThrow(() -> new NotFoundException("Milestone not found"));
            if (!milestone.getProject().getId().equals(projectId)) {
                throw new ForbiddenException("Milestone does not belong to this project");
            }
            ticket.setMilestone(milestone);
        }

        Ticket saved = ticketRepository.save(ticket);

        // tags (optional)
        if (req.getTagIds() != null && !req.getTagIds().isEmpty()) {
            List<Tag> tags = tagRepository.findAllById(req.getTagIds());
            // validate tags belong to project + all ids exist
            if (tags.size() != req.getTagIds().size()) {
                throw new NotFoundException("One or more tags not found");
            }
            boolean allSameProject = tags.stream().allMatch(t -> t.getProject().getId().equals(projectId));
            if (!allSameProject) {
                throw new ForbiddenException("One or more tags do not belong to this project");
            }

            List<TicketTag> links = tags.stream()
                    .map(tag -> new TicketTag(saved, tag))
                    .toList();
            ticketTagRepository.saveAll(links);
        }

        int numComments = 0;
        List<Tag> ticketTags = tagRepository.findTagsByTicketId(saved.getId());
        return ticketDetailMapper.toDto(saved, numComments, ticketTags);
    }

    @Override
    @Transactional
    public TicketDetailDto updateTicket(Long ticketId, UpdateTicketRequest req, Long currentUserId) {

        Ticket ticket = requireTicketAccess(ticketId, currentUserId);
        Long projectId = ticket.getProject().getId();

        if (req.getTitle() != null) ticket.setTitle(req.getTitle());
        if (req.getBody() != null) ticket.setBody(req.getBody());
        if (req.getStatus() != null) ticket.setStatus(req.getStatus());

        if (req.getAssignedToUserId() != null) {
            requireProjectMember(projectId, req.getAssignedToUserId());
            ticket.setAssignedTo(userRepository.getReferenceById(req.getAssignedToUserId()));
        } else if (req.getAssignedToUserId() == null) {
            /*
            for now assigned to being null (should not be the case) will just mean no change to the assignee,
            can be changed to unassign (no assignee)
             */
        }

        // milestone (nullable: same note as above)
        if (req.getMilestoneId() != null) {
            Milestone milestone = milestoneRepository.findById(req.getMilestoneId())
                    .orElseThrow(() -> new NotFoundException("Milestone not found"));
            if (!milestone.getProject().getId().equals(projectId)) {
                throw new ForbiddenException("Milestone does not belong to this project");
            }
            ticket.setMilestone(milestone);
        }

        Ticket saved = ticketRepository.save(ticket);

        // tags: replace-all semantics if tagIds provided
        if (req.getTagIds() != null) {

            // deleting all existing tags
            ticketTagRepository.deleteAllByTicketId(ticketId);

            if (!req.getTagIds().isEmpty()) {
                List<Tag> tags = tagRepository.findAllById(req.getTagIds());

                if (tags.size() != req.getTagIds().size()) {
                    throw new NotFoundException("One or more tags not found");
                }

                boolean allSameProject = tags.stream().allMatch(t -> t.getProject().getId().equals(projectId));
                if (!allSameProject) {
                    throw new ForbiddenException("One or more tags do not belong to this project");
                }

                List<TicketTag> links = tags.stream()
                        .map(tag -> new TicketTag(saved, tag))
                        .toList();
                ticketTagRepository.saveAll(links);
            }
        }

        int numComments = commentRepository.getNumComments(ticketId);
        List<Tag> ticketTags = tagRepository.findTagsByTicketId(ticketId);
        return ticketDetailMapper.toDto(saved, numComments, ticketTags);
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
