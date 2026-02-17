package com.rishab.workboard.api.dto.response.ticket;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TicketPageDto {

    private List<TicketListItemDto> tickets;

    // the number of pages of tickets
    private int page;

    // private int size;

    // is there a next page
    private boolean hasNext;

}
