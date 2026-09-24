package org.manu.hs.domain.question;

/**
 * What a learner's feedback on a question says. PRAISE: keep it, more like this; it never holds
 * a question back. The others are problems: UNCLEAR: the question itself is wrong, or can't be
 * understood or answered as written. WRONG: its model answer is incorrect. DUPLICATE: another
 * question asks the same. INAPPROPRIATE: offensive, spam or copied content. OTHER: anything else
 * (the note says what). The web app offers UNCLEAR, WRONG and OTHER.
 */
public enum ReportReason {
    PRAISE,
    WRONG,
    UNCLEAR,
    DUPLICATE,
    INAPPROPRIATE,
    OTHER
}
