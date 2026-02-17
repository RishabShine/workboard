package com.rishab.workboard.api.dto.request;

import com.rishab.workboard.api.dto.response.comment.CommentDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCommentRequest {

    private String body;

    private Long replyTo;

}
