\# RestaurantDao – Usage Guide



\## 1. Overview



`RestaurantDao` provides database operations for managing and retrieving restaurant information.



The DAO currently supports the following operations:



| Method | Description |

|---|---|

| `addRestaurant()` | Insert a new restaurant. |

| `getAllRestaurants()` | Retrieve all restaurants. |

| `getRestaurantById()` | Retrieve a restaurant by its ID. |

| `getRestaurantByManagerId()` | Retrieve the restaurant assigned to a specific manager. |

| `getRestaurantsWithAvailableItemCount()` | Retrieve restaurants with their available food item counts, with optional city filtering. |



\---



\## 2. Insert a New Restaurant



\### Method



```java

long addRestaurant(Restaurant restaurant);

```



\### Purpose



Inserts a new restaurant record into the database.



\### Example



```java

Restaurant restaurant = new Restaurant();



// Set restaurant information

restaurant.restaurantName = "New Restaurant";

restaurant.address = "123 Main Street";

restaurant.city = "Vancouver";

restaurant.managerId = 1;



// Insert the restaurant

long restaurantId = restaurantDao.addRestaurant(restaurant);

```



\### Return Value



Returns the automatically generated ID of the newly inserted restaurant.



The returned ID can be used to retrieve the restaurant or associate food items with it.



\---



\## 3. Get All Restaurants



\### Method



```java

List<Restaurant> getAllRestaurants();

```



\### Purpose



Retrieves all restaurant records from the database.



\### Example



```java

List<Restaurant> restaurants = restaurantDao.getAllRestaurants();



if (restaurants.isEmpty()) {

&#x20;   // Handle the case where no restaurants are found

} else {

&#x20;   for (Restaurant restaurant : restaurants) {

&#x20;       String name = restaurant.restaurantName;

&#x20;       String address = restaurant.address;

&#x20;       String city = restaurant.city;

&#x20;   }

}

```



\### When to Use



Use this method when you need to retrieve all restaurant records without calculating available food item counts.



\---



\## 4. Get a Restaurant by ID



\### Method



```java

Restaurant getRestaurantById(int restaurantId);

```



\### Purpose



Retrieves a specific restaurant using its restaurant ID.



\### Example



```java

int restaurantId = 1;



Restaurant restaurant = restaurantDao.getRestaurantById(restaurantId);



if (restaurant != null) {

&#x20;   String name = restaurant.restaurantName;

&#x20;   String address = restaurant.address;

&#x20;   String city = restaurant.city;

} else {

&#x20;   // Handle the case where the restaurant does not exist

}

```



\### When to Use



Use this method when you need to retrieve one specific restaurant.



For example:

\- Display restaurant information on a details screen.

\- Retrieve restaurant information using the selected restaurant ID.



\---



\## 5. Get the Restaurant Assigned to a Manager



\### Method



```java

Restaurant getRestaurantByManagerId(int managerId);

```



\### Purpose



Retrieves the restaurant assigned to a specific restaurant manager.



\### Example



```java

int managerId = 1;



Restaurant restaurant =

&#x20;       restaurantDao.getRestaurantByManagerId(managerId);



if (restaurant != null) {

&#x20;   String name = restaurant.restaurantName;

&#x20;   String address = restaurant.address;

} else {

&#x20;   // Handle the case where no restaurant is assigned

}

```



\### When to Use



Use this method when a restaurant manager logs in and needs to retrieve their assigned restaurant.



For example:

\- Display the manager's restaurant information.

\- Retrieve the restaurant ID before loading or managing its food items.



\*\*Note:\*\* This method returns a single `Restaurant` object, so it assumes that each manager is assigned to at most one restaurant.



\---



\## 6. Get Restaurants with Available Food Item Counts



\### Method



```java

List<RestaurantWithItemCount> getRestaurantsWithAvailableItemCount(

&#x20;       String currentDateTime,

&#x20;       String city

);

```



\### Purpose



Retrieves restaurant information together with the number of available food items for each restaurant.



This method also supports optional city filtering.



The returned information includes:



\- Restaurant ID

\- Restaurant name

\- Address

\- City

\- Number of available food items



\### Parameters



| Parameter | Description |

|---|---|

| `currentDateTime` | Current date and time used to check food item availability. |

| `city` | City name used to filter restaurants. Pass `null` to retrieve all restaurants. |



\### A. Load All Restaurants



Pass `null` as the city parameter to retrieve all restaurants.



```java

List<RestaurantWithItemCount> restaurants =

&#x20;       restaurantDao.getRestaurantsWithAvailableItemCount(

&#x20;               currentDateTime,

&#x20;               null

&#x20;       );

```



This retrieves all restaurants, including restaurants with zero available food items.



\### B. Search Restaurants by City



Pass the city name as the second parameter.



```java

List<RestaurantWithItemCount> restaurants =

&#x20;       restaurantDao.getRestaurantsWithAvailableItemCount(

&#x20;               currentDateTime,

&#x20;               "Vancouver"

&#x20;       );

```



The query uses `LIKE`, so partial city names can also be used.



For example, searching for `"Van"` can match `"Vancouver"`.



\### C. Handle Empty Search Input



If the city search field is empty, pass `null` instead of an empty string.



```java

String city = cityInput.trim().isEmpty()

&#x20;       ? null

&#x20;       : cityInput.trim();



List<RestaurantWithItemCount> restaurants =

&#x20;       restaurantDao.getRestaurantsWithAvailableItemCount(

&#x20;               currentDateTime,

&#x20;               city

&#x20;       );

```



\### D. Accessing the Results



The method returns a list of `RestaurantWithItemCount` objects.



```java

for (RestaurantWithItemCount restaurant : restaurants) {



&#x20;   int restaurantId = restaurant.restaurantId;

&#x20;   String name = restaurant.restaurantName;

&#x20;   String address = restaurant.address;

&#x20;   String cityName = restaurant.city;

&#x20;   int availableItems = restaurant.availableItemCount;



}

```



\### UI Mapping – Customer Home



Use this method to populate the restaurant cards on the Customer Home screen.



| UI Element | Property |

|---|---|

| Restaurant name | `restaurantName` |

| Address | `address` |

| City | `city` |

| Available food items | `availableItemCount` |

| Restaurant details navigation | `restaurantId` |



Example:



\- Vancouver Sushi – Vancouver – 5 items available

\- Fresh Bowl Cafe – Vancouver – 3 items available

\- Downtown Bakery – Vancouver – 0 items available



\### Important Notes



\- Restaurants with zero available food items are included in the results.

\- Only food items with `available\_quantity > 0` and `available\_datetime >= currentDateTime` are counted.

\- `availableItemCount` represents the number of available food item records, not the total inventory quantity.

\- When a restaurant is selected, use its `restaurantId` to retrieve food items from `FoodItemDao`.

\- If the returned list is empty, handle the empty state in the UI.



\---



\## 7. Important Implementation Notes



\### Database Operations



All DAO methods in this interface perform synchronous database operations.



\*\*Do not call these methods directly on the main UI thread.\*\*



Use a background thread, such as `ExecutorService`, when calling the DAO from an Activity or other UI-related class.



Example:



```java

ExecutorService executor =

&#x20;       Executors.newSingleThreadExecutor();



executor.execute(() -> {



&#x20;   List<Restaurant> restaurants =

&#x20;           restaurantDao.getAllRestaurants();



&#x20;   runOnUiThread(() -> {

&#x20;       // Update the UI here

&#x20;   });



});

```



\### Handling Empty Results



Always check whether the returned object or list is `null` or empty, depending on the return type.



\- `Restaurant` return type: check for `null`.

\- `List<Restaurant>` return type: check `isEmpty()`.



\### RestaurantWithItemCount



`RestaurantWithItemCount` is a POJO class used to receive the results of the restaurant count query.



It is not a Room Entity and does not create a new database table.



```java

public class RestaurantWithItemCount {

&#x20;   public int restaurantId;

&#x20;   public String restaurantName;

&#x20;   public String address;

&#x20;   public String city;

&#x20;   public int availableItemCount;

}

```





