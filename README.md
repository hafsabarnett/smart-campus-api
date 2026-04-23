# smart-campus-api
Clien-Server architechtures (Smart Campus Sensor and Room Management API - JAX-RS Coursework)

A RESTful API built with JAX-RS (Jersey) and Apache Tomcat for the University of Westminster 5COSC022W Client-Server Architectures module.
This API manages Rooms and Sensors across a university campus.


Base URL | `http://localhost:8080/smart-campus-api/api/v1`

Discovery | `GET /api/v1` 

Rooms | `/api/v1/rooms` 

Sensors | `/api/v1/sensors` 

Readings | `/api/v1/sensors/{id}/readings` 

## Sample curl Commands

### 1. Get the discovery endpoint
```bash
curl -X GET http://localhost:8080/smart-campus-api/api/v1
```

### 2. Get all rooms
```bash
curl -X GET http://localhost:8080/smart-campus-api/api/v1/rooms
```

### 3. Create a new room
```bash
curl -X POST http://localhost:8080/smart-campus-api/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id": "HALL-001", "name": "Great Hall", "capacity": 200}'
```

### 4. Create a new sensor
```bash
curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{"id": "TEMP-099", "type": "Temperature", "status": "ACTIVE", "currentValue": 20.0, "roomId": "LIB-301"}'
```

### 5. Get sensors filtered by type
```bash
curl -X GET "http://localhost:8080/smart-campus-api/api/v1/sensors?type=CO2"
```

### 6. Post a reading to a sensor
```bash
curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value": 23.4}'
```

### 7. Delete a room
```bash
curl -X DELETE http://localhost:8080/smart-campus-api/api/v1/rooms/HALL-001
```


### Question 1.1: In your report, explain the default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? Elaborate on how this architectural decision impacts the way you manage and synchronize your in-memory data structures (maps/lists) to prevent data loss or race conditions.


In JAX-RS a new instance of the resource class is created for every request that comes in. This means you cant store data in the resource class itself because it gets deleted after each request.
To fix this I put all the data in a separate class called DataStore using static fields. Static fields stay in memory for the whole time the app is running so the data doesnt get lost. I used ConcurrentHashMap instead of a normal HashMap because multiple requests can come in at the same time and ConcurrentHashMap is thread safe so it wont break if two requests try to write to it at once.

### Question 1.2: Why is the provision of ”Hypermedia” (links and navigation within responses) considered a hallmark of advanced RESTful design (HATEOAS)? How does this approach benefit client developers compared to static documentation?

HATEOAS means the API includes links in its responses to tell clients where to find other resources. So instead of having to know all the URLs in advance the client can just follow the links like clicking links on a website.


### Question 2.1: When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects? Consider network bandwidth and client side processing.

If you only return Ids the response is smaller and faster but the client has to make another request for every room to get the actual details which could mean lots of extra requests if there are many rooms. For my API I returned full objects because there are only a few rooms.

### Question 2.2: Is the DELETE operation idempotent in your implementation? Provide a detailed justification by describing what happens if a client mistakenly sends the exact same DELETE request for a room multiple times.


Yes DELETE is idempotent. This means calling it multiple times has the same effect as calling it once. If you delete HALL-001 the first time it gets removed and you get 204 No Content back. If you try to delete it again it doesnt exist anymore so you get 404 Not Found. 

### Question 3.1: We explicitly use the @Consumes (MediaType.APPLICATION_JSON) annotation on the POST method. Explain the technical consequences if a client attempts to send data in a different format, such as text/plain or application/xml. How does JAX-RS handle this mismatch?

If a client sends the wrong content type, JAX-RS automatically returns a 415 Unsupported Media Type error and the method never gets called. 

### Question 3.2: You implemented this filtering using @QueryParam. Contrast this with an alternative design where the type is part of the URL path (e.g., /api/vl/sensors/type/CO2). Why is the query parameter approach generally considered superior for filtering and searching collections?

Query parameters are better than using path parameter because @QueryParam is used to filter resources, they can be combined and are optional. on the other hand, path parameters seperate are a lot more complex and therefore can become messy.


### Question 4.1: Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity in large APIs compared to defining every nested path (e.g., sensors/{id}/readings/{rid}) in one massive controller class?

The sub resource locator pattern lets you split the handling of nested URLs across multiple classes instead of putting everything in one big class.

In my code SensorResource has a method that returns a SensorReadingResource object when a request comes in for /sensors/{id}/readings. Jersey then uses that object to handle the actual GET or POST request for readings.

This keeps things organised because each class only does one thing. SensorResource handles sensors and SensorReadingResource handles readings. If everything was in one class it would get really long and hard to read. It also makes it easier to test each part separately.

### Question 5.2: Why is HTTP 422 often considered more semantically accurate than a standard 404 when the issue is a missing reference inside a valid JSON payload?

404 means the URL doesnt exist but in this case the URL /api/v1/sensors does exist and works fine. The problem is the roomId in the request body is pointing to a room that doesnt exist.
422 is more accurate because it means the request was understood and the URL is valid but there is something wrong with the data inside the body. Using 404 would confuse the client into thinking the endpoint doesnt exist when it does.

### Question 5.4:  From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace?

Stack traces are dangerous to expose because they give away a lot of information about how the app works. They show the class and package names, the libraries being used and their versions, and sometimes even file paths on the server. An attacker could use this to find known security vulnerabilities in the libraries or to understand the structure of the app and find weaknesses.

In my API the GlobalExceptionMapper catches any unexpected errors and logs the full details on the server side but only returns a generic message to the client so none of this information gets leaked.


### Question 5.5: Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than manually inserting Logger.info() statements inside every single resource method?

If I added logging manually to every method I would have to write the same code over and over again in every resource class. If I wanted to change the format I would have to update every single method which would take ages and I might miss some.

Using a filter means I write the logging code once and it automatically runs on every single request and response without me having to do anything else. It also keeps the resource methods clean because they only contain the actual business logic and not logging code mixed in.
