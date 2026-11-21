-- An exercise's source: 200 characters held one docs link and its title, not a second one.
-- Mirrors MAX_SOURCE_LENGTH in QuestionService. Widening a VARCHAR is a catalog change in
-- PostgreSQL: no table rewrite, no long lock.
ALTER TABLE question ALTER COLUMN source TYPE VARCHAR(1000);
