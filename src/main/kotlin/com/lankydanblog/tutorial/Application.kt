package com.lankydanblog.tutorial

import com.lankydanblog.tutorial.person.Person
import com.lankydanblog.tutorial.person.repository.PersonRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import reactor.core.publisher.*
import java.time.Duration
import java.time.temporal.ChronoUnit.*

@SpringBootApplication
class Application : CommandLineRunner {

  @Autowired
  private lateinit var personRepository: PersonRepository

  override fun run(vararg args: String?) {
    // subscribe to live inserts to people table
    personRepository.events
      .doOnComplete { log.info("Events flux has closed") }
      .subscribe { log.info("From events stream - $it") }
    // insert people records over time
    MARVEL_CHARACTERS
//      .subList(0, 10)
      .toFlux()
      .delayElements(Duration.of(1, SECONDS))
      .concatMap { personRepository.save(it) }
      .subscribe()
//
//    MARVEL_CHARACTERS
//      .subList(10, 20)
//      .toFlux()
//      .delayElements(Duration.of(2, SECONDS))
//      .concatMap { personRepository.save(it) }
//      .subscribe()
//
//    MARVEL_CHARACTERS
//      .subList(20, 22)
//      .toFlux()
//      .delayElements(Duration.of(3, SECONDS))
//      .concatMap { personRepository.save(it) }
//      .subscribe()

    Thread.sleep(25000)

    personRepository.findAll().subscribe { log.info("From findAll - $it") }

    Thread.sleep(5000)
  }

  private companion object {
    val log = LoggerFactory.getLogger(Application::class.java)

    val MARVEL_CHARACTERS = listOf(
      Person(name = "Spiderman", age = 18),
      Person(name = "Ironman", age = 48),
      Person(name = "Thor", age = 1000),
      Person(name = "Hulk", age = 49),
      Person(name = "Antman", age = 49),
      Person(name = "Blackwidow", age = 34),
      Person(name = "Starlord", age = 38),
      Person(name = "Captain America", age = 100),
      Person(name = "Warmachine", age = 50),
      Person(name = "Wasp", age = 26),
      Person(name = "Winter Soldier", age = 101),
      Person(name = "Black Panther", age = 42),
      Person(name = "Doctor Strange", age = 42),
      Person(name = "Gamora", age = 29),
      Person(name = "Groot", age = 4),
      Person(name = "Hawkeye", age = 47),
      Person(name = "Pepper Potts", age = 44),
      Person(name = "Captain Marvel", age = 59),
      Person(name = "Rocket Raccoon", age = 30),
      Person(name = "Drax", age = 49),
      Person(name = "Nebula", age = 30),
      Person(name = "Mantis", age = 32)
    )
  }
}

fun main(args: Array<String>) {
  runApplication<Application>(*args)
}
