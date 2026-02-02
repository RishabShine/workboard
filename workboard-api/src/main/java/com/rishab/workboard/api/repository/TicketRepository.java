package com.rishab.workboard.api.repository;

import com.rishab.workboard.api.domain.Ticket;
import com.rishab.workboard.api.repository.custom.TicketRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long>, TicketRepositoryCustom {
}
