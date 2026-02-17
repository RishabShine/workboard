package com.rishab.workboard.api.dto.response.comment;

import com.rishab.workboard.api.dto.response.common.UserSummaryDto;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class CommentDto {

    private Long id;

    private String body;

    private OffsetDateTime createdAt;

    private UserSummaryDto user;

    private Long replyTo;

}
