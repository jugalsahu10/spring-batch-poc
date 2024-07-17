package com.spring_batch.poc

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication(scanBasePackages = ["com"])
@EnableJpaRepositories(basePackages = ["com"])
@EntityScan(basePackages  = ["com"])
@EnableBatchProcessing
@EnableScheduling
class PocApplication {
}

fun main(args: Array<String>) {
    runApplication<PocApplication>(*args)
}

