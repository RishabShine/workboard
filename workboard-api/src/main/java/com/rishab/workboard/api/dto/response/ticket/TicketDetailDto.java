package com.rishab.workboard.api.dto.response.ticket;

import com.rishab.workboard.api.domain.enums.TicketStatus;
import com.rishab.workboard.api.dto.response.common.MilestoneDto;
import com.rishab.workboard.api.dto.response.common.TagDto;
import com.rishab.workboard.api.dto.response.common.UserSummaryDto;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
public class TicketDetailDto {

    private Long id;

    private String title;

    private TicketStatus status;

    private UserSummaryDto assignee;

    private MilestoneDto milestone;

    private int numComments;

    private List<TagDto> tags;

    private String body;

    private OffsetDateTime createdAt;

    private UserSummaryDto createdBy;

}
