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

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
