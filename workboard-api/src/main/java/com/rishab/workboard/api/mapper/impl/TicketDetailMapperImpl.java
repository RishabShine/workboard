package com.rishab.workboard.api.mapper.impl;

import com.rishab.workboard.api.domain.*;
import com.rishab.workboard.api.dto.response.ticket.TicketDetailDto;
import com.rishab.workboard.api.mapper.TicketDetailMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TicketDetailMapperImpl implements TicketDetailMapper {

    private final UserMapperImpl userMapper;
    private final TagMapperImpl tagMapper;
    private final MilestoneMapperImpl milestoneMapper;

    public TicketDetailMapperImpl(UserMapperImpl userMapper,
                                   TagMapperImpl tagMapper,
                                   MilestoneMapperImpl milestoneMapper) {
        this.userMapper = userMapper;
        this.tagMapper = tagMapper;
        this.milestoneMapper = milestoneMapper;
    }

    @Override
    public TicketDetailDto toDto(Ticket ticket,
                                 int numComments,
                                 List<Tag> tags) {

        TicketDetailDto ticketDetails = new TicketDetailDto();
        ticketDetails.setId(ticket.getId());
        ticketDetails.setTitle(ticket.getTitle());
        ticketDetails.setStatus(ticket.getStatus());
        ticketDetails.setAssignee(
                ticket.getAssignedTo() == null ? null : userMapper.toDto(ticket.getAssignedTo())
        );
        ticketDetails.setTags(
                tags.stream()
                        .map(tagMapper::toDto)
                        .toList()
        );
        ticketDetails.setMilestone(
                ticket.getMilestone() == null ? null : milestoneMapper.toDto(ticket.getMilestone())
        );
        ticketDetails.setNumComments(numComments);
        ticketDetails.setBody(ticket.getBody());
        ticketDetails.setCreatedAt(ticket.getCreatedAt());
        ticketDetails.setCreatedBy(userMapper.toDto(ticket.getCreatedBy()));;

        return ticketDetails;
    }

}
