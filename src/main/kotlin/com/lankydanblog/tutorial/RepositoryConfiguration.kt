package com.lankydanblog.tutorial

import com.lankydanblog.tutorial.person.Person
import com.lankydanblog.tutorial.person.repository.PersonRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.r2dbc.core.ReactiveDataAccessStrategy
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation

@Configuration
class RepositoryConfiguration {

  @Bean
  fun personRepository(
    databaseClient: DatabaseClient,
    dataAccessStrategy: ReactiveDataAccessStrategy
  ): PersonRepository {
    val entity: RelationalPersistentEntity<Person> = dataAccessStrategy
      .converter
      .mappingContext
      .getRequiredPersistentEntity(Person::class.java) as RelationalPersistentEntity<Person>
    val relationEntityInformation: MappingRelationalEntityInformation<Person, Int> =
      MappingRelationalEntityInformation(entity, Int::class.java)
    return PersonRepository(
      relationEntityInformation,
      databaseClient,
      dataAccessStrategy.converter,
      dataAccessStrategy
    )
  }
}