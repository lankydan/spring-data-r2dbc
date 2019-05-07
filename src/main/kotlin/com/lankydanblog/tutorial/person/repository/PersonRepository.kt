package com.lankydanblog.tutorial.person.repository

import com.lankydanblog.tutorial.person.Person
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.data.r2dbc.repository.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

// need to define query "query derivation not yet supported"
@Repository
interface PersonRepository : R2dbcRepository<Person, Int> {

  @Query("SELECT * FROM people WHERE name = @name")
  fun findAllByName(name: String): Flux<Person>

  @Query("SELECT * FROM people WHERE age = @age")
  fun findAllByAge(age: Int): Flux<Person>
}