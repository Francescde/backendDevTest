

# SimilarProducts API

This readme provides detailed instructions on how to run the SimilarProducts service and examples of requests using the provided API.

## Table of Contents
1. [API Description](#api-description)
2. [Prerequisites](#prerequisites)
3. [Running the Service](#running-the-service)
4. [API Usage](#api-usage)
    - [Request](#request)
    - [Response](#response)
5. [Examples](#examples)

## API Description<a name="api-description"></a>

The SimilarProducts API allows you to retrieve a list of similar products based on a given product ID. The API is built using the OpenAPI 3.0.0 specification.

The base URL for the API is `http://localhost:5000`. The API supports the following endpoint:

- `/product/{productId}/similar` - Retrieves a list of similar products to the product with the specified `productId`.

The api uses redis for cache,logback for logging and provides swagger documentation under http://localhost:5000/swagger-ui/index.html

The code for the API can be found in the `myApp/myApp` folder. It is distributed across three classes in an MVC structure:

- `SimilarProductsController`: Receives the similar products request.
- `SimilarProductsService`: Handles the business logic.
- `MocksApiClient`: Handles the calls to the Mocks API service.

Redis caching is performed in the `MocksApiClient` class rather than the controller or the service. This decision is made to ensure that caching is applied at the lowest level possible in the API call chain. Here are the reasons for this approach:

1. **Responsibility Separation**: The API client class (`MocksApiClient`) is responsible for making the external API calls. By incorporating caching within the client class, we separate the concerns and keep the controller and service classes focused on their primary responsibilities.

2. **Reusability**: The API client class can be reused across different services or controllers. By including caching in the client, the caching logic becomes part of the client's behavior and can be leveraged wherever the client is used.

3. **Performance Optimization**: Caching at the client level allows us to avoid unnecessary external API calls. The client can check if the requested data is available in the cache before making an actual API call, reducing latency and improving overall performance.

By employing Redis caching within the API client, we ensure that caching is applied consistently and transparently across different service invocations.


## Prerequisites<a name="prerequisites"></a>

To run the SimilarProducts service, you need to have Docker installed.

## Running the Service<a name="running-the-service"></a>

To execute the SimilarProducts service, you can use the provided `docker-compose.yml` file. Follow the steps below:

1. You will find the `docker-compose.yml` file in the `myApp` folder.

```yaml
version: '3'
services:
  redis:
    image: redis:latest
    container_name: my-redis
    ports:
      - 6379:6379
  myapp:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: my-app
    ports:
      - 5000:5000
```

2. Open a terminal or command prompt and navigate to the directory with the `docker-compose.yml` file.

3. Run the following command to start the services:

```bash
docker-compose up
```

4. The SimilarProducts service will now be running on `http://localhost:5000`.

## API Usage<a name="api-usage"></a>

### Request<a name="request"></a>

To retrieve a list of similar products, send a GET request to the following endpoint:

```
GET /product/{productId}/similar
```

Replace `{productId}` in the endpoint URL with the actual ID of the product for which you want to find similar products.

### Response<a name="response"></a>

The API will respond with the following possible HTTP status codes:

- `200 OK` - The request was successful, and a list of similar products is returned in the response body.
- `404 Not Found` - The requested product could not be found.

The response body, when successful (HTTP status code 200), will be in JSON format and contain an array of similar products ordered by similarity. In case no matches are found, the result should be an empty list.

Each product in the response will have the following properties:

- `id` (string, required) - The unique identifier of the product.
- `name` (string, required) - The name of the product.
- `price` (number) - The price of the product.
- `availability` (boolean) - Indicates whether the product is available.

## Examples<a name="examples"></a>

### Example Request

To retrieve similar products for a product with the ID "12345", send the following GET request:

```
GET /product/12345/similar

/similar
```

### Example Response

Assuming the request is successful, the API will respond with a list of similar products in the following format:

```json
[
  {
    "id": "67890",
    "name": "Similar Product 1",
    "price": 19.99,
    "availability": true
  },
  {
    "id": "54321",
    "name": "Similar Product 2",
    "price": 29.99,
    "availability": false
  }
]
```

This response indicates that there are two similar products to the product with ID "12345". The first product has an ID of "67890", a name of "Similar Product 1", a price of $19.99, and is available. The second product has an ID of "54321", a name of "Similar Product 2", a price of $29.99, and is not available.

Please note that the actual response may vary depending on the data available in the system.

That's it! You now have the necessary information to run the SimilarProducts service and interact with the API.

# Backend dev technical test
We want to offer a new feature to our customers showing similar products to the one they are currently seeing. To do this we agreed with our front-end applications to create a new REST API operation that will provide them the product detail of the similar products for a given one. [Here](./similarProducts.yaml) is the contract we agreed.

We already have an endpoint that provides the product Ids similar for a given one. We also have another endpoint that returns the product detail by product Id. [Here](./existingApis.yaml) is the documentation of the existing APIs.

**Create a Spring boot application that exposes the agreed REST API on port 5000.**

![Diagram](./assets/diagram.jpg "Diagram")

Note that _Test_ and _Mocks_ components are given, you must only implement _yourApp_.

## Testing and Self-evaluation
You can run the same test we will put through your application. You just need to have docker installed.

First of all, you may need to enable file sharing for the `shared` folder on your docker dashboard -> settings -> resources -> file sharing.

Then you can start the mocks and other needed infrastructure with the following command.
```
docker-compose up -d simulado influxdb grafana
```
Check that mocks are working with a sample request to [http://localhost:3001/product/1/similarids](http://localhost:3001/product/1/similarids).

To execute the test run:
```
docker-compose run --rm k6 run scripts/test.js
```
Browse [http://localhost:3000/d/Le2Ku9NMk/k6-performance-test](http://localhost:3000/d/Le2Ku9NMk/k6-performance-test) to view the results.

## Evaluation
The following topics will be considered:
- Code clarity and maintainability
- Performance
- Resilience
