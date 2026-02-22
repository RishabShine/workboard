package com.rishab.workboard.api.service.exceptions;

public class InviteAlreadyPendingException extends ConflictException {
    public InviteAlreadyPendingException() {
        super("A pending invite already exists for this user");
    }
}
