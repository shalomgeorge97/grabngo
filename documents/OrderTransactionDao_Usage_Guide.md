# OrderTransactionDao -- Usage Guide

## 1. Overview

`OrderTransactionDao` provides database operations for placing orders,
managing order statuses, and maintaining food stock quantities.

It uses Room's `@Transaction` annotation for multi-step operations that
must succeed or fail as a single unit.

It supports the following operations:

  -----------------------------------------------------------------------
  Method                              Description
  ----------------------------------- -----------------------------------
  `addOrder()`                        Insert a new order and return its
                                      generated ID.

  `addOrderItems()`                   Insert the items associated with an
                                      order.

  `setOrderStatus()`                  Update an order's status without
                                      checking its current status.

  `getOrderById()`                    Retrieve an order by its ID.

  `decreaseAvailableQuantity()`       Decrease the available stock of a
                                      food item if enough stock exists.

  `updatePendingOrderStatus()`        Update an order's status only when
                                      its current status is `PENDING`.

  `getOrderItemsByOrderId()`          Retrieve all items belonging to a
                                      specific order.

  `increaseAvailableQuantity()`       Restore or increase the available
                                      stock of a food item.

  `placeOrder()`                      Insert an order and its items, then
                                      decrease the corresponding food
                                      stock in one transaction.

  `rejectOrder()`                     Reject a pending order and restore
                                      its food stock in one transaction.

  `cancelOrder()`                     Cancel a pending order and restore
                                      its food stock in one transaction.
  -----------------------------------------------------------------------

------------------------------------------------------------------------

## 2. Insert a New Order

### Method

``` java
long addOrder(Order order);
```

### Purpose

Inserts a new order into the `ORDERS` table.

### Example

``` java
long orderId = orderTransactionDao.addOrder(order);
```

### Return Value

Returns the automatically generated ID of the newly inserted order.

This ID can be assigned to the related `OrderItem` objects before they
are inserted.

### When to Use

This method is used internally by `placeOrder()` to create the order
record before inserting its order items.

------------------------------------------------------------------------

## 3. Insert Order Items

### Method

``` java
void addOrderItems(List<OrderItem> items);
```

### Purpose

Inserts a list of order items into the `ORDER_ITEM` table.

Each item must be associated with the correct order ID before insertion.

### Example

``` java
for (OrderItem item : items) {
    item.setOrderId((int) orderId);
}

orderTransactionDao.addOrderItems(items);
```

### When to Use

This method is used by `placeOrder()` after the new order has been
inserted and its generated ID has been assigned to each order item.

------------------------------------------------------------------------

## 4. Set an Order Status

### Method

``` java
int setOrderStatus(int orderId, String newStatus);
```

### Purpose

Updates the status of an order identified by its order ID.

The query does not check the order's current status before updating it.

### Example

``` java
int rowsUpdated = orderTransactionDao.setOrderStatus(
        orderId,
        OrderStatus.ACCEPTED
);
```

### Return Value

Returns the number of rows affected:

-   `1`: The order was updated.
-   `0`: No order with the specified ID was found.

### When to Use

Use this method when an order status needs to be updated without the
pending-only condition.

**Note:** This method is a direct status update. It does not validate
whether the requested status transition is allowed and does not create a
Reminder record.

------------------------------------------------------------------------

## 5. Get an Order by ID

### Method

``` java
Order getOrderById(int orderId);
```

### Purpose

Retrieves an order from the `ORDERS` table using its order ID.

### Example

``` java
Order order = orderTransactionDao.getOrderById(orderId);

if (order != null) {
    String currentStatus = order.getStatus();
} else {
    // Handle the case where the order does not exist
}
```

### Return Value

Returns the matching `Order` object, or `null` if no matching order is
found.

### When to Use

Use this method when the current order information is needed before
performing another operation.

------------------------------------------------------------------------

## 6. Decrease Available Food Quantity

### Method

``` java
int decreaseAvailableQuantity(int foodId, int quantity);
```

### Purpose

Decreases the available quantity of a food item in the `FOOD_ITEM`
table.

The update is performed only when:

-   The specified food item exists.
-   The requested quantity is greater than zero.
-   The current available quantity is greater than or equal to the
    requested quantity.

### Example

``` java
int result = orderTransactionDao.decreaseAvailableQuantity(
        foodId,
        quantity
);

if (result > 0) {
    // Stock was decreased successfully
} else {
    // The item was not found or there was insufficient stock
}
```

### Return Value

Returns the number of rows updated:

-   `1`: The stock quantity was decreased.
-   `0`: The update conditions were not satisfied.

### When to Use

This method is used by `placeOrder()` to decrease stock for each food
item included in a newly placed order.

------------------------------------------------------------------------

## 7. Update Status Only if the Order Is Pending

### Method

``` java
int updatePendingOrderStatus(int orderId, String newStatus);
```

### Purpose

Updates an order's status only if its current status is
`OrderStatus.PENDING`.

The query checks both the order ID and the current status before
performing the update.

### Example

``` java
int result = orderTransactionDao.updatePendingOrderStatus(
        orderId,
        OrderStatus.REJECTED
);

if (result > 0) {
    // The pending order status was updated
} else {
    // The order was not found or was no longer pending
}
```

### Return Value

Returns the number of rows updated:

-   `1`: The order existed and was still pending, so its status was
    updated.
-   `0`: No matching pending order was found.

### When to Use

This method is used by `rejectOrder()` and `cancelOrder()` to ensure
that only pending orders can be rejected or cancelled through these
methods.

------------------------------------------------------------------------

## 8. Get Order Items by Order ID

### Method

``` java
List<OrderItem> getOrderItemsByOrderId(int orderId);
```

### Purpose

Retrieves all records from `ORDER_ITEM` that belong to the specified
order.

### Example

``` java
List<OrderItem> items =
        orderTransactionDao.getOrderItemsByOrderId(orderId);

for (OrderItem item : items) {
    int foodId = item.getFoodId();
    int quantity = item.getQuantity();
}
```

### When to Use

This method is used by `rejectOrder()` and `cancelOrder()` to find the
food items and quantities that need to be returned to stock.

------------------------------------------------------------------------

## 9. Increase Available Food Quantity

### Method

``` java
int increaseAvailableQuantity(int foodId, int quantity);
```

### Purpose

Increases the available quantity of a food item in the `FOOD_ITEM`
table.

The update is performed only when the specified food item exists and the
quantity is greater than zero.

### Example

``` java
int result = orderTransactionDao.increaseAvailableQuantity(
        foodId,
        quantity
);

if (result > 0) {
    // Stock was restored successfully
} else {
    // The food item was not found or the quantity was invalid
}
```

### Return Value

Returns the number of rows updated:

-   `1`: The stock quantity was increased.
-   `0`: The update conditions were not satisfied.

### When to Use

This method is used by `rejectOrder()` and `cancelOrder()` to restore
the stock associated with an order.

------------------------------------------------------------------------

## 10. Place an Order

### Method

``` java
@Transaction
default long placeOrder(Order order, List<OrderItem> items);
```

### Purpose

Places a new order and updates food stock as one database transaction.

The method performs the following steps:

1.  Inserts the order into the `ORDERS` table.
2.  Gets the generated order ID.
3.  Assigns that order ID to every `OrderItem`.
4.  Inserts the order items into the `ORDER_ITEM` table.
5.  Decreases the available stock for each food item.
6.  Returns the generated order ID.

### Example

``` java
long orderId = orderTransactionDao.placeOrder(order, items);
```

### Return Value

Returns the generated ID of the newly inserted order if all operations
succeed.

### When to Use

Use this method when a customer places an order containing one or more
food items.

### Transaction Behavior

The `@Transaction` annotation makes the operations execute as one
transaction.

If any stock update fails, the method throws an `IllegalStateException`
with the message:

``` text
Not enough food stock.
```

Because the operations are transactional, the order insertion,
order-item insertion, and stock changes are rolled back if the
transaction fails.

This prevents an order from being partially saved when the requested
stock is unavailable.

------------------------------------------------------------------------

## 11. Reject an Order

### Method

``` java
@Transaction
default boolean rejectOrder(int orderId);
```

### Purpose

Rejects an order only if its current status is `PENDING`, then restores
the stock of all food items associated with that order.

### Process

1.  Attempts to change the order status from `PENDING` to `REJECTED`.
2.  If no pending order is updated, returns `false`.
3.  Retrieves the order items associated with the order.
4.  Increases the available stock for each item.
5.  Returns `true` if all operations succeed.

### Example

``` java
boolean rejected = orderTransactionDao.rejectOrder(orderId);

if (rejected) {
    // The order was rejected and its stock was restored
} else {
    // The order was not found or was not pending
}
```

### Return Value

-   `true`: The order was pending, its status was changed to `REJECTED`,
    and its food stock was restored.
-   `false`: The order was not found or was no longer pending.

### Transaction Behavior

The method is annotated with `@Transaction`.

If restoring any food item's stock fails, the method throws an
`IllegalStateException` with the message:

``` text
Failed to restore food stock.
```

The transaction is rolled back so that the status change and stock
updates are not left partially applied.

------------------------------------------------------------------------

## 12. Cancel an Order

### Method

``` java
@Transaction
default boolean cancelOrder(int orderId);
```

### Purpose

Cancels an order only if its current status is `PENDING`, then restores
the stock of all food items associated with that order.

### Process

1.  Attempts to change the order status from `PENDING` to `CANCELLED`.
2.  If no pending order is updated, returns `false`.
3.  Retrieves the order items associated with the order.
4.  Increases the available stock for each item.
5.  Returns `true` if all operations succeed.

### Example

``` java
boolean cancelled = orderTransactionDao.cancelOrder(orderId);

if (cancelled) {
    // The order was cancelled and its stock was restored
} else {
    // The order was not found or was not pending
}
```

### Return Value

-   `true`: The order was pending, its status was changed to
    `CANCELLED`, and its food stock was restored.
-   `false`: The order was not found or was no longer pending.

### Transaction Behavior

The method is annotated with `@Transaction`.

If restoring any food item's stock fails, the method throws an
`IllegalStateException` with the message:

``` text
Failed to restore food stock.
```

The transaction is rolled back so that the status change and stock
updates are not left partially applied.

------------------------------------------------------------------------

## 13. Important Implementation Notes

### Why Use Transactions?

A transaction groups multiple database operations into one unit of work.

For the methods in this DAO, this is important because an order and its
related stock changes must remain consistent.

For example, when placing an order:

-   The order should not be saved if its items cannot be inserted.
-   The order should not be saved if the requested food stock is
    unavailable.
-   Stock should not be decreased without the corresponding order being
    successfully placed.

Similarly, when rejecting or cancelling an order, the status change and
stock restoration should succeed together.

### Difference Between Status Update Methods

This DAO contains two direct status update methods with different
conditions:

  -----------------------------------------------------------------------
  Method                              Condition
  ----------------------------------- -----------------------------------
  `setOrderStatus()`                  Updates the order by ID without
                                      checking its current status.

  `updatePendingOrderStatus()`        Updates the order only when its
                                      current status is `PENDING`.
  -----------------------------------------------------------------------

Choose the method according to the operation's requirements.

### Reminder Integration

The current active methods in this DAO do not insert records into the
`Reminder` table.

If a status change needs to create a customer reminder, that reminder
insertion must be implemented separately or added to an appropriate
transaction method.

### Stock Quantity Checks

`decreaseAvailableQuantity()` prevents the available quantity from
becoming negative by checking that enough stock exists before decreasing
it.

`increaseAvailableQuantity()` only checks that the food item exists and
the quantity is greater than zero. It does not enforce a maximum stock
quantity.

### Threading

The DAO methods execute synchronously. The current `AppDatabase`
configuration uses `.allowMainThreadQueries()`, which permits database
calls on the main UI thread in this project.

However, database operations can block the UI. For a production
application, database work should generally run on a background thread.

### Related DAO

Use `OrderDao` for general order retrieval and updates.

Use `OrderItemDao` for other operations involving order items and their
food information.

`OrderTransactionDao` is specifically responsible for the multi-step
order placement, rejection, cancellation, and stock-management
operations described in this guide.
