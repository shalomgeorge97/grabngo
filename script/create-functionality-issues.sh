#!/usr/bin/env bash
# Creates the "[Func]" (functionality) issues for GrabNGo.
# It does NOT edit any existing issue; update your UI issues by hand.
# Each new issue says "Pairs with UI issue #N" so GitHub links the two. The UI issue is
# found by its original title or by its "[UI] ..." title, whichever exists.
# Safe to re-run: issues that already exist are skipped.
# Requirements: GitHub CLI logged in (gh auth login). Run from inside the cloned repo:
#     bash scripts/create-functionality-issues.sh
set -e

# ---- EDIT THESE: GitHub usernames, no "@" (same as in setup-github.sh) ----
SHALOM="shalomgeorge97@gmail.com"
AMI="ami0610-hub"
JENU="jenumoljoseph"
GABRIEL="gabriel-ordoyo"
# ----------------------------------------------------------------------------

MILESTONE="GrabNGo Functionality"
LIST="$(mktemp)"

gh auth status >/dev/null

b() { printf '%b' "$1"; }

refresh_list() {
  gh issue list --state all --limit 500 --json number,title \
    --jq '.[] | "\(.number)\t\(.title)"' > "$LIST"
}

find_num() {   # exact title -> issue number (empty if none)
  awk -F'\t' -v t="$1" '$2==t {print $1; exit}' "$LIST"
}

func_issue() { # title area assignee ui_new_title ui_old_title body
  local title="$1" area="$2" who="$3" ui_new="$4" ui_old="$5" body="$6" un ref=""
  if [ -n "$(find_num "$title")" ]; then echo "  = exists: $title"; return 0; fi
  if [ -n "$ui_new" ]; then
    un="$(find_num "$ui_new")"
    [ -z "$un" ] && un="$(find_num "$ui_old")"
    [ -n "$un" ] && ref="\n\nPairs with UI issue #$un. Depends on the repositories and DAOs from the database issues."
  fi
  local full
  full="$(b "**Functionality only.** Connect the finished screen to the data layer. Run database work off the main thread, handle errors and empty states, and test with the seed data.\n\n$body$ref")"
  if gh issue create --title "$title" --label functionality --label "$area" --label task \
        --assignee "$who" --milestone "$MILESTONE" --body "$full" >/dev/null 2>&1; then
    echo "  + $title  ->  $who"
  else
    gh issue create --title "$title" --label functionality --label "$area" --label task \
        --milestone "$MILESTONE" --body "$full" >/dev/null
    echo "  + $title  (could not assign $who, created unassigned)"
  fi
}

echo "Creating labels..."
gh label create ui            --color 5319E7 --description "Layout and screens (no logic)" --force >/dev/null
gh label create functionality --color 0052CC --description "Logic and data (no layout work)" --force >/dev/null

refresh_list

echo "Relabeling Ami's existing database issues as functionality too..."
# Her entities, DAOs, seed data and transactions ARE the functionality layer
# underneath everyone else's screens; they just predate the [Func] split.
for t in "Room entities from the ER diagram" "DAOs and repositories" \
         "Seed data for the demo" "Place-order, reject and cancel transactions"; do
  n="$(find_num "$t")"
  if [ -n "$n" ]; then
    gh issue edit "$n" --add-label functionality >/dev/null 2>&1 \
      && echo "  ~ #$n  +functionality label: $t" \
      || echo "  ! could not label #$n (check it allows the functionality label)"
  else
    echo "  ! not found (check the title): $t"
  fi
done

echo "Creating functionality issues..."
# ----- Ami: repository layer (new - unblocks everyone else's [Func] issues) -----
func_issue "[Func] Repository layer (shared)" database "$AMI" "" "" \
"- [ ] AuthRepository: register, login, get/update profile\n- [ ] RestaurantRepository: search by city, get food items with filters\n- [ ] OrderRepository: calculateTotal, placeOrder (transaction), updateStatus, cancel\n- [ ] ReminderRepository: save and fetch reminders\n- [ ] Every method runs off the main thread (background executor or LiveData)\n- [ ] Documentation - Share the method names and signatures with the team as soon as they're decided; everyone else's [Func] issues depend on this"

# ----- Shalom: shared rules first, then manager side -----
func_issue "[Func] OrderStatus constants and transition rules (shared)" manager "$SHALOM" "" "" \
"- [ ] OrderStatus class with all statuses (Pending, Accepted, Rejected, Ready, Picked Up, Delivered, Cancelled)\n- [ ] isAllowedTransition(from, to), including Picked Up for pickup and Delivered for delivery orders\n- [ ] Used by manager actions and customer cancel\n- [ ] Agree the names with the whole team before coding"
func_issue "[Func] Manager food list: load, search, filter, remove" manager "$SHALOM" "[UI] Manager Food List screen" "Manager Food List" \
"- [ ] Load all food items of the manager's restaurant (including sold out)\n- [ ] Search by name, filter by category and date\n- [ ] Remove an item with a confirmation dialog"
func_issue "[Func] Add and edit food item: validate and save" manager "$SHALOM" "[UI] Add and Edit Food Item screen" "Add and Edit Food Item" \
"- [ ] Required fields; discounted price below original; quantity 0 or more; valid date and time\n- [ ] Insert a new item or update an existing one (pre-fill in edit mode)\n- [ ] Delete in edit mode"
func_issue "[Func] Manager orders list: load and filter by status" manager "$SHALOM" "[UI] Manager Orders List screen" "Manager Orders List" \
"- [ ] Load orders for the manager's restaurant, newest first\n- [ ] Filter by status tab"
func_issue "[Func] Manager order actions: accept, reject, update status" manager "$SHALOM" "[UI] Manager Order Details screen" "Manager Order Details: accept, reject, update status" \
"- [ ] Accept and Reject only while Pending\n- [ ] Reject restores the stock\n- [ ] Allowed status transitions only (use the shared OrderStatus rules)\n- [ ] Save to the database and refresh the screen"
func_issue "[Func] Send reminder: validate, save and notify" manager "$SHALOM" "[UI] Send reminder screen" "Send reminder to customer" \
"- [ ] Only for Accepted or Ready orders\n- [ ] Save to the Reminder table\n- [ ] Trigger the customer notification (agree the hand-off with Gabriel)"
# func_issue "[Func] Manager order history: load completed orders" manager "$SHALOM" "[UI] Manager Order History screen" "Manager Order History" \
# "- [ ] Load completed, rejected and cancelled orders for the restaurant\n- [ ] Open Order Details from a card"

# ----- Jenu: customer side -----
func_issue "[Func] Load and search restaurants" customer "$JENU" "[UI] Customer Home and restaurant search screen" "Customer Home and restaurant search" \
"- [ ] Load restaurants from the repository\n- [ ] Search by city or location\n- [ ] Show the number of available items per restaurant\n- [ ] Empty and error states"
func_issue "[Func] Restaurant food list, search and filters" customer "$JENU" "[UI] Restaurant Details and food list screen" "Restaurant Details and food list" \
"- [ ] Load only available items (quantity above 0, within the available time)\n- [ ] Search by name; filter by category and price\n- [ ] Keep selected items and quantities in a cart\n- [ ] Do not allow more than the quantity left"
func_issue "[Func] Place order from checkout" customer "$JENU" "[UI] Checkout screen" "Checkout screen" \
"- [ ] Calculate the total from the cart\n- [ ] Require a delivery address for Delivery\n- [ ] Call the place-order transaction (status Pending, stock reduced)\n- [ ] Block double taps\n- [ ] Handle stock that changed since the item was added"
func_issue "[Func] Customer order details and cancel" customer "$JENU" "[UI] Customer Order Details and tracking screen" "Customer Order Details and tracking" \
"- [ ] Load the order, its items, status and reminders\n- [ ] Refresh the status when the screen opens\n- [ ] Cancel only while Pending, and restore the stock\n- [ ] Confirmation dialog before cancelling"
func_issue "[Func] Customer order history and active orders" customer "$JENU" "[UI] Customer Order History screen" "Customer Order History" \
"- [ ] Load the logged-in customer's orders\n- [ ] Show active orders (so tracking is reachable after checkout) and past orders\n- [ ] Open Order Details from a card"

# ----- Gabriel: auth, session, notifications -----
func_issue "[Func] Register: validate and save user" auth "$GABRIEL" "[UI] Register screen" "Register screen" \
"- [ ] Validate name, email format, password length and address\n- [ ] Unique email check\n- [ ] Hash the password (never store plain text)\n- [ ] Save the optional photo to app storage and keep its path\n- [ ] Create a restaurant record for new managers (decide which fields are asked)"
func_issue "[Func] Login: authenticate and route by role" auth "$GABRIEL" "[UI] Login screen" "Login screen and role routing" \
"- [ ] Validate input\n- [ ] Look up the user and compare the password hash\n- [ ] Show 'Invalid email or password' on failure\n- [ ] Save the session and open the manager or customer home"
func_issue "[Func] Session manager and logout" auth "$GABRIEL" "[UI] Bottom navigation" "Session manager and bottom navigation" \
"- [ ] Save user id and role in SharedPreferences\n- [ ] Skip the Welcome screen if already logged in\n- [ ] Logout clears the session and returns to Welcome\n- [ ] Role-based bottom navigation menu"
func_issue "[Func] Profile: load, edit and change photo" auth "$GABRIEL" "[UI] Profile screen" "Profile screen and logout" \
"- [ ] Load the logged-in user's details\n- [ ] Validate and save changes (email must stay unique)\n- [ ] Change the profile photo"
func_issue "[Func] Reminder notifications: schedule and show" notifications "$GABRIEL" "[UI] Reminder notification design and in-app reminder display" "Reminder notifications on the customer side" \
"- [ ] Notification channel and the Android 13+ notification permission\n- [ ] Check for new reminders (WorkManager or a timer)\n- [ ] Show a local notification; tapping it opens the order\n- [ ] Cite any library or API not covered in class"
func_issue "[Func] Password recovery logic (bonus)" bonus "$GABRIEL" "[UI] Forgot and reset password screens (bonus)" "Password recovery (bonus)" \
"- [ ] Email or security-question reset flow\n- [ ] The new password is hashed\n- [ ] Cite any library or service used"

rm -f "$LIST"
echo "Done. If you use the Project board, add the new issues to it (or turn on auto-add)."
