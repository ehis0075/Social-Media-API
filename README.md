# Spring Boot Airtime Purchase API with JWT Authentication
This is a Spring Boot application that serves as an API for a Social Media Platform. The API uses JWT (JSON Web Tokens) for user authentication to secure the endpoints.

# Requirements
-Java 11 or higher
-Maven build tool

# Getting Started
git clone https://github.com/ehis0075/Social-Media-API

# Navigate to the project directory:
cd social

# Build the project using Maven
mvn clean install

# Run the Application

The application will start on `http://localhost:8080`.

# JWT Authentication

The JWT is used for user authentication, clients must include the JWT token in the `Authorization` header of any other request apart from the sign-up and login API. 
The header should look like: `Authorization: Bearer <JWT_TOKEN>`. The JWT token is obtained by authenticating the user via the `/api/v1/users/sign-in` endpoint.


                                # API Endpoints
## User Registration API

**Endpoint:** `/api/v1/users/sign-up`
**Method:** `POST`

Registers a new user. Requires providing a `username`, `password` and `email` in the request body.


## User Login API

**Endpoint:** `/api/v1/users/sign-in`
**Method:** `POST`

Authenticates a user. Requires providing a `username` and `password` as request parameters. Returns a JWT token upon successful authentication.


## update user API

**Endpoint:** `/api/v1/users/update/{userId}`
**Method:** `POST`
**Authorization:** Bearer Token (JWT)

Allows authenticated users to update their user account. This API Requires providing the `userId` at the PathVariable and also the info of the user that needs to be updated in the request body. 
This API also requires that a valid JWT token must be passed on the header.


## delete user API


## get all users API


## Follow a friend API

**Endpoint:** `/api/v1/users/follow/{username}`
**Method:** `POST`
**Authorization:** Bearer Token (JWT)

Allows authenticated users to follow another registered user on the platform. Requires providing the `username` of the registered user in the request body. The request must be authenticated with a valid JWT token.


## unFollow a friend API


