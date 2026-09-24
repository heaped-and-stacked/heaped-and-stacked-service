package org.manu.hs.application.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.manu.hs.application.common.exception.InvalidFieldValueException;
import org.manu.hs.application.common.exception.RequiredFieldMissingException;

import java.util.Collection;

/**
 * Generic field checks shared by every feature. Each one reports the offending field
 * by name, so the client gets a message like "name is required".
 * <p>
 * Every free-text field has a maximum length and every list a maximum size, so no
 * request can store unbounded data.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Validations {

    private static final int ZERO_WIDTH_JOINER = 0x200D;
    private static final int ZERO_WIDTH_NON_JOINER = 0x200C;

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

    /** A missing value passes: whether it's required is checked separately. */
    public static void requireMaxLength(String value, int maxLength, String fieldName) {
        if (value != null && value.length() > maxLength) {
            throw new InvalidFieldValueException(fieldName, "must be at most %d characters".formatted(maxLength));
        }
    }

    /**
     * For short fields shown on one line (titles, names, sources): no line breaks, no control
     * characters and no invisible formatting characters, such as zero-width spaces or the
     * right-to-left overrides that make text display differently from what it says. A missing
     * value passes.
     */
    public static void requireSingleLine(String value, String fieldName) {
        if (!isSingleLine(value)) {
            throw new InvalidFieldValueException(fieldName, "must be one line without invisible characters");
        }
    }

    /** The test behind requireSingleLine, for lists whose entries are checked one by one. */
    public static boolean isSingleLine(String value) {
        return value == null || value.codePoints().noneMatch(c -> isControl(c) || isLineBreak(c) || isInvisibleFormat(c));
    }

    /**
     * For short texts that may span lines (descriptions, notes): line breaks and tabs are fine,
     * other control characters and invisible formatting characters aren't. A missing value passes.
     * Long texts that hold code (exercises, answers) aren't checked this way.
     */
    public static void requireNoInvisibleCharacters(String value, String fieldName) {
        if (value != null && value.codePoints().anyMatch(c -> (isControl(c) && !isAllowedWhitespace(c)) || isInvisibleFormat(c))) {
            throw new InvalidFieldValueException(fieldName, "must not contain invisible characters");
        }
    }

    private static boolean isControl(int codePoint) {
        return Character.getType(codePoint) == Character.CONTROL;
    }

    private static boolean isAllowedWhitespace(int codePoint) {
        return codePoint == '\n' || codePoint == '\r' || codePoint == '\t';
    }

    private static boolean isLineBreak(int codePoint) {
        return Character.getType(codePoint) == Character.LINE_SEPARATOR
                || Character.getType(codePoint) == Character.PARAGRAPH_SEPARATOR;
    }

    /**
     * Unicode "format" characters (zero-width space, direction marks and overrides, ...), except the
     * zero-width joiner and non-joiner: emoji such as 👩‍💻 and some scripts need them.
     */
    private static boolean isInvisibleFormat(int codePoint) {
        return Character.getType(codePoint) == Character.FORMAT
                && codePoint != ZERO_WIDTH_JOINER
                && codePoint != ZERO_WIDTH_NON_JOINER;
    }

    /**
     * For long texts that may hold any code (exercises, answers): everything is allowed except the
     * NUL character, which PostgreSQL can't store in a text column. A missing value passes.
     */
    public static void requireNoNulCharacter(String value, String fieldName) {
        if (value != null && value.indexOf('\0') >= 0) {
            throw new InvalidFieldValueException(fieldName, "must not contain the NUL character");
        }
    }

    /** Every entry of a list must be there and not blank. A missing list passes. */
    public static void requireNoEmptyEntries(Collection<String> values, String fieldName) {
        if (values != null && values.stream().anyMatch(value -> value == null || value.isBlank())) {
            throw new InvalidFieldValueException(fieldName, "must not contain empty entries");
        }
    }

    /** A missing list passes: whether it's required is checked separately. */
    public static void requireMaxSize(Collection<?> values, int maxSize, String fieldName) {
        if (values != null && values.size() > maxSize) {
            throw new InvalidFieldValueException(fieldName, "must have at most %d entries".formatted(maxSize));
        }
    }
}
