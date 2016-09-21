package org.springframework.samples.web.reactive.function;

import java.util.HashMap;
import java.util.Map;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Arjen Poutsma
 */
public class DummyPersonRepository implements PersonRepository {

	private final Map<Integer, Person> people = new HashMap<>();

	public DummyPersonRepository() {
		this.people.put(1, new Person("John Doe", 42));
		this.people.put(2, new Person("Jane Doe", 36));
	}

	@Override
	public Mono<Person> getPerson(int id) {
		return Mono.justOrEmpty(this.people.get(id));
	}

	@Override
	public Flux<Person> allPeople() {
		return Flux.fromIterable(this.people.values());
	}

	@Override
	public Mono<Void> savePerson(Mono<Person> personMono) {
		return personMono.then(person -> {
			int id = people.size() + 1;
			people.put(id, person);
			System.out.format("Saved %s with id %d%n", person, id);
			return Mono.empty();
		});
	}
}
