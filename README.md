# Spring 5 Functional Web Framework Sample

This repository contains a sample application that uses the functional web framework introduced in Spring 5.
It consists of the following types:

| Class                   | Description                                   |
| ----------------------- | --------------------------------------------- |
| `Person`                | POJO representing a person                    |
| `PersonRepository`      | Reactive repository for `Person`              |
| `DummyPersonRepository` | Dummy implementation of `PersonRepository`    |
| `PersonHandler`         | Web handler that exposes a `PersonRepository` |
| `Server`                | Contains a `main` method to start the server  |
| `Client`                | Contains a `main` method to start the client  |

### Running the Reactor Netty server
 - Build using maven
 - Run the `org.springframework.samples.web.reactive.function.Server` class
 
### Running the Tomcat server
 - Comment out the `startReactorServer()` line in `Server.java`
 - Uncomment the `startTomcatServer()` line in `Server.java`
 - Build using maven
 - Run the `org.springframework.samples.web.reactive.function.Server` class

### Running the Client
 - Build using maven
 - Run the `org.springframework.samples.web.reactive.function.Client` class
 
### Sample curl commands

Instead of running the client, here are some sample `curl` commands that access resources exposed
by this sample:

```sh
curl -v 'http://localhost:8080/person'
curl -v 'http://localhost:8080/person/1'
curl -d '{"name":"Jack Doe","age":"16"}' -H 'Content-Type: application/json' -v 'http://localhost:8080/person'
```

### License
This sample released under version 2.0 of the [Apache License][].

[Apache License]: http://www.apache.org/licenses/LICENSE-2.0


