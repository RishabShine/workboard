package com.rishab.workboard.api.repository;

import com.rishab.workboard.api.domain.TicketTag;
import com.rishab.workboard.api.domain.id.TicketTagId;
import com.rishab.workboard.api.repository.custom.TicketTagRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketTagRepository extends JpaRepository<TicketTag, TicketTagId>, TicketTagRepositoryCustom {
}
