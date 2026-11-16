\# UserDao – Usage Guide



\## 1. Overview



`UserDao` provides database operations for managing user information.



It supports the following operations:



| Method | Description |

|---|---|

| `addUser()` | Insert a new user during registration. |

| `getUserById()` | Retrieve a user by their user ID. |

| `getUserByEmail()` | Retrieve a user by their email address. |

| `isEmailExists()` | Check whether an email address is already registered. |

| `isEmailUsedByAnotherUser()` | Check whether another user is using the specified email address. |

| `update()` | Update an existing user's information. |



\---



\## 2. Insert a New User



\### Method



```java

long addUser(User user);

```



\### Purpose



Inserts a new user record into the database.



\### Example



```java

User user = new User();



// Set user information

user.name = "John Smith";

user.email = "john@example.com";

user.password = hashedPassword;

user.address = "Vancouver";

user.role = "CUSTOMER";



// Insert the user

long userId = userDao.addUser(user);

```



\### Return Value



Returns the automatically generated ID of the newly inserted user.



Use this ID to identify the user in other operations.



\### When to Use



Use this method during user registration.



For example:

\- Register a new customer.

\- Register a new restaurant manager, if manager registration is enabled.



\*\*Note:\*\* Passwords should be hashed before being stored in the database. Do not store plain-text passwords.



\---



\## 3. Get a User by ID



\### Method



```java

User getUserById(int userId);

```



\### Purpose



Retrieves a specific user using their user ID.



\### Example



```java

User user = userDao.getUserById(userId);



if (user != null) {



&#x20;   String name = user.name;

&#x20;   String email = user.email;

&#x20;   String address = user.address;

&#x20;   String role = user.role;



} else {

&#x20;   // Handle the case where the user does not exist

}

```



\### When to Use



Use this method when you need to retrieve user information using a user ID.



For example:

\- Display user information on the Profile screen.

\- Retrieve the logged-in user's information.

\- Load user information associated with an existing session.



\---



\## 4. Get a User by Email



\### Method



```java

User getUserByEmail(String email);

```



\### Purpose



Retrieves a user using their email address.



\### Example



```java

String email = "john@example.com";



User user = userDao.getUserByEmail(email);



if (user != null) {



&#x20;   int userId = user.userId;

&#x20;   String name = user.name;

&#x20;   String role = user.role;



} else {

&#x20;   // Handle the case where the email is not registered

}

```



\### When to Use



Use this method primarily during login.



For example:

1\. Retrieve the user using the entered email address.

2\. Check whether the user exists.

3\. Verify the entered password against the stored password hash.

4\. If authentication succeeds, proceed with the login process.



\*\*Important:\*\* Retrieving a user by email does not authenticate the user by itself. Password verification must be handled separately.



\---



\## 5. Check Whether an Email Already Exists



\### Method



```java

boolean isEmailExists(String email);

```



\### Purpose



Checks whether an email address is already registered in the database.



\### Example



```java

String email = "john@example.com";



boolean emailExists = userDao.isEmailExists(email);



if (emailExists) {

&#x20;   // Display an error message:

&#x20;   // "This email is already registered."

} else {

&#x20;   // Continue with registration

}

```



\### Return Value



| Return value | Meaning |

|---|---|

| `true` | The email address already exists. |

| `false` | The email address is not registered. |



\### When to Use



Use this method during registration to prevent duplicate email addresses.



\### Example Registration Flow



```java

boolean emailExists = userDao.isEmailExists(email);



if (emailExists) {

&#x20;   // Show an error message

} else {

&#x20;   // Hash the password and create a new user

&#x20;   userDao.addUser(user);

}

```



\---



\## 6. Check Whether Another User Is Using the Email



\### Method



```java

boolean isEmailUsedByAnotherUser(String email, int userId);

```



\### Purpose



Checks whether the specified email address is being used by another user, excluding the current user's own record.



This method is useful when a user edits their profile and wants to change their email address.



\### Parameters



| Parameter | Description |

|---|---|

| `email` | The email address being checked. |

| `userId` | The ID of the current user. This user is excluded from the search. |



\### Example



```java

String newEmail = "newemail@example.com";



boolean emailUsedByAnotherUser =

&#x20;       userDao.isEmailUsedByAnotherUser(newEmail, userId);



if (emailUsedByAnotherUser) {

&#x20;   // Display an error message:

&#x20;   // "This email is already used by another account."

} else {

&#x20;   // Continue updating the profile

}

```



\### Return Value



| Return value | Meaning |

|---|---|

| `true` | Another user is already using this email. |

| `false` | No other user is using this email. |



\### When to Use



Use this method when updating a user's email address.



For example:



\- User A currently has `userA@example.com`.

\- User A wants to change their email to `new@example.com`.

\- The query checks whether another user already has `new@example.com`.

\- User A's own existing record is excluded from the check.



This means users can save their profile without changing their email address, even though that email already exists in their own record.



\---



\## 7. Update an Existing User



\### Method



```java

void update(User user);

```



\### Purpose



Updates an existing user record in the database.



\### Example



```java

User user = userDao.getUserById(userId);



if (user != null) {



&#x20;   user.name = "John Updated";

&#x20;   user.email = "newemail@example.com";

&#x20;   user.address = "Burnaby";



&#x20;   userDao.update(user);

}

```



\### When to Use



Use this method when a user edits their profile.



For example:

\- Update the user's name.

\- Change the email address.

\- Update the address.

\- Update the profile photo path.



\*\*Important:\*\* If the user changes their email address, check it using `isEmailUsedByAnotherUser()` before updating the record.



\---



\## 8. Example: Customer Registration Flow



The following example demonstrates how the DAO methods can be used during registration.



```java

boolean emailExists = userDao.isEmailExists(email);



if (emailExists) {



&#x20;   // Display an error message:

&#x20;   // "This email is already registered."



} else {



&#x20;   User user = new User();



&#x20;   user.name = name;

&#x20;   user.email = email;

&#x20;   user.password = hashedPassword;

&#x20;   user.address = address;

&#x20;   user.role = "CUSTOMER";



&#x20;   long userId = userDao.addUser(user);



&#x20;   // Registration successful

}

```



\### Methods Used



1\. `isEmailExists()` – Check whether the email is already registered.

2\. `addUser()` – Insert the new user.



\---



\## 9. Example: Login Flow



The following example demonstrates how to retrieve a user during login.



```java

User user = userDao.getUserByEmail(email);



if (user == null) {



&#x20;   // Display: Invalid email or password



} else {



&#x20;   // Verify the entered password against the stored hash



&#x20;   if (passwordMatches) {



&#x20;       // Login successful

&#x20;       // Save user ID and role in SharedPreferences



&#x20;   } else {



&#x20;       // Display: Invalid email or password

&#x20;   }

}

```



\### Methods Used



1\. `getUserByEmail()` – Retrieve the user account.

2\. Password verification – Authenticate the user (handled outside the DAO).



\*\*Note:\*\* `passwordMatches` is an illustrative variable. Actual password verification must be implemented in the authentication logic.



\---



\## 10. Example: Update User Profile Flow



The following example demonstrates how to update a user's profile while checking email uniqueness.



```java

User user = userDao.getUserById(userId);



if (user != null) {



&#x20;   boolean emailUsed =

&#x20;           userDao.isEmailUsedByAnotherUser(newEmail, userId);



&#x20;   if (emailUsed) {



&#x20;       // Display an error message:

&#x20;       // "This email is already used by another account."



&#x20;   } else {



&#x20;       user.name = newName;

&#x20;       user.email = newEmail;

&#x20;       user.address = newAddress;



&#x20;       userDao.update(user);



&#x20;       // Profile updated successfully

&#x20;   }

}

```



\### Methods Used



1\. `getUserById()` – Retrieve the current user's information.

2\. `isEmailUsedByAnotherUser()` – Check email uniqueness, excluding the current user.

3\. `update()` – Save the updated user information.



\---



\## 11. Important Implementation Notes



\### Database Operations



All DAO methods in this interface perform synchronous database operations.



\*\*Do not call these methods directly on the main UI thread.\*\*



Use a background thread, such as `ExecutorService`, when calling the DAO from an Activity or other UI-related class.



Example:



```java

ExecutorService executor =

&#x20;       Executors.newSingleThreadExecutor();



executor.execute(() -> {



&#x20;   User user = userDao.getUserById(userId);



&#x20;   runOnUiThread(() -> {

&#x20;       // Update the UI here

&#x20;   });



});

```



\### Handling Return Values



| Return type | How to handle |

|---|---|

| `long` | Check the generated user ID after insertion. |

| `User` | Check for `null` if the user does not exist. |

| `boolean` | Check whether the email exists or is used by another user. |

| `void` | The method does not return a value. |



\### Email Uniqueness



\- Use `isEmailExists()` during registration.

\- Use `isEmailUsedByAnotherUser()` when editing a profile.

\- The application should prevent users from registering or updating their email to an address already used by another account.



For stronger protection against simultaneous registration or profile updates, email uniqueness should also be enforced at the database level with a unique constraint.



\### Password Security



\- Never store plain-text passwords.

\- Hash passwords before inserting or updating them.

\- Verify passwords through authentication logic, not by directly comparing plain-text values.



\### Session Management



`UserDao` retrieves and updates user information, but it does not manage login sessions.



Use `SharedPreferences` or the project's session management component to store the logged-in user's ID and role.



