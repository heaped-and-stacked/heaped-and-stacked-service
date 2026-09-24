package org.manu.hs.domain.question;

public enum QuestionType {
    DEFINITION,        // recall/explain a concept in your own words
    GOTCHA,            // exposes a common misconception or subtle trap
    CODE_ANALYSIS,     // "what does this code output/do"
    BUG_HUNT,          // given working-looking code, find the defect
    IMPLEMENTATION,    // write code from scratch to satisfy a requirement
    DESIGN             // architecture/trade-off discussion, no single code answer
}