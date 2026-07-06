package com.rishab.workboard.api.controller;

import com.rishab.workboard.api.dto.request.CreateTagRequest;
import com.rishab.workboard.api.dto.response.common.TagDto;
import com.rishab.workboard.api.security.auth.AuthUser;
import com.rishab.workboard.api.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects/{projectId}/tags")
public class TagController {

    private final TagService tagService;

    public TagController(
            TagService tagService
    ) {
        this.tagService = tagService;
    }

    @GetMapping
    public ResponseEntity<List<TagDto>> getTags(
            @PathVariable Long projectId,
            @AuthenticationPrincipal AuthUser user
    ) {
        List<TagDto> result = tagService.getTagsByProject(user.userId(), projectId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/create")
    public ResponseEntity<TagDto> addComment(
            @PathVariable Long projectId,
            @RequestBody CreateTagRequest request,
            @AuthenticationPrincipal AuthUser user
    ) {
        TagDto result = tagService.createTag(request, user.userId(), projectId);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

}
