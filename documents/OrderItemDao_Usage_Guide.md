\# OrderItemDao – Usage Guide



\## 1. Overview



`OrderItemDao` provides database operations for managing food items associated with customer orders.



It supports the following operations:



| Method | Description |

|---|---|

| `addOrderItems()` | Insert multiple food items into an order. |

| `updateOrderItem()` | Update an existing order item. |

| `deleteOrderItem()` | Delete an order item. |

| `getOrderItemsByOrderId()` | Retrieve all order items belonging to a specific order. |

| `getOrderItemDetails()` | Retrieve food item information for a specific order using a JOIN query. |



\---



\## 2. Add Food Items to an Order



\### Method



```java

void addOrderItems(List<OrderItem> orderItems);

```



\### Purpose



Inserts multiple order items into the `ORDER\_ITEM` table.



Each record represents a food item included in an order, together with its quantity and price.



\### Example



```java

List<OrderItem> orderItems = new ArrayList<>();



OrderItem item1 = new OrderItem();

item1.orderId = orderId;

item1.foodId = 1;

item1.quantity = 2;

item1.price = 10.00;



OrderItem item2 = new OrderItem();

item2.orderId = orderId;

item2.foodId = 2;

item2.quantity = 1;

item2.price = 8.00;



orderItems.add(item1);

orderItems.add(item2);



orderItemDao.addOrderItems(orderItems);

```



\### When to Use



Use this method when inserting the food items associated with a newly created order.



For example:

\- A customer selects multiple food items.

\- The application creates an order.

\- The selected food items are inserted into `ORDER\_ITEM`.



\*\*Important:\*\* During checkout, use `OrderTransactionDao.placeOrder()` rather than calling `addOrderItems()` independently. This ensures that order creation, order item insertion, and inventory reduction are handled within the same transaction.



\---



\## 3. Update an Existing Order Item



\### Method



```java

void updateOrderItem(OrderItem orderItem);

```



\### Purpose



Updates an existing order item record.



\### Example



```java

OrderItem orderItem = new OrderItem();



// Retrieve the order item before updating

// Example assumes the order item has already been retrieved.



orderItem.quantity = 3;



orderItemDao.updateOrderItem(orderItem);

```



\### When to Use



Use this method when an existing order item needs to be modified.



For example:

\- Change the quantity of an order item.

\- Update the price stored in the order item record.



\*\*Note:\*\* The example is illustrative. The actual OrderItem object must be retrieved or constructed with its correct primary key and existing field values before updating it.



\---



\## 4. Delete an Order Item



\### Method



```java

void deleteOrderItem(OrderItem orderItem);

```



\### Purpose



Deletes an existing order item record from the `ORDER\_ITEM` table.



\### Example



```java

OrderItem orderItem = /\* retrieve an existing order item \*/;



if (orderItem != null) {

&#x20;   orderItemDao.deleteOrderItem(orderItem);

}

```



\### When to Use



Use this method when an order item needs to be removed from an order.



\*\*Note:\*\* Deleting an order item does not automatically restore the corresponding food item's inventory. If inventory restoration is required, it must be handled separately within an appropriate transaction.



\---



\## 5. Get Order Items by Order ID



\### Method



```java

List<OrderItem> getOrderItemsByOrderId(int orderId);

```



\### Purpose



Retrieves all order item records belonging to a specific order.



This method returns the basic `OrderItem` information stored in the `ORDER\_ITEM` table.



\### Example



```java

List<OrderItem> orderItems =

&#x20;       orderItemDao.getOrderItemsByOrderId(orderId);



if (orderItems.isEmpty()) {

&#x20;   // Handle the case where no order items are found

} else {

&#x20;   for (OrderItem item : orderItems) {



&#x20;       int foodId = item.foodId;

&#x20;       int quantity = item.quantity;

&#x20;       double price = item.price;



&#x20;   }

}

```



\### When to Use



Use this method when you need to retrieve the order item records associated with a specific order.



For example:

\- Retrieve ordered food IDs.

\- Check the quantities associated with an order.

\- Retrieve order items before restoring inventory when an order is rejected or cancelled.



\---



\## 6. Get Order Item Details



\### Method



```java

List<OrderItemDetails> getOrderItemDetails(int orderId);

```



\### Purpose



Retrieves detailed food item information for a specific order by joining the `ORDER\_ITEM` and `FOOD\_ITEM` tables.



The returned information includes:



\- Food item ID

\- Food name

\- Ordered quantity

\- Price stored in the order item record



\### Example



```java

List<OrderItemDetails> items =

&#x20;       orderItemDao.getOrderItemDetails(orderId);



if (items.isEmpty()) {

&#x20;   // Handle the case where no order item details are found

} else {

&#x20;   for (OrderItemDetails item : items) {



&#x20;       int foodId = item.foodId;

&#x20;       String foodName = item.foodName;

&#x20;       int quantity = item.quantity;

&#x20;       double price = item.price;



&#x20;   }

}

```



\### When to Use



Use this method to display the food items included in a specific order.



For example:

\- Display the items on the Customer Order Details screen.

\- Display the items on the Restaurant Manager's Order Details screen.

\- Show food names, quantities, and prices.



\### UI Mapping – Order Details Screen



| UI Element | Property |

|---|---|

| Food item ID | `foodId` |

| Food name | `foodName` |

| Quantity | `quantity` |

| Price | `price` |



Example display:



\- Salmon Sushi – 2 × $10.00

\- Fresh Salad – 1 × $8.00



\*\*Note:\*\* The `price` comes from `ORDER\_ITEM`, not the current `FOOD\_ITEM.discounted\_price`. This allows the application to display the price recorded for the order rather than relying on a potentially changed current food item price.



\---



\## 7. OrderItemDetails POJO



`OrderItemDetails` is a POJO class used to receive the results of the JOIN query.



It is not a Room Entity and does not create a new database table.



Example structure:



```java

public class OrderItemDetails {



&#x20;   public int foodId;

&#x20;   public String foodName;

&#x20;   public int quantity;

&#x20;   public double price;



}

```



The field names and types must match the aliases and corresponding values returned by the SQL query.



\---



\## 8. Which Method Should Be Used?



The following table explains when to use each retrieval method.



| Requirement | Method |

|---|---|

| Retrieve order item IDs, quantities and prices | `getOrderItemsByOrderId()` |

| Display food names and order item details | `getOrderItemDetails()` |

| Insert multiple order items | `addOrderItems()` |

| Update an order item | `updateOrderItem()` |

| Delete an order item | `deleteOrderItem()` |



\### Example: Order Details Screen



When a user opens an order:



1\. Use `OrderDao.getOrderById()` or `OrderDao.getCustomerOrderById()` to retrieve order information.

2\. Use `getOrderItemDetails(orderId)` to retrieve the food items included in that order.

3\. Display the order information and its associated food items.



Example:



```java

Order order = orderDao.getOrderById(orderId);



List<OrderItemDetails> items =

&#x20;       orderItemDao.getOrderItemDetails(orderId);



if (order != null) {

&#x20;   // Display order information

}



// Display the associated food items

```



For the manager's order details screen, `getCustomerOrderById()` can be used to retrieve the order together with customer information.



\---



\## 9. Important Implementation Notes



\### Database Operations



All DAO methods in this interface perform synchronous database operations.



\*\*Do not call these methods directly on the main UI thread.\*\*



Use a background thread, such as `ExecutorService`, when calling the DAO from an Activity or other UI-related class.



Example:



```java

ExecutorService executor =

&#x20;       Executors.newSingleThreadExecutor();



executor.execute(() -> {



&#x20;   List<OrderItemDetails> items =

&#x20;           orderItemDao.getOrderItemDetails(orderId);



&#x20;   runOnUiThread(() -> {

&#x20;       // Update the UI here

&#x20;   });



});

```



\### Handling Empty Results



Check whether the returned list is empty before displaying the results.



```java

if (items.isEmpty()) {

&#x20;   // Display an appropriate empty state

}

```



\### Inventory Management



\- `OrderItemDao` manages the food items associated with orders.

\- It does not automatically update food item inventory.

\- Use `FoodItemDao` for inventory updates.

\- Use `OrderTransactionDao` for transactional order placement, rejection, and cancellation.



\### Price Information



The `price` field in `OrderItemDetails` represents the price stored in `ORDER\_ITEM`.



Do not replace it with the current food item's discounted price when displaying historical order information.



