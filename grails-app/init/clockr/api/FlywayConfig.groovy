package clockr.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import javax.sql.DataSource

@Configuration
class FlywayConfig {

    @Autowired
    DataSource dataSource

    @Bean
    @FlywayDataSource
    DataSource flywayDataSource() {
        return dataSource
    }
}

