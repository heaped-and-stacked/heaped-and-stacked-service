package org.manu.hs.application.user.exception;

import org.manu.hs.application.common.exception.base.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(Long id) {
        super("User with id '%d' not found".formatted(id));
    }
}
