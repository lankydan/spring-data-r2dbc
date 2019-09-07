package com.lankydanblog.tutorial.person.repository

import com.lankydanblog.tutorial.person.Person
import org.springframework.data.r2dbc.convert.R2dbcConverter
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.r2dbc.core.ReactiveDataAccessStrategy
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository
import org.springframework.data.relational.repository.query.RelationalEntityInformation
import reactor.core.publisher.DirectProcessor
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


class PersonRepository(
  entity: RelationalEntityInformation<Person, Int>,
  databaseClient: DatabaseClient,
  converter: R2dbcConverter,
  accessStrategy: ReactiveDataAccessStrategy
) : SimpleR2dbcRepository<Person, Int>(entity, databaseClient, converter, accessStrategy) {

  private val source: DirectProcessor<Person> = DirectProcessor.create<Person>()
  val events: Flux<Person> = source

  init {
    println("this has been created")
  }

  override fun <S : Person> save(objectToSave: S): Mono<S> {
    return super.save(objectToSave).doOnNext(source::onNext)
  }
}