package org.manu.hs.domain.question;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** How many learners voted a question up and down. */
@AllArgsConstructor
@Getter
public class VoteTotals {

    public static final VoteTotals NONE = new VoteTotals(0, 0);

    private int up;
    private int down;

}
