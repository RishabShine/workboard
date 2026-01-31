package com.rishab.workboard.api.domain;

import com.rishab.workboard.api.domain.id.TicketTagId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ticket_tags")
@Getter
@Setter
@NoArgsConstructor
public class TicketTag {

    @EmbeddedId
    private TicketTagId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("ticketId")
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("tagId")
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    public TicketTag(Ticket ticket, Tag tag) {
        this.ticket = ticket;
        this.tag = tag;
        this.id = new TicketTagId(ticket.getId(), tag.getId());
    }
}
