package org.manu.hs.application.content.exception;

import org.manu.hs.application.common.exception.base.NotFoundException;

public class ReportNotFoundException extends NotFoundException {
    public ReportNotFoundException(Long id) {
        super("Report with id '%d' not found".formatted(id));
    }
}
