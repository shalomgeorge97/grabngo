\# OrderDao – Usage Guide



\## 1. Overview



`OrderDao` provides database operations for creating, updating, retrieving, and managing customer orders.



It supports the following operations:



| Method | Description |

|---|---|

| `addOrder()` | Insert a new order. |

| `updateOrder()` | Update an existing order. |

| `getOrdersByCustomerId()` | Retrieve all orders placed by a specific customer. |

| `getActiveOrdersByCustomerId()` | Retrieve active orders for a customer. |

| `getPastOrdersByCustomerId()` | Retrieve past orders for a customer. |

| `getOrdersByRestaurantId()` | Retrieve all orders received by a specific restaurant. |

| `getOrderById()` | Retrieve a specific order by its ID. |

| `getOrdersByStatus()` | Retrieve restaurant orders with a specific status. |

| `updateOrderStatus()` | Update an order's status. |

| `getCustomerOrdersByRestaurantId()` | Retrieve restaurant orders together with customer information. |

| `getCustomerOrderById()` | Retrieve a specific order together with customer information. |



\---



\## 2. Create a New Order



\### Method



```java

long addOrder(Order order);

```



\### Purpose



Inserts a new order into the database.



\### Example



```java

Order order = new Order();



// Set order information

order.customerId = customerId;

order.restaurantId = restaurantId;

order.status = "PENDING";

order.totalAmount = totalAmount;



// Insert the order

long orderId = orderDao.addOrder(order);

```



\### Return Value



Returns the automatically generated ID of the newly inserted order.



Use this ID when inserting related order items into the `ORDER\_ITEM` table.



\*\*Note:\*\* The example assumes the field names shown in the Order Entity. Adjust the assignments to match the actual Entity implementation.



\---



\## 3. Update an Existing Order



\### Method



```java

void updateOrder(Order order);

```



\### Purpose



Updates an existing order record.



\### Example



```java

Order order = orderDao.getOrderById(orderId);



if (order != null) {



&#x20;   order.status = "ACCEPTED";



&#x20;   orderDao.updateOrder(order);

}

```



\### When to Use



Use this method when you need to update an existing order record.



For example:

\- Update order information.

\- Change order status using the complete Order object.



\---



\## 4. Get All Orders by Customer ID



\### Method



```java

List<Order> getOrdersByCustomerId(int customerId);

```



\### Purpose



Retrieves all orders placed by a specific customer.



Orders are sorted by `order\_id` in descending order, so newer order IDs appear first.



\### Example



```java

List<Order> orders =

&#x20;       orderDao.getOrdersByCustomerId(customerId);



if (orders.isEmpty()) {

&#x20;   // Display an empty state

} else {

&#x20;   for (Order order : orders) {

&#x20;       int orderId = order.orderId;

&#x20;       String status = order.status;

&#x20;       double totalAmount = order.totalAmount;

&#x20;   }

}

```



\### When to Use



Use this method when you need to retrieve all orders belonging to a customer, regardless of their status.



\---



\## 5. Get Active Orders by Customer ID



\### Method



```java

List<Order> getActiveOrdersByCustomerId(int customerId);

```



\### Purpose



Retrieves active orders for a specific customer.



The following statuses are considered active:



\- `PENDING`

\- `ACCEPTED`

\- `READY`



\### Example



```java

List<Order> activeOrders =

&#x20;       orderDao.getActiveOrdersByCustomerId(customerId);



if (activeOrders.isEmpty()) {

&#x20;   // Display an empty state

} else {

&#x20;   for (Order order : activeOrders) {

&#x20;       int orderId = order.orderId;

&#x20;       String status = order.status;

&#x20;       double totalAmount = order.totalAmount;

&#x20;   }

}

```



\### When to Use



Use this method to display active orders on the Customer Orders screen.



\---



\## 6. Get Past Orders by Customer ID



\### Method



```java

List<Order> getPastOrdersByCustomerId(int customerId);

```



\### Purpose



Retrieves completed or otherwise inactive orders for a specific customer.



The following statuses are considered past orders:



\- `PICKED\_UP`

\- `DELIVERED`

\- `REJECTED`

\- `CANCELLED`



\### Example



```java

List<Order> pastOrders =

&#x20;       orderDao.getPastOrdersByCustomerId(customerId);



if (pastOrders.isEmpty()) {

&#x20;   // Display an empty state

} else {

&#x20;   for (Order order : pastOrders) {

&#x20;       int orderId = order.orderId;

&#x20;       String status = order.status;

&#x20;       double totalAmount = order.totalAmount;

&#x20;   }

}

```



\### When to Use



Use this method to display past orders on the Customer Orders screen.



\---



\## 7. Get All Orders by Restaurant ID



\### Method



```java

List<Order> getOrdersByRestaurantId(int restaurantId);

```



\### Purpose



Retrieves all orders received by a specific restaurant.



Orders are sorted by `order\_id` in descending order.



\### Example



```java

List<Order> orders =

&#x20;       orderDao.getOrdersByRestaurantId(restaurantId);



if (orders.isEmpty()) {

&#x20;   // Display an empty state

} else {

&#x20;   for (Order order : orders) {

&#x20;       int orderId = order.orderId;

&#x20;       int customerId = order.customerId;

&#x20;       String status = order.status;

&#x20;   }

}

```



\### When to Use



Use this method when a restaurant manager needs to retrieve all orders belonging to their restaurant.



\---



\## 8. Get an Order by ID



\### Method



```java

Order getOrderById(int orderId);

```



\### Purpose



Retrieves one specific order using its order ID.



\### Example



```java

Order order = orderDao.getOrderById(orderId);



if (order != null) {

&#x20;   String status = order.status;

&#x20;   double totalAmount = order.totalAmount;

} else {

&#x20;   // Handle the case where the order does not exist

}

```



\### When to Use



Use this method when you need to retrieve the basic order information for a specific order.



For example:

\- Display order details.

\- Check the current order status.

\- Retrieve an order before updating it.



\---



\## 9. Get Orders by Status



\### Method



```java

List<Order> getOrdersByStatus(int restaurantId, String status);

```



\### Purpose



Retrieves orders belonging to a specific restaurant with a specified status.



\### Parameters



| Parameter | Description |

|---|---|

| `restaurantId` | ID of the restaurant. |

| `status` | Order status used to filter the results. |



\### Example



```java

List<Order> pendingOrders =

&#x20;       orderDao.getOrdersByStatus(restaurantId, "PENDING");

```



To retrieve accepted orders:



```java

List<Order> acceptedOrders =

&#x20;       orderDao.getOrdersByStatus(restaurantId, "ACCEPTED");

```



\### When to Use



Use this method when a restaurant manager needs to filter orders by status.



\*\*Note:\*\* The current query does not specify an ordering rule for the results.



\---



\## 10. Update Order Status



\### Method



```java

int updateOrderStatus(int orderId, String status);

```



\### Purpose



Updates the status of a specific order.



\### Example



```java

int rowsUpdated =

&#x20;       orderDao.updateOrderStatus(orderId, "ACCEPTED");



if (rowsUpdated > 0) {

&#x20;   // Order status was updated successfully

} else {

&#x20;   // No matching order was updated

}

```



\### Return Value



Returns the number of rows updated.



\- `1`: One order was updated.

\- `0`: No matching order was found.



\### When to Use



Use this method when the order status needs to be changed.



For example:

\- Accept an order.

\- Mark an order as ready.

\- Mark an order as delivered.



\*\*Important:\*\* This method updates the status without checking the current status. If the operation must only be allowed for orders with a particular current status, use an appropriate conditional query or transaction instead.



\---



\## 11. Get Customer and Order Information by Restaurant ID



\### Method



```java

List<CustomerOrder> getCustomerOrdersByRestaurantId(int restaurantId);

```



\### Purpose



Retrieves order information together with customer information by joining the `ORDERS` and `USER` tables.



The returned information includes:



\- Order ID

\- Customer ID

\- Customer name

\- Order date

\- Fulfillment type

\- Order status

\- Total amount

\- Delivery address



Orders are sorted by `order\_id` in descending order.



\### Example



```java

List<CustomerOrder> orders =

&#x20;       orderDao.getCustomerOrdersByRestaurantId(restaurantId);



if (orders.isEmpty()) {

&#x20;   // Display an empty state

} else {

&#x20;   for (CustomerOrder order : orders) {



&#x20;       int orderId = order.orderId;

&#x20;       int customerId = order.customerId;

&#x20;       String customerName = order.customerName;

&#x20;       String status = order.status;

&#x20;       double totalAmount = order.totalAmount;



&#x20;   }

}

```



\### When to Use



Use this method for the Restaurant Manager's order list.



It allows the manager to see customer information without making a separate query for each customer.



\---



\## 12. Get Customer and Order Information by Order ID



\### Method



```java

CustomerOrder getCustomerOrderById(

&#x20;       int orderId,

&#x20;       int restaurantId

);

```



\### Purpose



Retrieves customer and order information for a specific order.



The query joins the `ORDERS` and `USER` tables and filters by both order ID and restaurant ID.



\### Parameters



| Parameter | Description |

|---|---|

| `orderId` | ID of the order. |

| `restaurantId` | ID of the restaurant that received the order. |



\### Example



```java

CustomerOrder order =

&#x20;       orderDao.getCustomerOrderById(orderId, restaurantId);



if (order != null) {



&#x20;   String customerName = order.customerName;

&#x20;   String status = order.status;

&#x20;   double totalAmount = order.totalAmount;

&#x20;   String fulfillmentType = order.fulfillmentType;



} else {

&#x20;   // Handle the case where the order does not exist

&#x20;   // or does not belong to the specified restaurant

}

```



\### When to Use



Use this method when a restaurant manager opens a specific order to view its details.



Passing the restaurant ID ensures that the query only retrieves an order belonging to that restaurant.



\---



\## 13. CustomerOrder POJO



`CustomerOrder` is a POJO class used to receive the results of JOIN queries.



It is not a Room Entity and does not create a new database table.



The class contains the order and customer information returned by:



\- `getCustomerOrdersByRestaurantId()`

\- `getCustomerOrderById()`



Example structure:



```java

public class CustomerOrder {



&#x20;   public int orderId;

&#x20;   public int customerId;

&#x20;   public String customerName;

&#x20;   public String orderDate;

&#x20;   public String fulfillmentType;

&#x20;   public String status;

&#x20;   public double totalAmount;

&#x20;   public String deliveryAddress;



}

```



The actual field types should match the corresponding fields in the project.



\---



\## 14. Important Implementation Notes



\### Database Operations



All DAO methods in this interface perform synchronous database operations.



\*\*Do not call these methods directly on the main UI thread.\*\*



Use a background thread, such as `ExecutorService`, when calling the DAO from an Activity or other UI-related class.



Example:



```java

ExecutorService executor =

&#x20;       Executors.newSingleThreadExecutor();



executor.execute(() -> {



&#x20;   List<Order> orders =

&#x20;           orderDao.getOrdersByCustomerId(customerId);



&#x20;   runOnUiThread(() -> {

&#x20;       // Update the UI here

&#x20;   });



});

```



\### Handling Empty Results



Check the return value before displaying data.



\- `Order` return type: check for `null`.

\- `CustomerOrder` return type: check for `null`.

\- `List<Order>` return type: check `isEmpty()`.



\### Order Status Values



The current DAO uses the following status values:



| Status | Classification |

|---|---|

| `PENDING` | Active |

| `ACCEPTED` | Active |

| `READY` | Active |

| `PICKED\_UP` | Past |

| `DELIVERED` | Past |

| `REJECTED` | Past |

| `CANCELLED` | Past |



Make sure the status strings used by the application match these values exactly.



\### Order Items



`OrderDao` handles order records in the `ORDERS` table.



To retrieve the individual food items associated with an order, use `OrderItemDao`.



For example:



```java

List<OrderItemDetails> items =

&#x20;       orderItemDao.getOrderItemDetails(orderId);

```



Use the order ID to retrieve the corresponding order items and their food information.



