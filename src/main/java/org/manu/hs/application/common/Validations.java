package org.manu.hs.application.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.manu.hs.application.common.exception.RequiredFieldMissingException;

/**
 * Generic field checks shared by every feature. Each one reports the offending field
 * by name, so the client gets a message like "name is required".
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Validations {

    public static void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new RequiredFieldMissingException(fieldName);
        }
    }

    public static void requireNonNull(Object value, String fieldName) {
        if (value == null) {
            throw new RequiredFieldMissingException(fieldName);
        }
    }
}
