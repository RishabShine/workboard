package com.rishab.workboard.api.dto.request;

import com.rishab.workboard.api.domain.enums.TicketStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateTicketRequest {

    private String title;

    private String body;

    private TicketStatus status;

    private Long assignedToUserId;

    private Long milestoneId;

    private List<Long> tagIds;

}
