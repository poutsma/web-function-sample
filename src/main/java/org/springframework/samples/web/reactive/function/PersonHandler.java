package org.springframework.samples.web.reactive.function;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.web.reactive.function.Rendering;
import org.springframework.web.reactive.function.Request;
import org.springframework.web.reactive.function.Response;

import static org.springframework.web.reactive.function.BodyExtractors.toMono;
import static org.springframework.web.reactive.function.BodyInserters.fromPublisher;

public class PersonHandler {

	private final PersonRepository repository;

	public PersonHandler(PersonRepository repository) {
		this.repository = repository;
	}

	public Response<Publisher<Person>> getPerson(Request request) {
		Mono<Person> person = Mono.justOrEmpty(request.pathVariable("id"))
				.map(Integer::valueOf)
				.then(this.repository::getPerson);
		return Response.ok().body(fromPublisher(person, Person.class));
	}

	public Response<Mono<Void>> createPerson(Request request) {
		Mono<Person> person = request.body(toMono(Person.class));
		return Response.ok().build(this.repository.savePerson(person));
	}

	public Response<Publisher<Person>> listPeople(Request request) {
		Flux<Person> people = this.repository.allPeople();
		return Response.ok().body(fromPublisher(people, Person.class));
	}

}
