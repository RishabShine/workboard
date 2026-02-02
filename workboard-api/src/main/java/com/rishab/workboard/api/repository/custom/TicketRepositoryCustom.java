package com.rishab.workboard.api.repository.custom;

import com.rishab.workboard.api.domain.Ticket;

import java.util.List;

public interface TicketRepositoryCustom {

    public List<Ticket> findTicketsByProject(Long projectId);

}
