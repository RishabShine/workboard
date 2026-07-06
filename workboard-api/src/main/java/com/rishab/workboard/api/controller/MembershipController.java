package com.rishab.workboard.api.controller;

import com.rishab.workboard.api.dto.request.CreateProjectInviteRequest;
import com.rishab.workboard.api.dto.response.project.MemberDto;
import com.rishab.workboard.api.dto.response.project.ProjectInviteDto;
import com.rishab.workboard.api.security.auth.AuthUser;
import com.rishab.workboard.api.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/membership")
public class MembershipController {

    private final MemberService memberService;

    public MembershipController(
            MemberService memberService
    ) {
        this.memberService = memberService;
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<List<MemberDto>> getMembers(
            @PathVariable Long projectId,
            @AuthenticationPrincipal AuthUser user
    ) {
        List<MemberDto> result = memberService.listMembers(projectId, user.userId());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{projectId}/createInvite")
    public ResponseEntity<ProjectInviteDto> createInvite(
            @PathVariable Long projectId,
            @RequestBody CreateProjectInviteRequest request,
            @AuthenticationPrincipal AuthUser user
    ) {
        ProjectInviteDto result = memberService.createInvite(projectId, request, user.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/invites")
    public ResponseEntity<List<ProjectInviteDto>> getInvites(
            @AuthenticationPrincipal AuthUser user
    ) {
        List<ProjectInviteDto> result = memberService.getInvites(user.userId());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{inviteId}/accept")
    public ResponseEntity<Void> acceptInvite(
            @PathVariable Long inviteId,
            @AuthenticationPrincipal AuthUser user
    ) {
        memberService.acceptInvite(inviteId, user.userId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{inviteId}/reject")
    public ResponseEntity<Void> rejectInvite(
            @PathVariable Long inviteId,
            @AuthenticationPrincipal AuthUser user
    ) {
        memberService.rejectInvite(inviteId, user.userId());
        return ResponseEntity.noContent().build();
    }

}
