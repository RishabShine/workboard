package com.rishab.workboard.api.service.impl;

import com.rishab.workboard.api.domain.Comment;
import com.rishab.workboard.api.domain.Ticket;
import com.rishab.workboard.api.domain.User;
import com.rishab.workboard.api.dto.request.CreateCommentRequest;
import com.rishab.workboard.api.dto.response.comment.CommentDto;
import com.rishab.workboard.api.mapper.CommentMapper;
import com.rishab.workboard.api.mapper.Mapper;
import com.rishab.workboard.api.mapper.impl.CommentMapperImpl;
import com.rishab.workboard.api.repository.CommentRepository;
import com.rishab.workboard.api.repository.MemberRepository;
import com.rishab.workboard.api.repository.TicketRepository;
import com.rishab.workboard.api.repository.UserRepository;
import com.rishab.workboard.api.service.CommentService;
import com.rishab.workboard.api.service.exceptions.ForbiddenException;
import com.rishab.workboard.api.service.exceptions.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final TicketRepository ticketRepository;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    CommentServiceImpl(TicketRepository ticketRepository,
                       MemberRepository memberRepository,
                       UserRepository userRepository,
                       CommentRepository commentRepository,
                       CommentMapper commentMapper) {
        this.ticketRepository = ticketRepository;
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
    }

    @Override
    public List<CommentDto> listComments(Long ticketId, Long currentUserId) {

        requireTicketAccess(ticketId, currentUserId);

        List<Comment> comments = commentRepository.findCommentsByTicketId(ticketId);

        return comments.stream()
                .map(commentMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public CommentDto addComment(Long ticketId, CreateCommentRequest req, Long currentUserId) {

        Ticket ticket = requireTicketAccess(ticketId, currentUserId);

        if (req.getBody() == null || req.getBody().trim().isEmpty()) {
            throw new ForbiddenException("Comment body is required");
        }

        User userRef = userRepository.getReferenceById(currentUserId);

        Comment comment = new Comment();
        comment.setTicket(ticket);
        comment.setUser(userRef);
        comment.setBody(req.getBody().trim());

        // replyTo (optional)
        if (req.getReplyTo() != null) {
            Comment parent = commentRepository.findById(req.getReplyTo())
                    .orElseThrow(() -> new NotFoundException("Reply-to comment not found"));

            // ensure the parent comment belongs to the same ticket
            if (!parent.getTicket().getId().equals(ticketId)) {
                throw new ForbiddenException("Reply-to comment does not belong to this ticket");
            }

            comment.setReplyTo(parent);
        }

        Comment saved = commentRepository.save(comment);

        return commentMapper.toDto(saved);
    }

    private Ticket requireTicketAccess(Long ticketId, Long currentUserId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new NotFoundException("Ticket not found"));
        Long projectId = ticket.getProject().getId();

        if (!memberRepository.isUserInProject(projectId, currentUserId)) {
            throw new ForbiddenException("You are not a member of this project");
        }

        return ticket;
    }
}