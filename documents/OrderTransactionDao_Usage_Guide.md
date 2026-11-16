\# OrderTransactionDao – Usage Guide



\## 1. Overview



`OrderTransactionDao` manages order-related database operations that require multiple database changes to be executed together.



It uses Room's `@Transaction` annotation to ensure that related database operations are handled as a single transaction.



It supports the following operations:



| Method | Description |

|---|---|

| `placeOrder()` | Create a new order, insert its order items, and decrease food inventory. |

| `rejectOrder()` | Reject a pending order and restore food inventory. |

| `cancelOrder()` | Cancel a pending order and restore food inventory. |



\### Why Use OrderTransactionDao?



Some order operations require changes to multiple database tables.



For example, placing an order involves:



1\. Inserting a new record into `ORDERS`.

2\. Inserting related records into `ORDER\_ITEM`.

3\. Decreasing the available quantity in `FOOD\_ITEM`.



These operations should succeed or fail together.



If any inventory update fails, the transaction is rolled back so that incomplete order data is not saved.



\---



\## 2. Place an Order



\### Method



```java

long placeOrder(Order order, List<OrderItem> items);

```



\### Purpose



Creates a new order, inserts its associated order items, and decreases the available quantity of each food item.



All operations are executed within a single transaction.



\### Example



```java

Order order = new Order();



// Set order information

order.setCustomerId(customerId);

order.setRestaurantId(restaurantId);

order.setStatus("PENDING");

order.setTotalAmount(totalAmount);



List<OrderItem> items = new ArrayList<>();



OrderItem item1 = new OrderItem();

item1.setFoodId(1);

item1.setQuantity(2);

item1.setPrice(10.00);



OrderItem item2 = new OrderItem();

item2.setFoodId(2);

item2.setQuantity(1);

item2.setPrice(8.00);



items.add(item1);

items.add(item2);



// Place the order

long orderId = orderTransactionDao.placeOrder(order, items);

```



\### Processing Flow



The method performs the following operations:



1\. Insert the order into `ORDERS`.

2\. Retrieve the generated order ID.

3\. Assign the order ID to every OrderItem.

4\. Insert all order items into `ORDER\_ITEM`.

5\. Decrease the available quantity of each food item.

6\. Return the generated order ID.



\### Return Value



Returns the generated order ID when the transaction succeeds.



\### Error Handling



If a food item does not have sufficient stock, the method throws:



```java

IllegalStateException("Not enough food stock.");

```



Because the method uses `@Transaction`, the preceding database changes are rolled back.



This prevents an order from being saved when its required inventory cannot be reserved.



\### When to Use



Use this method during Customer Checkout.



For example:

\- Customer confirms an order.

\- The application creates the order with `PENDING` status.

\- Order items are inserted.

\- Available food quantities are decreased.



\*\*Important:\*\* Do not call `OrderDao.addOrder()`, `OrderItemDao.addOrderItems()`, and `FoodItemDao.decreaseAvailableQuantity()` separately for the same checkout operation. Use `placeOrder()` to keep the operations within one transaction.



\---



\## 3. Reject an Order



\### Method



```java

boolean rejectOrder(int orderId);

```



\### Purpose



Rejects an existing pending order and restores the food quantities associated with that order.



All operations are executed within a single transaction.



\### Example



```java

boolean rejected =

&#x20;       orderTransactionDao.rejectOrder(orderId);



if (rejected) {

&#x20;   // Order rejected successfully

} else {

&#x20;   // Order does not exist or is no longer pending

}

```



\### Processing Flow



The method performs the following operations:



1\. Change the order status from `PENDING` to `REJECTED`.

2\. Retrieve all order items associated with the order.

3\. Restore the available quantity of each food item.

4\. Return `true` if the operation succeeds.



\### Return Value



| Return value | Meaning |

|---|---|

| `true` | Order was successfully rejected and inventory was restored. |

| `false` | The order was not pending or no matching order was updated. |



\### Error Handling



If inventory restoration fails, the method throws:



```java

IllegalStateException("Failed to restore food stock.");

```



The transaction is rolled back, including the status change.



\### When to Use



Use this method when a Restaurant Manager rejects a customer's pending order.



For example:

\- Manager reviews a new order.

\- Manager selects Reject.

\- Order status changes to `REJECTED`.

\- Previously reserved food quantities are returned to inventory.



\*\*Important:\*\* Only orders with `PENDING` status can be rejected using this method.



\---



\## 4. Cancel an Order



\### Method



```java

boolean cancelOrder(int orderId);

```



\### Purpose



Cancels an existing pending order and restores the food quantities associated with that order.



All operations are executed within a single transaction.



\### Example



```java

boolean cancelled =

&#x20;       orderTransactionDao.cancelOrder(orderId);



if (cancelled) {

&#x20;   // Order cancelled successfully

} else {

&#x20;   // Order does not exist or is no longer pending

}

```



\### Processing Flow



The method performs the following operations:



1\. Change the order status from `PENDING` to `CANCELLED`.

2\. Retrieve all order items associated with the order.

3\. Restore the available quantity of each food item.

4\. Return `true` if the operation succeeds.



\### Return Value



| Return value | Meaning |

|---|---|

| `true` | Order was successfully cancelled and inventory was restored. |

| `false` | The order was not pending or no matching order was updated. |



\### Error Handling



If inventory restoration fails, the method throws:



```java

IllegalStateException("Failed to restore food stock.");

```



The transaction is rolled back, including the status change.



\### When to Use



Use this method when a customer cancels an order that is still pending.



For example:

\- Customer opens Order Details.

\- Customer selects Cancel Order.

\- Application asks for confirmation.

\- After confirmation, the order is cancelled and inventory is restored.



\*\*Important:\*\* Only orders with `PENDING` status can be cancelled using this method.



\---



\## 5. Order Processing Summary



The following table summarizes how inventory and order status are handled.



| Operation | Order Status | Inventory |

|---|---|---|

| Place order | `PENDING` | Decreased |

| Accept order | `ACCEPTED` | No change |

| Reject order | `REJECTED` | Restored |

| Cancel order | `CANCELLED` | Restored |



\### Order Lifecycle



```text

Customer places an order

&#x20;         |

&#x20;         v

&#x20;      PENDING

&#x20;         |

&#x20;    +----+----+

&#x20;    |         |

&#x20;    v         v

&#x20; ACCEPTED   REJECTED

&#x20;    |         |

&#x20;    v         v

&#x20;  READY    Inventory

&#x20;    |      restored

&#x20;    v

&#x20;PICKED\_UP

```



A customer can also cancel an order while its status is `PENDING`, in which case its status becomes `CANCELLED` and inventory is restored.



\---



\## 6. Important Implementation Notes



\### Database Transactions



The methods `placeOrder()`, `rejectOrder()`, and `cancelOrder()` use Room's `@Transaction` annotation.



This ensures that related database operations are committed together or rolled back when an exception occurs.



\### Preventing Duplicate Processing



The order status is updated using the following condition:



```sql

WHERE order\_id = :orderId AND status = 'PENDING'

```



This ensures that only pending orders can be rejected or cancelled.



If the order has already been accepted, rejected, or cancelled, the operation returns `false` without restoring inventory again.



\### Inventory Management



\- Inventory is decreased when an order is placed.

\- Accepting an order does not change inventory.

\- Rejecting an order restores inventory.

\- Cancelling a pending order restores inventory.

\- Inventory updates are performed within the transaction.



\### Error Handling



Callers should handle exceptions from transaction methods.



For example:



```java

executor.execute(() -> {

&#x20;   try {

&#x20;       long orderId =

&#x20;               orderTransactionDao.placeOrder(order, items);



&#x20;       runOnUiThread(() -> {

&#x20;           // Handle successful order placement

&#x20;       });



&#x20;   } catch (IllegalStateException e) {



&#x20;       runOnUiThread(() -> {

&#x20;           // Display an appropriate error message

&#x20;       });

&#x20;   }

});

```



\### Background Thread Execution



All DAO operations are synchronous.



\*\*Do not call transaction methods directly on the main UI thread.\*\*



Use a background thread, such as `ExecutorService`, when calling these methods from an Activity.



\### Relationship with Other DAOs



| DAO | Responsibility |

|---|---|

| `OrderDao` | Retrieve and update order information. |

| `OrderItemDao` | Manage individual order items. |

| `FoodItemDao` | Manage food item information and inventory. |

| `OrderTransactionDao` | Coordinate multiple order and inventory operations within a transaction. |



Use `OrderTransactionDao` whenever an operation requires multiple related database changes to succeed or fail together.





