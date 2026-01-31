package com.rishab.workboard.api.domain.enums;

public enum TicketStatus {
    BACKLOG("Backlog"),
    READY("Ready"),
    IN_PROGRESS("In progress"),
    IN_REVIEW("In review"),
    COMPLETED("Completed");

    private final String display;

    TicketStatus(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }
}

