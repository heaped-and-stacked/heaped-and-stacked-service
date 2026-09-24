package org.manu.hs.application.user.exception;

import org.manu.hs.application.common.exception.base.NotFoundException;

public class InviteNotFoundException extends NotFoundException {
    public InviteNotFoundException(Long id) {
        super("Invite with id '%d' not found".formatted(id));
    }
}
