package com.rishab.workboard.api.domain;

import com.rishab.workboard.api.domain.id.TicketSubscriptionId;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "ticket_subscriptions")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TicketSubscription {

    @EmbeddedId
    private TicketSubscriptionId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("ticketId")
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    public TicketSubscription(Ticket ticket, User user) {
        this.ticket = ticket;
        this.user = user;
        this.id = new TicketSubscriptionId(ticket.getId(), user.getId());
    }
}
