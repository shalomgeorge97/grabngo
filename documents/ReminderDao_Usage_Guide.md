# ReminderDao -- Usage Guide

## 1. Overview

`ReminderDao` provides database operations for saving reminders,
creating status-specific reminder messages, retrieving reminders for
customers, and managing read status.

It supports the following operations:

  ------------------------------------------------------------------------
  Method                               Description
  ------------------------------------ -----------------------------------
  `addReminder()`                      Insert a reminder into the database
                                       and return its generated ID.

  `createStatusReminder()`             Create and save a predefined
                                       message based on an order status.

  `getRemindersByOrderId()`            Retrieve all reminders associated
                                       with a specific order.

  `getLatestRemindersByCustomerId()`   Retrieve only the newest reminder
                                       for each order belonging to a
                                       customer.

  `hasUnreadLatestReminder()`          Check whether the customer has any
                                       unread latest reminders.

  `markCustomerRemindersAsRead()`      Mark all reminders belonging to the
                                       customer's orders as read.
  ------------------------------------------------------------------------

The DAO uses the `REMINDER` table and joins it with the `ORDERS` table
when retrieving reminders for a customer.

------------------------------------------------------------------------

## 2. Add a Reminder

### Method

``` java
long addReminder(Reminder reminder);
```

### Purpose

Inserts a `Reminder` record into the database.

### Example

``` java
Reminder reminder = new Reminder(
        orderId,
        status,
        message,
        dateTime
);

long reminderId = reminderDao.addReminder(reminder);
```

### Return Value

Returns the automatically generated ID of the newly inserted reminder.

### When to Use

Use this method when a `Reminder` object has already been created and
needs to be saved.

For status-based reminders, `createStatusReminder()` calls this method
after generating the message and timestamp.

------------------------------------------------------------------------

## 3. Create a Status Reminder

### Method

``` java
default void createStatusReminder(int orderId, String status);
```

### Purpose

Creates and saves a reminder for an order using a predefined message
that corresponds to the supplied order status.

The method selects a message based on the status:

  Order Status       Reminder Message
  ------------------ ------------------------------------------
  `ACCEPTED`         `Your order has been accepted.`
  `REJECTED`         `Sorry, your order has been rejected.`
  `READY`            `Your order is ready.`
  `PICKED_UP`        `Your order has been picked up.`
  `DELIVERED`        `Your order has been delivered.`
  `CANCELLED`        `Your order has been cancelled.`
  Any other status   `Your order status changed to [status].`

### Example

``` java
reminderDao.createStatusReminder(
        orderId,
        OrderStatus.ACCEPTED
);
```

This creates a reminder with the order ID, status, generated message,
and current date and time.

### Timestamp

The method generates the timestamp using:

``` java
String dateTime = new SimpleDateFormat(
        "yyyy-MM-dd HH:mm:ss",
        Locale.getDefault()
).format(new Date());
```

The timestamp is stored as a formatted string in the `Reminder` object.

### When to Use

Use this method when an order status update should generate a
customer-facing reminder.

**Important:** This method creates and saves the reminder, but it does
not update the order status itself. The caller is responsible for
ensuring that the order status has been updated successfully before
creating the reminder.

------------------------------------------------------------------------

## 4. Get Reminders by Order ID

### Method

``` java
List<Reminder> getRemindersByOrderId(int orderId);
```

### Purpose

Retrieves all reminder records associated with a specific order.

### Example

``` java
List<Reminder> reminders =
        reminderDao.getRemindersByOrderId(orderId);

if (reminders.isEmpty()) {
    // No reminders exist for this order
} else {
    for (Reminder reminder : reminders) {
        String type = reminder.getType();
        String message = reminder.getMessage();
        String time = reminder.getTime();
    }
}
```

### When to Use

Use this method when all reminder records for a particular order are
needed, for example to display the reminder history for that order.

**Note:** The current query does not specify an ordering rule, so the
result order is not guaranteed.

------------------------------------------------------------------------

## 5. Get the Latest Reminder for Each Order by Customer ID

### Method

``` java
List<Reminder> getLatestRemindersByCustomerId(int customerId);
```

### Purpose

Retrieves only the latest reminder for each order belonging to a
specific customer.

The query:

1.  Groups reminder records by `order_id`.
2.  Finds the maximum `reminder_id` for each order.
3.  Retrieves the corresponding reminder records.
4.  Joins `ORDERS` to identify which reminders belong to the specified
    customer.
5.  Sorts the resulting reminders by `reminder_id` in descending order.

This means that if an order has multiple status updates, only the
reminder with the highest reminder ID is returned for that order.

### Example

``` java
List<Reminder> reminders =
        reminderDao.getLatestRemindersByCustomerId(customerId);

if (reminders.isEmpty()) {
    // Display an empty state
} else {
    for (Reminder reminder : reminders) {
        int orderId = reminder.getOrderId();
        String message = reminder.getMessage();
        String time = reminder.getTime();
    }
}
```

### When to Use

Use this method to populate the Customer Reminder screen when the
customer should see only the latest update for each order.

### Important Note

The query uses `MAX(reminder_id)` as the definition of the latest
reminder. This relies on reminder IDs increasing as new records are
inserted.

The query does not use the `time` column to determine which reminder is
latest.

------------------------------------------------------------------------

## 6. Check for Unread Latest Reminders

### Method

``` java
boolean hasUnreadLatestReminder(int customerId);
```

### Purpose

Checks whether a customer has at least one unread latest reminder across
their orders.

The query:

1.  Finds the latest reminder ID for each order.
2.  Retrieves those latest reminder records.
3.  Joins with `ORDERS` to filter by customer ID.
4.  Checks whether any of those latest reminders have `is_read = 0`.

### Example

``` java
boolean hasUnread = reminderDao.hasUnreadLatestReminder(customerId);

if (hasUnread) {
    // Display the unread notification bell icon
} else {
    // Display the regular notification bell icon
}
```

### Return Value

-   `true`: At least one latest reminder belonging to the customer is
    unread.
-   `false`: No latest reminder belonging to the customer is unread.

### When to Use

Use this method to determine which notification bell icon should be
displayed on the Customer Home screen.

**Note:** This method checks only the latest reminder for each order. An
older unread reminder does not cause the method to return `true` if a
newer reminder for that same order is already marked as read.

------------------------------------------------------------------------

## 7. Mark Customer Reminders as Read

### Method

``` java
void markCustomerRemindersAsRead(int customerId);
```

### Purpose

Marks all reminder records associated with the specified customer's
orders as read by setting `is_read` to `1`.

The query identifies the customer's orders through the `ORDERS` table
and updates every reminder whose `order_id` belongs to one of those
orders.

### Example

``` java
reminderDao.markCustomerRemindersAsRead(customerId);
```

### When to Use

Use this method when the customer opens the Reminder screen.

### Important Note

This method marks **all reminders** belonging to the customer's orders
as read, not only the latest reminder displayed on the screen.

------------------------------------------------------------------------

## 8. Reminder Data and Read Status

The current DAO uses the following reminder information when creating a
status reminder:

  -----------------------------------------------------------------------
  Information                         Source
  ----------------------------------- -----------------------------------
  Order ID                            `orderId` method parameter

  Reminder type                       `status` method parameter

  Message                             Predefined message selected from
                                      the status

  Date and time                       Current date and time formatted as
                                      `yyyy-MM-dd HH:mm:ss`
  -----------------------------------------------------------------------

The `is_read` field is used by the unread-check and mark-as-read
queries.

The current DAO does not actively use an `is_sent` field. The previously
considered methods for marking reminders as sent and retrieving unsent
or due reminders are commented out.

------------------------------------------------------------------------

## 9. Important Implementation Notes

### Reminder Creation and Order Status Updates

`createStatusReminder()` only creates a reminder record. It does not
change the corresponding order status.

The application should call it after confirming that the order status
update succeeded.

For example, the application logic should follow this general sequence:

``` java
int rowsUpdated = orderDao.updateOrderStatus(orderId, newStatus);

if (rowsUpdated > 0) {
    reminderDao.createStatusReminder(orderId, newStatus);
}
```

This is an illustrative sequence. If the status update and reminder
insertion must be guaranteed to succeed or fail together, they should be
implemented within an appropriate Room transaction.

### Latest Reminder Selection

The latest-reminder queries use the largest `reminder_id` for each
order.

This works as intended when reminder IDs are generated in increasing
order as records are inserted.

### Customer Filtering

The customer reminder queries join `REMINDER` with `ORDERS` using
`order_id`.

This allows the DAO to retrieve reminders for orders belonging to a
particular customer without storing a separate customer ID in each
reminder record.

### Read Status

-   `is_read = 0`: Unread.
-   `is_read = 1`: Read.

`hasUnreadLatestReminder()` checks the read status of only the latest
reminder for each order.

`markCustomerRemindersAsRead()` marks all reminders for the customer's
orders as read.

### Threading

The DAO methods execute synchronously. The current `AppDatabase`
configuration uses `.allowMainThreadQueries()`, which permits database
calls on the main UI thread in this project.

However, database operations can block the UI. For a production
application, database operations should generally run on a background
thread.

### Related DAOs

-   Use `OrderDao` to retrieve and update order records.
-   Use `OrderTransactionDao` for transactional order placement,
    rejection, cancellation, and stock-management operations.
-   Use `ReminderDao` to create and retrieve customer-facing reminder
    records and manage their read status.
