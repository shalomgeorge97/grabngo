#!/usr/bin/env bash
# Creates labels, milestones and starter issues for GrabNGo.
# Requirements: GitHub CLI (https://cli.github.com), logged in with `gh auth login`.
# Run from inside your cloned repo:  bash scripts/setup-github.sh
set -e

# ---- EDIT THESE: GitHub usernames (they must be collaborators on the repo) ----
SHALOM="shalomgeorge97"
AMI="ami0610-hub"
JENU="jenumoljoseph"
GABRIEL="shalomgeorge97"
# ------------------------------------------------------------------------------

MILESTONE="Final project - Nov 26"
TESTING="Testing and report - Nov 24"

gh auth status >/dev/null

echo "Creating labels..."
mk_label() { gh label create "$1" --color "$2" --description "$3" --force >/dev/null; }
mk_label database       1D76DB "Room entities, DAOs, repositories"
mk_label customer       2E7D32 "Customer screens"
mk_label manager        C2410C "Restaurant manager screens"
mk_label auth           334E68 "Register, login, profile, session"
mk_label notifications  7B1FA2 "Reminders and notifications"
mk_label report         FBCA04 "Technical report, diagrams, presentation"
mk_label testing        00796B "Test passes and QA"
mk_label bonus          BFD4F2 "Bonus-point features"
mk_label task           0E8A16 "Planned work"
mk_label bug            D73A4A "Something is broken"

echo "Creating milestones..."
gh api repos/:owner/:repo/milestones -f title="$MILESTONE" -f due_on="2026-11-26T23:59:00Z" >/dev/null 2>&1 || echo "  (milestone may already exist)"
gh api repos/:owner/:repo/milestones -f title="$TESTING"   -f due_on="2026-11-24T23:59:00Z" >/dev/null 2>&1 || echo "  (milestone may already exist)"

mk_issue() {  # title, label, assignee, milestone, body
  gh issue create --title "$1" --label "$2" --label task --assignee "$3" \
    --milestone "$4" --body "$5" >/dev/null
  echo "  + $1  ->  $3"
}

echo "Creating issues..."

# ----- Ami: database -----
mk_issue "Room entities from the ER diagram"            database "$AMI" "$MILESTONE" "User, Restaurant, FoodItem, Orders, OrderItem, Reminder (and Delivery if used). Share entity and DAO method names early so others can code against them."
mk_issue "DAOs and repositories"                        database "$AMI" "$MILESTONE" "Queries for auth, restaurant search, food filters, orders, reminders. Run on a background thread."
mk_issue "Seed data for the demo"                       database "$AMI" "$MILESTONE" "Sample restaurants, food items and one manager and one customer account."
mk_issue "Place-order, reject and cancel transactions"  database "$AMI" "$MILESTONE" "One transaction: insert order and items, reduce stock. Restore stock on reject or cancel."
mk_issue "Class diagram and ER diagram for the report"  report   "$AMI" "$TESTING"   "Final versions for the technical report."

# ----- Jenu: customer -----
mk_issue "App theme, colours and shared styles"         customer "$JENU" "$MILESTONE" "colors.xml, themes.xml, button and chip styles, shared adapters."
mk_issue "Customer Home and restaurant search"          customer "$JENU" "$MILESTONE" "Search by city or location, restaurant cards."
mk_issue "Restaurant Details and food list"             customer "$JENU" "$MILESTONE" "Discounted items, search and filters (name, category, price), quantity."
mk_issue "Checkout screen"                              customer "$JENU" "$MILESTONE" "Pickup or Delivery, total price, confirm order."
mk_issue "Customer Order Details and tracking"          customer "$JENU" "$MILESTONE" "Status, reminders, cancel order when allowed."
mk_issue "Customer Order History"                       customer "$JENU" "$MILESTONE" "Past orders list with status."

# ----- Shalom: manager -----
mk_issue "Manager Food List"                            manager "$SHALOM" "$MILESTONE" "Search and filter by name, category and date. Tap to edit or remove."
mk_issue "Add and Edit Food Item"                       manager "$SHALOM" "$MILESTONE" "Name, description, category, prices, quantity, date and time, ingredients and allergens. Validate discounted < original."
mk_issue "Manager Orders List"                          manager "$SHALOM" "$MILESTONE" "Status filter tabs: All, Pending, Accepted, Ready."
mk_issue "Manager Order Details: accept, reject, update status" manager "$SHALOM" "$MILESTONE" "Follow the status transitions from the sequence diagrams. Restore stock on reject."
mk_issue "Send reminder to customer"                    manager "$SHALOM" "$MILESTONE" "Separate screen or dialog. Saved in the Reminder table."
mk_issue "Manager Order History"                        manager "$SHALOM" "$MILESTONE" "Completed, rejected and cancelled orders."

# ----- Gabriel: auth and notifications -----
mk_issue "Welcome screen"                               auth "$GABRIEL" "$MILESTONE" "Login and Register buttons."
mk_issue "Register screen"                              auth "$GABRIEL" "$MILESTONE" "Role selector, name, email, password, address, optional photo."
mk_issue "Login screen and role routing"                auth "$GABRIEL" "$MILESTONE" "Validate, check the database, route managers and customers to their home screens."
mk_issue "Profile screen and logout"                    auth "$GABRIEL" "$MILESTONE" "View and edit profile, photo, logout."
mk_issue "Session manager and bottom navigation"        auth "$GABRIEL" "$MILESTONE" "Keep the user logged in, role-based navigation."
mk_issue "Reminder notifications on the customer side"  notifications "$GABRIEL" "$MILESTONE" "Show reminders as local notifications. Cite any library or API not covered in class."
mk_issue "Password recovery (bonus)"                    bonus "$GABRIEL" "$MILESTONE" "Email or security-question reset. Needs a citation."

# ----- Everyone -----
mk_issue "Cross-testing round (test each other's screens)" testing "$SHALOM" "$TESTING" "Everyone tests another person's screens and files bugs with the bug template."
mk_issue "Technical report: individual sections"        report "$SHALOM" "$TESTING" "Each member writes the work they completed and the part they were responsible for, with screenshots."
mk_issue "Technical report: merge and PDF"              report "$SHALOM" "$TESTING" "Features, use case, sequence, class and ER diagrams, citations. Export as PDF."
mk_issue "Presentation and demo rehearsal"              report "$SHALOM" "$MILESTONE" "15 to 20 minutes including questions. Everyone presents their part."
mk_issue "Prepare the G2-CSIS3175 submission folder"    report "$SHALOM" "$MILESTONE" "Android Studio project plus PDF report. Upload to Blackboard."

echo "Done. Create the Kanban project in the Projects tab and add these issues to it."
