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

import org.springframework.web.reactive.function.ServerRequest;
import org.springframework.web.reactive.function.ServerResponse;

public class PersonHandler {

	private final PersonRepository repository;

	public PersonHandler(PersonRepository repository) {
		this.repository = repository;
	}

	public ServerResponse<Publisher<Person>> getPerson(ServerRequest request) {
		int personId = Integer.valueOf(request.pathVariable("id"));
		Mono<Person> person = this.repository.getPerson(personId);
		return ServerResponse.ok().body(person, Person.class);
	}

	public ServerResponse<Mono<Void>> createPerson(ServerRequest request) {
		Mono<Person> person = request.bodyToMono(Person.class);
		return ServerResponse.ok().build(this.repository.savePerson(person));
	}

	public ServerResponse<Publisher<Person>> listPeople(ServerRequest request) {
		Flux<Person> people = this.repository.allPeople();
		return ServerResponse.ok().body(people, Person.class);
	}

}
