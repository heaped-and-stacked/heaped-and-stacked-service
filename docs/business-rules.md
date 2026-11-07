# Business rules

What the API allows, and for whom. Each rule is enforced in the services, and in the database
where it can be.

## Roles

| Role | Can do |
| ---- | ------ |
| `MEMBER` | Curates their own content. |
| `STAFF` | Curates every concept and question: reviews, reports, edits, retiring, deleting what nobody uses. |
| `ADMIN` | Staff who also manage users: roles, blocking, invites. |

Content rules ask `CurrentUserProvider.isStaff()`, user management asks `isAdmin()`. Who curates a
concept is decided in one place, `Concept.isCuratedBy`.

## Accounts

- An account is created at the first GitHub sign-in (`app_user` plus a `user_identity`).
- Accounts in `BOOTSTRAP_ADMIN_IDS` become admin at every sign-in, so an admin can't lock everyone
  out. That's the only way to become admin: the API refuses to grant the role.
- A role or status change ends all the person's sessions (`UserSessions`). A blocked user is out at
  once and can't sign in again; a new role applies from the next sign-in.
- Deleting an account is a soft delete: name, picture and the GitHub link go at once, and the
  person's sessions end. Answers and contributions stay, attached to a nameless account.
- The last active admin can't be removed.
- With any invite on the list, only invited GitHub logins and `BOOTSTRAP_ADMIN_IDS` can sign in.
  With an empty list, anyone can.

## Who may contribute

Two settings decide (`MEMBERS_ADD_QUESTIONS`, `MEMBERS_ADD_CONCEPTS`, both off by default). Off, only
staff add that kind of content: a member who tries gets 403, and `GET /users/me` says
`canAddQuestions` / `canAddConcepts`, so the web app hides the button. Members can always edit what
they already wrote.

With both on, members add questions, and up to five concepts of their own. A question waits for
approval on concepts that ask for it; featured concepts do.

## Concepts

- A concept belongs to its author and is published when created, as their own (`featured` false):
  listed in the community, and curated by its owner alone (edit, any status, delete, decide and
  verify its questions). Staff may only retire it, or bring it back.
- Staff feature a published concept to take it into the gym's pool: it joins the default workout,
  and staff curate it as well as its owner. A featured concept must be unfeatured before it can be
  unpublished, retired or deleted.
- **House-owned concepts** (`houseOwned` in responses) belong to the house account and always stay
  featured: unfeaturing one is refused (409), since nobody can sign in as the house account to
  curate it outside the pool.
- The owner chooses whether new questions need approval (`contributionPolicy`: `OWNER_APPROVAL`) or
  not (`AUTO_ACCEPT`).
- Drafts are listed only to their owner.

## Questions

- A question has a required title, at most 120 characters.
- It's live when submitted, on each concept once accepted there.
- Authors can edit at any time. Only staff delete, and only what nobody has answered; others retire.
- Answered questions are never deleted, only retired.
- Lists come a page at a time (1 to 100 items, 20 by default), newest first: members see their own,
  staff everyone's.

## Fit and quality are separate decisions

**Fit is per concept.** A question added to a concept by someone who doesn't curate it waits
(`PENDING`) if the concept asks for approval, until its curator accepts or declines it there. The
owner's and staff's own questions are accepted straight away. A decline stands, and the concept
drops off the question, until the author picks that concept again, which asks again.

**Quality is per question.** Verified (`question.verified_by`, `verified_at`) is checked once,
whatever concepts the question is on.

- Who may verify: staff when the question is on any featured concept; otherwise the owners of its
  concepts.
- Only a question accepted somewhere can be verified (409).
- An edit by someone who may verify keeps the check; anyone else's edit clears it.

## When an owner leaves

When an owner deletes their account or is blocked, their concepts pass to the house account, and
staff curate them, featured or not. Blocking also declines their waiting questions.

## Picking the next question

The API picks each learner's next question:

1. Questions the learner never answered come first.
2. Then answered ones.

Within each group the pick is random but weighted, so nothing is ever certain:

- **Age:** days since the learner last answered it + 1 (1 for a new one). A month-old question is
  about 31 times as likely as one answered today.
- **Likes:** 1 + ln(1 + likes), where likes are learners who voted it up or praised it, each counted
  once. A nudge that flattens out: 1 like 1.7, 5 likes 2.8, 20 likes 4.0.

The two weights multiply.

A workout sends the questions it has shown as `skip`, so it never repeats one. Each next question
comes with `unansweredCount`: how many questions for the requested concepts the learner has never
answered (skipped or not), this one included if it's new. 0 means only repeats are left.

## Feedback

- **Votes:** up or down, one per learner, which they can change or take back. Authors see the totals
  on their own questions. Up votes count as likes; down votes don't change how often a question is
  served.
- **Reports:** praise, or a problem: wrong, unclear, duplicate, inappropriate or other, with an
  optional note of up to 2000 characters. Each learner has at most one open report per question,
  which they can change or withdraw until it's decided; then they may send a new one.
- **Holding back:** a question with 3 open problem reports is no longer served until some are
  decided. Praise never holds a question back, and counts as a like.
- **Deciding:** the question's author, the owner of any concept it's linked to, or staff mark a
  report resolved or dismissed. Reports never say who sent them. A decision is final.

## Text fields

Every free-text field has a maximum length. Short single-line fields (titles, names, sources,
display names, reference links) refuse line breaks, control characters and invisible formatting
characters such as zero-width spaces and direction overrides. Descriptions and report notes refuse
the invisible ones but allow line breaks. Question texts and answers may hold any code. The API
stores text as given; the web app shows it as text, never as HTML.

## Limits

- **Request size:** bodies over 256 KB are refused with 413 before they're read; the largest valid
  request stays under 100 KB.
- **Rate limits:** per client address and minute, 10 sign-in starts, 60 writes and 300 reads; health
  checks are free. Over the limit: 429 with `Retry-After`.
