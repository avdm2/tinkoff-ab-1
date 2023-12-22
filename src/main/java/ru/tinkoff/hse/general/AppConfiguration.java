package ru.tinkoff.hse.general;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaRepositories(basePackages = {"ru.tinkoff.hse.notes.repositories", "ru.tinkoff.hse.security.repositories"})
@EntityScan(basePackages = {"ru.tinkoff.hse.notes.entities", "ru.tinkoff.hse.security.entities"})

@Configuration
public class AppConfiguration {
}
