package com.rishab.workboard.api.mapper.impl;

import com.rishab.workboard.api.domain.Tag;
import com.rishab.workboard.api.domain.Ticket;
import com.rishab.workboard.api.dto.response.ticket.TicketListItemDto;
import com.rishab.workboard.api.mapper.TicketListItemMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TicketListItemMapperImpl implements TicketListItemMapper {

    private final UserMapperImpl userMapper;
    private final TagMapperImpl tagMapper;
    private final MilestoneMapperImpl milestoneMapper;

    public TicketListItemMapperImpl(UserMapperImpl userMapper,
                                  TagMapperImpl tagMapper,
                                  MilestoneMapperImpl milestoneMapper) {
        this.userMapper = userMapper;
        this.tagMapper = tagMapper;
        this.milestoneMapper = milestoneMapper;
    }

    @Override
    public TicketListItemDto toDto(Ticket ticket,
                                   int numComments,
                                   List<Tag> tags) {

        TicketListItemDto ticketListItem = new TicketListItemDto();
        ticketListItem.setId(ticket.getId());
        ticketListItem.setTitle(ticket.getTitle());
        ticketListItem.setStatus(ticket.getStatus());
        ticketListItem.setAssignee(userMapper.toDto(ticket.getAssignedTo()));
        ticketListItem.setTags(
                tags.stream()
                        .map(tagMapper::toDto)
                        .toList()
        );
        ticketListItem.setMilestone(milestoneMapper.toDto(ticket.getMilestone()));
        ticketListItem.setNumComments(numComments);

        return ticketListItem;

    }

}
