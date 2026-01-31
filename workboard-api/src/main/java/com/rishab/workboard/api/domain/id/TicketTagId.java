package com.rishab.workboard.api.domain.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class TicketTagId implements Serializable {

    @Column(name = "ticket_id")
    private Long ticketId;

    @Column(name = "tag_id")
    private Long tagId;
}
