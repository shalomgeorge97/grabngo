\# FoodItemDao – Usage Guide



\## 1. Overview



`FoodItemDao` provides database operations for managing food items associated with restaurants.



It supports the following operations:



| Method | Description |

|---|---|

| `addFoodItem()` | Insert a new food item. |

| `updateFoodItem()` | Update an existing food item. |

| `deleteFoodItem()` | Delete a food item. |

| `getFoodItemsByRestaurantId()` | Retrieve all food items belonging to a specific restaurant. |

| `getFoodItemById()` | Retrieve a specific food item by its ID. |

| `searchFoodItemsByName()` | Search food items by name within a specific restaurant. |

| `decreaseAvailableQuantity()` | Decrease the available quantity of a food item. |

| `increaseAvailableQuantity()` | Increase the available quantity of a food item. |

| `getAvailableFoodItems()` | Retrieve available food items with optional search and filters. |



\---



\## 2. Add a New Food Item



\### Method



```java

void addFoodItem(FoodItem foodItem);

```



\### Purpose



Inserts a new food item into the database.



\### Example



```java

FoodItem foodItem = new FoodItem();



// Set food item information

foodItem.restaurantId = restaurantId;

foodItem.name = "Salmon Sushi";

foodItem.description = "Fresh salmon sushi";

foodItem.category = "Japanese";

foodItem.originalPrice = 15.00;

foodItem.discountedPrice = 10.00;

foodItem.availableQuantity = 5;



// Insert the food item

foodItemDao.addFoodItem(foodItem);

```



\### When to Use



Use this method when a restaurant manager adds a new food item.



For example:

\- Add surplus food items.

\- Enter food name and description.

\- Set original and discounted prices.

\- Set available quantity and available date/time.



\*\*Note:\*\* The example assumes the field names shown in the FoodItem Entity. Adjust them to match the actual Entity implementation.



\---



\## 3. Update an Existing Food Item



\### Method



```java

void updateFoodItem(FoodItem foodItem);

```



\### Purpose



Updates an existing food item record.



\### Example



```java

FoodItem foodItem =

&#x20;       foodItemDao.getFoodItemById(foodId);



if (foodItem != null) {



&#x20;   foodItem.discountedPrice = 8.00;

&#x20;   foodItem.availableQuantity = 10;



&#x20;   foodItemDao.updateFoodItem(foodItem);

}

```



\### When to Use



Use this method when a restaurant manager edits food item information.



For example:

\- Change the food name or description.

\- Update original or discounted price.

\- Change available quantity.

\- Update available date/time.



\---



\## 4. Delete a Food Item



\### Method



```java

void deleteFoodItem(FoodItem foodItem);

```



\### Purpose



Deletes an existing food item from the database.



\### Example



```java

FoodItem foodItem =

&#x20;       foodItemDao.getFoodItemById(foodId);



if (foodItem != null) {

&#x20;   foodItemDao.deleteFoodItem(foodItem);

}

```



\### When to Use



Use this method when a restaurant manager removes a food item that is no longer available.



\*\*Note:\*\* If the application needs to retain historical order information, consider whether deleting a food item referenced by an existing order is appropriate. The database foreign key configuration should also be checked.



\---



\## 5. Get Food Items by Restaurant ID



\### Method



```java

List<FoodItem> getFoodItemsByRestaurantId(int restaurantId);

```



\### Purpose



Retrieves all food items belonging to a specific restaurant.



This query does not filter by quantity or availability date/time.



\### Example



```java

List<FoodItem> foodItems =

&#x20;       foodItemDao.getFoodItemsByRestaurantId(restaurantId);



if (foodItems.isEmpty()) {

&#x20;   // Handle the case where no food items are found

} else {

&#x20;   for (FoodItem foodItem : foodItems) {



&#x20;       int foodId = foodItem.foodId;

&#x20;       String name = foodItem.name;

&#x20;       double price = foodItem.discountedPrice;

&#x20;       int quantity = foodItem.availableQuantity;



&#x20;   }

}

```



\### When to Use



Use this method when you need to retrieve all food items associated with a restaurant.



For example:

\- Display all food items in the Restaurant Manager's food management screen.

\- Retrieve food items regardless of their availability.



\---



\## 6. Get a Food Item by ID



\### Method



```java

FoodItem getFoodItemById(int foodId);

```



\### Purpose



Retrieves one specific food item using its food ID.



\### Example



```java

FoodItem foodItem =

&#x20;       foodItemDao.getFoodItemById(foodId);



if (foodItem != null) {



&#x20;   String name = foodItem.name;

&#x20;   String description = foodItem.description;

&#x20;   double price = foodItem.discountedPrice;

&#x20;   int quantity = foodItem.availableQuantity;



} else {

&#x20;   // Handle the case where the food item does not exist

}

```



\### When to Use



Use this method when you need to retrieve information about one specific food item.



For example:

\- Display food item details.

\- Retrieve the latest quantity before checkout.

\- Load a food item before editing it.



\---



\## 7. Search Food Items by Name



\### Method



```java

List<FoodItem> searchFoodItemsByName(

&#x20;       int restaurantId,

&#x20;       String keyword

);

```



\### Purpose



Searches food items by name within a specific restaurant.



The query uses `LIKE`, so partial name matching is supported.



\### Example



```java

List<FoodItem> foodItems =

&#x20;       foodItemDao.searchFoodItemsByName(

&#x20;               restaurantId,

&#x20;               "Salmon"

&#x20;       );

```



This can return food items such as:

\- Salmon Sushi

\- Salmon Roll

\- Grilled Salmon



\### When to Use



Use this method when searching for food items by name within a restaurant.



\*\*Note:\*\* This query does not filter by available quantity or available date/time. If the Customer screen should only display currently available items, use `getAvailableFoodItems()` instead.



\---



\## 8. Decrease Available Quantity



\### Method



```java

int decreaseAvailableQuantity(

&#x20;       int foodId,

&#x20;       int quantity

);

```



\### Purpose



Decreases the available quantity of a food item.



The query only updates the record when:

\- The requested quantity is greater than zero.

\- The current available quantity is greater than or equal to the requested quantity.



\### Example



```java

int rowsUpdated =

&#x20;       foodItemDao.decreaseAvailableQuantity(foodId, 2);



if (rowsUpdated > 0) {

&#x20;   // Quantity was decreased successfully

} else {

&#x20;   // Insufficient quantity or invalid quantity

}

```



\### Return Value



| Return value | Meaning |

|---|---|

| `1` | Quantity was successfully decreased. |

| `0` | No update occurred. The food item may have insufficient quantity or the requested quantity may be invalid. |



\### When to Use



Use this method when an order is placed and the available quantity needs to be reduced.



For example:

\- A customer orders 2 units.

\- The available quantity decreases by 2.



\*\*Important:\*\* For order placement, use the project's `OrderTransactionDao.placeOrder()` transaction rather than calling this method separately. This ensures order creation, order item insertion, and inventory reduction are handled together.



\---



\## 9. Increase Available Quantity



\### Method



```java

int increaseAvailableQuantity(

&#x20;       int foodId,

&#x20;       int quantity

);

```



\### Purpose



Increases the available quantity of a food item.



The query only updates the record when the requested quantity is greater than zero.



\### Example



```java

int rowsUpdated =

&#x20;       foodItemDao.increaseAvailableQuantity(foodId, 2);



if (rowsUpdated > 0) {

&#x20;   // Quantity was increased successfully

} else {

&#x20;   // No update occurred

}

```



\### When to Use



Use this method when previously reserved food items need to be returned to inventory.



For example:

\- A restaurant manager rejects an order.

\- A customer cancels an eligible order.

\- The quantities associated with the order are restored.



\*\*Important:\*\* Use the project's `OrderTransactionDao` for rejection or cancellation so that the status update and inventory restoration happen within the same transaction.



\---



\## 10. Get Available Food Items with Search and Filters



\### Method



```java

List<FoodItem> getAvailableFoodItems(

&#x20;       int restaurantId,

&#x20;       String currentDateTime,

&#x20;       String keyword,

&#x20;       String category,

&#x20;       Double minPrice,

&#x20;       Double maxPrice

);

```



\### Purpose



Retrieves food items that are currently available from a specific restaurant.



This method supports:

\- Search by food name.

\- Filter by category.

\- Filter by minimum discounted price.

\- Filter by maximum discounted price.



\### Parameters



| Parameter | Description |

|---|---|

| `restaurantId` | ID of the selected restaurant. |

| `currentDateTime` | Current date and time used to check availability. |

| `keyword` | Food name search keyword. Pass `null` to disable name searching. |

| `category` | Category used to filter food items. Pass `null` to disable category filtering. |

| `minPrice` | Minimum discounted price. Pass `null` if no minimum is required. |

| `maxPrice` | Maximum discounted price. Pass `null` if no maximum is required. |



\### Availability Conditions



The query only retrieves food items satisfying:



```sql

available\_quantity > 0

AND available\_datetime >= currentDateTime

```



This means:

\- Food items must have at least one unit available.

\- Food items must satisfy the specified date/time condition.



\### A. Load All Available Food Items



Pass `null` for all optional filters.



```java

List<FoodItem> foodItems =

&#x20;       foodItemDao.getAvailableFoodItems(

&#x20;               restaurantId,

&#x20;               currentDateTime,

&#x20;               null,

&#x20;               null,

&#x20;               null,

&#x20;               null

&#x20;       );

```



\### B. Search by Food Name



Pass a keyword as the third parameter.



```java

List<FoodItem> foodItems =

&#x20;       foodItemDao.getAvailableFoodItems(

&#x20;               restaurantId,

&#x20;               currentDateTime,

&#x20;               "Salmon",

&#x20;               null,

&#x20;               null,

&#x20;               null

&#x20;       );

```



The query uses partial matching.



\### C. Filter by Category



Pass the selected category.



```java

List<FoodItem> foodItems =

&#x20;       foodItemDao.getAvailableFoodItems(

&#x20;               restaurantId,

&#x20;               currentDateTime,

&#x20;               null,

&#x20;               "Japanese",

&#x20;               null,

&#x20;               null

&#x20;       );

```



The category comparison uses exact matching.



\### D. Filter by Price Range



Pass the minimum and maximum discounted prices.



```java

List<FoodItem> foodItems =

&#x20;       foodItemDao.getAvailableFoodItems(

&#x20;               restaurantId,

&#x20;               currentDateTime,

&#x20;               null,

&#x20;               null,

&#x20;               5.00,

&#x20;               15.00

&#x20;       );

```



This retrieves food items with discounted prices between $5 and $15, inclusive.



\### E. Combine Search and Filters



All filters can be used together.



```java

List<FoodItem> foodItems =

&#x20;       foodItemDao.getAvailableFoodItems(

&#x20;               restaurantId,

&#x20;               currentDateTime,

&#x20;               "Salmon",

&#x20;               "Japanese",

&#x20;               5.00,

&#x20;               15.00

&#x20;       );

```



This retrieves available Japanese food items whose names contain "Salmon" and whose discounted prices are between $5 and $15.



\### F. Handle Empty Search and Filter Inputs



Convert empty search fields to `null` so that the corresponding filters are disabled.



```java

String keyword = keywordInput.trim().isEmpty()

&#x20;       ? null

&#x20;       : keywordInput.trim();



String category = categoryInput.trim().isEmpty()

&#x20;       ? null

&#x20;       : categoryInput.trim();

```



For optional price filters, use `null` when the user has not selected a minimum or maximum price.



\### When to Use



Use this method for the Customer's Restaurant Details screen.



It allows customers to:

\- Browse available food items.

\- Search food items by name.

\- Filter by category.

\- Filter by price range.



\---



\## 11. UI Mapping – Customer Restaurant Details



Use `getAvailableFoodItems()` to populate the food item list on the Restaurant Details screen.



| UI Element | FoodItem Property |

|---|---|

| Food name | `name` |

| Description | `description` |

| Original price | `originalPrice` |

| Discounted price | `discountedPrice` |

| Available quantity | `availableQuantity` |

| Food category | `category` |

| Food item ID | `foodId` |



Use the selected restaurant's `restaurantId` when retrieving food items.



\---



\## 12. Important Implementation Notes



\### Database Operations



All DAO methods in this interface perform synchronous database operations.



\*\*Do not call these methods directly on the main UI thread.\*\*



Use a background thread, such as `ExecutorService`, when calling the DAO from an Activity or other UI-related class.



Example:



```java

ExecutorService executor =

&#x20;       Executors.newSingleThreadExecutor();



executor.execute(() -> {



&#x20;   List<FoodItem> foodItems =

&#x20;           foodItemDao.getFoodItemsByRestaurantId(restaurantId);



&#x20;   runOnUiThread(() -> {

&#x20;       // Update the UI here

&#x20;   });



});

```



\### Handling Return Values



| Return type | How to handle |

|---|---|

| `void` | No return value. |

| `FoodItem` | Check for `null`. |

| `List<FoodItem>` | Check `isEmpty()`. |

| `int` | Check whether any rows were updated (`> 0`). |



\### Cart and Inventory



\- The cart should keep track of selected food items and requested quantities.

\- Do not allow customers to select more than the available quantity.

\- The available quantity may change after an item is added to the cart.

\- Always validate stock again when placing an order.

\- Use `OrderTransactionDao` for transactional order placement, rejection, and cancellation.



\### Date and Time



The `currentDateTime` parameter must use the same date/time format as the stored `available\_datetime` values.



The condition `available\_datetime >= currentDateTime` assumes that `available\_datetime` represents the end of the period during which the food item is available.





