# GrabNGo - Food Wastage Reduction App

Android app (Java, XML layouts) that lets restaurants sell excess food at a discount and lets customers find it, order it, and pick it up or have it delivered.
CSIS 3175, Group 2, Fall 2026.

## Roles
- **Customer:** search restaurants, view discounted food, order (pickup or delivery), track orders, receive reminders, view history.
- **Restaurant manager:** add and manage food items, accept or reject orders, update order status, send reminders, view history.

## Team
| Member | Area |
|---|---|
| Ami | Database layer (entities, DAOs, repositories, seed data) |
| Jenu | Customer screens, theme and styles |
| Shalom | Restaurant manager screens, navigation |
| Gabriel | Auth, profile, session, reminder notifications, password recovery |

## Tech
- Android Studio, Java, minimum SDK 26+
- SQLite with Room
- Anything not covered in class is cited in the technical report

## Getting started
1. Clone the repo and open the project folder in Android Studio.
2. Let Gradle sync, then run on an emulator (API 34 recommended).
3. Demo accounts are created by the seed data (see `docs/demo-accounts.md`).

## Workflow
- Never commit directly to `main`. Create a branch per issue: `feature/<issue-number>-short-name`.
- Open a pull request, link it with `Closes #<issue>`, and get one teammate to review it.
- Pull requests must pass the **Android CI** check before merging.
- Use the issue templates for tasks and bugs.

## Submission checklist (due Nov 26)
- [ ] Folder named `G2-CSIS3175` containing the Android Studio project and the PDF report
- [ ] Report: each member's work, features, use case, sequence, class and ER diagrams, screenshots, citations
- [ ] App runs from a clean install with seed data
- [ ] Uploaded to Blackboard (one submission per group)
- [ ] Presentation and demo rehearsed (15 to 20 minutes)
