/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.web.reactive.function;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.ipc.netty.http.HttpServer;

import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.http.server.reactive.ServletHttpHandlerAdapter;
import org.springframework.web.reactive.function.HandlerFilterFunction;
import org.springframework.web.reactive.function.HandlerFunction;
import org.springframework.web.reactive.function.RouterFunction;
import org.springframework.web.reactive.function.ServerRequest;
import org.springframework.web.reactive.function.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.RequestPredicates.GET;
import static org.springframework.web.reactive.function.RequestPredicates.POST;
import static org.springframework.web.reactive.function.RequestPredicates.accept;
import static org.springframework.web.reactive.function.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.RouterFunctions.route;
import static org.springframework.web.reactive.function.RouterFunctions.toHttpHandler;

public class Server {

	public static final String HOST = "localhost";

	public static final int PORT = 8080;

	public static void main(String[] args) throws Exception {
		Server server = new Server();
		server.startReactorServer();
//		server.startTomcatServer();

		System.out.println("Press ENTER to exit.");
		System.in.read();
	}

	public RouterFunction<?> routingFunction() {
		PersonRepository repository = new DummyPersonRepository();
		PersonHandler handler = new PersonHandler(repository);

		// Note that andSame() groups routes with the same response body type together...
		RouterFunction<Publisher<Person>> personRoutes =
				route(GET("/person/{id}"), handler::getPerson)
				.andSame(route(GET("/person").and(accept(APPLICATION_JSON)),
						handler::listPeople));

		// ... which allows us filter these routes with one filter.
		RouterFunction<Publisher<Person>> filteredPersonRoutes =
				personRoutes.filter(this::toUpperCase);

		return filteredPersonRoutes
				.andRoute(POST("/person").and(contentType(APPLICATION_JSON)),
						handler::createPerson);
	}

	/**
	 * A {@link HandlerFilterFunction} method that converts the names of all {@link Person} objects
	 * contained in the response body to upper case. Similar methods could do object-level
	 * authorisation, auditing, logging, and related cross-cutting concerns.
	 */
	private ServerResponse<Publisher<Person>> toUpperCase(ServerRequest request,
			HandlerFunction<Publisher<Person>> next) {
		ServerResponse<Publisher<Person>> response = next.handle(request);
		Publisher<Person> people = response.body();
		Flux<Person> map = Flux.from(people)
				.map(person -> new Person(person.getName().toUpperCase(), person.getAge()));
		return ServerResponse.from(response).body(map, Person.class);

	}

	public void startReactorServer() throws InterruptedException {
		RouterFunction<?> route = routingFunction();
		HttpHandler httpHandler = toHttpHandler(route);

		ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);
		HttpServer server = HttpServer.create(HOST, PORT);
		server.startAndAwait(adapter);
	}

	public void startTomcatServer() throws LifecycleException {
		RouterFunction<?> route = routingFunction();
		HttpHandler httpHandler = toHttpHandler(route);

		Tomcat tomcatServer = new Tomcat();
		tomcatServer.setHostname(HOST);
		tomcatServer.setPort(PORT);
		Context rootContext = tomcatServer.addContext("", System.getProperty("java.io.tmpdir"));
		ServletHttpHandlerAdapter servlet = new ServletHttpHandlerAdapter(httpHandler);
		Tomcat.addServlet(rootContext, "httpHandlerServlet", servlet);
		rootContext.addServletMapping("/", "httpHandlerServlet");
		tomcatServer.start();
	}

}
