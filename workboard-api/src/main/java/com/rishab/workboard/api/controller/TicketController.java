package com.rishab.workboard.api.controller;

import com.rishab.workboard.api.dto.request.CreateCommentRequest;
import com.rishab.workboard.api.dto.request.CreateTicketRequest;
import com.rishab.workboard.api.dto.request.UpdateTicketRequest;
import com.rishab.workboard.api.dto.response.comment.CommentDto;
import com.rishab.workboard.api.dto.response.ticket.TicketDetailDto;
import com.rishab.workboard.api.dto.response.ticket.TicketListItemDto;
import com.rishab.workboard.api.security.auth.AuthUser;
import com.rishab.workboard.api.service.CommentService;
import com.rishab.workboard.api.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final CommentService commentService;

    public TicketController(
            TicketService ticketService,
            CommentService commentService
    ) {
        this.ticketService = ticketService;
        this.commentService = commentService;
    }

    @GetMapping("/{projectId}/tickets")
    public ResponseEntity<List<TicketListItemDto>> getTickets(
            @PathVariable Long projectId,
            @AuthenticationPrincipal AuthUser user
    ) {
        List<TicketListItemDto> result = ticketService.listTickets(projectId, user.userId());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<TicketDetailDto> getTicket(
            @PathVariable Long ticketId,
            @AuthenticationPrincipal AuthUser user
    ) {
        TicketDetailDto result = ticketService.getTicket(ticketId, user.userId());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{projectId}/new")
    public ResponseEntity<TicketDetailDto> createTicket(
            @PathVariable Long projectId,
            @RequestBody CreateTicketRequest request,
            @AuthenticationPrincipal AuthUser user
    ) {
        TicketDetailDto result = ticketService.createTicket(projectId, request, user.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PatchMapping("/{ticketId}/update")
    public ResponseEntity<TicketDetailDto> updateTicket(
            @PathVariable Long ticketId,
            @RequestBody UpdateTicketRequest request,
            @AuthenticationPrincipal AuthUser user
    ) {
        TicketDetailDto result = ticketService.updateTicket(ticketId, request, user.userId());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{ticketId}/comments")
    public ResponseEntity<List<CommentDto>> getComments(
            @PathVariable Long ticketId,
            @AuthenticationPrincipal AuthUser user) {
        List<CommentDto> result = commentService.listComments(ticketId, user.userId());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{ticketId}/addComment")
    public ResponseEntity<CommentDto> addComment(
            @PathVariable Long ticketId,
            @RequestBody CreateCommentRequest request,
            @AuthenticationPrincipal AuthUser user
    ) {
        CommentDto result = commentService.addComment(ticketId, request, user.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

}
