package ar.edu.utn.frba.tacs.tp.api.herocardsgame.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import java.io.File
import javax.sql.DataSource


@Configuration
class DataSourceConfig(
    @Value("\${MYSQL_USER_FILE}") private val usernameFile: String,
    @Value("\${MYSQL_DB_FILE}") private val nameFile: String,
    @Value("\${MYSQL_PASSWORD_FILE}") private val passwordFile: String
) {

    @Bean
    fun dataSource(): DataSource {
        val dataSourceBuilder = DataSourceBuilder.create()
        dataSourceBuilder.driverClassName("com.mysql.cj.jdbc.Driver")
        dataSourceBuilder.url("jdbc:mysql://mysql_db:3306/${readProperty(nameFile)}")
        dataSourceBuilder.username(readProperty(usernameFile))
        dataSourceBuilder.password(readProperty(passwordFile))

        return dataSourceBuilder.build()
    }

    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory? =
        LettuceConnectionFactory(RedisStandaloneConfiguration("redis_db", 6380))


    private fun readProperty(pathName: String) = File(pathName).bufferedReader().readText().trim()
}