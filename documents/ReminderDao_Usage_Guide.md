\# ReminderDao Usage Guide



\## Overview



`ReminderDao` provides methods to create, retrieve, and update reminders associated with orders.



It supports:

\- Adding a reminder.

\- Retrieving reminders for a specific order.

\- Marking a reminder as sent.

\- Retrieving all unsent reminders.

\- Retrieving reminders that are due to be sent.



\## Available Methods



\### 1. Add a Reminder



```java

long addReminder(Reminder reminder);

```



Inserts a new reminder into the database.



\*\*Parameters:\*\*

\- `reminder`: The Reminder object to insert.



\*\*Returns:\*\*

\- The generated reminder ID.



\*\*Example:\*\*



```java

Reminder reminder = new Reminder();

reminder.setOrderId(orderId);

// Set other reminder fields



long reminderId = reminderDao.addReminder(reminder);

```



\### 2. Get Reminders by Order ID



```java

List<Reminder> getRemindersByOrderId(int orderId);

```



Retrieves all reminders associated with a specific order.



\*\*Parameters:\*\*

\- `orderId`: The ID of the order.



\*\*Returns:\*\*

\- A list of Reminder objects associated with the specified order.



\*\*Example:\*\*



```java

List<Reminder> reminders =

&#x20;   reminderDao.getRemindersByOrderId(orderId);

```



\### 3. Mark a Reminder as Sent



```java

void markReminderAsSent(int reminderId);

```



Updates the reminder's `is\_sent` field to `1`, indicating that the reminder has been sent.



\*\*Parameters:\*\*

\- `reminderId`: The ID of the reminder to update.



\*\*Example:\*\*



```java

reminderDao.markReminderAsSent(reminderId);

```



\### 4. Get Unsent Reminders



```java

List<Reminder> getUnsentReminders();

```



Retrieves all reminders that have not been sent yet (`is\_sent = 0`).



\*\*Returns:\*\*

\- A list of unsent Reminder objects.



\*\*Example:\*\*



```java

List<Reminder> reminders =

&#x20;   reminderDao.getUnsentReminders();

```



\### 5. Get Due Reminders



```java

List<Reminder> getDueReminders(String currentDateTime);

```



Retrieves reminders that:

\- Have not been sent (`is\_sent = 0`).

\- Have a `reminder\_datetime` less than or equal to the current date and time.



\*\*Parameters:\*\*

\- `currentDateTime`: The current date and time, using the same format as `reminder\_datetime`.



\*\*Returns:\*\*

\- A list of reminders that are due to be sent.



\*\*Example:\*\*



```java

String currentDateTime = getCurrentDateTime();



List<Reminder> dueReminders =

&#x20;   reminderDao.getDueReminders(currentDateTime);

```



\## Typical Usage Flow



1\. Create a reminder when an order requires a notification.

2\. Save the reminder using `addReminder()`.

3\. Retrieve reminders that are due using `getDueReminders()`.

4\. Send the notification through the appropriate notification mechanism.

5\. After successfully sending the notification, call `markReminderAsSent()`.



\## Important Notes



\- `ReminderDao` only manages reminder data in the database. It does not send notifications itself.

\- The application must implement the actual notification mechanism separately.

\- Call `markReminderAsSent()` only after the notification has been successfully sent.

\- `getUnsentReminders()` retrieves all unsent reminders, including reminders scheduled for a future date.

\- `getDueReminders()` retrieves only unsent reminders whose scheduled date and time have arrived.

\- Database operations should be performed on a background thread, not the main UI thread.

\- The date/time format used for `currentDateTime` must be consistent with the format stored in `reminder\_datetime` for the comparison to work correctly.

