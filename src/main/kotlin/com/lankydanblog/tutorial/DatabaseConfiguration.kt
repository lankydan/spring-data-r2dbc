package com.lankydanblog.tutorial

import io.r2dbc.mssql.MssqlConnectionConfiguration
import io.r2dbc.mssql.MssqlConnectionFactory
import io.r2dbc.spi.ConnectionFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@Configuration
@EnableR2dbcRepositories
class DatabaseConfiguration(
  @Value("\${spring.data.mssql.host}") private val host: String,
  @Value("\${spring.data.mssql.port}") private val port: Int,
  @Value("\${spring.data.mssql.database}") private val database: String,
  @Value("\${spring.data.mssql.username}") private val username: String,
  @Value("\${spring.data.mssql.password}") private val password: String
) : AbstractR2dbcConfiguration() {

  override fun connectionFactory(): ConnectionFactory {
    return MssqlConnectionFactory(
      MssqlConnectionConfiguration.builder()
        .host(host)
        .port(port)
        .database(database)
        .username(username)
        .password(password).build()
    )
  }
}