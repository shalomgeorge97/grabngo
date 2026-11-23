# OrderDao -- Usage Guide

## 1. Overview

`OrderDao` provides database operations for creating, updating,
retrieving, and managing customer orders in the Room database.

It supports the following operations:

  -------------------------------------------------------------------------
  Method                                Description
  ------------------------------------- -----------------------------------
  `addOrder()`                          Insert a new order and return its
                                        generated ID.

  `updateOrder()`                       Update an existing order.

  `getOrdersByCustomerId()`             Retrieve all orders placed by a
                                        specific customer.

  `getActiveOrdersByCustomerId()`       Retrieve a customer's active
                                        orders.

  `getPastOrdersByCustomerId()`         Retrieve a customer's past orders.

  `getOrdersByRestaurantId()`           Retrieve all orders received by a
                                        specific restaurant.

  `getOrderById()`                      Retrieve a specific order by its
                                        ID.

  `getOrdersByStatus()`                 Retrieve a restaurant's orders with
                                        a specified status.

  `updateOrderStatus()`                 Update only the status of a
                                        specific order.

  `getCustomerOrdersByRestaurantId()`   Retrieve a restaurant's orders
                                        together with customer information.

  `getCustomerOrderById()`              Retrieve one order together with
                                        customer information.
  -------------------------------------------------------------------------

`OrderDao` handles records in the `ORDERS` table. It does not retrieve
individual food items for an order; use `OrderItemDao` for that.

------------------------------------------------------------------------

## 2. Create a New Order

### Method

``` java
long addOrder(Order order);
```

### Purpose

Inserts a new order into the database.

### Example

``` java
Order order = new Order(
        customerId,
        restaurantId,
        orderDate,
        fulfillmentType,
        OrderStatus.PENDING,
        totalAmount,
        deliveryAddress
);

long orderId = orderDao.addOrder(order);
```

### Return Value

Returns the row ID generated for the newly inserted order.

Use this ID when inserting related records into the `ORDER_ITEM` table.

------------------------------------------------------------------------

## 3. Update an Existing Order

### Method

``` java
void updateOrder(Order order);
```

### Purpose

Updates an existing order using the supplied `Order` object.

### Example

``` java
Order order = orderDao.getOrderById(orderId);

if (order != null) {
    order.setStatus(OrderStatus.ACCEPTED);
    orderDao.updateOrder(order);
}
```

### When to Use

Use this method when updating an existing order record, including
changes to one or more fields represented by the `Order` entity.

For a status-only update, `updateOrderStatus()` can be used instead.

------------------------------------------------------------------------

## 4. Get All Orders by Customer ID

### Method

``` java
List<Order> getOrdersByCustomerId(int customerId);
```

### Purpose

Retrieves all orders placed by a specific customer, regardless of their
status.

Orders are sorted by `order_id` in descending order, so higher order IDs
appear first.

### Example

``` java
List<Order> orders = orderDao.getOrdersByCustomerId(customerId);

if (orders.isEmpty()) {
    // Display an empty state
} else {
    for (Order order : orders) {
        int orderId = order.getOrderId();
        String status = order.getStatus();
        double totalAmount = order.getTotalAmount();
    }
}
```

### When to Use

Use this method when retrieving the complete order history for a
customer.

------------------------------------------------------------------------

## 5. Get Active Orders by Customer ID

### Method

``` java
List<Order> getActiveOrdersByCustomerId(int customerId);
```

### Purpose

Retrieves active orders for a specific customer.

The query considers the following statuses active:

-   `OrderStatus.PENDING`
-   `OrderStatus.ACCEPTED`
-   `OrderStatus.READY`

Results are sorted by `order_id` in descending order.

### Example

``` java
List<Order> activeOrders =
        orderDao.getActiveOrdersByCustomerId(customerId);

if (activeOrders.isEmpty()) {
    // Display an empty state
} else {
    for (Order order : activeOrders) {
        int orderId = order.getOrderId();
        String status = order.getStatus();
    }
}
```

### When to Use

Use this method to display active orders on the Customer Orders screen.

**Note:** The active statuses are determined by the SQL query in
`OrderDao`. If the application's status workflow changes, review this
query so the classification remains consistent.

------------------------------------------------------------------------

## 6. Get Past Orders by Customer ID

### Method

``` java
List<Order> getPastOrdersByCustomerId(int customerId);
```

### Purpose

Retrieves past orders for a specific customer.

The query considers the following statuses past orders:

-   `OrderStatus.PICKED_UP`
-   `OrderStatus.DELIVERED`
-   `OrderStatus.REJECTED`
-   `OrderStatus.CANCELLED`

Results are sorted by `order_id` in descending order.

### Example

``` java
List<Order> pastOrders =
        orderDao.getPastOrdersByCustomerId(customerId);

if (pastOrders.isEmpty()) {
    // Display an empty state
} else {
    for (Order order : pastOrders) {
        int orderId = order.getOrderId();
        String status = order.getStatus();
    }
}
```

### When to Use

Use this method to display past orders on the Customer Orders screen.

**Note:** The past statuses are determined by the SQL query in
`OrderDao`. If the application's status workflow changes, review this
query so the classification remains consistent.

------------------------------------------------------------------------

## 7. Get All Orders by Restaurant ID

### Method

``` java
List<Order> getOrdersByRestaurantId(int restaurantId);
```

### Purpose

Retrieves all orders received by a specific restaurant.

Orders are sorted by `order_id` in descending order.

### Example

``` java
List<Order> orders =
        orderDao.getOrdersByRestaurantId(restaurantId);

if (orders.isEmpty()) {
    // Display an empty state
} else {
    for (Order order : orders) {
        int orderId = order.getOrderId();
        int customerId = order.getCustomerId();
        String status = order.getStatus();
    }
}
```

### When to Use

Use this method when a restaurant manager needs to retrieve all orders
belonging to their restaurant.

------------------------------------------------------------------------

## 8. Get an Order by ID

### Method

``` java
Order getOrderById(int orderId);
```

### Purpose

Retrieves one specific order using its order ID.

### Example

``` java
Order order = orderDao.getOrderById(orderId);

if (order != null) {
    String status = order.getStatus();
    double totalAmount = order.getTotalAmount();
} else {
    // Handle the case where the order does not exist
}
```

### Return Value

Returns the matching `Order` object, or `null` if no matching order is
found.

### When to Use

Use this method when you need the basic order information for a specific
order, such as displaying order details, checking its current status, or
retrieving it before an update.

------------------------------------------------------------------------

## 9. Get Orders by Status

### Method

``` java
List<Order> getOrdersByStatus(int restaurantId, String status);
```

### Purpose

Retrieves orders belonging to a specific restaurant that have the
specified status.

### Parameters

  Parameter        Description
  ---------------- ------------------------------------------
  `restaurantId`   ID of the restaurant.
  `status`         Order status used to filter the results.

### Example

``` java
List<Order> pendingOrders =
        orderDao.getOrdersByStatus(restaurantId, OrderStatus.PENDING);

List<Order> acceptedOrders =
        orderDao.getOrdersByStatus(restaurantId, OrderStatus.ACCEPTED);
```

### When to Use

Use this method when a restaurant manager needs to filter orders by
status.

**Note:** The current query does not specify an ordering rule, so the
result order is not guaranteed.

------------------------------------------------------------------------

## 10. Update Order Status

### Method

``` java
int updateOrderStatus(int orderId, String status);
```

### Purpose

Updates the `status` field of a specific order without updating the
other order fields.

### Parameters

  Parameter   Description
  ----------- ------------------------------------
  `orderId`   ID of the order to update.
  `status`    New status to assign to the order.

### Example

``` java
int rowsUpdated = orderDao.updateOrderStatus(
        orderId,
        OrderStatus.ACCEPTED
);

if (rowsUpdated > 0) {
    // The order status was updated
} else {
    // No matching order was found
}
```

### Return Value

Returns the number of rows affected by the update:

-   `1`: One order was updated.
-   `0`: No order matched the supplied `orderId`.

### When to Use

Use this method when only the order status needs to change, for example
when accepting an order, marking it as ready, or marking it as
delivered.

**Important:** This query updates the status without checking the
order's current status or validating whether the transition is allowed.
Validate the transition in the application logic before calling this
method if the workflow requires specific status transitions.

**Reminder integration:** This method only updates the order status. It
does not create a Reminder record. Reminder creation must be handled
separately by the relevant application logic after a successful status
update.

------------------------------------------------------------------------

## 11. Get Customer and Order Information by Restaurant ID

### Method

``` java
List<CustomerOrder> getCustomerOrdersByRestaurantId(int restaurantId);
```

### Purpose

Retrieves order information together with customer information by
joining the `ORDERS` and `USER` tables.

The query returns:

-   Order ID
-   Customer ID
-   Customer name
-   Order date
-   Fulfillment type
-   Order status
-   Total amount
-   Delivery address

Orders are sorted by `order_id` in descending order.

### Example

``` java
List<CustomerOrder> orders =
        orderDao.getCustomerOrdersByRestaurantId(restaurantId);

if (orders.isEmpty()) {
    // Display an empty state
} else {
    for (CustomerOrder order : orders) {
        int orderId = order.orderId;
        int customerId = order.customerId;
        String customerName = order.customerName;
        String status = order.status;
        double totalAmount = order.totalAmount;
    }
}
```

### When to Use

Use this method for the Restaurant Manager's order list.

It allows the manager to see customer information with each order
without making a separate query for each customer.

------------------------------------------------------------------------

## 12. Get Customer and Order Information by Order ID

### Method

``` java
CustomerOrder getCustomerOrderById(int orderId, int restaurantId);
```

### Purpose

Retrieves customer and order information for one specific order.

The query joins the `ORDERS` and `USER` tables and filters by both order
ID and restaurant ID.

### Parameters

  Parameter        Description
  ---------------- -----------------------------------------------
  `orderId`        ID of the order.
  `restaurantId`   ID of the restaurant that received the order.

### Example

``` java
CustomerOrder order =
        orderDao.getCustomerOrderById(orderId, restaurantId);

if (order != null) {
    String customerName = order.customerName;
    String status = order.status;
    double totalAmount = order.totalAmount;
    String fulfillmentType = order.fulfillmentType;
} else {
    // Handle the case where the order does not exist
    // or does not belong to the specified restaurant
}
```

### Return Value

Returns the matching `CustomerOrder` object, or `null` if no matching
record is found.

### When to Use

Use this method when a restaurant manager opens a specific order to view
its details.

Passing the restaurant ID ensures that the query only retrieves an order
belonging to the specified restaurant.

------------------------------------------------------------------------

## 13. CustomerOrder POJO

`CustomerOrder` is a POJO class used to receive the results of the JOIN
queries.

It is not a Room Entity and does not create a new database table.

The class is used by:

-   `getCustomerOrdersByRestaurantId()`
-   `getCustomerOrderById()`

The query aliases must match the corresponding fields in
`CustomerOrder`.

Example structure:

``` java
public class CustomerOrder {
    public int orderId;
    public int customerId;
    public String customerName;
    public String orderDate;
    public String fulfillmentType;
    public String status;
    public double totalAmount;
    public String deliveryAddress;
}
```

The actual field names and types must match the `CustomerOrder` class in
the project.

------------------------------------------------------------------------

## 14. Important Implementation Notes

### Database Operations and Threading

The DAO methods are synchronous: they return their results directly and
do not automatically run on a background thread.

The current `AppDatabase` configuration uses
`.allowMainThreadQueries()`, which permits DAO calls on the main UI
thread in this project.

However, database operations on the main thread can block the UI if a
query or write takes time. For a production application, database
operations should generally be performed on a background thread, with UI
updates posted back to the main thread.

### Handling Empty Results

Check the return value before using the result:

-   `Order` return type: check for `null`.
-   `CustomerOrder` return type: check for `null`.
-   `List<Order>` return type: check `isEmpty()`.

### Order Status Values

The current active/past order queries classify statuses as follows:

  Status        Classification
  ------------- ----------------
  `PENDING`     Active
  `ACCEPTED`    Active
  `READY`       Active
  `PICKED_UP`   Past
  `DELIVERED`   Past
  `REJECTED`    Past
  `CANCELLED`   Past

These classifications reflect the current SQL queries in `OrderDao`.
Make sure the status values stored in the database match the values
defined in `OrderStatus`.

### Order Items

`OrderDao` manages order records in the `ORDERS` table.

To retrieve the individual food items associated with an order, use
`OrderItemDao`.

For example:

``` java
List<OrderItemDetails> items =
        orderItemDao.getOrderItemDetails(orderId);
```

Use the order ID to retrieve the corresponding order items and their
food information.
