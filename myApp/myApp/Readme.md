
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
GET /product/12345

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
