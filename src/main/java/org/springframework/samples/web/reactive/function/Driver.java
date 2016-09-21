package org.springframework.samples.web.reactive.function;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import reactor.ipc.netty.http.HttpServer;

import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.http.server.reactive.ServletHttpHandlerAdapter;
import org.springframework.web.reactive.function.RouterFunction;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.RequestPredicates.GET;
import static org.springframework.web.reactive.function.RequestPredicates.POST;
import static org.springframework.web.reactive.function.RequestPredicates.accept;
import static org.springframework.web.reactive.function.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.RouterFunctions.route;
import static org.springframework.web.reactive.function.RouterFunctions.toHttpHandler;

public class Driver {

	public static final String HOST = "localhost";

	public static final int PORT = 8080;

	public static void main(String[] args) throws Exception {
		Driver driver = new Driver();
		driver.startReactorServer();
//		driver.startTomcatServer();

		System.out.println("Press ENTER to exit.");
		System.in.read();
	}

	public RouterFunction<?> routingFunction() {
		PersonRepository repository = new DummyPersonRepository();
		PersonHandler handler = new PersonHandler(repository);

		return route(GET("/person/{id}"), handler::getPerson)
				.and(route(GET("/person").and(accept(APPLICATION_JSON)),
						handler::listPeople))
				.and(route(POST("/person").and(contentType(APPLICATION_JSON)),
						handler::createPerson));
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
