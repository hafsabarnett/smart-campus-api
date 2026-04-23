# smart-campus-api
Clien-Server architechtures (Smart Campus Sensor and Room Management API - JAX-RS Coursework)

A RESTful API built with JAX-RS (Jersey) and Apache Tomcat for the University of Westminster 5COSC022W Client-Server Architectures module.
This API manages Rooms and Sensors across a university campus.


Base URL | `http://localhost:8080/smart-campus-api/api/v1`

Discovery | `GET /api/v1` 

Rooms | `/api/v1/rooms` 

Sensors | `/api/v1/sensors` 

Readings | `/api/v1/sensors/{id}/readings` 

### Question 01: 
In JAX-RS a new instance of the resource class is created for every request that comes in. This means you cant store data in the resource class itself because it gets thrown away after each request.
To fix this I put all the data in a separate class called DataStore using static fields. Static fields stay in memory for the whole time the app is running so the data doesnt get lost. I used ConcurrentHashMap instead of a normal HashMap because multiple requests can come in at the same time and ConcurrentHashMap is thread safe so it wont break if two requests try to write to it at once.

