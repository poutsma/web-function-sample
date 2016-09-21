package org.springframework.samples.web.reactive.function;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Person {

	private final String name;

	private final int age;

	public Person(@JsonProperty("name") String name, @JsonProperty("age") int age) {
		this.name = name;
		this.age = age;
	}

	public String getName() {
		return this.name;
	}

	public int getAge() {
		return this.age;
	}

	@Override
	public String toString() {
		return "Person{" +
				"name='" + name + '\'' +
				", age=" + age +
				'}';
	}
}
